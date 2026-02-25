package com.relatospapel.ms_books_catalogue.service.impl;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.opensearch._types.FieldValue;
import org.opensearch.client.opensearch._types.OpenSearchException;
import org.opensearch.client.opensearch.core.SearchResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.relatospapel.ms_books_catalogue.dto.request.BookCreateRequest;
import com.relatospapel.ms_books_catalogue.dto.request.BookPatchRequest;
import com.relatospapel.ms_books_catalogue.dto.response.BookAvailabilityResponse;
import com.relatospapel.ms_books_catalogue.dto.response.BookResponse;
import com.relatospapel.ms_books_catalogue.entity.BookEntity;
import com.relatospapel.ms_books_catalogue.exception.NotFoundException;
import com.relatospapel.ms_books_catalogue.repository.BookRepository;
import com.relatospapel.ms_books_catalogue.repository.CategoryRepository;
import com.relatospapel.ms_books_catalogue.search.document.BookDocument;
import com.relatospapel.ms_books_catalogue.service.BookService;
import com.relatospapel.ms_books_catalogue.service.SearchSyncService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class BookServiceImpl implements BookService {

  private final BookRepository bookRepo;             
  private final CategoryRepository categoryRepo;      
  private final SearchSyncService searchSyncService; 
  private final OpenSearchClient openSearchClient;

  @Override
  public BookResponse create(BookCreateRequest req) {
    BookEntity book = BookEntity.builder()
        .title(req.getTitle())
        .author(req.getAuthor())
        .publicationDate(req.getPublicationDate())
        .isbn(req.getIsbn())
        .rating(req.getRating())
        .visible(req.getVisible())
        .stock(req.getStock())
        .build();

    if (req.getCategoryId() != null) {
      var cat = categoryRepo.findById(req.getCategoryId())
          .orElseThrow(() -> new NotFoundException("Categoría no encontrada"));
      book.setCategory(cat);
    }

    BookEntity saved = bookRepo.save(book);

    searchSyncService.upsertBook(saved);

    return toResponse(saved);
  }

  @Override
  @Transactional(readOnly = true)
  public BookResponse getById(UUID id) {
    return toResponse(bookRepo.findById(id)
        .orElseThrow(() -> new NotFoundException("Libro no encontrado")));
  }

  @Override
  @Transactional(readOnly = true)
  public List<BookResponse> search(String q, LocalDate publicationDate,
      UUID categoryId, Integer rating, Boolean visible) {

    final String term = (q == null) ? "" : q.trim();
    final boolean hasText = !term.isBlank();

    try {
      SearchResponse<BookDocument> response = openSearchClient.search(s -> {
        s.index(SearchIndexServiceImpl.BOOKS_INDEX).size(100);

        boolean hasFilters =
            publicationDate != null ||
            categoryId != null ||
            rating != null ||
            visible != null;

        if (!hasText && !hasFilters) {
          s.query(x -> x.matchAll(m -> m));
          return s;
        }

        s.query(x -> x.bool(b -> {

          if (hasText) {
            b.must(m -> m.simpleQueryString(sqs -> sqs
                .query(term)
                .fields(List.of(
                    "title^3",
                    "title._2gram^2",
                    "title._3gram^2",
                    "author^2",
                    "author._2gram",
                    "author._3gram",
                    "isbn^4",
                    "category.name"
                ))
                .defaultOperator(org.opensearch.client.opensearch._types.query_dsl.Operator.And)
            ));
          }

          if (publicationDate != null) {
            b.filter(f -> f.term(t -> t.field("publicationDate")
                .value(FieldValue.of(publicationDate.toString()))));
          }
          if (categoryId != null) {
            b.filter(f -> f.term(t -> t.field("category.id")
                .value(FieldValue.of(categoryId.toString()))));
          }
          if (rating != null) {
            b.filter(f -> f.term(t -> t.field("rating").value(FieldValue.of(rating))));
          }
          if (visible != null) {
            b.filter(f -> f.term(t -> t.field("visible").value(FieldValue.of(visible))));
          }

          return b;
        }));

        return s;
      }, BookDocument.class);

      return response.hits().hits().stream()
          .map(h -> {
            BookDocument d = h.source();
            if (d == null) return null;

            return BookResponse.builder()
                .id(d.getId() != null ? UUID.fromString(d.getId()) : null)
                .title(d.getTitle())
                .author(d.getAuthor())
                .publicationDate(d.getPublicationDate() != null ? LocalDate.parse(d.getPublicationDate()) : null)
                .isbn(d.getIsbn())
                .rating(d.getRating())
                .visible(d.getVisible())
                .stock(d.getStock())
                .categoryId(d.getCategory() != null && d.getCategory().getId() != null
                    ? UUID.fromString(d.getCategory().getId())
                    : null)
                .categoryName(d.getCategory() != null ? d.getCategory().getName() : null)
                .build();
          })
          .filter(Objects::nonNull)
          .toList();

    } catch (IOException | OpenSearchException e) {
      return List.of();
    }
  }

  @Override
  public BookResponse patch(UUID id, BookPatchRequest req) {
    var b = bookRepo.findById(id).orElseThrow(() -> new NotFoundException("Libro no encontrado"));

    if (req.getTitle() != null) b.setTitle(req.getTitle());
    if (req.getAuthor() != null) b.setAuthor(req.getAuthor());
    if (req.getPublicationDate() != null) b.setPublicationDate(req.getPublicationDate());
    if (req.getIsbn() != null) b.setIsbn(req.getIsbn());
    if (req.getRating() != null) b.setRating(req.getRating());
    if (req.getVisible() != null) b.setVisible(req.getVisible());
    if (req.getStock() != null) b.setStock(req.getStock());

    if (req.getCategoryId() != null) {
      var cat = categoryRepo.findById(req.getCategoryId())
          .orElseThrow(() -> new NotFoundException("Categoría no encontrada"));
      b.setCategory(cat);
    }

    BookEntity saved = bookRepo.save(b);

    searchSyncService.upsertBook(saved);

    return toResponse(saved);
  }

  @Override
  public void delete(UUID id) {
    if (!bookRepo.existsById(id)) throw new NotFoundException("Libro no encontrado");
    bookRepo.deleteById(id);

    searchSyncService.deleteBook(id);
  }

  @Override
  @Transactional(readOnly = true)
  public BookAvailabilityResponse availability(UUID id, int quantity) {
    var opt = bookRepo.findById(id);

    if (opt.isEmpty()) {
      return BookAvailabilityResponse.builder()
          .bookId(id).exists(false).visible(false).currentStock(0).available(false).reason("NO_ENCONTRADO")
          .build();
    }

    var b = opt.get();
    boolean visible = Boolean.TRUE.equals(b.getVisible());
    Integer stockValue = b.getStock();
    int stock = stockValue == null ? 0 : stockValue;

    if (!visible) {
      return BookAvailabilityResponse.builder()
          .bookId(id).exists(true).visible(false).currentStock(stock).available(false).reason("OCULTO")
          .build();
    }
    if (quantity <= 0) {
      return BookAvailabilityResponse.builder()
          .bookId(id).exists(true).visible(true).currentStock(stock).available(false).reason("CANTIDAD_INVALIDA")
          .build();
    }
    if (stock < quantity) {
      return BookAvailabilityResponse.builder()
          .bookId(id).exists(true).visible(true).currentStock(stock).available(false).reason("SIN_STOCK")
          .build();
    }

    return BookAvailabilityResponse.builder()
        .bookId(id).exists(true).visible(true).currentStock(stock).available(true).reason("OK")
        .build();
  }

  private BookResponse toResponse(BookEntity b) {
    return BookResponse.builder()
        .id(b.getId())
        .title(b.getTitle())
        .author(b.getAuthor())
        .publicationDate(b.getPublicationDate())
        .isbn(b.getIsbn())
        .rating(b.getRating())
        .visible(b.getVisible())
        .stock(b.getStock())
        .categoryId(b.getCategory() != null ? b.getCategory().getId() : null)
        .categoryName(b.getCategory() != null ? b.getCategory().getName() : null)
        .build();
  }
}
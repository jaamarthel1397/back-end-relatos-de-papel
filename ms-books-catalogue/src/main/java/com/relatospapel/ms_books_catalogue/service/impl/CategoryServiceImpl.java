package com.relatospapel.ms_books_catalogue.service.impl;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.opensearch.core.SearchResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.relatospapel.ms_books_catalogue.dto.request.CategoryCreateRequest;
import com.relatospapel.ms_books_catalogue.dto.response.CategoryResponse;
import com.relatospapel.ms_books_catalogue.entity.CategoryEntity;
import com.relatospapel.ms_books_catalogue.exception.NotFoundException;
import com.relatospapel.ms_books_catalogue.repository.CategoryRepository;
import com.relatospapel.ms_books_catalogue.search.document.CategoryDocument;
import com.relatospapel.ms_books_catalogue.service.CategoryService;
import com.relatospapel.ms_books_catalogue.service.SearchSyncService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class CategoryServiceImpl implements CategoryService {

  private final CategoryRepository repo;
  private final SearchSyncService searchSyncService;
  private final OpenSearchClient openSearchClient;

  @Override
  public CategoryResponse create(CategoryCreateRequest req) {
    CategoryEntity c = CategoryEntity.builder()
        .name(req.getName())
        .description(req.getDescription())
        .build();

    c = repo.save(c);

    searchSyncService.upsertCategory(c);

    return CategoryResponse.builder()
        .id(c.getId())
        .name(c.getName())
        .description(c.getDescription())
        .build();
  }
  @Override
  @Transactional(readOnly = true)
  public List<CategoryResponse> list() {
    try {
      SearchResponse<CategoryDocument> response = openSearchClient.search(s -> {
        s.index(SearchIndexServiceImpl.CATEGORIES_INDEX).size(100);
        s.query(q -> q.matchAll(m -> m));
        return s;
      }, CategoryDocument.class);

      return response.hits().hits().stream()
          .map(h -> {
            CategoryDocument d = h.source();
            if (d == null) return null;
            return CategoryResponse.builder()
                .id(d.getId() != null ? UUID.fromString(d.getId()) : null)
                .name(d.getName())
                .description(d.getDescription())
                .build();
          })
          .filter(Objects::nonNull)
          .toList();

    } catch (IOException | RuntimeException e) {
      return List.of();
    }
  }

  @Override
  @Transactional(readOnly = true)
  public CategoryResponse get(UUID id) {
    var c = repo.findById(id).orElseThrow(() -> new NotFoundException("Categoría no encontrada"));
    return CategoryResponse.builder()
        .id(c.getId())
        .name(c.getName())
        .description(c.getDescription())
        .build();
  }
}
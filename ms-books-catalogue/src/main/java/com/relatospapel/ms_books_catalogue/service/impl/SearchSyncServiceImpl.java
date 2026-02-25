package com.relatospapel.ms_books_catalogue.service.impl;

import java.io.IOException;
import java.util.UUID;

import org.opensearch.client.opensearch.OpenSearchClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.relatospapel.ms_books_catalogue.entity.BookEntity;
import com.relatospapel.ms_books_catalogue.entity.CategoryEntity;
import com.relatospapel.ms_books_catalogue.search.SearchMapper;
import com.relatospapel.ms_books_catalogue.search.document.BookDocument;
import com.relatospapel.ms_books_catalogue.search.document.CategoryDocument;
import com.relatospapel.ms_books_catalogue.service.SearchSyncService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SearchSyncServiceImpl implements SearchSyncService {

  private static final Logger log = LoggerFactory.getLogger(SearchSyncServiceImpl.class);

  private final OpenSearchClient client;

  @Override
  public void upsertBook(BookEntity book) {
    if (book == null || book.getId() == null) return;

    try {
      // ✅ evita problemas por LAZY (si aplica)
      if (book.getCategory() != null) {
        book.getCategory().getName();
      }

      BookDocument doc = SearchMapper.toBookDoc(book);

      client.index(i -> i
          .index(SearchIndexServiceImpl.BOOKS_INDEX)
          .id(doc.getId())     // _id
          .document(doc)       // incluye campo id dentro también
      );
    } catch (IOException | RuntimeException e) {
      log.warn("OpenSearch upsertBook falló (se continúa).", e);
    }
  }

  @Override
  public void deleteBook(UUID id) {
    if (id == null) return;

    try {
      client.delete(d -> d
          .index(SearchIndexServiceImpl.BOOKS_INDEX)
          .id(id.toString()));
    } catch (IOException | RuntimeException e) {
      log.warn("OpenSearch deleteBook falló (se continúa).", e);
    }
  }

  @Override
  public void upsertCategory(CategoryEntity category) {
    if (category == null || category.getId() == null) return;

    try {
      CategoryDocument doc = SearchMapper.toCategoryDoc(category);

      client.index(i -> i
          .index(SearchIndexServiceImpl.CATEGORIES_INDEX)
          .id(doc.getId())
          .document(doc)
      );
    } catch (IOException | RuntimeException e) {
      log.warn("OpenSearch upsertCategory falló (se continúa).", e);
    }
  }

  @Override
  public void deleteCategory(UUID id) {
    if (id == null) return;

    try {
      client.delete(d -> d
          .index(SearchIndexServiceImpl.CATEGORIES_INDEX)
          .id(id.toString()));
    } catch (IOException | RuntimeException e) {
      log.warn("OpenSearch deleteCategory falló (se continúa).", e);
    }
  }
}
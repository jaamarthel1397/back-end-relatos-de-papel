package com.relatospapel.ms_books_catalogue.service;

import java.util.UUID;

import com.relatospapel.ms_books_catalogue.entity.BookEntity;
import com.relatospapel.ms_books_catalogue.entity.CategoryEntity;

public interface SearchSyncService {

  void upsertBook(BookEntity book);
  void deleteBook(UUID id);

  void upsertCategory(CategoryEntity category);
  void deleteCategory(UUID id);
}

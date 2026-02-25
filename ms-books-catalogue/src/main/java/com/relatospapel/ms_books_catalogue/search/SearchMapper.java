package com.relatospapel.ms_books_catalogue.search;

import com.relatospapel.ms_books_catalogue.entity.BookEntity;
import com.relatospapel.ms_books_catalogue.entity.CategoryEntity;
import com.relatospapel.ms_books_catalogue.search.document.BookDocument;
import com.relatospapel.ms_books_catalogue.search.document.CategoryDocument;

public class SearchMapper {

  private SearchMapper() {}

  public static CategoryDocument toCategoryDoc(CategoryEntity c) {
    if (c == null) return null;
    return CategoryDocument.builder()
        .id(c.getId() != null ? c.getId().toString() : null)
        .name(c.getName())
        .description(c.getDescription())
        .build();
  }

  public static BookDocument toBookDoc(BookEntity b) {
    return BookDocument.builder()
        .id(b.getId() != null ? b.getId().toString() : null)
        .title(b.getTitle())
        .author(b.getAuthor())
        .publicationDate(b.getPublicationDate() != null ? b.getPublicationDate().toString() : null)
        .isbn(b.getIsbn())
        .rating(b.getRating())
        .visible(b.getVisible())
        .stock(b.getStock())
        .category(toCategoryDoc(b.getCategory()))
        .build();
  }
}

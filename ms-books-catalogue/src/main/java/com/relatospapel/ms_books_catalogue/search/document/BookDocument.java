package com.relatospapel.ms_books_catalogue.search.document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class BookDocument {
  private String id;

  private String title;
  private String author;
  private String publicationDate;

  private String isbn;
  private Integer rating;
  private Boolean visible;
  private Integer stock;

  private CategoryDocument category;
}

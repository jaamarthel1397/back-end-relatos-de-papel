package com.relatospapel.ms_books_catalogue.search.document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class CategoryDocument {
  private String id;
  private String name;
  private String description;
}
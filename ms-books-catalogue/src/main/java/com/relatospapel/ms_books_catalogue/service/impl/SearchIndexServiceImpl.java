package com.relatospapel.ms_books_catalogue.service.impl;

import java.io.IOException;

import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.opensearch._types.mapping.Property;
import org.opensearch.client.opensearch._types.mapping.TypeMapping;
import org.opensearch.client.opensearch.indices.CreateIndexRequest;
import org.opensearch.client.opensearch.indices.ExistsRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.relatospapel.ms_books_catalogue.service.SearchIndexService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SearchIndexServiceImpl implements SearchIndexService {

  private final OpenSearchClient client;
  private static final Logger log = LoggerFactory.getLogger(SearchIndexServiceImpl.class);

  public static final String BOOKS_INDEX = "books_catalogue";
  public static final String CATEGORIES_INDEX = "categories_catalogue";

  @Override
  public void ensureIndexes() {
    try {
      ensureBooksIndex();
      ensureCategoriesIndex();
    } catch (IOException | RuntimeException e) {
      log.warn("No se pudieron asegurar los índices de OpenSearch (se continúa sin romper la app).", e);
    }
  }

  private void ensureBooksIndex() throws IOException {
    boolean exists = client.indices()
        .exists(ExistsRequest.of(e -> e.index(BOOKS_INDEX)))
        .value();

    if (exists) return;

    TypeMapping mapping = new TypeMapping.Builder()
        .properties("id", Property.of(p -> p.keyword(k -> k)))
        .properties("title", Property.of(p -> p.searchAsYouType(s -> s)))
        .properties("author", Property.of(p -> p.searchAsYouType(s -> s)))
        .properties("publicationDate", Property.of(p -> p.date(d -> d)))
        .properties("isbn", Property.of(p -> p.keyword(k -> k)))
        .properties("rating", Property.of(p -> p.integer(i -> i)))
        .properties("visible", Property.of(p -> p.boolean_(b -> b)))
        .properties("stock", Property.of(p -> p.integer(i -> i)))
        .properties("category", Property.of(p -> p.object(o -> o
            .properties("id", Property.of(pp -> pp.keyword(k -> k)))
            .properties("name", Property.of(pp -> pp.searchAsYouType(s -> s)))
            .properties("description", Property.of(pp -> pp.text(t -> t)))
        )))
        .build();

    client.indices().create(new CreateIndexRequest.Builder()
        .index(BOOKS_INDEX)
        .mappings(mapping)
        .build());
  }

  private void ensureCategoriesIndex() throws IOException {
    boolean exists = client.indices()
        .exists(ExistsRequest.of(e -> e.index(CATEGORIES_INDEX)))
        .value();

    if (exists) return;

    TypeMapping mapping = new TypeMapping.Builder()
        .properties("id", Property.of(p -> p.keyword(k -> k)))
        .properties("name", Property.of(p -> p.searchAsYouType(s -> s)))
        .properties("description", Property.of(p -> p.text(t -> t)))
        .build();

    client.indices().create(new CreateIndexRequest.Builder()
        .index(CATEGORIES_INDEX)
        .mappings(mapping)
        .build());
  }
}
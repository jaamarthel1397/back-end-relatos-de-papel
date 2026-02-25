package com.relatospapel.ms_books_catalogue.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.relatospapel.ms_books_catalogue.service.SearchIndexService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SearchStartupRunner implements CommandLineRunner {

  private final SearchIndexService searchIndexService;

  @Override
  public void run(String... args) {
    searchIndexService.ensureIndexes();
  }
}
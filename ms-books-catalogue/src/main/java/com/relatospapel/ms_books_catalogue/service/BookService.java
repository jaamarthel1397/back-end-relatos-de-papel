package com.relatospapel.ms_books_catalogue.service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import com.relatospapel.ms_books_catalogue.dto.request.BookCreateRequest;
import com.relatospapel.ms_books_catalogue.dto.request.BookPatchRequest;
import com.relatospapel.ms_books_catalogue.dto.response.BookAvailabilityResponse;
import com.relatospapel.ms_books_catalogue.dto.response.BookResponse;

public interface BookService {
  BookResponse create(BookCreateRequest req);
  BookResponse getById(UUID id);
  List<BookResponse> search(String q,LocalDate publicationDate,UUID categoryId,Integer rating,Boolean visible);
  BookResponse patch(UUID id, BookPatchRequest req);
  void delete(UUID id);

  BookAvailabilityResponse availability(UUID id, int quantity);
}

package com.relatospapel.ms_books_catalogue.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import com.relatospapel.ms_books_catalogue.dto.request.BookCreateRequest;
import com.relatospapel.ms_books_catalogue.dto.request.BookPatchRequest;
import com.relatospapel.ms_books_catalogue.dto.response.BookAvailabilityResponse;
import com.relatospapel.ms_books_catalogue.dto.response.BookResponse;
import com.relatospapel.ms_books_catalogue.service.BookService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/catalogue/books")
@RequiredArgsConstructor
public class BookController {

  private final BookService service;

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public BookResponse create(@Valid @RequestBody BookCreateRequest req) {
    return service.create(req);
  }

  @GetMapping("/{id}")
  public BookResponse get(@PathVariable UUID id) {
    return service.getById(id);
  }

  @GetMapping
  public List<BookResponse> search(
      @RequestParam(required = false) String q,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate publicationDate,
      @RequestParam(required = false) UUID categoryId,
      @RequestParam(required = false) Integer rating,
      @RequestParam(required = false) Boolean visible
  ) {
    return service.search(q, publicationDate, categoryId, rating, visible);
  }

  @PatchMapping("/{id}")
  public BookResponse patch(@PathVariable UUID id, @Valid @RequestBody BookPatchRequest req) {
    return service.patch(id, req);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(@PathVariable UUID id) {
    service.delete(id);
  }

  @GetMapping("/{id}/availability")
  public BookAvailabilityResponse availability(@PathVariable UUID id, @RequestParam int quantity) {
    return service.availability(id, quantity);
  }
}
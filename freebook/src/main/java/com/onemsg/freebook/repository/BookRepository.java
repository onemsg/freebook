package com.onemsg.freebook.repository;

import com.onemsg.freebook.model.Book;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface BookRepository {
    
    Mono<Book> findById(String id);

    Flux<Book> findByTitleContaining(String title);
    
    Flux<Book> findAll();

    Mono<Boolean> save(Book book);

    Mono<Boolean> update(String id, Book book);

    Mono<Boolean> delete(String id);

}

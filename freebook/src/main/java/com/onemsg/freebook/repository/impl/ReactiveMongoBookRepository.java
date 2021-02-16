package com.onemsg.freebook.repository.impl;

import java.time.LocalDateTime;

import com.mongodb.client.model.Filters;
import com.mongodb.reactivestreams.client.MongoCollection;
import com.mongodb.reactivestreams.client.MongoDatabase;
import com.onemsg.freebook.model.Book;
import com.onemsg.freebook.repository.BookRepository;

import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public class ReactiveMongoBookRepository implements BookRepository {

    private static final String COLLECTION_NAME = "book";

    private MongoCollection<Book> collection;

    @Autowired
    public ReactiveMongoBookRepository(MongoDatabase database) {
        this.collection = database.getCollection(COLLECTION_NAME, Book.class);
    }

    @Override
    public Mono<Book> findById(String id) {
        Bson filter = Filters.eq("_id", new ObjectId(id));
        return Mono.from(collection.find(filter).first());
    }

    @Override
    public Flux<Book> findByTitleContaining(String title) {
        Bson filter = Filters.text(title);
        return Flux.from(collection.find(filter));
    }

    @Override
    public Flux<Book> findAll() {
        return Flux.from(collection.find());
    }

    @Override
    public Mono<Boolean> save(Book book) {
        book.setId(null);
        book.setCreatedOn(LocalDateTime.now());
        return Mono.from(collection.insertOne(book)).map(r -> r.wasAcknowledged());
    }

    @Override
    public Mono<Boolean> update(String id, Book book) {
        book.setId(null);
        book.setUpdatedOn(LocalDateTime.now());
        Bson filter = Filters.eq("_id", new ObjectId(id));
        return Mono.from(collection.replaceOne(filter, book)).map(r -> r.getModifiedCount() > 0L);
    }

    @Override
    public Mono<Boolean> delete(String id) {
        Bson filter = Filters.eq("_id", new ObjectId(id));
        return Mono.from(collection.deleteOne(filter)).map(r -> r.wasAcknowledged());
    }


    
}

package com.onemsg.freebook.repository.impl;

import java.time.LocalDateTime;
import java.util.Objects;

import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
import com.mongodb.reactivestreams.client.MongoCollection;
import com.mongodb.reactivestreams.client.MongoDatabase;
import com.onemsg.freebook.model.Comment;
import com.onemsg.freebook.repository.CommentRepository;

import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public class ReactiveMongoCommentRepository implements CommentRepository {

    private static final String COLLECTION_NAME = "comment";

    private MongoCollection<Comment> collection;

    @Autowired
    public ReactiveMongoCommentRepository(MongoDatabase database){
        collection = database.getCollection(COLLECTION_NAME, Comment.class);
    }

    @Override
    public Mono<Comment> findById(String id) {
        Bson filter = Filters.eq("_id", new ObjectId(id));
        return Mono.from(collection.find(filter).first());
    }

    @Override
    public Flux<Comment> findAll() {
        return Flux.from(collection.find().sort(Sorts.descending("cretedOn")));
    }

    @Override
    public Flux<Comment> findAll(Integer start, Integer limit) {

        var findPublisher = collection.find().sort(Sorts.descending("cretedOn"));
        if(Objects.nonNull(start)) findPublisher.skip(start.intValue());
        if(Objects.nonNull(limit)) findPublisher.limit(limit.intValue());
        return Flux.from(findPublisher);
    }

    @Override
    public Mono<Boolean> save(Comment comment) {
        comment.setId(null);
        comment.setCretedOn(LocalDateTime.now());
        return Mono.from(collection.insertOne(comment)).map(result -> result.wasAcknowledged());
    }

    @Override
    public Mono<Boolean> update(String id, Comment comment) {
        comment.setId(null);
        Bson filter = Filters.eq("_id", new ObjectId(id));
        return Mono.from(collection.replaceOne(filter, comment)).map(result -> result.wasAcknowledged());
    }

    @Override
    public Mono<Boolean> delete(String id) {
        Bson filter = Filters.eq("_id", new ObjectId(id));
        return Mono.from(collection.deleteOne(filter)).map(result -> result.wasAcknowledged());
    }
    
}

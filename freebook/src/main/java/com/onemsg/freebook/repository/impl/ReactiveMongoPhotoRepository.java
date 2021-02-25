package com.onemsg.freebook.repository.impl;

import java.time.LocalDateTime;

import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import com.mongodb.reactivestreams.client.MongoCollection;
import com.mongodb.reactivestreams.client.MongoDatabase;
import com.onemsg.freebook.model.Photo;
import com.onemsg.freebook.repository.PhotoRepository;

import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import reactor.core.publisher.Mono;

@Repository
public class ReactiveMongoPhotoRepository implements PhotoRepository {

    private static final String COLLECTION_NAME = "photo";

    private MongoCollection<Photo> collection;

    @Autowired
    public ReactiveMongoPhotoRepository(MongoDatabase database) {
        collection = database.getCollection(COLLECTION_NAME, Photo.class);
    }

    @Override
    public Mono<Photo> find(String id) {
        Bson filter = Filters.eq("_id", new ObjectId(id));
        return Mono.from(collection.find(filter).first());
    }

    @Override
    public Mono<byte[]> findBytes(String id) {
        Bson filter = Filters.eq("_id", new ObjectId(id)); 
        return Mono.from(
            collection.find(filter).projection(Projections.include("bytes")).first())
                .map(photo -> photo.getBytes());
    }

    @Override
    public Mono<String> save(Photo photo) {
        photo.setId(null);
        photo.setCreatedOn(LocalDateTime.now());
        return Mono.from(collection.insertOne(photo)).map(result -> 
            result.getInsertedId().asObjectId().getValue().toHexString()   
        );
    }

    @Override
    public Mono<Boolean> delete(String id) {
        Bson filter = Filters.eq("_id", new ObjectId(id));
        return Mono.from(collection.deleteOne(filter)).map(result -> result.wasAcknowledged());
    }
}

package com.onemsg.freebook.repository.impl;

import com.mongodb.client.model.Filters;
import com.mongodb.reactivestreams.client.MongoCollection;
import com.mongodb.reactivestreams.client.MongoDatabase;
import com.onemsg.freebook.model.User;
import com.onemsg.freebook.repository.UserRepository;

import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import reactor.core.publisher.Mono;

/**
 * ReactiveMongoUserRepository
 */
@Repository
public class ReactiveMongoUserRepository implements UserRepository{
	
	private static final String COLLECTION_NAME = "user";

    private MongoCollection<User> collection;

    @Autowired
    public ReactiveMongoUserRepository(MongoDatabase database){
        collection = database.getCollection(COLLECTION_NAME, User.class);
    }


	@Override
	public Mono<User> findById(String id) {
		Bson filter = Filters.eq("_id", new ObjectId(id));
        return Mono.from(collection.find(filter).first());
	}

	@Override
	public Mono<User> findByPhoneNumber(String phoneNumber) {
		Bson filter = Filters.eq("phoneNumber", phoneNumber);
        return Mono.from(collection.find(filter).first());
	}

	@Override
	public Mono<User> findByName(String username) {
		Bson filter = Filters.eq("username", username);
        return Mono.from(collection.find(filter).first());
	}

    
}
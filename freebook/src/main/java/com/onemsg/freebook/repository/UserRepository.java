package com.onemsg.freebook.repository;

import com.onemsg.freebook.model.User;

import reactor.core.publisher.Mono;

public interface UserRepository {
    
    Mono<User> findById(String id);

    Mono<User> findByPhoneNumber(String phoneNumber);

    Mono<User> findByName(String name);

}

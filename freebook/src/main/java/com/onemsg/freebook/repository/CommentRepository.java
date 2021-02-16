package com.onemsg.freebook.repository;

import com.onemsg.freebook.model.Comment;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CommentRepository {
    
    Mono<Comment> findById(String id);
    
    Flux<Comment> findAll();

    Flux<Comment> findAll(Integer start, Integer limit);

    Mono<Boolean> save(Comment comment);

    Mono<Boolean> update(String id, Comment comment);

    Mono<Boolean> delete(String id);
}

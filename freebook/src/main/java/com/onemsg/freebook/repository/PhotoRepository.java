package com.onemsg.freebook.repository;

import com.onemsg.freebook.model.Photo;

import reactor.core.publisher.Mono;

public interface PhotoRepository {
    
    Mono<Photo> find(String id);

    Mono<byte[]> findBytes(String id);

    // Flux<Photo> find(List<String> ids);

    Mono<String> save(Photo photo);

    // Mono<List<String>> save(List<String> file);

    Mono<Boolean> delete(String id);
}

package com.onemsg.freebook.handler;

import java.util.Objects;

import com.onemsg.freebook.model.User;
import com.onemsg.freebook.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import reactor.core.publisher.Mono;

@Component
public class UserHandler {
    
    @Autowired
    private UserRepository userRepository;

    public Mono<ServerResponse> login(ServerRequest request){
        return request.bodyToMono(User.class).flatMap( user -> {
            if(user == null || user.getPhoneNumber() == null || user.getPassword() == null){
                return ServerResponse.badRequest().build();
            }
            return userRepository.findByPhoneNumber(user.getPassword())
                .flatMap( user2 -> {
                    if (user2 == null || Objects.equals(user.getPassword(), user2.getPassword())){
                        return ServerResponse.notFound().build();
                    }
                    request.session().subscribe(session -> session.getAttributes().put("user", user2) );
                    return ServerResponse.ok().build();
                });
        });
    }

    public Mono<ServerResponse> logout(ServerRequest request){
        return request.session().flatMap( session -> {
            if(session.getAttribute("user") == null){
                return ServerResponse.status(HttpStatus.UNAUTHORIZED).build();
            }
            session.getAttributes().remove("user");
            return ServerResponse.ok().build();
        });
    }
}

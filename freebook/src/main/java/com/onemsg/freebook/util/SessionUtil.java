package com.onemsg.freebook.util;

import com.onemsg.freebook.model.User;

import org.springframework.web.reactive.function.server.ServerRequest;

import reactor.core.publisher.Mono;

import static com.onemsg.freebook.config.Constants.WEB_SESSION_KEY_USER;


public class SessionUtil {
    
    public static Mono<User> thenUser(ServerRequest request){
        return request.session().map(session -> (User) session.getAttribute(WEB_SESSION_KEY_USER));
    }
}

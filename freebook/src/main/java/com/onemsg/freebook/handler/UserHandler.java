package com.onemsg.freebook.handler;

import java.util.Map;
import java.util.Objects;

import com.onemsg.freebook.model.ErrorModel;
import com.onemsg.freebook.model.User;
import com.onemsg.freebook.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import reactor.core.publisher.Mono;

import static com.onemsg.freebook.config.Constants.WEB_SESSION_KEY_USER;

@Component
public class UserHandler {
    
    @Autowired
    private UserRepository userRepository;

    public Mono<ServerResponse> login(ServerRequest request){
        return request.session().map( session -> session.getAttribute(WEB_SESSION_KEY_USER) != null )
            .flatMap( logon -> {
                if(logon){
                    return ServerResponse.status(HttpStatus.FORBIDDEN).bodyValue(
                        ErrorModel.of("HAS_LOGON", "你已登录，请勿重复登录")
                    );
                }
                return request.bodyToMono(User.class).flatMap( user -> {
                    if(user == null || user.getPhoneNumber() == null || user.getPassword() == null){
                        return ServerResponse.badRequest().bodyValue(ErrorModel.of("PARAMS_CANT_NULL", "登录信息不能有空"));
                    }
                    return userRepository.findByPhoneNumber(user.getPhoneNumber())
                        .flatMap( user2 -> {
                            if (user2 == null || !Objects.equals(user.getPassword(), user2.getPassword())){
                                return ServerResponse.badRequest().bodyValue(ErrorModel.of("ERROR_LOGIN_INFO", "登录信息不正确"));
                            }
                            request.session().subscribe(session -> session.getAttributes().put(WEB_SESSION_KEY_USER, user2) );
                            return ServerResponse.ok().build();
                        });
                });
            });
    }

    public Mono<ServerResponse> logout(ServerRequest request){
        return request.session().flatMap( session -> {
            session.getAttributes().remove("user");
            return ServerResponse.ok().header("Clear-Site-Data", "cookies").build();
        });
    }

    public Mono<ServerResponse> currentUser(ServerRequest request){
    return request.session().flatMap( session -> {
        User user = session.getAttribute(WEB_SESSION_KEY_USER);
        if(user== null){
            return ServerResponse.status(HttpStatus.NOT_FOUND).bodyValue(
                Map.of("logged", Boolean.FALSE)
            );
        }
        return ServerResponse.ok().bodyValue(
            Map.of("logged", Boolean.TRUE, "username", user.getUsername())  
        );
    });
    }
}

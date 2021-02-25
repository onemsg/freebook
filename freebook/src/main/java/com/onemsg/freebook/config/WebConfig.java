package com.onemsg.freebook.config;

import static com.onemsg.freebook.config.Constants.WEB_SESSION_KEY_USER;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.onemsg.freebook.handler.BookHandler;
import com.onemsg.freebook.handler.CommentHandler;
import com.onemsg.freebook.handler.PhotoHandler;
import com.onemsg.freebook.handler.UserHandler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.web.reactive.config.EnableWebFlux;
import org.springframework.web.reactive.config.ResourceHandlerRegistry;
import org.springframework.web.reactive.config.WebFluxConfigurer;
import org.springframework.web.reactive.function.server.HandlerFilterFunction;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
@EnableWebFlux
public class WebConfig implements WebFluxConfigurer{
    
    @Autowired
    private BookHandler bookHandler;
    @Autowired
    private CommentHandler commentHandler;
    @Autowired
    private UserHandler userHandler;
    @Autowired
    private PhotoHandler photoHandler;


    @Autowired
    private ObjectMapper objectMapper;

    @Bean
    public RouterFunction<?> bookRouterFunction() {
        return RouterFunctions.route()
            .GET("/api/book", bookHandler::listBook)
            .GET("/api/book/{id}", bookHandler::getBook)
            .add(RouterFunctions.route()
                .filter(userFilterFunction())
                .POST("/api/book", bookHandler::createBook)
                .PUT("/api/book/{id}", bookHandler::updateBook)
                .DELETE("/api/book/{id}", bookHandler::deleteBook)
                .build()
            )
            .build();
    }

    @Bean
    public RouterFunction<?> commentRouterFunction() {
        return RouterFunctions.route()
            .GET("/api/comment", commentHandler::listComment)
            .GET("/api/comment/{id}", commentHandler::getComment)
            .add(RouterFunctions.route()
                .filter(userFilterFunction())
                .POST("/api/comment", commentHandler::createComment)
                .DELETE("/api/comment/{id}", commentHandler::deleteComment)
                .build()
            )
            .build();
    }


    @Bean
    public RouterFunction<?> photoRouterFunction() {
        return RouterFunctions.route()
            .GET("/public/img/{id}", photoHandler::get)
            .add(RouterFunctions.route()
                .filter(userFilterFunction())
                .POST("/api/photo/upload", photoHandler::upload)
                .DELETE("/api/photo/{id}", photoHandler::delete)
                .build()
            )
            .build();
    }

    @Bean
    public RouterFunction<?> userRouterFunction() {
        return RouterFunctions.route()
            .POST("/api/login", userHandler::login)
            .GET("/api/currentuser", userHandler::currentUser)
            .add(RouterFunctions.route()
                .filter(userFilterFunction())
                .DELETE("/api/logout", userHandler::logout)
                .build()
            )
            .build();
    }


    @Bean
    public HandlerFilterFunction<ServerResponse, ServerResponse> userFilterFunction(){
        return (request, next) -> {
            return request.session()
                .map( session -> session.getAttribute(WEB_SESSION_KEY_USER) != null )
                .flatMap( logon -> 
                    logon ? next.handle(request) : ServerResponse.status(HttpStatus.UNAUTHORIZED).build()
                );
        };
    }

    @Override
    public void configureHttpMessageCodecs(ServerCodecConfigurer configurer) {
        configurer.defaultCodecs().maxInMemorySize(512 * 1024);
        configurer.defaultCodecs().jackson2JsonDecoder( new Jackson2JsonDecoder(objectMapper));
        configurer.defaultCodecs().jackson2JsonEncoder(new Jackson2JsonEncoder(objectMapper));
    }


    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry){
        registry.addResourceHandler("/**").addResourceLocations("/static/");
    }
}

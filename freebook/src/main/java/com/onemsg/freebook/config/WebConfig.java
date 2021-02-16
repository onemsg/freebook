package com.onemsg.freebook.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.onemsg.freebook.handler.BookHandler;
import com.onemsg.freebook.handler.CommentHandler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.web.reactive.config.EnableWebFlux;
import org.springframework.web.reactive.config.WebFluxConfigurer;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;

@Configuration
@EnableWebFlux
public class WebConfig implements WebFluxConfigurer{
    
    @Autowired
    private BookHandler bookHandler;
    @Autowired
    private CommentHandler commentHandler;
    @Autowired
    private ObjectMapper objectMapper;

    @Bean
    public RouterFunction<?> routerFunction() {
        return RouterFunctions.route()
            .GET("/api/book", bookHandler::listBook)
            .GET("/api/book/{id}", bookHandler::getBook)
            .POST("/api/book", bookHandler::createBook)
            .PUT("/api/book/{id}", bookHandler::updateBook)
            .DELETE("/api/book/{id}", bookHandler::deleteBook)
            .GET("/api/comment", commentHandler::listComment)
            .GET("/api/comment/{id}", commentHandler::getComment)
            .POST("/api/comment", commentHandler::createComment)
            .DELETE("/api/comment/{id}", commentHandler::deleteComment)
            .build();
    }

    @Override
    public void configureHttpMessageCodecs(ServerCodecConfigurer configurer) {
        configurer.defaultCodecs().maxInMemorySize(512 * 1024);
        configurer.defaultCodecs().jackson2JsonDecoder( new Jackson2JsonDecoder(objectMapper));
        configurer.defaultCodecs().jackson2JsonEncoder(new Jackson2JsonEncoder(objectMapper));
    }

}

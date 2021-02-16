package com.onemsg.freebook.handler;

import static org.springframework.http.MediaType.APPLICATION_JSON;

import com.onemsg.freebook.model.Book;
import com.onemsg.freebook.model.ErrorModel;
import com.onemsg.freebook.repository.BookRepository;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import reactor.core.publisher.Mono;

@Component
public class BookHandler {

    @Autowired
    private BookRepository bookRepository;

    public Mono<ServerResponse> listBook(ServerRequest request){
        var title = request.queryParam("title");
        if(title.isEmpty()){
            return ServerResponse.ok().body(bookRepository.findAll(), Book.class );
        }
        return searchBookByTitle(title.get());
    }

    public Mono<ServerResponse> getBook(ServerRequest request){
        String id =  request.pathVariable("id");
        if(!ObjectId.isValid(id)){
            return ServerResponse.notFound().build();
        }

        return bookRepository.findById(id)
            .flatMap( book -> ServerResponse.ok().contentType(APPLICATION_JSON).bodyValue(book) )
            .switchIfEmpty(ServerResponse.notFound().build()); 
        
    }

    private Mono<ServerResponse> searchBookByTitle(String title){
        return ServerResponse
            .ok()
            .contentType(APPLICATION_JSON)
            .body(bookRepository.findByTitleContaining(title) , Book.class);
    }

    public Mono<ServerResponse> createBook(ServerRequest request){
        return request.bodyToMono(Book.class)
            .flatMap( book -> bookRepository.save(book) )
            .flatMap( created -> {
                if(created){
                    return ServerResponse.status(HttpStatus.CREATED).build();
                }
                return ServerResponse
                    .status(HttpStatus.BAD_REQUEST)
                    .contentType(APPLICATION_JSON)
                    .bodyValue(ErrorModel.of("CREATE_ERROR", "未知原因，创建失败"));
            });
    }

    public Mono<ServerResponse> updateBook(ServerRequest request){

        String id =  request.pathVariable("id");

        if(!ObjectId.isValid(id)){
            return ServerResponse.notFound().build();
        }

        return request.bodyToMono(Book.class)
            .flatMap( book -> bookRepository.update(id, book) )
            .flatMap( created -> {
                if(created){
                    return ServerResponse.status(HttpStatus.CREATED).build();
                }
                return ServerResponse
                    .status(HttpStatus.BAD_REQUEST)
                    .contentType(APPLICATION_JSON)
                    .bodyValue(ErrorModel.of("CREATE_ERROR", "未知原因，创建失败"));
            });
    }

    public Mono<ServerResponse> deleteBook(ServerRequest request){
        String id =  request.pathVariable("id");
        if(!ObjectId.isValid(id)){
            return ServerResponse.notFound().build();
        }
        return bookRepository.delete(id).flatMap( deleted -> {
            if(deleted){
                return ServerResponse.ok().build();
            }
            return ServerResponse
                .status(HttpStatus.BAD_REQUEST)
                .contentType(APPLICATION_JSON)
                .bodyValue(ErrorModel.of("DELETE_ERROR", "删除失败，可能 id 不存在"));
        });
    }
}

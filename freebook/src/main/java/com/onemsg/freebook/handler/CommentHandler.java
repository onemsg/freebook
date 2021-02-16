package com.onemsg.freebook.handler;

import static org.springframework.http.MediaType.APPLICATION_JSON;

import com.onemsg.freebook.model.Comment;
import com.onemsg.freebook.model.ErrorModel;
import com.onemsg.freebook.repository.CommentRepository;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import reactor.core.publisher.Mono;

@Component
public class CommentHandler {

    @Autowired
    private CommentRepository commentRepository;

    public Mono<ServerResponse> listComment(ServerRequest request) {
        var start = request.queryParam("start").map(CommentHandler::toInteger);
        var limit = request.queryParam("limit").map(CommentHandler::toInteger);
        return ServerResponse.ok().contentType(APPLICATION_JSON)
                .body(commentRepository.findAll(start.get(), limit.get()), Comment.class);
    }

    public Mono<ServerResponse> getComment(ServerRequest request) {
        String id = request.pathVariable("id");
        if (!ObjectId.isValid(id)) {
            return ServerResponse.notFound().build();
        }
        return ServerResponse.ok().contentType(APPLICATION_JSON)
                .body(commentRepository.findById(id), Comment.class);
    }

    public Mono<ServerResponse> createComment(ServerRequest request) {
        return request.bodyToMono(Comment.class).flatMap(comment -> commentRepository.save(comment))
                .flatMap(created -> {
                    if (created) {
                        return ServerResponse.status(HttpStatus.CREATED).build();
                    }
                    return ServerResponse.status(HttpStatus.BAD_REQUEST).contentType(APPLICATION_JSON)
                            .bodyValue(ErrorModel.of("CREATE_ERROR", "未知原因，创建失败"));
                });

    }

    public Mono<ServerResponse> deleteComment(ServerRequest request) {
        String id = request.pathVariable("id");
        if (!ObjectId.isValid(id)) {
            return ServerResponse.notFound().build();
        }
        return commentRepository.delete(id).flatMap(deleted -> {
            if (deleted) {
                return ServerResponse.ok().build();
            }
            return ServerResponse.status(HttpStatus.BAD_REQUEST).contentType(APPLICATION_JSON)
                    .bodyValue(ErrorModel.of("DELETE_ERROR", "删除失败，可能 id 不存在"));
        });
    }

    private static Integer toInteger(String s) {
        if (s == null)
            return null;
        Integer number = null;
        try {
            number = Integer.valueOf(s);
        } catch (NumberFormatException e) {
            return null;
        }
        return number;
    }

}

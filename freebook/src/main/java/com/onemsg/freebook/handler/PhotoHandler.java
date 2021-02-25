package com.onemsg.freebook.handler;

import java.net.URI;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import com.onemsg.freebook.model.ErrorModel;
import com.onemsg.freebook.model.Photo;
import com.onemsg.freebook.repository.PhotoRepository;
import com.onemsg.freebook.util.SessionUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import reactor.core.publisher.Mono;

@Component
public class PhotoHandler {

    @Autowired
    private PhotoRepository photoRepository;

    // 只处理 `photos` 的第一个文件
    public Mono<ServerResponse> upload(ServerRequest serverRequest){

        return serverRequest.multipartData()
            .map(map -> map.get("photo").get(0))
            .flatMap(part -> DataBufferUtils.join(part.content()))
            .map(dataBuffer -> {
                byte[] bytes = new byte[dataBuffer.readableByteCount()];
                dataBuffer.read(bytes);
                return bytes;
            })
            .flatMap(bytes -> SessionUtil.thenUser(serverRequest)
                    .flatMap(user -> {
                        Photo photo = new Photo();
                        photo.setBytes(bytes);
                        photo.setCreatedBy(user.getPhoneNumber());
                        return photoRepository.save(photo);
                    })
            )
            .map(PhotoHandler::toURL)
            .flatMap(url -> ServerResponse
                .created(URI.create(url))
                .bodyValue(Map.of("url", url))
            );
    }


    public Mono<ServerResponse> get(ServerRequest serverRequest){
        String id =  serverRequest.pathVariable("id");
        return photoRepository
            .findBytes(id)
            .flatMap( img -> img == null ? null : 
                ServerResponse.ok()
                    .cacheControl(CACHE_CONTROL)
                    .contentType(MediaType.IMAGE_JPEG)
                    .bodyValue(img) 
            ).switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> delete(ServerRequest serverRequest){
        String id =  serverRequest.pathVariable("id");
        return photoRepository
            .delete(id)
            .flatMap( deleted -> {
                if(deleted){
                   return ServerResponse.ok().build();    
                }
                return ServerResponse.badRequest().bodyValue(
                    ErrorModel.of("BAD_PARAMS", "要删除的目标资源不存在")
                );
            });

    }

    private static final String PATH_IMG = "/public/img/";
    private static final CacheControl CACHE_CONTROL = CacheControl.maxAge(7L, TimeUnit.DAYS).cachePublic();

    private static String toURL(String id){
        Objects.requireNonNull(id, "要生成图片路径的图片id不能为null");
        return PATH_IMG + id;
    }
    
}

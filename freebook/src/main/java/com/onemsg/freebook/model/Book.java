package com.onemsg.freebook.model;

import java.time.LocalDateTime;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.types.ObjectId;

import lombok.Data;

@Data
public class Book {
    
    @BsonId
    @JsonSerialize(using = ToStringSerializer.class)
    private ObjectId id;
    private String title;
    private String introduction;
    private Double price;
    private Double originalPrice;
    private String about;
    private Seller seller;

    private String status;

    private DoubanInfo doubanInfo;
    private JDInfo jdInfo;

    private LocalDateTime createdOn;
    private LocalDateTime createdBy;
    private LocalDateTime updatedOn;


    @Data
    public static class Seller {
        
        private String name;
        private String contact;
        private String about;
    }

    @Data
    public static class DoubanInfo{

        private Double score;
        private String link;
        private String shortComment;
    }

    @Data
    public static class JDInfo{

        private Double price;
        private String link;
    }
}

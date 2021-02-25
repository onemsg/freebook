package com.onemsg.freebook.model;

import java.time.LocalDateTime;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.types.ObjectId;

import lombok.Data;

@Data
public class Photo {
    
    @BsonId
    @JsonSerialize(using = ToStringSerializer.class)
    private ObjectId id;
    private String name;
    private byte[] bytes;
    private LocalDateTime createdOn;
    private String createdBy;
}

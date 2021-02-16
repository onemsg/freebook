package com.onemsg.freebook.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.types.ObjectId;

import lombok.Data;

@Data
public class User {

    @BsonId
    @JsonSerialize(using = ToStringSerializer.class)
    private ObjectId id;
    private final String username;
    private final String password;
    private final String phoneNumber;
}

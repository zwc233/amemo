package com.example.amemo.models;

import java.util.HashSet;
import java.util.Set;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document("group")
public class Group {
    @Id
    public String id;

    @Field("name")
    public String name;

    @Field("description")
    public String description;

    @Field("owner")
    public String owner;

    @Field("admins")
    public Set<String> admins;

    @Field("members")
    public Set<String> members;

    public Group(String name, String description) {
        this.name         =  name;
        this.description  =  description;
        this.admins       =  new HashSet<>();
        this.members      =  new HashSet<>();
    }
}

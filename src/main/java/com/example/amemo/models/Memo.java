package com.example.amemo.models;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document("memo")
public class Memo {
    @Id
    public String id;

    @Field("group")
    public String group;

    @Field("creator")
    public String creator;

    @Field("title")
    public String title;

    @Field("content")
    public String content;

    @Field("when")
    public Date when;

    @Field("cycle")
    public long cycle;

    public Memo(String title, String content, Date when, long cycle) {
        this.title    =  title;
        this.content  =  content;
        this.when     =  when;
        this.cycle    =  cycle;
    }
}

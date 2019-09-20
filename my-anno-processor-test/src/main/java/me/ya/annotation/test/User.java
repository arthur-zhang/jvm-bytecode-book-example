package me.ya.annotation.test;

import me.ya.annotation.MyBuilder;

/**
 * Created By Arthur Zhang at 2019/9/14
 */
public class User {
    @MyBuilder
    private Integer id;
    @MyBuilder
    private String name;

    public void setId(Integer id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }
}

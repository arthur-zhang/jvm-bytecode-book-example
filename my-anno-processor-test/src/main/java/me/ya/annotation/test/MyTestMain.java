package me.ya.annotation.test;

/**
 * Created By Arthur Zhang at 2019/9/14
 */
public class MyTestMain {
    private String hello;

    public static void main(String[] args) {
        User user = new me.ya.annotation.test.UserBuilder().id(1).name("hello").build();
    }
}

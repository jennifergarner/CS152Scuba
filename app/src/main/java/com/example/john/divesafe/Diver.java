package com.example.john.divesafe;

/**
 * Created by Jennifer on 4/14/2015.
 */
public class Diver {

    private int id;
    private String name;

    public long getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
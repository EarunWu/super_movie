package com.example.super_movie.entity;


import java.io.Serializable;

/**
 * <p>
 * 
 * </p>
 *
 * @author earun
 * @since 2020-01-12
 */

public class Test implements Serializable {

    private static final long serialVersionUID = 1L;

    private int id;

    private String magicId;

    private String firstName;

    private String lastName;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getMagicId() {
        return magicId;
    }

    public void setMagicId(String magicId) {
        this.magicId = magicId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
}

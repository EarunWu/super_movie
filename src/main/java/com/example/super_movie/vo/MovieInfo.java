package com.example.super_movie.vo;

import com.example.super_movie.entity.Movie;
import com.example.super_movie.entity.Person;

import java.time.LocalDate;
import java.util.List;

public class MovieInfo extends Movie {
    private List<Person> director;
    private List<Person> screenwriter;
    private List<Person> actor;
    private List<String> kind;
    private List<String> language;
    private Double score;

    public MovieInfo(int id, String name, LocalDate time, String country, Integer length, String info,List<Person> director, List<Person> screenwriter, List<Person> actor, List<String> kind, List<String> language,Double score) {
        super(id, name, time, country, length, info);
        this.director = director;
        this.screenwriter = screenwriter;
        this.actor = actor;
        this.kind = kind;
        this.language = language;
        this.score=score;
    }
    public MovieInfo(){
    };

    public List<Person> getDirector() {
        return director;
    }

    public void setDirector(List<Person> director) {
        this.director = director;
    }

    public List<Person> getScreenwriter() {
        return screenwriter;
    }

    public void setScreenwriter(List<Person> screenwriter) {
        this.screenwriter = screenwriter;
    }

    public List<Person> getActor() {
        return actor;
    }

    public void setActor(List<Person> actor) {
        this.actor = actor;
    }

    public List<String> getKind() {
        return kind;
    }

    public void setKind(List<String> kind) {
        this.kind = kind;
    }

    public List<String> getLanguage() {
        return language;
    }

    public void setLanguage(List<String> language) {
        this.language = language;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }
}

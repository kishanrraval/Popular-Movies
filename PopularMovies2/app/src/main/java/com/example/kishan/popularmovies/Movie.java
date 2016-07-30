package com.example.kishan.popularmovies;

public class Movie {

    private String name;
    private String poster;

    public Movie(String name, String poster)
    {
        this.name = name;
        this.poster = poster;
    }

    public String getName() {
        return name;
    }

    public String getPoster() {
        return poster;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }

}

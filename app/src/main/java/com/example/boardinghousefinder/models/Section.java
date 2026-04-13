package com.example.boardinghousefinder.models;

import java.util.List;

public class Section {
    private String title;
    private List<BoardingHouse> boardingList;

    public Section(String title, List<BoardingHouse> boardingList) {
        this.title = title;
        this.boardingList = boardingList;
    }

    public String getTitle() {
        return title;
    }

    public List<BoardingHouse> getBoardingList() {
        return boardingList;
    }
}

package com.trickybhai.todolist.datamodel;

import java.time.LocalDate;

public class Todoitems {
    private String shortDesciption;
    private String details;
    private LocalDate deadline;

    public Todoitems(String shortDesciption, String details, LocalDate deadline) {
        this.shortDesciption = shortDesciption;
        this.details = details;
        this.deadline = deadline;
    }

    public String getShortDesciption() {
        return shortDesciption;
    }

    public void setShortDesciption(String shortDesciption) {
        this.shortDesciption = shortDesciption;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public LocalDate getDeadline() {
        return deadline;
    }

    public void setDeadline(LocalDate deadline) {
        this.deadline = deadline;
    }

    @Override
    public String toString() {
        return shortDesciption;
    }

}

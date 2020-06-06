package com.trickybhai.todolist.datamodel;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class TodoData {
    private static final TodoData instance = new TodoData();
    private static final String filename = "TotolistItems.txt";

    private ObservableList<Todoitems> todoitems;
    private final DateTimeFormatter formatter;

    public void addTodoItem(Todoitems todoitems){
        this.todoitems.add(todoitems);
    }


    public static TodoData getInstance(){
        return instance;
    }

    private TodoData(){
        formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    }

    public ObservableList<Todoitems> getTodoitems() {
        return todoitems;
    }


    public void loadTodoItems() throws IOException{
        todoitems = FXCollections.observableArrayList();
        Path path = Paths.get(filename);
        BufferedReader br = Files.newBufferedReader(path);

        String input;

        try{
            while ((input = br.readLine()) != null){
                String[] itemPieces = input.split("\t");
                String shortDescription = itemPieces[0];
                String details = itemPieces[1];
                String dateString = itemPieces[2];

                LocalDate date = LocalDate.parse(dateString, formatter);

                Todoitems todoitems = new Todoitems(shortDescription, details, date);
                this.todoitems.add(todoitems);
            }
        } finally {
            if (br!=null){
                br.close();
            }
        }
    }

    public void storeTodoItems() throws IOException{
        Path path = Paths.get(filename);
        BufferedWriter bw = Files.newBufferedWriter(path);

        try{
            for (Todoitems item : todoitems) {
                bw.write(String.format("%s\t%s\t%s", item.getShortDesciption(), item.getDetails()
                        , item.getDeadline().format(formatter)));
                bw.newLine();
            }
        }finally {
            if (bw!=null){
                bw.close();
            }
        }
    }
}

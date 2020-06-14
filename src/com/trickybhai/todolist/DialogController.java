package com.trickybhai.todolist;

import com.trickybhai.todolist.datamodel.TodoData;
import com.trickybhai.todolist.datamodel.Todoitems;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.time.LocalDate;

public class DialogController {

    @FXML
    private TextField shortDescriptionField;

    @FXML
    private TextArea detailsArea;

    @FXML
    private DatePicker deadlinePicker;

    public static boolean editing;
    public static Todoitems itemToBeEdited;

    public void initialize(){
        if (editing){
            shortDescriptionField.setText(itemToBeEdited.getShortDesciption());
            detailsArea.setText(itemToBeEdited.getDetails());
            deadlinePicker.setValue(itemToBeEdited.getDeadline());
        }
    }

    @FXML
    public Todoitems processResults(){
        String shortDescription = shortDescriptionField.getText().trim();
        String details = detailsArea.getText().trim();
        LocalDate deadLine = deadlinePicker.getValue();
        Todoitems newItem = new Todoitems(shortDescription, details, deadLine);
        if (!editing){
            TodoData.getInstance().addTodoItem(newItem);
        }
        return newItem;
    }

    public Todoitems editingItem(){
        itemToBeEdited.setShortDesciption(shortDescriptionField.getText());
        itemToBeEdited.setDetails(detailsArea.getText());
        itemToBeEdited.setDeadline(deadlinePicker.getValue());
        return itemToBeEdited;
    }


  /*  @FXML
    public Todoitems editingItems(Todoitems editingItem){
        shortDescriptionField.setText(editingItem.getShortDesciption());
        detailsArea.setText(editingItem.getDetails());
        deadlinePicker.setValue(editingItem.getDeadline());

        return new Todoitems(shortDescriptionField.getText(), detailsArea.getText(), deadlinePicker.getValue());
    }*/
}

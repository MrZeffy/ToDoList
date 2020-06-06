package com.trickybhai.todolist;

import com.trickybhai.todolist.datamodel.TodoData;
import com.trickybhai.todolist.datamodel.Todoitems;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.Background;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.util.Callback;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;


public class Controller {

    @FXML
    private ListView<Todoitems> todoListView;

    @FXML
    private TextArea itemDetailTextArea;
    @FXML
    private Label dateLabel;

    @FXML
    private BorderPane mainBorderPane;


    public void initialize() {

        todoListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Todoitems>() {
            @Override
            public void changed(ObservableValue<? extends Todoitems> observableValue, Todoitems todoitems, Todoitems t1) {
                if (t1!=null){
                    Todoitems item = todoListView.getSelectionModel().getSelectedItem();
                    itemDetailTextArea.setText(item.getDetails());
                    DateTimeFormatter df = DateTimeFormatter.ofPattern("MMMM d, yyyy");
                    dateLabel.setText(df.format(item.getDeadline()));
                }
            }
        });


       // todoListView.getItems().setAll(TodoData.getInstance().getTodoitems());
        todoListView.setItems(TodoData.getInstance().getTodoitems());
        todoListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        todoListView.getSelectionModel().selectFirst();

        todoListView.setCellFactory(new Callback<ListView<Todoitems>, ListCell<Todoitems>>() {
            @Override
            public ListCell<Todoitems> call(ListView<Todoitems> todoitemsListView) {
                ListCell<Todoitems> cell = new ListCell<>(){
                    @Override
                    protected void updateItem(Todoitems todoitems, boolean b) {
                        super.updateItem(todoitems, b);
                        if (b){
                            setText(null);
                        }else{
                            setText(todoitems.getShortDesciption());
                            if (todoitems.getDeadline().equals(LocalDate.now())){
                                setTextFill(Color.RED);
                            }
                        }
                    }
                };
                return cell;
            }
        });
    }

    @FXML
    public void showNewItemDialog(){
        Dialog<ButtonType> dialog = new Dialog<>();
        //Make dialog box modul.
        dialog.initOwner(mainBorderPane.getScene().getWindow());
        dialog.setTitle("Add new Todo item");
        dialog.setHeaderText("Use this dialog to create a new Todo item");
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("todoItemDialog.fxml"));
        try{
            dialog.getDialogPane().setContent(fxmlLoader.load());

        }catch (IOException e){
            System.out.println("Couldn't load dialog");
            e.printStackTrace();
            return;
        }
        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);
        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get()==ButtonType.OK){
            DialogController controller = fxmlLoader.getController();
            Todoitems newItem = controller.processResults();
            //todoListView.getItems().setAll(TodoData.getInstance().getTodoitems());
            todoListView.getSelectionModel().select(newItem);
        }
    }
}

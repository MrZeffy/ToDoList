package com.trickybhai.todolist;

import com.trickybhai.todolist.datamodel.TodoData;
import com.trickybhai.todolist.datamodel.Todoitems;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.util.Callback;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.Optional;
import java.util.function.Predicate;


public class Controller {

    @FXML
    private ListView<Todoitems> todoListView;

    @FXML
    private TextArea itemDetailTextArea;
    @FXML
    private Label dateLabel;

    @FXML
    private BorderPane mainBorderPane;

    @FXML
    private ContextMenu listContextMenu;

    @FXML
    private ToggleButton filterToggleButton;

    private FilteredList<Todoitems> filteredList;

    private Predicate<Todoitems> wantAllItems;
    private Predicate<Todoitems> wantTodayItems;


    public void initialize() {
        //Creating context menu.
        listContextMenu = new ContextMenu();

        //Creating menu item "delete"
        MenuItem deleteMenuItem = new MenuItem("Delete");

        //Anonymous event handler.
        deleteMenuItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                //getting item to be deleted and calling the deletitem function.
                Todoitems todoitems = todoListView.getSelectionModel().getSelectedItem();
                deleteItem(todoitems);
            }
        });

        wantAllItems = new Predicate<Todoitems>() {
            @Override
            public boolean test(Todoitems todoitems) {
                return true;
            }
        };
        wantTodayItems = new Predicate<Todoitems>() {
            @Override
            public boolean test(Todoitems todoitems) {
                return todoitems.getDeadline().equals(LocalDate.now());
            }
        };


        //Adding menu item to context item.
        listContextMenu.getItems().addAll(deleteMenuItem);
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

        filteredList = new FilteredList<>(TodoData.getInstance().getTodoitems(), wantAllItems);
        //Sorting list using a comparator.
        SortedList<Todoitems> sortedList = new SortedList<>(filteredList, new Comparator<Todoitems>() {
            @Override
            public int compare(Todoitems o1, Todoitems o2) {
                return o1.getDeadline().compareTo(o2.getDeadline());
            }
        });
       // todoListView.getItems().setAll(TodoData.getInstance().getTodoitems());
        todoListView.setItems(TodoData.getInstance().getTodoitems());
        todoListView.setItems(sortedList);
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
                            if (todoitems.getDeadline().isBefore(LocalDate.now().plusDays(1))){
                                setTextFill(Color.RED);
                            }else if (todoitems.getDeadline().equals(LocalDate.now().plusDays(1))){
                                setTextFill(Color.BLUEVIOLET);
                            }
                        }
                    }
                };

                //Lambda expression.
                cell.emptyProperty().addListener(
                        (obs, wasEmpty, isNowEmpty) -> {
                            if (isNowEmpty){
                                cell.setContextMenu(null);
                            }else {
                                cell.setContextMenu(listContextMenu);
                            }
                }
                );
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

    @FXML
    public void showEditItemDialogBox(){
        Todoitems item = todoListView.getSelectionModel().getSelectedItem();
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("todoItemDialog.fxml"));
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(mainBorderPane.getScene().getWindow());

        dialog.setTitle("Edit item");
        dialog.setHeaderText("Make your changes and then click OK");
        DialogController.editing = true;
        DialogController.itemToBeEdited = item;
        try{
            dialog.getDialogPane().setContent(fxmlLoader.load());
        }catch (IOException e){
            System.out.println("Couldn't load dialog box.");
            e.printStackTrace();
        }

        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);

        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get().equals(ButtonType.OK)){
            DialogController controller = fxmlLoader.getController();
            Todoitems editedItem = controller.editingItem();
            todoListView.getSelectionModel().select(editedItem);
        }

    }

    @FXML
    public void handleKeyPressed(KeyEvent keyEvent){
        Todoitems selected = todoListView.getSelectionModel().getSelectedItem();
        if (selected!=null){
            if (keyEvent.getCode().equals(KeyCode.DELETE)){
                deleteItem(selected);
            }
        }
    }

    public void deleteItem(Todoitems item){
        //Confirmation dialog.
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Todo item");
        alert.setHeaderText("Delete item: "+ item.getShortDesciption());
        alert.setContentText("Are you sure? Press OK to confirm");
        //make visible
        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent() && result.get().equals(ButtonType.OK)){
            TodoData.getInstance().deleteTodoItem(item);
        }

    }

    public void handleFilterButton(){
        Todoitems selectedItem = todoListView.getSelectionModel().getSelectedItem();
        if (filterToggleButton.isSelected()){
            filteredList.setPredicate(wantTodayItems);
            if (filteredList.isEmpty()){
                itemDetailTextArea.clear();
                dateLabel.setText("");
            }else if (filteredList.contains(selectedItem)){
                todoListView.getSelectionModel().select(selectedItem);
            }else {
                todoListView.getSelectionModel().selectFirst();
            }
        }else {
            boolean wasEmpty = filteredList.isEmpty();
            filteredList.setPredicate(wantAllItems);
            if (wasEmpty){
                todoListView.getSelectionModel().selectFirst();
            }else{
                todoListView.getSelectionModel().select(selectedItem);
            }
        }
    }

    @FXML
    public void handleExit(){
        Platform.exit();
    }

}













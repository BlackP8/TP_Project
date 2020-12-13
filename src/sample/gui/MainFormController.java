package sample.gui;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import sample.models.*;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class MainFormController implements Initializable {

    public TableView mainTable;

    public ChoiceBox cmdObjType;

    //Создаем экзмепляр модели
    SpaceObjectModel model = new SpaceObjectModel();

    ObservableList<Class<? extends SpaceObject>> objTypes = FXCollections.observableArrayList(
            SpaceObject.class,
            Planet.class,
            Star.class,
            Comet.class
    );

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        // добавляем реакцию на изменения данных
        // тут мы используем наши любимое лямбда-выражение,
        // за счет которого мы передаем в поле модели функцию
        // эту функцию вызовут из модели, причем модель не будет знать что именно функция делает
        model.addDataChangedListener(spaceObjects -> {
            mainTable.setItems(FXCollections.observableArrayList(spaceObjects));
        });
//        model.load();

        TableColumn<SpaceObject, String> distanceColumn = new TableColumn<>("Расстояние до земли");
        distanceColumn.setCellValueFactory(new PropertyValueFactory<>("distance"));

        TableColumn<SpaceObject, String> descriptionColumn = new TableColumn<>("Описание");
        descriptionColumn.setCellValueFactory(cellData -> {
            return new SimpleStringProperty(cellData.getValue().getDescription());
        });

        mainTable.getColumns().addAll(distanceColumn, descriptionColumn);


        //Привязываем список доступных объектов к комбобоксу
        cmdObjType.setItems(objTypes);
        // выбрали первый элемент в списке
        cmdObjType.getSelectionModel().select(0);

        // переопределил метод преобразования имени класса в текст
        cmdObjType.setConverter(new StringConverter<Class>() {
            @Override
            public String toString(Class object) {
                // просто перебираем тут все возможные варианты
                if (SpaceObject.class.equals(object)) {
                    return "Все";
                } else if (Planet.class.equals(object)) {
                    return "Планета";
                } else if (Star.class.equals(object)) {
                    return "Звезда";
                } else if (Comet.class.equals(object)) {
                    return "Комета";
                }
                return null;
            }

            @Override
            public Class fromString(String string) {
                return null;
            }
        });

        //Добавляем реакцию на переключение типа
        cmdObjType.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            this.model.setSpaceObjectFilter((Class<? extends SpaceObject>) newValue);
        });
    }

    //Открытие формы добавления объекта
    public void addClick(ActionEvent actionEvent) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("NewObjectForm.fxml"));
        Parent root = loader.load();
        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(this.mainTable.getScene().getWindow());

        // сначала берем контроллер
        NewObjectForm controller = loader.getController();
        // передаем модель
        controller.spaceObjectModel = model;
        // показываем форму
        stage.showAndWait();
    }

    //Редактирвоание данных
    public void onEditClick(ActionEvent actionEvent) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("NewObjectForm.fxml"));
        Parent root = loader.load();

        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(this.mainTable.getScene().getWindow());

        // передаем выбранную еду
        NewObjectForm controller = loader.getController();
        controller.setObject((SpaceObject) this.mainTable.getSelectionModel().getSelectedItem());
        // передаем модель
        controller.spaceObjectModel = model;

        stage.showAndWait();

    }

    //Метод удаления выбранных данных
    public void onDeleteClick(ActionEvent actionEvent) {
        // берем выбранную на форме еду
        SpaceObject spaceObject = (SpaceObject) this.mainTable.getSelectionModel().getSelectedItem();

        // выдаем подтверждающее сообщение
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Подтверждение");
        alert.setHeaderText(String.format("Точно удалить %s?", spaceObject.getDistance()));

        // если пользователь нажал OK
        Optional<ButtonType> option = alert.showAndWait();
        if (option.get() == ButtonType.OK) {
            model.delete(spaceObject.id); // тут вызываем метод модели, и передаем идентификатор
        }
    }



    public void onDownloadFromFileClick(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Загрузить данные");
        fileChooser.setInitialDirectory(new File("."));

        // а тут диалог для открытия файла
        File file = fileChooser.showOpenDialog(mainTable.getScene().getWindow());

        if (file != null) {
            model.loadFromFile(file.getPath());
        }
    }

    public void onSaveToFileClick(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Сохранить данные");
        fileChooser.setInitialDirectory(new File("."));


        // тут вызываем диалог для сохранения файла
        File file = fileChooser.showSaveDialog(mainTable.getScene().getWindow());

        if (file != null) {
            model.saveToFile(file.getPath());
        }
    }
}

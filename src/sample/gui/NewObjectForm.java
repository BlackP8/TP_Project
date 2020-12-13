package sample.gui;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import sample.models.*;

import java.net.URL;
import java.util.ResourceBundle;

public class NewObjectForm implements Initializable {

    //Добавляем идентификатор
    private Integer id = null;

    //Тип объекта
    public ChoiceBox objectType;

    //Расстояние до Земли
    public TextField distanceTF;

    //Параметры планеты
    public VBox planetPanel;
    public TextField radiusTF;
    public CheckBox atmoCheckBox;
    public TextField gravityTF;

    //Параметры звезды
    public VBox starPanel;
    public ChoiceBox colorChoiceBox;
    public TextField densityTF;
    public TextField tempTF;

    //Параметры кометы
    public VBox cometPanel;
    public TextField nameTF;
    public TextField periodTF;

    public Button btnSave;
    public Button btnClose;

    //Константы
    final String SPACE_PLANET = "Планета";
    final String SPACE_STAR = "Звезда";
    final String SPACE_COMET = "Комета";

    //Поле модели
    public SpaceObjectModel spaceObjectModel;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //Заполняем выпадающий список значениями
        objectType.setItems(FXCollections.observableArrayList(
                SPACE_PLANET,
                SPACE_STAR,
                SPACE_COMET
        ));

        //Настраиваем видимость контейнером в зависимости от типа объекта
        objectType.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            this.planetPanel.setVisible(newValue.equals(SPACE_PLANET));
            this.starPanel.setVisible(newValue.equals(SPACE_STAR));
            this.cometPanel.setVisible(newValue.equals(SPACE_COMET));
        });

        //Убираем лишние оступы при скрытии полей
        objectType.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            updatePanes((String) newValue);
        });

        //Добавляем выбор увета звезды
        colorChoiceBox.getItems().setAll(
                Star.Color.Red,
                Star.Color.Blue,
                Star.Color.Brown,
                Star.Color.White,
                Star.Color.Orange
        );

        // и используем метод setConverter,
        // чтобы типы объекты рендерились как строки
        colorChoiceBox.setConverter(new StringConverter<Star.Color>() {
            @Override
            public String toString(Star.Color object) {
                // просто указываем как рендерить
                switch (object) {
                    case Red:
                        return "Красный карлик";
                    case Blue:
                        return "Голубой супергигант";
                    case Brown:
                        return "Коричневый карлик";
                    case White:
                        return "Белый карлик";
                    case Orange:
                        return "Оранжевый карлик";
                }
                return null;
            }

            @Override
            public Star.Color fromString(String string) {
                return null;
            }
        });

        updatePanes("");
    }

    //Метод изменения расстояний между контейнерами
    public void updatePanes(String value) {
        this.planetPanel.setVisible(value.equals(SPACE_PLANET));
        this.planetPanel.setManaged(value.equals(SPACE_PLANET)); // добавили
        this.starPanel.setVisible(value.equals(SPACE_STAR));
        this.starPanel.setManaged(value.equals(SPACE_STAR)); // добавили
        this.cometPanel.setVisible(value.equals(SPACE_COMET));
        this.cometPanel.setManaged(value.equals(SPACE_COMET)); // добавили
    }

    //Обработчик кнопки сохранить
    public void onSaveClick(ActionEvent actionEvent) {
        // проверяем передали ли идентификатор
        if (this.id != null) {
            // если передавали значит у нас редактирование
            // собираем еду с формы
            SpaceObject object = getObject();
            // подвязываем переданный идентификатор к собранной с формы еды
            object.id = this.id;
            // отправляем в модель на изменение
            this.spaceObjectModel.edit(object);
        } else {
            this.spaceObjectModel.add(getObject());
        }
        //Закрываем окно
        ((Stage)((Node)actionEvent.getSource()).getScene().getWindow()).close();
    }

    //Обработчик кнопки закрыть
    public void onCancelClick(ActionEvent actionEvent) {
        //Закрываем окно
        ((Stage)((Node)actionEvent.getSource()).getScene().getWindow()).close();
    }

    //Метод, устанавливающий значения выбранной ячейки в окно редактирования
    public void setObject(SpaceObject object) {
        //Запрещаем переключать тип
        this.objectType.setDisable(object != null);

        // присвоим значение идентификатора
        this.id = object != null ? object.id : null;

        if (object != null) {
            //Заполняем поля
            this.distanceTF.setText(String.valueOf(object.getDistance()));

            //Если выбрали планету
            if (object instanceof Planet) {
                this.objectType.setValue(SPACE_PLANET);
                this.atmoCheckBox.setSelected(((Planet) object).atmosphere);
                this.gravityTF.setText(Double.toString((double) ((Planet) object).gravity_force));
                this.radiusTF.setText(Integer.toString((int) ((Planet) object).radius));
                //Если выбрали звезду
            } else if (object instanceof Star) {
                this.objectType.setValue(SPACE_STAR);
                this.densityTF.setText(Double.toString((double) ((Star) object).density));
                this.tempTF.setText(Integer.toString((int) ((Star) object).temp));
                this.colorChoiceBox.setValue(((Star) object).color);
                //Если выбрали комету
            } else if (object instanceof Comet) {
                this.objectType.setValue(SPACE_COMET);
                this.periodTF.setText(Integer.toString((int) ((Comet) object).period));
                this.nameTF.setText(((Comet) object).name);
            }
        }
    }

    //Метод извлекающий значения из окна добавления
    public SpaceObject getObject() {
        SpaceObject result = null;
        try {
            double distance = Double.parseDouble(this.distanceTF.getText());

            switch ((String)this.objectType.getValue()) {
                case SPACE_PLANET:
                    Integer radius = Integer.valueOf(this.radiusTF.getText());
                    double gravity_force = Double.parseDouble(this.gravityTF.getText());
                    result = new Planet(distance, radius, this.atmoCheckBox.isSelected(), gravity_force);
                    break;
                case SPACE_STAR:
                    double density = Double.parseDouble(this.densityTF.getText());
                    Integer temperature = Integer.valueOf(this.tempTF.getText());
                    result = new Star(distance, density, temperature, (Star.Color) this.colorChoiceBox.getValue());
                    break;
                case SPACE_COMET:
                    Integer period = Integer.valueOf(this.periodTF.getText());
                    result = new Comet(distance, period, this.nameTF.getText());
                    break;
            }
        }
        catch (NumberFormatException e) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Уведомление");
            alert.setHeaderText("Ошибка ввода:");
            alert.setContentText("Некорректное значение в поле!");

            alert.showAndWait();
        }

        return result;
    }
}

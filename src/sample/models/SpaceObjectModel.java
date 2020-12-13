package sample.models;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.util.ArrayList;
import java.util.stream.Collectors;

//Класс, осуществляющий работу с данными в соответствии с принципом МVP
public class SpaceObjectModel {
    //Добавляем счетчик для идентификатора
    private int counter = 1;

    //Поле-фильтр типа объекта в модели
    Class<? extends SpaceObject> spaceObjectFilter = SpaceObject.class;

    ArrayList<SpaceObject> spaceObjectsList = new ArrayList<>();

    //Создаем функциональный интерфейс с помощью него организуем общение между моделью и контроллером
    public interface DataChangedListener {
        void dataChanged(ArrayList<SpaceObject> spaceObjects);
    }

    private ArrayList<DataChangedListener> dataChangedListeners = new ArrayList<>();

    public void addDataChangedListener(DataChangedListener listener) {
        this.dataChangedListeners.add(listener);
    }

    //Метод, загружающий данные
    public void load() {
        spaceObjectsList.clear();

        this.add(new Planet(320000, 49000,true, 10.5), false);
        this.add(new Star(1000000,0.8 , 6000, Star.Color.Orange), false);
        this.add(new Comet(40000, 180, "Faust"), false);

        this.emitDataChanged();
    }

    //Метод добавления объекта
    public void add(SpaceObject object, boolean emit) {
        object.id = counter; // присваиваем id еды, значение счетчика
        counter += 1;

        this.spaceObjectsList.add(object);

        if (emit) {
            this.emitDataChanged();
        }
    }

    // а это получается перегруженный метод, но с одним параметром
    // который вызывает add с двумя параметрами,
    // передавая в качестве второго параметра true
    // то есть вызывая add с одним параметром будет происходит оповещение
    public void add(SpaceObject object) {
        add(object, true);
    }

    //Добавляем метод редактирования данных, который находит объект
    //по идентификатору, заменяет его новым и оповещает слушателей
    public void edit(SpaceObject object) {
        // ищем объект в списке
        for (int i = 0; i< this.spaceObjectsList.size(); ++i) {
            // чтобы id совпадал с id переданным форме
            if (this.spaceObjectsList.get(i).id == object.id) {
                // если нашли, то подменяем объект
                this.spaceObjectsList.set(i, object);
                break;
            }
        }

        this.emitDataChanged();
    }

    //Добавляем метод для удаления
    public void delete(int id)
    {
        for (int i = 0; i< this.spaceObjectsList.size(); ++i) {
            // ищем в цикле еду с данным айдишником
            if (this.spaceObjectsList.get(i).id == id) {
                // если нашли то удаляем
                this.spaceObjectsList.remove(i);
                break;
            }
        }

        // оповещаем об изменениях
        this.emitDataChanged();
    }

    //Метод сохранения данных в файл
    public void saveToFile(String path) {
        // открываем файл для чтения
        try (Writer writer =  new FileWriter(path)) {
            // создаем сериализатор
            ObjectMapper mapper = new ObjectMapper();
            // записываем данные списка foodList в файл
            mapper.writerFor(new TypeReference<ArrayList<SpaceObject>>() { }) // указали какой тип подсовываем
                    .withDefaultPrettyPrinter() // кстати эта строчка чтобы в файлике все красиво печаталось
                    .writeValue(writer, spaceObjectsList); // а это непосредственно запись
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Создаем метод для чтения из файла
    public void loadFromFile(String path) {
        try (Reader reader =  new FileReader(path)) {
            // создаем сериализатор
            ObjectMapper mapper = new ObjectMapper();

            // читаем из файла
            spaceObjectsList = mapper.readerFor(new TypeReference<ArrayList<SpaceObject>>() { })
                    .readValue(reader);

            // рассчитываем счетчик как максимальное значение id плюс 1
            this.counter = spaceObjectsList.stream()
                    .map(food -> food.id)
                    .max(Integer::compareTo)
                    .orElse(0) + 1;

        } catch (IOException e) {
            e.printStackTrace();
        }

        // оповещаем что данные загрузились
        this.emitDataChanged();
    }

    //Метод смены фильтра
    public void setSpaceObjectFilter(Class<? extends SpaceObject> spaceObjectFilter) {
        this.spaceObjectFilter = spaceObjectFilter;

        this.emitDataChanged();
    }

    //Функция оповещения слушателей
    private void emitDataChanged() {
        for (DataChangedListener listener : dataChangedListeners) {
            ArrayList<SpaceObject> filteredList = new ArrayList<>(
                    spaceObjectsList.stream() // запускаем стрим
                            .filter(food -> spaceObjectFilter.isInstance(food)) // фильтруем по типу
                            .collect(Collectors.toList()) // превращаем в список
            );
            listener.dataChanged(filteredList); // подсовывам сюда список
        }
    }
}

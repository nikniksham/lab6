package my_programm;

import my_programm.enums.Climate;
import my_programm.enums.StandardOfLiving;
import my_programm.obj.City;
import my_programm.obj.Coordinates;
import my_programm.obj.Human;

import java.io.IOException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;

public class Manager {
    private Hashtable<Integer, City> table = new Hashtable<>();
    private Integer id = 1;
    private Date dateIni;
    private Integer loc_id = null;
    private boolean change_something = false;
    private boolean isFirst = true;
    private List<String> arr = new ArrayList<>();

    public Manager() {
        dateIni = Calendar.getInstance().getTime();
    }

    public void setFile(String filename, boolean f_isFirst) throws IOException {
        List<String> stroki = CustomFileReader.readFile(filename);
        createCitiesFromJson(stroki);
        if (stroki == null) {return;}
        if (isFirst && f_isFirst) {
            isFirst = false;
        } else {
            change_something = true;
            arr.add("Таблица загружена");
        }
    }

    public List<String> commandHandler(String input) throws IOException {
        arr.clear();
        if (input.equals("help")) {
            this.help();
        } else if (input.equals("info")) {
            this.info();
        } else if (input.equals("show")) {
            this.show();
        } else if (input.equals("clear")) {
            this.clear();
        } else if (input.contains("insert ")) {
            this.insert_id(input.split("\s")[1], input.substring(input.indexOf("{")));
        } else if (input.contains("remove_key ")) {
            this.remove_key(input.split("\s")[1]);
        } else if (input.equals("exit")) {
            System.exit(0);
        } else if (input.equals("print_unique_climate")) {
            this.print_unique_climate();
        } else if (input.contains("update ")) {
            this.update_id(input.split("\s")[1], input.substring(input.indexOf("{")));
        } else if (input.contains("remove_lower ")) {
            this.remove_lower(input.split("\s")[1]);
        } else if (input.contains("replace_if_lower ")) {
            this.replace_if_lower(input.split("\s")[1], input.substring(input.indexOf("{")));
        } else if (input.contains("remove_greater_key ")) {
            this.remove_greater_key(input.split("\s")[1]);
        } else if (input.equals("sum_of_meters_above_sea_level")) {
            this.sum_of_meters_above_sea_level();
        } else if (input.equals("print_field_descending_governor")) {
            this.print_field_descending_governor();
        } else if (input.contains("save ")) {
            this.save(input.split("\s")[1]);
        } else if (input.contains("execute_script ")) {
            return this.get_list_of_commands(input.split("\s")[1]);
        } else if (input.contains("load_new_table ")) {
            this.clear();
            this.setFile(input.split("\s")[1], false);
        } else {
            arr.add("Я не знаю команды " + input + ", для справки по командам напишите help");
        }
        return arr;
    }

    public void help() {
        arr.add("help : вывести справку по доступным командам\n" +
                "info : вывести в стандартный поток вывода информацию о коллекции (тип, дата инициализации, количество элементов и т.д.)\n" +
                "show : вывести в стандартный поток вывода все элементы коллекции в строковом представлении\n" +
                "load_new_table {filename} : вывести в стандартный поток вывода все элементы коллекции в строковом представлении\n" +
                "insert {id} {element} : создаст новый элемент с заданными параметрами\n" +
                "update {id} {element} : откроет меню создания нового элемента, для замены старого по id\n" +
                "remove_key {id} : удалить элемент из коллекции по его ключу\n" +
                "clear : очистить коллекцию\n" +
                "save {filename} : сохранить коллекцию в файл\n" +
                "execute_script {filename} : считать и исполнить скрипт из указанного файла. В скрипте содержатся команды в таком же виде, в котором их вводит пользователь в интерактивном режиме.\n" +
                "exit : завершить программу (без сохранения в файл)\n" +
                "remove_lower {element}: удалить из коллекции все элементы, меньшие чем заданный\n" +
                "replace_if_lower {id} {element} : заменить значение по ключу, если новое созданное значение меньше старого (по выбранному параметру)\n" +
                "remove_greater_key {id} : удалить из коллекции все элементы, ключ которых превышает заданный\n" +
                "sum_of_meters_above_sea_level : вывести сумму значений поля metersAboveSeaLevel для всех элементов коллекции\n" +
                "print_unique_climate : вывести уникальные значения поля climate всех элементов в коллекции\n" +
                "print_field_descending_governor : вывести значения поля governor всех элементов в порядке убывания\n" +
                "*Под {filename} подразумевается название файла\n" +
                "*Под {id} подразумевается id города в таблице\n" +
                "*Под {element} подразумевается {<String>name, [<Integer>x, <Integer>y] <Long>area, <Long>population, <Integer>MASL, <Integer>carCode, <Integer>[null/1-5], <Integer>[null/1-5], [null/<Integer>year, <Integer>month, <Integer>day, <String>name_gov]}");
    }

    public void info() {
        arr.add("Таблица: ключ - Integer, хранимые данные - City\nДата инициализации: " + dateIni.toString() + "\nКоличество элементов: " + table.size());
    }

    public void show() throws IOException {
        this.save("sendData.json");
        if (table.size() == 0) {
            arr.add("Таблица пуста");
        }
        arr.add("отправить json");
//        ArrayList<City> arr_val = new ArrayList<>();
//        ArrayList<Integer> arr_key = new ArrayList<>();
//        for (Map.Entry<Integer,City> entry : table.entrySet()) {
//            arr_val.add(entry.getValue());
//            arr_key.add(entry.getKey());
//        }
//
//        for (int i = arr_val.size() - 1; i > -1; --i) {
//            arr.add(arr_key.get(i) + " " + arr_val.get(i));
//            arr.add("-------------------------------------");
//        }
    }

    public void clear() {
        table.clear();
        arr.add("Таблица очищена");
        this.id = 1;
        change_something = true;
    }

    public void insert_id(String sid, String element) {
        int id = Pomogtor.StringToInt(sid);
        if (id < 0) {
            throw new RuntimeException("id должен быть больше 0");
        } else if (table.containsKey(id)) {
            throw new RuntimeException("Этот id уже занят");
        } else {
            loc_id = id;
            table.put(loc_id++, this.create_city_by_string(element));
            this.id = loc_id;
            loc_id = null;
            arr.add("Новый город добавлен " + id);
            change_something = true;
        }
    }

    public void print_unique_climate() {
        if (table.size() == 0) {
            arr.add("Таблица пуста");
        }

        ArrayList<Climate> arr = new ArrayList<>();
        for (Map.Entry<Integer,City> entry : table.entrySet()) {
            Climate climate = entry.getValue().getClimate();
            if (climate != null && !arr.contains(climate)) {
                arr.add(climate);
                arr.add(climate);
            }
        }
    }

    public void remove_key(String sid) {
        int id = Pomogtor.StringToInt(sid);
        if (table.containsKey(id)) {
            table.remove(id);
            arr.add("Город удалён");
            change_something = true;
        } else {
            arr.add("Не найден город с таким id");
        }
    }

    public void update_id(String sid, String string) {
        int id = Pomogtor.StringToInt(sid);
        if (!table.containsKey(id)) {
            throw new RuntimeException("По этому id ничего не найдено");
        } else {
            loc_id = id;
            table.replace(id, this.create_city_by_string(string));
            loc_id = null;
            arr.add("Новое значение задано");
            change_something = true;
        }
    }

    public void remove_lower(String sid) {
        int id = Pomogtor.StringToInt(sid);
        if (!table.containsKey(id)) {
            throw new RuntimeException("По этому id ничего не найдено");
        } else {
            long num = table.get(id).get_num_for_srav();
            ArrayList<City> arr_val = new ArrayList<>();
            ArrayList<Integer> arr_key = new ArrayList<>();
            for (Map.Entry<Integer,City> entry : table.entrySet()) {
                arr_val.add(entry.getValue());
                arr_key.add(entry.getKey());
            }

            for (int i = 0; i < arr_key.size(); ++i) {
                if (arr_val.get(i).get_num_for_srav() < num) {
                    table.remove(arr_key.get(i));
                }
            }
            arr.add("Все лишние города удалены");
            change_something = true;
        }
    }

    public void replace_if_lower(String sid, String string) {
        int id = Pomogtor.StringToInt(sid);
        if (!table.containsKey(id)) {
            throw new RuntimeException("По этому id ничего не найдено");
        } else {
            City old_city = table.get(id);
            loc_id = id;
            City new_city = create_city_by_string(string);
            loc_id = null;
            if (old_city.get_num_for_srav() > new_city.get_num_for_srav()) {
                table.replace(id, new_city);
                arr.add("Город заменён");
                change_something = true;
                return;
            }
            arr.add("Город не заменён");
        }
    }

    public void remove_greater_key(String sid) {
        int id = Pomogtor.StringToInt(sid);
        ArrayList<Integer> arr_key = new ArrayList<>();
        for (Map.Entry<Integer,City> entry : table.entrySet()) {
            arr_key.add(entry.getKey());
        }
        for (Integer integer : arr_key) {
            if (integer > id) {
                table.remove(integer);
            }
        }
        arr.add("Всё слишком большое удалено");
        change_something = true;
    }

    public void sum_of_meters_above_sea_level() {
        int sum = 0;
        for (Map.Entry<Integer,City> entry : table.entrySet()) {
            sum += entry.getValue().getMetersAboveSeaLevel();
        }
        arr.add(sum + "");
    }

    public void print_field_descending_governor() {
        if (table.size() == 0) {
            arr.add("Таблица пуста");
        }

        for (Map.Entry<Integer,City> entry : table.entrySet()) {
            arr.add(entry.getValue().getGovernor().toString());
        }
    }

    public List<String> get_list_of_commands(String filename) throws IOException {
        return CustomFileReader.readFile(filename);
    }

    private City create_city_by_string(String string) {
//         {name [x y] area population MASL carCode [null/1-5] [null/1-5] [null/year month day name_gov]}
        boolean climate_is_set = false, level_is_set = false;
        String name = null, gov_name = "";
        Coordinates coordinates = null;
        Long area = null, population = null;
        Integer MASL = null, gov_year = null, gov_month = null, gov_day = null, cor_x = null, cor_y = null, carCode = null;
        Climate climate = null;
        StandardOfLiving standardOfLiving = null;
        Human gover = null;
        ArrayList<String> stt = new ArrayList<>();
        for (String s : string.strip().replace("{", "").replace("}", "").split(",\s")) {
            stt.add(Pomogtor.StringToString(s, new String[]{"[", "]"}));
        }
        String s;
        for (int i = 0; i < stt.size(); ++i) {
            s = stt.get(i);
            if (name == null) {
                name = s;
            } else if (cor_x == null) {
                cor_x = Pomogtor.StringToInteger(s);
            } else if (cor_y == null) {
                cor_y = Pomogtor.StringToInteger(s);
                coordinates = new Coordinates(cor_x, cor_y);
            } else if (area == null) {
                area = Pomogtor.StringToLong(s);
            } else if (population == null) {
                population = Pomogtor.StringToLong(s);
            } else if (MASL == null) {
                MASL = Pomogtor.StringToInteger(s);
            } else if (carCode == null) {
                carCode = Pomogtor.StringToInteger(s);
            } else if (!climate_is_set) {
                if (!s.equals("null")) {
                    climate = Climate.getById(Pomogtor.StringToInt(s));
                }
                climate_is_set = true;
            } else if (!level_is_set) {
                if (!s.equals("null")) {
                    standardOfLiving = StandardOfLiving.getById(Pomogtor.StringToInt(s));
                }
                level_is_set = true;
            } else {
                if (!s.equals("null")) {
                    if (gov_year == null) {
                        gov_year = Pomogtor.StringToInteger(s);
                    } else if (gov_month == null) {
                        gov_month = Pomogtor.StringToInteger(s);
                    } else if (gov_day == null) {
                        gov_day = Pomogtor.StringToInteger(s);
                    } else {
                        if (i < stt.size() - 1) {
                            gov_name += s + " ";
                        } else {
                            gov_name += s;
                            ZonedDateTime dateTime = ZonedDateTime.of(gov_year, gov_month, gov_day, 0, 0, 0, 0, ZoneId.of("Europe/Moscow"));
                            gover = new Human(dateTime, gov_name);
                        }
                    }
                } else {
                    gover = null;
                    break;
                }
            }
        }
        City city;
        if (loc_id != null) {
            city = new City(loc_id, name, coordinates, area, population, MASL, carCode, climate, standardOfLiving, gover);
        } else {
            city = new City(id, name, coordinates, area, population, MASL, carCode, climate, standardOfLiving, gover);
        }
        id++;
//        System.out.println(city);
        return city;
    }

    public void save(String filename) throws IOException {
        ArrayList<String> strings = new ArrayList<>();
        strings.add("{");
        strings.add("  \"city\": [");
        ArrayList<City> cities = new ArrayList<>();
        for (Map.Entry<Integer,City> entry : table.entrySet()) {
            cities.add(entry.getValue());
        }
        for (int i = cities.size() - 1; i > -1; --i) {
            City city = cities.get(i);
            strings.add("    {");
            strings.add("      \"name\": " + '"' + city.getName().replace("\"", "'").replace("\\", "/") + "\",");
            strings.add("      \"coordinates\": [");
            strings.add("        "+city.getCoordinates().getX()+",");
            strings.add("        "+city.getCoordinates().getY());
            strings.add("      ],");
            strings.add("      \"area\": " + city.getArea() + ",");
            strings.add("      \"population\": " + city.getPopulation() + ",");
            strings.add("      \"metersAboveSeaLevel\": " + city.getMetersAboveSeaLevel() + ",");
            strings.add("      \"carCode\": " + city.getCarCode() + ",");
            if (city.getClimate() != null) {
                strings.add("      \"Climate\": " + Climate.getIdByName(city.getClimate()) + ",");
            } else {
                strings.add("      \"Climate\": " + null + ",");
            }
            if (city.getStandardOfLiving() != null) {
                strings.add("      \"StandardOfLiving\": " + StandardOfLiving.getIdByName(city.getStandardOfLiving()) + ",");
            } else {
                strings.add("      \"StandardOfLiving\": " + null + ",");
            }
            if (city.getGovernor() != null) {
                strings.add("      \"Governor\": [");
                strings.add("        [");
                strings.add("          "+city.getGovernor().getBirthday().getYear()+",");
                strings.add("          "+city.getGovernor().getBirthday().getMonthValue()+",");
                strings.add("          "+city.getGovernor().getBirthday().getDayOfMonth());
                strings.add("        ],");
                strings.add("        \""+city.getGovernor().getName()+"\"");
                strings.add("      ]");
            } else {
                strings.add("      \"Governor\": " + null);
            }
            if (i == 0) {
                strings.add("    }");
            } else {
                strings.add("    },");
            }
        }
        strings.add("  ]");
        strings.add("}");

        CustomFileWriter.writeString(filename, strings);
        arr.add("Данные сохранены в файл " + filename);
        change_something = false;
    }

    private City createNewCity(Scanner scanner) {
        arr.add("Как город обзывается?");
        String name = scanner.nextLine();
        arr.add("На каких координатах тот находится? (2 числа через пробел в диапазоне [0, 136])");
        String coords = scanner.nextLine();
        int x_cor = Pomogtor.StringToInt(coords.split(" ")[0]), y_cor = Pomogtor.StringToInt(coords.split(" ")[1]);
        Coordinates coordinates = new Coordinates(x_cor, y_cor);
        arr.add("Какую площадь занимает город?");
        long area = Pomogtor.StringToLong(scanner.nextLine());
        arr.add("Сколько человеков живёт в городе?");
        Long population = Pomogtor.StringToLong(scanner.nextLine());
        arr.add("Какая высота над уровнем моря?");
        Integer MASL = Pomogtor.StringToInteger(scanner.nextLine());
        arr.add("Какой код региона для авто?");
        int carCode = Pomogtor.StringToInt(scanner.nextLine());
        arr.add("Какой климат в городе? (написать '-' чтобы пропустить пункт)");
        int id_c = 1;
        for (Climate c : Climate.values()) {arr.add(id_c++ + " --> " + c);}
        Climate climate = null;
        String res = scanner.nextLine();
        if (!res.equals("-")) {climate = Climate.getById(Pomogtor.StringToInt(res));}
        arr.add("Какой уровень жизни в городе? (написать '-' чтобы пропустить пункт)");
        id_c = 1;
        for (StandardOfLiving s : StandardOfLiving.values()) {arr.add(id_c++ + " --> " + s);}
        StandardOfLiving standardOfLiving = null;
        res = scanner.nextLine();
        if (!res.equals("-")) {standardOfLiving = StandardOfLiving.getById(Pomogtor.StringToInt(res));}
        arr.add("Лично вы знаете старосту этого города? ('-' пропусть)");
        res = scanner.nextLine();
        Human gover = null;
        if (!res.equals("-")) {
            arr.add("Как его зовут?");
            String gov_name = scanner.nextLine();
            arr.add("Год рождения?");
            int gov_year = Pomogtor.StringToInt(scanner.nextLine());
            arr.add("Месяц рождения?");
            int gov_month = Pomogtor.StringToInt(scanner.nextLine());
            arr.add("День рождения?");
            int gov_day = Pomogtor.StringToInt(scanner.nextLine());
            ZonedDateTime dateTime = ZonedDateTime.of(gov_year, gov_month, gov_day, 0, 0, 0, 0, ZoneId.of("Europe/Moscow"));
            gover = new Human(dateTime, gov_name);
        }
        if (loc_id == null) {
            return new City(id, name, coordinates, area, population, MASL, carCode, climate, standardOfLiving, gover);
        }
        return new City(loc_id, name, coordinates, area, population, MASL, carCode, climate, standardOfLiving, gover);
    }

    private void createCitiesFromJson(List<String> stroki) {
        if (stroki == null) {
            arr.add("Файл не найден");
            return;
        }
        boolean start_create_city = false, past_city = false, start_coordinates = false, start_governor = false, start_governor_birthdate = false, can_create_new = true;
        String name = null, gov_name = null;
        Coordinates coordinates = null;
        Long area = null, population = null;
        Integer MASL = null, gov_year = null, gov_month = null, gov_day = null, cor_x = null, cor_y = null, carCode = null;
        Climate climate = null;
        StandardOfLiving standardOfLiving = null;
        Human gover = null;
        for (String s : stroki) {
            if (!past_city) {if (s.contains("\"city\": [")) {past_city = true;}}
            else if (s.contains("{")) {
                start_create_city = true;
                can_create_new = true;
                start_coordinates = false; start_governor = false; start_governor_birthdate = false;
                name = null; gov_name = null;
                coordinates = null;
                area = null; population = null;
                MASL = null; gov_year = null; gov_month = null; gov_day = null; cor_x = null; cor_y = null; carCode = null;
                climate = null;
                standardOfLiving = null;
                gover = null;
            }
            else if (s.contains("}") && can_create_new) {
                start_create_city = false;
                can_create_new = false;
                if (area != null && MASL != null && carCode != null) {
                    City city = new City(id, name, coordinates, area, population, MASL, carCode, climate, standardOfLiving, gover);
                    this.table.put(id, city);
                    id++;
                }
            }
            if (start_create_city) {
                if (start_coordinates) {
                    if (cor_x == null) {
                        cor_x = Pomogtor.StringToInteger(s);
                    } else if (cor_y == null) {cor_y = Pomogtor.StringToInteger(s);
                    } else if (s.contains("]")) {
                        start_coordinates = false;
                        coordinates = new Coordinates(cor_x, cor_y);
                    }
                } else if (start_governor) {
                    if (start_governor_birthdate) {
                        if (gov_year == null) {gov_year = Pomogtor.StringToInteger(s);
                        } else if (gov_month == null) {gov_month = Pomogtor.StringToInteger(s);
                        } else if (gov_day == null) {gov_day = Pomogtor.StringToInteger(s);
                        } else if (s.contains("]")) {start_governor_birthdate = false;}
                    } else if (s.contains("]")) {
                        start_governor = false;
                        ZonedDateTime dateTime = ZonedDateTime.of(gov_year, gov_month, gov_day, 0, 0, 0, 0, ZoneId.of("Europe/Moscow"));
                        gover = new Human(dateTime, gov_name);
                    } else {gov_name = Pomogtor.StringToNormalString(s);}
                    if (s.contains("[")) {start_governor_birthdate = true;}
                } else if (s.contains("\"name\":")) {
                    name = Pomogtor.StringToNormalString(s.split("\":")[1]);
                } else if (s.contains("\"coordinates\":")) {
                    start_coordinates = true;
                } else if (s.contains("\"area\":")) {
                    area = Pomogtor.StringToLong(s.split("\":")[1]);
                } else if (s.contains("\"population\":")) {
                    population = Pomogtor.StringToLong(s.split("\":")[1]);
                } else if (s.contains("\"metersAboveSeaLevel\":")) {
                    MASL = Pomogtor.StringToInteger(s.split("\":")[1]);
                } else if (s.contains("\"carCode\":")) {
                    carCode = Pomogtor.StringToInteger(s.split("\":")[1]);
                } else if (s.contains("\"Climate\":")) {
                    if (!s.split("\":")[1].replace(",", "").strip().equals("null")) {
                        climate = Climate.getById(Pomogtor.StringToInt(s.split("\":")[1]));
                    }
                } else if (s.contains("\"StandardOfLiving\":")) {
                    if (!s.split("\":")[1].replace(",", "").strip().equals("null")) {
                        standardOfLiving = StandardOfLiving.getById(Pomogtor.StringToInt(s.split("\":")[1]));
                    }
                } else if (s.contains("\"Governor\":")) {
                    if (!s.split("\":")[1].replace(",", "").strip().equals("null")) {
                        start_governor = true;
                    }
                }
            }
//            System.out.println(s);
        }
    }

    public boolean isChange_something() {
        return change_something;
    }
}


class Pomogtor {
    public static Integer StringToInteger(String s) {
        return Integer.valueOf(StringToNormalString(s));
    }

    public static Long StringToLong(String s) {
        return Long.valueOf(StringToNormalString(s));
    }

    public static Integer StringToInt(String s) {
        return Integer.parseInt(StringToNormalString(s));
    }

    public static String StringToNormalString(String s) {
        return s.replace(",", "").replace("\"", "").strip();
    }

    public static String StringToString(String string, String[] extra) {
        string = string.strip();
        for (String s : extra) {
            string = string.replace(s, "");
        }
        return string;
    }
}
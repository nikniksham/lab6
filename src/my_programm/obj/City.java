package my_programm.obj;

import my_programm.enums.Climate;
import my_programm.enums.StandardOfLiving;
import java.util.Calendar;
import java.util.Date;

public class City {
    private Integer id; //Поле не может быть null, Значение поля должно быть больше 0, Значение этого поля должно быть уникальным, Значение этого поля должно генерироваться автоматически
    private String name; //Поле не может быть null, Строка не может быть пустой
    private Coordinates coordinates; //Поле не может быть null
    private Date creationDate; //Поле не может быть null, Значение этого поля должно генерироваться автоматически
    private long area; //Значение поля должно быть больше 0
    private Long population; //Значение поля должно быть больше 0, Поле не может быть null
    private Integer metersAboveSeaLevel;
    private int carCode; //Значение поля должно быть больше 0, Максимальное значение поля: 1000
    private Climate climate; //Поле может быть null
    private StandardOfLiving standardOfLiving; //Поле может быть null
    private Human governor; //Поле может быть null

    public City(Integer id_p, String name_p, Coordinates coordinates_p, long area_p, Long population_p, int metersAboveSeaLevel_p, int carCode_p, Climate climate_p, StandardOfLiving standardOfLiving_p, Human governor_p) {
        if (name_p == null) {throw new RuntimeException("Имя города не должно быть null");}
        if (area_p < 1) {throw new RuntimeException("Площадь города должна быть больше 0");}
        if (population_p == null) {throw new RuntimeException("Население города не должно быть null");}
        if (population_p < 1) {throw new RuntimeException("Население города должна быть больше 0");}
        if (carCode_p < 1 || carCode_p > 1000) {throw new RuntimeException("Код автомобиля должен быть в диапозоне [1; 1000]");}
        this.id = id_p;
        this.setName(name_p);
        this.setCoordinates(coordinates_p);
        this.setArea(area_p);
        this.setPopulation(population_p);
        this.setMetersAboveSeaLevel(metersAboveSeaLevel_p);
        this.setCarCode(carCode_p);
        this.setClimate(climate_p);
        this.setStandardOfLiving(standardOfLiving_p);
        this.setGovernor(governor_p);
        this.setCreationDate(Calendar.getInstance().getTime());
    }

    public long get_num_for_srav() {
        return (area + population + metersAboveSeaLevel + carCode) * (climate == null ? 1 : 2) * (standardOfLiving == null ? 1 : 2);
    }

    @Override
    public String toString() {
        return "Город: " + getName() +
               "\nid: " + id +
               "\nкоординаты: " + getCoordinates() +
               "\nдата добавления в таблицу: " + getCreationDate() +
               "\nплощадь: " + getArea() +
               "\nнаселение: " + getPopulation() +
               "\nвысота над уровнем моря: " + getMetersAboveSeaLevel() +
               "\nкод региона для авто: " + getCarCode() +
               "\nклимат: " + getClimate() +
               "\nуровень жизни: " + getStandardOfLiving() +
               "\nправитель: " + getGovernor();
    }

    public String getName() {
        return name;
    }

    private void setName(String name) {
        this.name = name;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    private void setCoordinates(Coordinates coordinates) {
        this.coordinates = coordinates;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    private void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public long getArea() {
        return area;
    }

    private void setArea(long area) {
        this.area = area;
    }

    public Long getPopulation() {
        return population;
    }

    private void setPopulation(Long population) {
        this.population = population;
    }

    public Integer getMetersAboveSeaLevel() {
        return metersAboveSeaLevel;
    }

    private void setMetersAboveSeaLevel(Integer metersAboveSeaLevel) {
        this.metersAboveSeaLevel = metersAboveSeaLevel;
    }

    public int getCarCode() {
        return carCode;
    }

    private void setCarCode(int carCode) {
        this.carCode = carCode;
    }

    public Climate getClimate() {
        return climate;
    }

    private void setClimate(Climate climate) {
        this.climate = climate;
    }

    public StandardOfLiving getStandardOfLiving() {
        return standardOfLiving;
    }

    private void setStandardOfLiving(StandardOfLiving standardOfLiving) {
        this.standardOfLiving = standardOfLiving;
    }

    public Human getGovernor() {
        return governor;
    }

    private void setGovernor(Human governor) {
        this.governor = governor;
    }
}

package com.university.shelter.model;

import com.university.shelter.exception.InvalidAnimalDataException;

import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

public abstract class Animal implements Comparable<Animal> {

    private final UUID id;
    private final String name;
    private final LocalDate birthDate;
    private final double weight;
    private final HealthStatus healthStatus;

    public Animal(UUID id, String name, LocalDate birthDate, double weight, HealthStatus healthStatus){

        // REVIEW[DEBT] (с ревью 1.2): сообщения не говорят, КАКОЕ поле невалидно
        // ("Поле должно быть заполнено." — какое из трёх?). Добавь имя поля, напр.
        // "healthStatus не задан", "id не задан", "birthDate не задана". В диагностике
        // на проде это сэкономит часы. И унифицируй пунктуацию (где-то с точкой, где-то без).
        // REVIEW[THINK]: birthDate в будущем (LocalDate.now().plusYears(1)) сейчас проходит
        // валидацию. Логично ли «животное родится завтра»? Если нет — добавь проверку.
        // REVIEW[GOOD]: equals через getClass()+Objects.equals(id), hashCode и Comparable
        // (натуральный по birthDate) сделаны корректно — это правильный канон.
        if(healthStatus == null){
            throw new InvalidAnimalDataException("Поле должно быть заполнено.");
        }

        if(id == null){
            throw new InvalidAnimalDataException("Поле должно быть заполнено.");
        }

        if(name == null || name.isBlank()){
            throw new InvalidAnimalDataException("Некорректное название.");
        }

        if(birthDate == null){
            throw new InvalidAnimalDataException("Поле должно быть заполнено.");
        }

        if(weight <= 0){
            throw new InvalidAnimalDataException("Вес не может быть меньше 0.");
        }

        this.name = name;
        this.weight = weight;
        this.birthDate = birthDate;
        this.healthStatus = healthStatus;
        this.id = id;
    }

    public String getName(){
        return name;
    }

    public UUID getId(){
        return id;
    }

    public HealthStatus getHealthStatus(){
        return healthStatus;
    }

    public double getWeight() {
        return weight;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }


    @Override
    public String toString(){
        return String.format("Животное зовут %s, его порядковый номер - %s, он весит %.2f кг, его дата рождения %s, состояние здоровья %s", name, id, weight, birthDate, healthStatus);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Animal animal = (Animal) o;
        return Objects.equals(id, animal.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public int compareTo(Animal other){
        return this.birthDate.compareTo(other.getBirthDate());
    }

    public abstract String makeSound();
}

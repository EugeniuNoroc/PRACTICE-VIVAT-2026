package com.university.shelter;

import com.university.shelter.model.Animal;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class Shelter {
    private final Map<UUID, Animal> animals;

    public Shelter(){
        this.animals = new HashMap<>();
    }

    public void accept(Animal animal){
        animals.put(animal.getId(), animal);
    }

    public Optional<Animal> findById(UUID id){
        return Optional.ofNullable(animals.get(id));
    }

    public List<Animal> findByType(Class<? extends Animal> type){
        List<Animal> result = new ArrayList<>();
        for(Animal animal : animals.values()){
            if(type.isInstance(animal)){
                result.add(animal);
            }
        }
        return result;
    }

    public void release(UUID id){
        animals.remove(id);
    }

    public List<Animal> findAll(){
        return new ArrayList<>(animals.values());
    }

    public List<Animal> findOlderThan(int years){
        List<Animal> result = animals.values()
                .stream()
                .filter(animal -> years < (LocalDate.now().getYear() - animal.getBirthDate().getYear()))
                .collect(Collectors.toList());
        return result;
    }

}

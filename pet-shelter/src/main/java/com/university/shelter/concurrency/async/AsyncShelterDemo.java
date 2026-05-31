package com.university.shelter.concurrency.async;

import com.university.shelter.ShelterService;
import com.university.shelter.dao.InMemoryAnimalDao;
import com.university.shelter.model.Animal;
import com.university.shelter.model.Cat;
import com.university.shelter.model.Dog;
import com.university.shelter.model.HealthStatus;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.*;

public class AsyncShelterDemo {
    public static void main(String[] args) throws ExecutionException, InterruptedException, TimeoutException {
        ExecutorService executor = Executors.newFixedThreadPool(4);
        ShelterService service = new ShelterService(new InMemoryAnimalDao());
        List<Animal> animals = List.of(
                new Cat(UUID.randomUUID(), "Кот1", LocalDate.of(2020,1,1), 4.0, HealthStatus.HEALTHY, "Persian", true),
                new Dog(UUID.randomUUID(), "Пёс1", LocalDate.of(2019,1,1), 10.0, HealthStatus.HEALTHY, "Beagle", 5),
                new Cat(UUID.randomUUID(), "Кот2", LocalDate.of(2021,1,1), 3.5, HealthStatus.SICK, "Beagle", false)
        );

        List<UUID> ids = service.acceptManyAsync(animals, executor).get(5, TimeUnit.SECONDS);

        System.out.println("Добавлено: " + ids.size());
        executor.shutdown();
    }
}

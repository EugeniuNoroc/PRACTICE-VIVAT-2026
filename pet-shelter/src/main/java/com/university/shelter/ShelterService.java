package com.university.shelter;

import com.university.shelter.dao.AnimalDao;
import com.university.shelter.model.Animal;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

/**
 * REVIEW[GOOD] (день 3): сервис принимает AnimalDao через конструктор (constructor
 * injection руками) и зависит от ИНТЕРФЕЙСА, а не от реализации. Это и есть Dependency
 * Inversion — в тестах подставляешь InMemoryAnimalDao, в проде AnimalDaoJdbc. В W2 ровно
 * это сделает Spring через @Service/@Autowired "бесплатно".
 *
 * REVIEW[THINK]: сейчас сервис — тонкий проброс в DAO (anemic, без своей логики).
 * Для этого этапа ОК: место под бизнес-правила/валидацию/транзакции готово, наполнится
 * в W2-W3. Просто держи в голове, что слой пока пустой — это не финал.
 */
public class ShelterService {
    private final AnimalDao dao;

    public ShelterService(AnimalDao dao) {
        this.dao = dao;
    }

    public void accept(Animal animal) {
        dao.save(animal);
    }

    public Optional<Animal> findById(UUID id) {
        return dao.findById(id);
    }

    public List<Animal> findAll() {
        return dao.findAll();
    }

    public List<Animal> findByType(Class<? extends Animal> type) {
        return dao.findByType(type);
    }

    public void release(UUID id) {
        dao.delete(id);
    }

    public Optional<Animal> findHeaviest() {
        return dao.findHeaviest();
    }

    public List<Animal> findOlderThan(int years) {
        return dao.findOlderThan(years);
    }

    public double averageAge() {
        return dao.averageAge();
    }

    public Map<Class<? extends Animal>, Long> countByType() {
        return dao.countByType();
    }

    /**
     * REVIEW (день 4) — разбор по пунктам:
     *
     * [GOOD] Идиоматичный паттерн: supplyAsync на элемент + allOf + join в маппинге.
     *        Хорошо, что executor передаётся (а не new каждый раз) и есть таймаут на стороне вызова.
     *
     * [IMPROVE] executor приходит ПАРАМЕТРОМ метода. Значит временем жизни пула управляет
     *        вызывающий код, у каждого клиента свой пул, легко забыть shutdown. Лучше сделать
     *        ExecutorService полем сервиса и инжектить через конструктор (привет, DI в W2).
     *
     * [BUG] Нет обработки ошибок внутри future. Если dao.save(animal) бросит (например,
     *        DuplicateAnimalException), то .join() кинет CompletionException и ВЕСЬ метод
     *        упадёт, потеряв уже сохранённые id. Добавь .exceptionally на каждый future:
     *           .exceptionally(ex -> { log.warn("не сохранил {}", animal.getId(), ex); return null; })
     *        и отфильтруй null в результате.
     *
     * [THINK] dao здесь — InMemoryAnimalDao с race condition в save (см. там). Многопоточный
     *        acceptManyAsync — ровно тот сценарий, где она стреляет. Свяжи два места:
     *        что будет, если в animals попадут два животных с ОДИНАКОВЫМ id?
     */
    public CompletableFuture<List<UUID>> acceptManyAsync(List<Animal> animals, ExecutorService executor) {
        List<CompletableFuture<UUID>> futures = animals.stream()
                .map(animal -> CompletableFuture.supplyAsync(() -> {
                    dao.save(animal);
                    return animal.getId();
                }, executor))
                .collect(Collectors.toList());

        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .thenApply(v -> futures.stream()
                        .map(CompletableFuture::join)
                        .collect(Collectors.toList()));
    }
}

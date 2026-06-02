package com.university.shelter;

import com.university.shelter.dao.AnimalDaoJdbc;
import com.university.shelter.exception.ShelterException;
import com.university.shelter.model.Animal;
import com.university.shelter.model.Cat;
import com.university.shelter.model.Dog;
import com.university.shelter.model.HealthStatus;

import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;

/**
 * REVIEW[DEBT] (с ревью 1.1/1.2, всё ещё не сделано): метод main разросся (>100 строк)
 * и делает всё сразу: рисует меню, парсит ввод, создаёт объекты, дёргает сервис.
 * Много дублирования между ветками "создать кошку" и "создать собаку".
 *
 * Выдели методы: Cat readCat(Scanner), Dog readDog(Scanner) (а лучше отдельный класс
 * ConsoleMenu / InputReader). В тикете 1.5 ввода прибавится (партии животных, опции
 * транзакций) — без рефакторинга станет совсем тяжело читать и менять.
 *
 * REVIEW[NIT]: переменная shelter имеет тип ShelterService — имя вводит в заблуждение,
 * назови service.
 */
public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        ShelterService shelter = new ShelterService(new AnimalDaoJdbc());
            while (true) {
                try {
                System.out.println("1. Принять животное");
                System.out.println("2. Список всех животных");
                System.out.println("3. Найти по ID");
                System.out.println("4. Отдать животное");
                System.out.println("5. Список животных по типу");
                System.out.println("0. Выход");

                int choice = scanner.nextInt();
                scanner.nextLine();

                if (choice == 1) {
                    System.out.println("Введите 1 если кошка, 2 если собака");
                    // REVIEW[NIT] (с ревью 1.1): переменная choice переиспользуется и для
                    // главного меню, и для этого подменю. Работает, но запутывает (после
                    // выхода из ветки choice уже не "пункт меню"). Заведи отдельную subChoice.
                    choice = scanner.nextInt();
                    scanner.nextLine(); // съедает перенос строки который крашит все
                    if (choice == 1) {
                        System.out.println("Введите имя кота/кошки:");
                        String name = scanner.nextLine();
                        System.out.println("Введите вес кота/кошки:");
                        double weight = scanner.nextDouble();
                        scanner.nextLine();
                        System.out.println("Введите состояние здоровья кота/кошки(SICK, HEALTHY or RECOVERING:");
                        HealthStatus healthStatus = HealthStatus.valueOf(scanner.next());
                        System.out.println("Введите дату рождения кота/кошки(xxxx-xx-xx):");
                        LocalDate date = LocalDate.parse(scanner.next());
                        System.out.println("Введите домашняя/ий ли кот/кошка (true или false):");
                        Boolean indoorOnly = Boolean.parseBoolean(scanner.next());
                        System.out.println("Введите породу кота/кошки:");
                        String breed = scanner.nextLine();
                        Cat cat = new Cat(UUID.randomUUID(), name, date, weight, healthStatus, breed, indoorOnly);

                        shelter.accept(cat);
                    } else if (choice == 2) {
                        System.out.println("Введите имя собаки:");
                        String name = scanner.nextLine();
                        System.out.println("Введите вес собаки:");
                        double weight = scanner.nextDouble();
                        scanner.nextLine();
                        System.out.println("Введите состояние здоровья собаки(SICK, HEALTHY or RECOVERING:");
                        HealthStatus healthStatus = HealthStatus.valueOf(scanner.next());
                        System.out.println("Введите дату рождения собаки(xxxx-xx-xx):");
                        LocalDate date = LocalDate.parse(scanner.next());
                        System.out.println("Введите уровень послушания собаки:");
                        int obedienceLevel = scanner.nextInt();
                        scanner.nextLine();
                        System.out.println("Введите породу собаки:");
                        String breed = scanner.nextLine();
                        Dog dog = new Dog(UUID.randomUUID(), name, date, weight, healthStatus, breed, obedienceLevel);

                        shelter.accept(dog);
                    }
                } else if (choice == 2) {
                    List<Animal> all = shelter.findAll();
                    for (Animal a : all) {
                        System.out.println(a);
                    }
                } else if (choice == 3) {
                    System.out.println("Введите айди животного:");
                    String input = scanner.next();
                    UUID uuid = UUID.fromString(input);
                    shelter.findById(uuid).ifPresentOrElse(
                            a -> System.out.println(a),
                            () -> System.out.println("Животное не найдено")
                    );
                } else if (choice == 4) {
                    System.out.println("Введите айди животного:");
                    String input = scanner.next();
                    UUID uuid = UUID.fromString(input);
                    shelter.release(uuid);
                } else if (choice == 5) {
                    System.out.println("Введите 1 если поиск по котам, 2 если по собакам:");
                    choice = scanner.nextInt();
                    scanner.nextLine();
                    if (choice == 1) {
                        List<Animal> all = shelter.findByType(Cat.class);
                        for (Animal a : all) {
                            System.out.println(a);
                        }
                    } else if (choice == 2) {
                        List<Animal> all = shelter.findByType(Dog.class);
                        for (Animal a : all) {
                            System.out.println(a);
                        }
                    }
                } else if (choice == 0) {
                    break;
                }
            // REVIEW[GOOD]: разделение catch ShelterException (ожидаемая бизнес-ошибка)
            // и catch Exception (неожиданная) + очистка буфера scanner.nextLine() после
            // кривого ввода — грамотно. Это спасает от вечного цикла на scanner.nextInt("abc").
            } catch (ShelterException exception) {
                    System.out.println("Ошибка " + exception.getMessage());
                } catch (Exception exception) {
                    System.out.println("Неожиданная ошибка " + exception.getMessage());
                    scanner.nextLine(); // святая пилюля от вечного цикла, очистка буфера после невалидного ввода
                }
        }
    }
}

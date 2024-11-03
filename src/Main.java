// Варіант 4

// Створіть Callable, який приймає масив чисел і повертає масив їх
// квадратів. Використовуйте ExecutorService для виконання завдання
// асинхронно та отримання результату через Future.
// Діапазон [0,5; 99,5] – дробові значення. Використати
// CopyOnWriteArraySet.

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) {
        List<Double> listOfNumbers = randomArraySet(0.5, 99.5, 40, 60);

        System.out.println("Початковий CopyOnWriteArraySet: " + listOfNumbers);

        // Використовуємо ExecutorService з трьома потоками
        ExecutorService executorService = Executors.newFixedThreadPool(3);

        // Розділяємо масив на три частини
        int partitionSize = listOfNumbers.size() / 3;
        List<Double> part1 = listOfNumbers.subList(0, partitionSize);
        List<Double> part2 = listOfNumbers.subList(partitionSize, partitionSize * 2);
        List<Double> part3 = listOfNumbers.subList(partitionSize * 2, listOfNumbers.size());

        // Створюємо списки для частин CopyOnWriteArraySet
        CopyOnWriteArraySet<Double> setPart1 = new CopyOnWriteArraySet<>(part1);
        CopyOnWriteArraySet<Double> setPart2 = new CopyOnWriteArraySet<>(part2);
        CopyOnWriteArraySet<Double> setPart3 = new CopyOnWriteArraySet<>(part3);

        long startTime = System.currentTimeMillis();

        // Виконуємо завдання для кожної частини
        List<Future<CopyOnWriteArraySet<Double>>> futures = new ArrayList<>();
        futures.add(executorService.submit(new SquareCalculator(setPart1)));
        futures.add(executorService.submit(new SquareCalculator(setPart2)));
        futures.add(executorService.submit(new SquareCalculator(setPart3)));

        // Обробка результатів
        for (int i = 0; i < futures.size(); i++) {
            Future<CopyOnWriteArraySet<Double>> future = futures.get(i);
            try {
                // Очікуємо завершення виконання
                CopyOnWriteArraySet<Double> result = future.get();

                // Перевіряємо статус виконання
                if (future.isDone()) {
                    System.out.println("Частина " + (i + 1) + " оброблена: " + result);
                }
                if (future.isCancelled()) {
                    System.out.println("Частина " + (i + 1) + " була відмінена.");
                }

            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }

        // Закриваємо ExecutorService
        executorService.shutdown();

        // Час виконання програми
        long endTime = System.currentTimeMillis();
        System.out.println("Час виконання програми: " + (endTime - startTime) + " мс");
    }

    private static List<Double> randomArraySet(double rangeMin, double rangeMax, int minNumbers, int maxNumbers) {
        List<Double> arraySet = new ArrayList<>();

        Random random  = new Random();
        int numOfNumbers = minNumbers + random.nextInt(maxNumbers - minNumbers + 1);

        while (arraySet.size() < numOfNumbers) {    // цикл для унікальних випадкових чисел
            double randomNumber = rangeMin + (Math.random() * (rangeMax - rangeMin + 1)); // генеруємо число від 0.5 до 99.5
            randomNumber = Math.round(randomNumber * 100.0) / 100.0;    // округлення числа до 2 знаків після коми
                                                // можна прибрати, або змінити, якщо потрібна інша кількість знаків

            arraySet.add(randomNumber);
        }

        return arraySet;
    }

    // Callable для обчислення квадратів чисел
    static class SquareCalculator implements Callable<CopyOnWriteArraySet<Double>> {
        private final CopyOnWriteArraySet<Double> numbers;

        public SquareCalculator(CopyOnWriteArraySet<Double> numbers) {
            this.numbers = numbers;
        }

        @Override
        public CopyOnWriteArraySet<Double> call() {
            return numbers.stream()
                    .map(number -> Math.round(number * number * 100.0) / 100.0) // Обчислюємо квадрат і округлюємо до 2 знаків
                    .collect(Collectors.toCollection(CopyOnWriteArraySet::new));
        }
    }
}

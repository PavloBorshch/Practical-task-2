import java.util.concurrent.Callable;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.stream.Collectors;

// Callable для обчислення квадратів чисел
public class SquareCalculator implements Callable<CopyOnWriteArraySet<Double>> {
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

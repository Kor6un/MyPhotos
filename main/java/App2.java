import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.*;

public class App2 {

    public static void main(String[] args) {

        Set<String> threads_names = new HashSet<>();
        long sum = 0;

        //"Исполнитель" задач, имеет один метод, который принимает задачу
        //типа Runnable
        Executor executor = null;

        //Расширенная версия Executor'а, которая снабжена дополнительными методами
        //ExecutorService service = null;

        /* SingleThreadExecutor - выполняет все в одном потоке
         */
        //ExecutorService service = Executors.newSingleThreadExecutor();

        //int procs = Runtime.getRuntime().availableProcessors(); //количество доступных процессоров
        //System.out.println(procs);

        /* FixedThreadPool - создает пул из фиксированного количества потоков
         */
        //ExecutorService service = Executors.newFixedThreadPool(procs);

        /* CachedThreadPool - берет потоки из очереди, если они там есть,
            иначе создает новые, а при завершении задачи кладет их в очередь
        */
        //ExecutorService service = Executors.newCachedThreadPool();

        /* WorkStealingPool - может перекинуть поток с одной задачи на другую
        если увидит, что задача стала простаивать (ждать чего-то)
        */
        ExecutorService service = Executors.newWorkStealingPool();

        //ExecutorService service = Executors.newScheduledThreadPool(10);

        final int TASKS_COUNT = 100000;


        long time1 = System.nanoTime();

        List<Future<Long>> futures = new ArrayList<>();

        for (int i = 0; i < TASKS_COUNT; i++) {
            futures.add(service.submit(new SumFunc(threads_names, i * 100000000, (i+1)*100000000)));
        }

        for (Future<Long> f : futures) {
            try {
                sum += f.get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }

        //thread1.join() // текущий поток (в данном случае "main") ждет поток thread1


        long time2 = System.nanoTime();

        System.out.println("Время = " + (time2 - time1) / 1e9 + " с");
        System.out.println("Количество потоков = " + threads_names.size());
        System.out.println("Сумма = " + sum);

        service.shutdown();
    }

}


//Некоторая задача, которая возвращает значение
class SumFunc implements Callable<Long> {

    private int start;
    private int end;
    private Set<String> thread_names;

    public SumFunc(Set<String> thread_names, int start, int end) {
        this.start = start;
        this.end = end;
        this.thread_names = thread_names;
    }


    @Override
    public Long call() {
        thread_names.add(Thread.currentThread().getName());
        long sum_ = 0;
        for (int i = start; i < end; i++) {
            sum_ += i;
        }
        return sum_;
    }
}
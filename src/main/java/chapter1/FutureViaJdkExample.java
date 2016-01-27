package chapter1;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * 演示JDK中的Future
 */
public class FutureViaJdkExample {
    public static void main(String[] args) {
        ExecutorService executor = Executors.newCachedThreadPool();
        Runnable task1 = new Runnable() {
            @Override
            public void run() {
                // do some heavy work
                try {
                    Thread.sleep(1000 * 1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        Callable<Integer> task2 = new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                // do some heavy work with result
                try {
                    Thread.sleep(1000 * 2);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }                
                return 0;
            }
        };
        
        Future<?> future1 = executor.submit(task1);
        Future<Integer> future2 = executor.submit(task2);
        while(!future1.isDone() || !future2.isDone()) {
            System.out.println("do something else...");
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } 
        }
        executor.shutdown();
    }
}

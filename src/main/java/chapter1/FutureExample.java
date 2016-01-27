package chapter1;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * 演示Future
 *
 */
public class FutureExample {
    
    public static void main(String[] args) {
        FutureExample callbackExample = new FutureExample();
        Worker workder = callbackExample.new Worker();
        workder.doWork();
    }

    interface Fetcher {
        Future<Data> fetchData();
    }

    class Data {
        
        private String content;

        public Data(String content) {
            super();
            this.content = content;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        @Override
        public String toString() {
            return "Data [content=" + content + "]";
        }
        
    }

    class RunnableImpl implements Runnable {
        private FutureImpl futureImpl;
        public RunnableImpl(FutureImpl futureImpl) {
            this.futureImpl = futureImpl;
        }
        @Override
        public void run() {
            // begin business
            // ......
            // end business
            
            try {
                Thread.sleep(1000 * 2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            futureImpl.setFinished();
        }
        
    }
    
    class FutureImpl implements Future<Data> {
        private Boolean finished = false;
        private Data data = null;
        public void setFinished() {
            finished = true;
            data = new Data("result is coming!");
        }
        @Override
        public boolean cancel(boolean mayInterruptIfRunning) {
            return false;
        }
        @Override
        public boolean isCancelled() {
            return false;
        }
        @Override
        public boolean isDone() {
            return finished;
        }
        @Override
        public Data get() throws InterruptedException, ExecutionException {
            return data;
        }
        @Override
        public Data get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException,
                TimeoutException {
            return null;
        }
    }
    
    class Worker {
        public void doWork() {
            Fetcher fetcher = new Fetcher() {
                @Override
                public Future<Data> fetchData() {
                    FutureImpl future = new FutureImpl();
                    RunnableImpl runnable = new RunnableImpl(future);
                    new Thread(runnable).start();
                    return future;
                }
            };
            Future<Data> future = fetcher.fetchData();
            while(!future.isDone()) {
                System.out.println("do something else...");
                try {
                    Thread.sleep(500 * 1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            try {
                System.out.println(future.get());
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
    }
}

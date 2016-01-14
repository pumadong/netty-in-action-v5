package chapter1;

/**
 * 演示回调
 *
 */
public class CallbackExample {
    
    public static void main(String[] args) {
        CallbackExample callbackExample = new CallbackExample();
        Worker workder = callbackExample.new Worker();
        workder.doWork();
    }

    interface Fetcher {
        void fetchData(FetchCallback callback);
    }

    interface FetchCallback {
        void onData(Data data);

        void onError(Throwable cause);
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

    class Worker {
        public void doWork() {
            Fetcher fetcher = new Fetcher() {
                @Override
                public void fetchData(FetchCallback callback) {
                    // begin business
                    // ......
                    // end business
                    
                    // executing callback
                    callback.onData(new Data("result is coming!"));
                    callback.onError(new RuntimeException("result is error!"));
                }
            };
            fetcher.fetchData(new FetchCallback() {

                // #1  Call if data is fetched without error
                @Override
                public void onData(Data data) {
                    System.out.println("Data received: " + data);
                }

                // #2 Call if error is received during fetch
                @Override
                public void onError(Throwable cause) {
                    System.err.println("An error accour: " + cause.getMessage());
                }

            });
        }
    }
}

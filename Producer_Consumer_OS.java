public class Main {
    static final int SIZE = 3;
    static int[] buffer = new int[SIZE];
    static int in = 0, out = 0;
    
    // Semaphore class
    static class Semaphore {
        private int value;

        public Semaphore(int initial) {
            this.value = initial;
        }

        public synchronized void down() {
            while (value == 0) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
            value--;
        }

        public synchronized void up() {
            value++;
            notify();  // wake up one waiting thread
        }
    }

    // Semaphores
    static Semaphore mutex = new Semaphore(1);    // for critical section
    static Semaphore empty = new Semaphore(SIZE); // initially all empty
    static Semaphore full = new Semaphore(0);     // initially none full

    static class Producer extends Thread {
        public void run() {
            for (int i = 0; i < 10; i++) {
                empty.down();     // wait for empty slot
                mutex.down();     // enter critical section

                buffer[in] = i;
                System.out.println("Produced: " + i + " at index " + in);
                in = (in + 1) % SIZE;

                mutex.up();       // exit critical section
                full.up();        // signal full slot

                try {
                    Thread.sleep((int)(Math.random() * 500));
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    static class Consumer extends Thread {
        public void run() {
            for (int i = 0; i < 10; i++) {
                full.down();      // wait for data
                mutex.down();     // enter critical section

                int data = buffer[out];
                System.out.println("Consumed: " + data + " at index " + out);
                out = (out + 1) % SIZE;

                mutex.up();       // exit critical section
                empty.up();       // signal empty slot

                try {
                    Thread.sleep((int)(Math.random() * 500));
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    public static void main(String[] args) {
        Thread p1 = new Producer();
        Thread p2 = new Producer();
        Thread c1 = new Consumer();
        Thread c2 = new Consumer();

        p1.start();
        p2.start();
        c1.start();
        c2.start();

        try {
            p1.join();
            p2.join();
            c1.join();
            c2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("All producers and consumers have finished.");
    }
}


/*

1. mutex (Binary Semaphore)
Initial value: 1 (acts like a lock)
Used to make sure only one thread at a time (producer or consumer) accesses the shared buffer.
Protects in, out, and buffer[].

2. empty (Counting Semaphore)
Initial value: SIZE (e.g., 5)
Keeps track of the number of empty slots in the buffer.
A producer must wait if the buffer is full (empty == 0).

3. full (Counting Semaphore)
Initial value: 0
Keeps track of the number of filled slots (i.e., how many items are available to consume).
A consumer must wait if the buffer is empty (full == 0).


=> Producer flow

empty.down();     // wait if buffer is full
mutex.down();     // enter critical section

// Add item to buffer

mutex.up();       // exit critical section
full.up();        // signal that buffer has new item


=> Consumer flow

full.down();      // wait if buffer is empty
mutex.down();     // enter critical section

// Remove item from buffer

mutex.up();       // exit critical section
empty.up();       // signal that buffer has space now


*/

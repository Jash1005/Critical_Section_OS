class Main {
    static volatile int rc = 0; // reader count

    static class Semaphore {
        private int value;

        Semaphore(int value) {
            this.value = value;
        }

        public synchronized void down() {
            while (value <= 0) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    System.out.println(e);
                }
            }
            value--;
        }

        public synchronized void up() {
            value++;
            notifyAll(); // notify all waiting threads
        }
    }

    static class Page {
        private int value = 0;

        public void write() {
            value++;
        }

        public int read() {
            return value;
        }
    }

    static class Reader extends Thread {
        Page p;
        Semaphore mutex;
        Semaphore db;

        Reader(Page p, Semaphore mutex, Semaphore db) {
            this.p = p;
            this.mutex = mutex;
            this.db = db;
        }

        public void run() {
            mutex.down();
            rc = rc + 1;
            if (rc == 1) db.down(); // first reader locks DB
            mutex.up();

            System.out.println(getName() + " Read: " + p.read()); // Critical Section

            mutex.down();
            rc = rc - 1;
            if (rc == 0) db.up(); // last reader unlocks DB
            mutex.up();
        }
    }

    static class Writer extends Thread {
        Page p;
        Semaphore db;

        Writer(Page p, Semaphore db) {
            this.p = p;
            this.db = db;
        }

        public void run() {
            db.down();
            p.write();
            System.out.println(getName() + " Write: " + p.read()); // Critical Section
            db.up();
        }
    }

    public static void main(String[] args) {
        Page p = new Page();
        Semaphore mutex = new Semaphore(1);
        Semaphore db = new Semaphore(1);

        // You can spawn multiple readers and writers
        Reader r1 = new Reader(p, mutex, db);
        Reader r2 = new Reader(p, mutex, db);
        Writer w1 = new Writer(p, db);
        Writer w2 = new Writer(p, db);

        r1.setName("Reader-1");
        r2.setName("Reader-2");
        w1.setName("Writer-1");
        w2.setName("Writer-2");

        r1.start();
        w1.start();
        r2.start();
        w2.start();
    }
}

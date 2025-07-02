public class Main {
    static volatile int turn = 0; // Shared between threads
    static volatile boolean[] flag = new boolean[]{false,false};

    static class AadharID {
        private int count = 0;

        public void increment() {
            count++;
        }

        public int getcount() {
            return count;
        }
    }

    static class A extends Thread {
        AadharID adhar_id;

        A(AadharID adhar_id) {
            this.adhar_id = adhar_id;
        }

        public void run() {
              for(int i=0;i<3;i++) {
                while (flag[1] && turn != 0) ;
                flag[0] = true;
                System.out.println(Thread.currentThread().getName());
                adhar_id.increment();
                turn = 1;
                flag[0] = false; 
            }
        }
    }

    static class B extends Thread {
        AadharID adhar_id;

        B(AadharID adhar_id) {
            this.adhar_id = adhar_id;
        }

        public void run() {
            for (int i = 0; i < 3; i++) {
                while (flag[0] && turn != 1) ;
                flag[1] = true;
                System.out.println(Thread.currentThread().getName());
                adhar_id.increment();
                turn = 0;
                flag[1] = false;
            }
        }
    }

    public static void main(String[] args) {
        AadharID adhar_id = new AadharID();
        A t1 = new A(adhar_id);
        B t2 = new B(adhar_id);

        t1.setName("Thread A");
        t2.setName("Thread B");

        t1.start();
        t2.start();

        try {
            t1.join();
            t2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("Final Count: " + adhar_id.getcount());
    }
}

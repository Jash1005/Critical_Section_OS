public class Main {
    static volatile int turn = 0; // Shared between threads

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
                while (turn != 0) ;
                System.out.println(Thread.currentThread().getName());
                adhar_id.increment();
                turn = 1;
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
                while (turn != 1) ;
                System.out.println(Thread.currentThread().getName());
                adhar_id.increment();
                turn = 0;
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






/* Some IMP concepts : 

  1) what is volatile keyword:  It tells java that this variable can be used by multiple threads so donot cache it. Without volatile, thread might cached in their local cpu and changes made can't be visible to other threads

  2) In this case threads are given a particular order to use critical section but instead they should be given freedom. This is drawback of this method
  
*/


public class Main {
    
    static class Semaphore {
        private int value;
        
        Semaphore(int x) {
            this.value = x;
        }
        
        public synchronized void up() {
            value++;
            // try {
            //     Thread.sleep(1000);
            // }
            // catch (InterruptedException e) {
            //      Thread.currentThread().interrupt();
            // }
             notify();  // wake-up one thread from suspended list , it does not mean it will access CS, it just mean it can try again for CS
        }
        
        public synchronized void down() {
            
            while(value <= 0) {
                try {
                   wait();
                }
                catch(Exception e) {
                    Thread.currentThread().interrupt();
                }
            }
            value--;
        }
    }
    
    
    static class critical_section {              // Critical Section
        public void read() {
            System.out.print("Hi - ");
        }
    }

    static class A extends Thread {
        critical_section cs;
        Semaphore sem;

        A(critical_section cs,Semaphore sem) {
            this.cs = cs;
            this.sem = sem;
        }

        public void run() {
              for(int i=0;i<10;i++) {
                  sem.down();
                  cs.read(); // CS
                  System.out.println(Thread.currentThread().getName());
                  sem.up();
             }
        }
    }

    static class B extends Thread {
        critical_section cs;
        Semaphore sem;

        B(critical_section cs,Semaphore sem) {
            this.cs = cs;
            this.sem = sem;
        }

        public void run() {
            for (int i = 0; i < 10; i++) {
                sem.down();
                 cs.read();   // CS
                System.out.println(Thread.currentThread().getName());
                sem.up();
            }
        }
    }

    public static void main(String[] args) {
        critical_section cs = new critical_section();
        Semaphore sem = new Semaphore(4);
        
        A t1 = new A(cs,sem);
        B t2 = new B(cs,sem);
        
        t1.start();
        t2.start();
        
        try {
            t1.join();
            t2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}


/*
Up and down semaphore
down -> entry 
up -> exit

IMP : Java threads may wake up from wait() without any notify().
This is called a spurious wakeup — and it's allowed by the JVM specification.

If you use only if, the thread will wrongly continue, even if the condition value > 0 is not yet true.
while ensures it re-checks the condition after waking up.
*/

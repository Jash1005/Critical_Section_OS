class Main {
    static int N = 5;
    static volatile Semaphore[] arr = new Semaphore[N];
    static volatile boolean[] fork = new boolean[N];

    static class Semaphore {
        int val;

        Semaphore() {
            this.val = 1;
        }

        public synchronized void down() {
            while (val <= 0) {
                try {
                    wait();
                } catch (Exception e) {
                    System.out.println(e);
                }
            }
            val--;
        }

        public synchronized void up() {
            val++;
            notifyAll();
        }
    }

    static class Philosopher extends Thread {
        int idx;

        Philosopher(int idx, String name) {
            this.idx = idx;
            this.setName(name);
        }

        public void run() {
            try {
                if (idx == N - 1) {
                  
                    arr[(idx + 1) % arr.length].down();
                    fork[(idx + 1) % arr.length] = true;

                    arr[idx].down();
                    fork[idx] = true;
                  
                } else {

                    arr[idx].down();
                    fork[idx] = true;

                    arr[(idx + 1) % arr.length].down();
                    fork[(idx + 1) % arr.length] = true;
                }

                System.out.println(getName() + " is eating...");
                Thread.sleep(1000); // Eating
                System.out.println(getName() + " is done eating.");

                
                arr[idx].up();
                fork[idx] = false;

                arr[(idx + 1) % arr.length].up();
                fork[(idx + 1) % arr.length] = false;

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        
        for (int i = 0; i < N; i++) {
            arr[i] = new Semaphore();
            fork[i] = false;
        }

        Philosopher[] philosophers = new Philosopher[N];
        for (int i = 0; i < N; i++) {
            philosophers[i] = new Philosopher(i, "P" + i);
        }

        for (int i = 0; i < N; i++) {
            philosophers[i].start();
        }
    }
}





/*
| Philosopher | Forks Requested                                     |
| ----------- | --------------------------------------------------- |
| P0          | Fork 0 (taken), waiting for Fork 1                  |
| P1          | Fork 1 (taken), waiting for Fork 2                  |
| P2          | Fork 2 (taken), waiting for Fork 3                  |
| P3          | Fork 3 (taken), waiting for Fork 4                  |
| P4          | Fork 4 (taken), waiting for Fork 0 ←     held by P0 |

This is condition for deadlock. No one now releases any of their resource. For fixing it we make change in P4's orientation of taking fork. If P4 take first 0 and than 4 then during taking 0 it will be blocked due to fork in use. 
This solves our problem as now fork 4 can be used by other.
*/

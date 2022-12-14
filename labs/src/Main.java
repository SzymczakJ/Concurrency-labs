public class Main {
    public static void main(String[] args) throws InterruptedException {
        Monitor sut;
        sut = new Monitor(60);
        Thread thread1 = new Thread(new Runnable() {
            @Override
            public void run() {
                for (;;) {
                    sut.increment(true);
                }

            }
        });
        Thread thread3 = new Thread(new Runnable() {
            @Override
            public void run() {
                for (;;) {
                    sut.increment(false);
                }

            }
        });
        Thread thread2 = new Thread(new Runnable() {
            @Override
            public void run() {
                for (;;) {
                    sut.decrement(true);
                }

            }
        });
        Thread thread4 = new Thread(new Runnable() {
            @Override
            public void run() {
                for (;;) {
                    sut.decrement(false);
                }

            }
        });
        thread1.start();
        thread2.start();
        thread3.start();
        thread4.start();
        thread2.join();
        thread1.join();
        thread3.join();
        thread4.join();
    }
}
// Dwóch producentów (P1 i P2) i konsument(K1). 1. Wchodzi P1 i produkuje(B = 1). 2. Wchodzi P2 i próbuje produkować, więc waituje(B = 1). 3. Wchodzi K1 i zjada(B = 0). 4. Wchodzi
//Dwóch producentów (P1 i P2) i konsument(K1). 1. Wchodzi P1 i produkuje(B = 1). 2 Wchodzi P2 i próbuje produkować, więc waituje(B = 1). P1 znowu się blokuje. Konsument zmienia bufor na zero i budzi P2 i obaj są na locku. Wchodzi K1 i waituje. P1 produkuje i wybudza P2. P1 waituje i P2 waituje.
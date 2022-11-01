import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class Monitor {
    int variable = 0;
    int bigCounter = 0;
    int smallCounter = 0;
    Buffer buffer = new Buffer();
    ReentrantLock lock;
    Condition prodCond;
    Condition consCond;

    Monitor() {
        lock = new ReentrantLock();
        prodCond = lock.newCondition();
        consCond = lock.newCondition();
    }
    public void decrement() {
        lock.lock();
        try {
            System.out.println("sitting in cons lock");
            while (buffer.resource == 0) {
                System.out.println("waiting to cons");
                consCond.await();

            }
            buffer.resource--;
            System.out.print(buffer.resource);
            System.out.println(" consume");
            prodCond.signal();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            lock.unlock();
        }
    }

    public void increment() {
        lock.lock();
        try {
            System.out.println("sitting in prod lock");
            while (buffer.resource > 0) {
                System.out.println("waiting to prod");
                prodCond.await();
            }
            buffer.resource++;
//            if (howManyToProd == 5) {
//                bigCounter++;
//            }
//            if (howManyToProd == 1) {
//                smallCounter++;
//            }
            System.out.print(buffer.resource);
            System.out.println(" produce");
            consCond.signal();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            lock.unlock();
        }
    }
}


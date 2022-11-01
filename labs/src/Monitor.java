import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class Monitor {
    int variable = 0;
    int bigCounter = 0;
    int smallCounter = 0;
    Buffer buffer = new Buffer();
    ReentrantLock lock = new ReentrantLock();
    Condition prodCond = lock.newCondition();
    Condition consCond = lock.newCondition();

    public void decrement(int howManyToCons) {
        lock.lock();
        try {
            while (buffer.resource < howManyToCons) {
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

    public void increment(int howManyToProd) {
        lock.lock();
        try {
            while (buffer.resource + howManyToProd > 5) {
                System.out.println("waiting to prod");
                prodCond.await();
            }
            buffer.resource++;
            if (howManyToProd == 5) {
                bigCounter++;
            }
            if (howManyToProd == 1) {
                smallCounter++;
            }
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


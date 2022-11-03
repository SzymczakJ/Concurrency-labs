import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class Monitor {
    int bufferSize;
    int bigIncrementCounter = 0;
    int smallIncrementCounter = 0;
    int bigDecrementCounter = 0;
    int smallDecrementCounter = 0;
    Buffer buffer = new Buffer();
    ReentrantLock lock;
    Condition prodCond;
    Condition consCond;

    Monitor(int bufferSize) {
        lock = new ReentrantLock();
        prodCond = lock.newCondition();
        consCond = lock.newCondition();
        this.bufferSize = bufferSize;
    }
    public void decrement(boolean isDecrementBigger) {
        int bufferChange = bufferChangeSize(isDecrementBigger);
        lock.lock();
        try {
            System.out.println("sitting in cons lock");
            while (buffer.resource < bufferChange) {
                System.out.println("waiting to cons");
                consCond.await();
            }
            buffer.resource -= bufferChange;
            countDecrementEntries(isDecrementBigger);
            System.out.print(buffer.resource);
            System.out.println(" consume");
            prodCond.signal();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            lock.unlock();
        }
    }

    public void increment(boolean isIncrementBigger) {
        int bufferChange = bufferChangeSize(isIncrementBigger);
        lock.lock();
        try {
//            System.out.println("sitting in prod lock");
//            System.out.println(bufferSize);
//            System.out.println(buffer.resource);
//            System.out.println(bufferChange);
            while (buffer.resource + bufferChange > bufferSize) {
                System.out.println("waiting to prod");
                prodCond.await();
            }
            buffer.resource += bufferChange;
            countIncrementEntries(isIncrementBigger);
            System.out.print(buffer.resource);
            System.out.println(" produce");
            consCond.signal();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            lock.unlock();
        }
    }

    int bufferChangeSize(boolean isBig) {
        if (isBig) {
            return 5;
        } else {
            return 1;
        }
    }

    void countDecrementEntries(boolean isBig) {
        if (isBig) {
            bigDecrementCounter++;
        } else {
            smallDecrementCounter++;
        }
        System.out.print("big decrement counter: ");
        System.out.println(bigDecrementCounter);
        System.out.print("small decrement counter: ");
        System.out.println(smallDecrementCounter);
    }

    void countIncrementEntries(boolean isBig) {
        if (isBig) {
            bigIncrementCounter++;
        } else {
            smallIncrementCounter++;
        }
        System.out.print("big increment counter: ");
        System.out.println(bigDecrementCounter);
        System.out.print("small increment counter: ");
        System.out.println(smallIncrementCounter);
    }
}


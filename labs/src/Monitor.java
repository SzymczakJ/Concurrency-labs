import java.util.Random;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class Monitor {
    int bufferSize;
    long bigIncrementCounter = 0;
    long smallIncrementCounter = 0;
    long bigDecrementCounter = 0;
    long smallDecrementCounter = 0;
    Random random = new Random();
    Buffer buffer = new Buffer();
    ReentrantLock lock;
    Condition firstProdCond;
    Condition secondaryProdCond;
    Condition firstConsCond;
    Condition secondaryConsCond;
    boolean isFirstProdCondOccupied = false;
    boolean isFirstConsCondOccupied = false;

    Monitor(int bufferSize) {
        lock = new ReentrantLock();
        firstConsCond = lock.newCondition();
        secondaryConsCond = lock.newCondition();
        firstProdCond = lock.newCondition();
        secondaryProdCond = lock.newCondition();
        this.bufferSize = bufferSize;
    }
    public void decrement(boolean isDecrementBigger) {
        int bufferChange = bufferChangeSize(isDecrementBigger);
        lock.lock();
        try {
            System.out.println("sitting in cons lock");
            while (isFirstConsCondOccupied) {
                System.out.println("waiting in secondary cons");
                secondaryConsCond.await();
            }
            while (buffer.resource < bufferChange) {
                System.out.println("waiting in first cons");
                isFirstConsCondOccupied = true;
                firstConsCond.await();
            }
            buffer.resource -= bufferChange;
            countDecrementEntries(isDecrementBigger);
            System.out.println(buffer.resource);
            System.out.println(" consume");
            isFirstConsCondOccupied = false;
            secondaryConsCond.signal();
            firstProdCond.signal();
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
            System.out.println("sitting in prod lock");
            while (isFirstProdCondOccupied) {
                System.out.println("waiting in secondary prod");
                secondaryProdCond.await();
            }
            while (buffer.resource + bufferChange > bufferSize) {
                System.out.println("waiting in first prod");
                isFirstProdCondOccupied = true;
                firstProdCond.await();
            }
            buffer.resource += bufferChange;
            countIncrementEntries(isIncrementBigger);
            System.out.print(buffer.resource);
            System.out.println(" prod");
            isFirstProdCondOccupied = false;
            secondaryProdCond.signal();
            firstConsCond.signal();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            lock.unlock();
        }
    }

    int bufferChangeSize(boolean isBig) {
        if (isBig) {
            return random.nextInt(10) + 20;
        } else {
            return random.nextInt(5);
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


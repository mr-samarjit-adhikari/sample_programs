public class FifoReadWriteLock {
    int readAcquires = 0, readReleases = 0;
    boolean writer = false;
    ReentrantLock lock;
    Condition condition = lock.newCondition(); // This is the condition variable.

    void readLock () {
        lock.lock();
        try {
            while(writer)
                condition.await();
            readAcquires++;
        }
        finally {
            lock.unlock();
        }
    }

    void readUnlock () {
        lock.lock();
        try {
            readReleases++;
            if (readAcquires == readReleases)
                condition.signalAll();
        }
        finally {
            lock.unlock();
        }
    }

    void writeLock () {
        lock.lock();
        try {
            while (writer)
                condition.await();

            writer = true;

            while (readAcquires != readReleases)
                condition.await();
        }
        finally {
            lock.unlock();
        }
    }

    void writeUnlock() {
        writer = false;
        condition.signalAll();
    }
}

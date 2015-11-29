package Services;

import java.io.Closeable;
import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.Semaphore;

/**
 * this class provides a mean to secure access to shared data based on a string value.
 * Designed to be used as a lock access for a particular index of a database.

 * WARNING: Prone to deadlock if not used properly.
 * Be particularly cautious NOT TO try to read a protected section while writing since write lock will
 * block access to both reads and writes. (resulting in a deadLock)
 * Reads are concurrent. No limit set for the max concurrency on reads.
 *
 * NOTE: this implementation follows the following readers-writers solution:
 * https://en.wikipedia.org/wiki/Readers%E2%80%93writers_problem#Third_readers-writers_problem
 */
public class LockFactory implements Closeable {

    private final int DEFAULT_MAX_CONCURRENT_ACCESS = 1;
    private static LockFactory ourInstance = new LockFactory();

    private HashMap<String, Integer> readCounts;

    private HashMap<String, Semaphore> writeLocks;
    private HashMap<String, Semaphore> readLocks;
    private HashMap<String, Semaphore> readCountsMutex;


    private LockFactory() {

        this.writeLocks = new HashMap<>();
        this.readLocks = new HashMap<>();
        this.readCountsMutex = new HashMap<>();
        this.readCounts = new HashMap<>();
    }

    public static LockFactory getInstance() {

        return ourInstance;
    }

    /**
     * Ensures that no writer accesses a shared data while being read.
     * If so, readers will finish their job before writer can get in.
     * @param valueToLockOn
     */
    public void readlock(String valueToLockOn) {

        Semaphore writeLock = getLazyWriteLock(valueToLockOn);
        Semaphore readLock = getLazyReadLock(valueToLockOn);
        Semaphore readCount = getLazyReadCountLock(valueToLockOn);

        try {
            readLock.acquire();
            readCount.acquire();
            Integer oldCount = getCount(valueToLockOn);
            Integer newCount = oldCount + 1;

            if (newCount == 1) {
                writeLock.acquire();
            }

            this.readCounts.replace(valueToLockOn, oldCount, newCount);

        } catch (InterruptedException e) {
        } finally {
            readCount.release();
            readLock.release();
        }

    }

    /**
     * If it's the last reader to leave the protected area, it will signal any potential writer
     * @param valueToUnlock
     */
    public void readUnlock(String valueToUnlock) {

        Semaphore writeLock = getLazyWriteLock(valueToUnlock);
        Semaphore readCount = getLazyReadCountLock(valueToUnlock);

        try {
            readCount.acquire();

            Integer oldCount = this.readCounts.get(valueToUnlock);
            Integer newCount = oldCount - 1;
            this.readCounts.replace(valueToUnlock, oldCount, newCount);

            if (newCount == 0) {
                writeLock.release();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            readCount.release();
        }
    }

    /**
     * Writer locks the access for this particular value.
     * WARNING: Do not perform a protected read (using readLock) while being in a writeLock area,
     * since read is not allowed while modifying data.
     * @param valueToLockOn
     */
    public void writeLock(String valueToLockOn) {

        System.out.println(
                String.format("Thread #%d WRITE LOCKS on value %s.", Thread.currentThread().getId(), valueToLockOn)
        );


        Semaphore writeLock = getLazyWriteLock(valueToLockOn);
        Semaphore readLock = getLazyReadLock(valueToLockOn);

        try {
            readLock.acquire();
            writeLock.acquire();
        } catch (InterruptedException e) {
            System.err.println(
                    String.format("Thread #%d COULD NOT WRITE LOCK on value %s.", Thread.currentThread().getId(), valueToLockOn)
            );
        }
    }

    /**
     * Unlocks access to the data for the given value.
     * @param valueToUnlock
     */
    public void writeUnlock(String valueToUnlock) {

        System.out.println(
                String.format("Thread #%d WRITE UNLOCKS value %s.", Thread.currentThread().getId(), valueToUnlock)
        );

        Semaphore writeLock = getLazyWriteLock(valueToUnlock);
        Semaphore readLock = getLazyReadLock(valueToUnlock);

        writeLock.release();
        readLock.release();
    }

    private Integer getCount(String index) {
        Integer count = this.readCounts.get(index);
        if (count == null) {
            count = 0;
            this.readCounts.put(index, count);
        }
        return count;
    }

    private synchronized Semaphore getLazyWriteLock(String valueToLockOn) {
        Semaphore valueLock = this.writeLocks.get(valueToLockOn);
        if (valueLock == null) {
            valueLock = new Semaphore(1);
            this.writeLocks.put(valueToLockOn, valueLock);
        }
        return this.writeLocks.get(valueToLockOn);
    }

    private synchronized Semaphore getLazyReadLock(String valueToLockOn) {
        Semaphore readLock = this.readLocks.get(valueToLockOn);
        if (readLock == null) {
            readLock = new Semaphore(1);
            this.readLocks.put(valueToLockOn, readLock);
        }
        return this.readLocks.get(valueToLockOn);
    }

    private synchronized Semaphore getLazyReadCountLock(String valueToLockOn) {
        Semaphore mutex = this.readCountsMutex.get(valueToLockOn);
        if (mutex == null) {
            mutex = new Semaphore(1);
            this.readCountsMutex.put(valueToLockOn, mutex);
        }

        return mutex;
    }

    /**
     * Release any lock that could be active.
     * Mind the interrepted exceptions and IOExceptions.
     * @throws IOException
     */
    @Override
    public void close() throws IOException {
        this.writeLocks
                .values()
                .forEach(l -> l.release());
    }
}

package com.spj;

import java.util.concurrent.atomic.AtomicInteger;

public class LeakyBucket {
  private final int capacity;
  private final int leakRate;
  private AtomicInteger water;
  private long lastLeakTime;

  public LeakyBucket(int capacity, int leakRate) {
    this.capacity = capacity;
    this.leakRate = leakRate;
    this.water = new AtomicInteger(0);
    this.lastLeakTime = System.currentTimeMillis();
  }

  public synchronized boolean add(int amount) {
    leak();
    if (water.get() + amount > capacity) {
      return false; // Bucket overflows
    }
    water.addAndGet(amount);
    return true;
  }

  private void leak() {
    long now = System.currentTimeMillis();
    long elapsed = now - lastLeakTime;
    int leak = (int) (elapsed / 1000.0 * leakRate);
    if (leak > 0) {
      water.addAndGet(-leak);
      lastLeakTime = now;
    }
  }
}

/*
 * Copyright 2015-present wequick.net
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain
 * a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package com.qiqi.util

public final class ThreadUtil {
    synchronized static int copyCount = 0;

    public static void syncRun(final List<Runnable> runnables) {
        List<Runnable> tempRunnables = new ArrayList<>();
        for (final int i = 0; i < runnables.size(); i++) {
            final Runnable runnable = runnables.get(i);
            tempRunnables.add(new Runnable() {
                @Override
                public void run() {
                    try {
                        runnable.run();
                        synchronized (ThreadUtil.class) {
                            copyCount = copyCount + 1;
                            Log.Stute("第" + i + "个执行完毕，copyCount：" + copyCount + " runnables.size: " + tempRunnables.size())
                            Log.Stute("比较：" + (copyCount >= runnables.size()))
                            if (copyCount >= runnables.size()) {
                                copyCount = 0;
                                synchronized (lock) {
                                    lock.notifyAll();
                                    Log.Stute("释放")
                                }
                            }
                        }
                    } catch (Exception e1) {
                        e1.printStackTrace();
                        copyCount = 0;
                        synchronized (lock) {
                            lock.notifyAll();
                        }
                    }
                }
            });
        }
        sync(tempRunnables);
    }
    static Object lock = new Object();

    private static void sync(List<Runnable> runs) throws Exception {
        long start = System.currentTimeMillis();
        synchronized (lock) {
            for (int i = 0; i < runs.size(); i++) {
                new Thread(runs.get(i)).start();
            }
            lock.wait();
            long end = System.currentTimeMillis();
        }
    }

    static runnableCount = 0;

    public static void put(Runnable runnable) {
        Thread thread = new Thread(new Runnable() {
            @Override
            void run() {
                runnable.run();
                synchronized (ThreadUtil.class) {
                    runnableCount = runnableCount - 1;
                    if (runnableCount <= 0) {
                        copyCount = 0;
                        synchronized (lock) {
                            lock.notifyAll();
                            Log.Stute("释放")
                        }
                    }
                }
            }
        });
        runnableCount++;
        thread.start();
    }

    public static void checkBackRunnableAndWait() {
        synchronized (lock) {
            if (runnableCount > 0)
                lock.wait();
        }
    }
}


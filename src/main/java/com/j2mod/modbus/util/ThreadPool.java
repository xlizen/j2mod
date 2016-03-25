/*
 * Copyright 2002-2016 jamod & j2mod development teams
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.j2mod.modbus.util;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * Class implementing a simple thread pool.
 *
 * @author Dieter Wimberger
 * @author Steve O'Hara (4energy)
 * @version 2.0 (March 2016)
 */
public class ThreadPool {

    private static final ModbusLogger logger = ModbusLogger.getLogger(ThreadPool.class);

    //instance attributes and associations
    private LinkedBlockingQueue<Runnable> m_TaskPool;
    private int m_Size = 1;

    /**
     * Constructs a new <tt>ThreadPool</tt> instance.
     *
     * @param size the size of the thread pool.
     */
    public ThreadPool(int size) {
        m_Size = size;
        m_TaskPool = new LinkedBlockingQueue<Runnable>();
        initPool();
    }

    /**
     * Execute the <tt>Runnable</tt> instance
     * through a thread in this <tt>ThreadPool</tt>.
     *
     * @param task the <tt>Runnable</tt> to be executed.
     */
    public synchronized void execute(Runnable task) {
        try {
            m_TaskPool.put(task);
        }
        catch (InterruptedException ex) {
            //FIXME: Handle!?
        }
    }

    /**
     * Initializes the pool, populating it with
     * n started threads.
     */
    protected void initPool() {
        for (int i = m_Size; --i >= 0; ) {
            new PoolThread().start();
        }
    }

    /**
     * Inner class implementing a thread that can be
     * run in a <tt>ThreadPool</tt>.
     *
     * @author Dieter Wimberger
     * @version 1.2rc1 (09/11/2004)
     */
    private class PoolThread extends Thread {

        /**
         * Runs the <tt>PoolThread</tt>.
         * <p>
         * This method will infinitely loop, picking
         * up available tasks from the <tt>LinkedQueue</tt>.
         */
        public void run() {
            logger.debug("Running PoolThread");
            do {
                try {
                    logger.debug(this.toString());
                    m_TaskPool.take().run();
                }
                catch (Exception ex) {
                    //FIXME: Handle somehow!?
                    ex.printStackTrace();
                }
            } while (true);
        }
    }

}

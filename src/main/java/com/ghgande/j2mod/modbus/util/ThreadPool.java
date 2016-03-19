/*
 * This file is part of j2mod.
 *
 * j2mod is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * j2mod is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Foobar.  If not, see <http://www.gnu.org/licenses
 */
package com.ghgande.j2mod.modbus.util;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * Class implementing a simple thread pool.
 *
 * @author Dieter Wimberger
 * @version 1.2rc1 (09/11/2004)
 */
public class ThreadPool {

    private static final Logger logger = Logger.getLogger(ThreadPool.class);

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
            //System.out.println("Running PoolThread");
            do {
                try {
                    //System.out.println(this.toString());
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

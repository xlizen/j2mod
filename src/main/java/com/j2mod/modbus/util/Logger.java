/*
 * This file is part of j2mod.
 *
 * j2mod is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * j2mod is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with j2mod.  If not, see <http://www.gnu.org/licenses
 */

package com.j2mod.modbus.util;

/**
 * Simply wrapper class that extends the standard Logger to allow for some
 * more convenient means of sending out useful formatted messages
 */
public class Logger extends org.apache.log4j.Logger {

    /**
     * Constructs a logger for our use
     *
     * @param name Name of the Category
     */
    public Logger(String name) {
        super(name);
    }

    /**
     * Creates a custom logger using the name as the Category
     *
     * @param name Name of the Category
     *
     * @return Logger
     */
    static public Logger getLogger(String name) {
        return (Logger)org.apache.log4j.Logger.getLogger(name, new LoggerFactory());
    }

    /**
     * Creates a custom logger using the name as the Category
     *
     * @param clazz Class to get name from
     *
     * @return Logger
     */
    static public Logger getLogger(Class clazz) {
        return (Logger)org.apache.log4j.Logger.getLogger(clazz.getName(), new LoggerFactory());
    }

    /**
     * Creates a message using the message with placeholders for the variables
     * See String.format() for more detail
     *
     * @param message Message to format
     * @param values  Replacement values
     */
    public void debug(String message, Object... values) {
        if (isDebugEnabled()) {
            super.debug(getMessage(message, values));
        }
    }

    /**
     * Creates a message using the message with placeholders for the variables
     * See String.format() for more detail
     * The message is sent direct to the System.out stream
     *
     * @param message Message to format
     * @param values  Replacement values
     */
    public void system(String message, Object... values) {
        System.out.println(getMessage(message, values));
    }

    /**
     * @see Logger#debug(String, Object...)
     */
    public void error(String message, Object... values) {
        super.error(getMessage(message, values));
    }

    /**
     * @see Logger#debug(String, Object...)
     */
    public void fatal(String message, Object... values) {
        super.fatal(getMessage(message, values));
    }

    /**
     * @see Logger#debug(String, Object...)
     */
    public void info(String message, Object... values) {
        if (isInfoEnabled()) {
            super.info(getMessage(message, values));
        }
    }

    /**
     * @see Logger#debug(String, Object...)
     */
    public void warn(String message, Object... values) {
        super.warn(getMessage(message, values));
    }

    /**
     * Creates a message using the message with placeholders for the variables
     * See String.format() for more detail
     *
     * @param message Message to format
     * @param t       the exception to log, including its stack trace.
     * @param values  Replacement values
     */
    public void warn(String message, Throwable t, Object... values) {
        super.warn(getMessage(message, values), t);
    }

    /**
     * @see Logger#warn(String, Throwable, Object...)
     */
    public void info(String message, Throwable t, Object... values) {
        super.info(getMessage(message, values), t);
    }

    /**
     * @see Logger#warn(String, Throwable, Object...)
     */
    public void fatal(String message, Throwable t, Object... values) {
        super.fatal(getMessage(message, values), t);
    }

    /**
     * @see Logger#warn(String, Throwable, Object...)
     */
    public void error(String message, Throwable t, Object... values) {
        super.error(getMessage(message, values), t);
    }

    /**
     * @see Logger#warn(String, Throwable, Object...)
     */
    public void debug(String message, Throwable t, Object... values) {
        super.debug(getMessage(message, values), t);
    }

    /**
     * Safely formats a string without tears
     *
     * @param message Message to format
     * @param values  Replacement variables
     *
     * @return Converted string
     */
    private String getMessage(String message, Object... values) {
        try {
            return String.format(message, values);
        }
        catch (Exception e) {
            return String.format("%s ERROR:%s", message, e.getMessage());
        }
    }

    /**
     * Creates a custom logger factory to use to get our own loggers
     */
    public static class LoggerFactory implements org.apache.log4j.spi.LoggerFactory {
        @Override
        public org.apache.log4j.Logger makeNewLoggerInstance(String name) {
            return new Logger(name);
        }
    }

}

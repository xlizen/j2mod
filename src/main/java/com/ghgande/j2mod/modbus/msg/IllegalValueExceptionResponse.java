/*
 * This file is part of j2mod-steve.
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
package com.ghgande.j2mod.modbus.msg;

import com.ghgande.j2mod.modbus.Modbus;

/**
 * @author Julie
 * @version @version@ (@date@)
 */
public class IllegalValueExceptionResponse extends ExceptionResponse {

    /**
     *
     */
    public IllegalValueExceptionResponse() {
        super(0, Modbus.ILLEGAL_VALUE_EXCEPTION);
    }

    public IllegalValueExceptionResponse(int function) {
        super(function, Modbus.ILLEGAL_VALUE_EXCEPTION);
    }

    /**
     *
     */
    public void setFunctionCode(int fc) {
        super.setFunctionCode(fc | Modbus.EXCEPTION_OFFSET);
    }
}

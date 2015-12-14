/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.extension.basic;

import org.mule.extension.annotation.api.Operation;

import java.util.Calendar;

public class BasicOperations
{

    @Operation
    public char passthruChar(char value)
    {
        return value;
    }

    /**
     * Passthru string
     *
     * @param value Value to passthru
     * @return The same string
     */
    @Operation
    public String passthruString(String value)
    {
        return value;
    }

    /**
     * Passthru float
     *
     * @param value Value to passthru
     * @return The same float
     */
    @Operation
    public float passthruFloat(float value)
    {
        return value;
    }

    /**
     * Passthru boolean
     *
     * @param value Value to passthru
     * @return The same boolean
     */
    @Operation
    public boolean passthruBoolean(boolean value)
    {
        return value;
    }

    /**
     * Passthru integer
     *
     * @param value Value to passthru
     * @return The same integer
     */
    @Operation
    public int passthruInteger(int value)
    {
        return value;
    }

    /**
     * Passthru long
     *
     * @param value Value to passthru
     * @return The same long
     */
    @Operation
    public long passthruLong(long value)
    {
        return value;
    }

    /**
     * Passthru complex float
     *
     * @param value Value to passthru
     * @return The same complex float
     */
    @Operation
    public Float passthruComplexFloat(Float value)
    {
        return value;
    }

    /**
     * Passthru complex boolean
     *
     * @param value Value to passthru
     * @return The same complex boolean
     */
    @Operation
    public Boolean passthruComplexBoolean(Boolean value)
    {
        return value;
    }

    /**
     * Passthru complex integer
     *
     * @param value Value to passthru
     * @return The same complex integer
     */
    @Operation
    public Integer passthruComplexInteger(Integer value)
    {
        return value;
    }

    /**
     * Passthru complex long
     *
     * @param value Value to passthru
     * @return The same complex long
     */
    @Operation
    public Long passthruComplexLong(Long value)
    {
        return value;
    }

    public enum Mode
    {
        In,
        Out;
    }

    /**
     * Passthru mode enum
     *
     * @param mode Value to passthru
     * @return The same cmode enum
     */
    @Operation
    public String passthruEnum(Mode mode)
    {
        return mode.name();
    }

    /**
     * Passthru complex object
     *
     * @param myComplexObject Value to passthru
     * @return The same complex object
     */
    @Operation
    public String passthruComplexRef(MyComplexObject myComplexObject)
    {
        return myComplexObject.getValue();
    }

    /**
     * Passthru calendar
     *
     * @param calendar Value to passthru
     * @return The same calendar
     */
    @Operation
    public Calendar passthruCalendar(Calendar calendar)
    {
        return calendar;
    }

}

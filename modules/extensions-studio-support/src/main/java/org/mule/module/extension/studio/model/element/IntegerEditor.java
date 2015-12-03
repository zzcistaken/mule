/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.extension.studio.model.element;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.mule.module.extension.studio.model.IEditorElementVisitor;

@XmlRootElement(name = "integer")
public class IntegerEditor extends BaseFieldEditorElement {

    private Integer min;
    private Integer max;
    private Integer step;
    private Integer defaultValue;

    private String xsiNamespace;
    private String xsiType;

    @XmlAttribute
    public Integer getMin() {
        return min;
    }

    public void setMin(Integer min) {
        this.min = min;
    }

    @XmlAttribute
    public Integer getMax() {
        return max;
    }

    public void setMax(Integer max) {
        this.max = max;
    }

    @XmlAttribute
    public Integer getStep() {
        return step;
    }

    public void setStep(Integer step) {
        this.step = step;
    }

    @XmlAttribute
    public Integer getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(Integer defaultValue) {
        this.defaultValue = defaultValue;
    }

    @Override
    public String toString() {
        return "IntegerEditor [min=" + min + ", max=" + max + ", step=" + step + ", defaultValue=" + defaultValue + ", getName()=" + getName() + ", getCaption()=" + getCaption()
                + "]";
    }

    @Override
    public void accept(IEditorElementVisitor visitor) {
        visitor.visit(this);
    }

    @XmlAttribute(name = "xmlns:xsi")
    public String getXsiNamespace() {
        return xsiNamespace;
    }

    public void setXsiNamespace(String xsiNamespace) {
        this.xsiNamespace = xsiNamespace;
    }

    @XmlAttribute(name = "xsi:type")
    public String getXsiType() {
        return xsiType;
    }

    public void setXsiType(String xsiType) {
        this.xsiType = xsiType;
    }
}

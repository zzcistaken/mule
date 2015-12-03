/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.extension.studio.model.element;

import org.mule.module.extension.studio.model.IEditorElementVisitor;
import org.mule.module.extension.studio.model.ModeType;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Group extends BaseChildEditorElement {

    private String id;
    private List<BaseChildEditorElement> childs;
    private UseMetaData useMetaData;
    private ModeType mode;
    private Boolean collapsed;
    private Boolean collapsable;
    private String topAnchor;
    private String bottomAnchor;

    @XmlElementRef
    public List<BaseChildEditorElement> getChilds() {
        if (childs == null) {
            childs = new ArrayList<BaseChildEditorElement>();
        }
        return childs;
    }

    public void setChilds(List<BaseChildEditorElement> childs) {
        this.childs = childs;
    }

    @XmlAttribute
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Group [id=" + id + ", childs=" + childs + ", getCaption()=" + getCaption() + "]";
    }

    @XmlElement
    public UseMetaData getUserMetaData() {
        return useMetaData;
    }

    public void setUserMetaData(UseMetaData userMetaData) {
        this.useMetaData = userMetaData;
    }

    @Override
    public void accept(IEditorElementVisitor visitor) {
        visitor.visit(this);
    }

    @XmlAttribute
    public ModeType getMode() {
        return mode;
    }

    public void setMode(ModeType mode) {
        this.mode = mode;
    }

    @XmlAttribute
    public Boolean getCollapsed() {
        return collapsed;
    }

    public void setCollapsed(Boolean collapsed) {
        this.collapsed = collapsed;
    }

    @XmlAttribute
    public Boolean getCollapsable() {
        return collapsable;
    }

    public void setCollapsable(Boolean collapsable) {
        this.collapsable = collapsable;
    }

    @XmlElement
    public UseMetaData getUseMetaData() {
        return useMetaData;
    }

    public void setUseMetaData(UseMetaData useMetaData) {
        this.useMetaData = useMetaData;
    }

    @XmlAttribute
    public String getTopAnchor() {
        return topAnchor;
    }

    public void setTopAnchor(String topAnchor) {
        this.topAnchor = topAnchor;
    }

    @XmlAttribute
    public String getBottomAnchor() {
        return bottomAnchor;
    }

    public void setBottomAnchor(String bottomAnchor) {
        this.bottomAnchor = bottomAnchor;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((bottomAnchor == null) ? 0 : bottomAnchor.hashCode());
        result = prime * result + ((childs == null) ? 0 : childs.hashCode());
        result = prime * result + ((collapsable == null) ? 0 : collapsable.hashCode());
        result = prime * result + ((collapsed == null) ? 0 : collapsed.hashCode());
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((mode == null) ? 0 : mode.hashCode());
        result = prime * result + ((topAnchor == null) ? 0 : topAnchor.hashCode());
        result = prime * result + ((useMetaData == null) ? 0 : useMetaData.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;
        Group other = (Group) obj;
        if (bottomAnchor == null) {
            if (other.bottomAnchor != null)
                return false;
        } else if (!bottomAnchor.equals(other.bottomAnchor))
            return false;
        if (childs == null) {
            if (other.childs != null)
                return false;
        } else if (!childs.equals(other.childs))
            return false;
        if (collapsable == null) {
            if (other.collapsable != null)
                return false;
        } else if (!collapsable.equals(other.collapsable))
            return false;
        if (collapsed == null) {
            if (other.collapsed != null)
                return false;
        } else if (!collapsed.equals(other.collapsed))
            return false;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        if (mode != other.mode)
            return false;
        if (topAnchor == null) {
            if (other.topAnchor != null)
                return false;
        } else if (!topAnchor.equals(other.topAnchor))
            return false;
        if (useMetaData == null) {
            if (other.useMetaData != null)
                return false;
        } else if (!useMetaData.equals(other.useMetaData))
            return false;
        return true;
    }

}

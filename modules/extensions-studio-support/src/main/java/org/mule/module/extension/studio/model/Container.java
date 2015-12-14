/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.extension.studio.model;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Container extends AbstractPaletteComponent
{

    private Boolean allowsAllExceptInbounds;
    private String location;
    private String acceptedByElements;
    private String acceptsElements;
    private Boolean inbound;
    private String elementMatcher;
    private String defaultNestedContainer;
    private Boolean allowMulipleChildren;
    private String containerBehavior;
    private Boolean visibleInPalette;
    private String displayNameAttribute;
    private String titleColor;
    private String returnType;
    private String pathExpression;
    private Boolean forcesResponse;
    private List<AbstractContainer> innerContainers;

    @XmlAttribute
    public String getReturnType()
    {
        return returnType;
    }

    public void setReturnType(String returnType)
    {
        this.returnType = returnType;
    }

    @XmlAttribute
    public String getLocation()
    {
        return location;
    }

    public void setLocation(String location)
    {
        this.location = location;
    }

    @XmlAttribute
    public String getAcceptedByElements()
    {
        return acceptedByElements;
    }

    public void setAcceptedByElements(String acceptedByElements)
    {
        this.acceptedByElements = acceptedByElements;
    }

    @XmlAttribute
    public String getAcceptsElements()
    {
        return acceptsElements;
    }

    public void setAcceptsElements(String acceptsElements)
    {
        this.acceptsElements = acceptsElements;
    }

    @XmlAttribute
    public Boolean getInbound()
    {
        return inbound;
    }

    public void setInbound(Boolean inbound)
    {
        this.inbound = inbound;
    }

    @XmlAttribute
    public String getElementMatcher()
    {
        return elementMatcher;
    }

    public void setElementMatcher(String elementMatcher)
    {
        this.elementMatcher = elementMatcher;
    }

    @XmlAttribute
    public String getDefaultNestedContainer()
    {
        return defaultNestedContainer;
    }

    public void setDefaultNestedContainer(String defaultNestedContainer)
    {
        this.defaultNestedContainer = defaultNestedContainer;
    }

    @XmlAttribute
    public Boolean getAllowMulipleChildren()
    {
        return allowMulipleChildren;
    }

    public void setAllowMulipleChildren(Boolean allowMulipleChildren)
    {
        this.allowMulipleChildren = allowMulipleChildren;
    }

    @XmlAttribute
    public String getContainerBehavior()
    {
        return containerBehavior;
    }

    public void setContainerBehavior(String containerBehavior)
    {
        this.containerBehavior = containerBehavior;
    }

    @XmlAttribute
    public Boolean getVisibleInPalette()
    {
        return visibleInPalette;
    }

    public void setVisibleInPalette(Boolean visibleInPalette)
    {
        this.visibleInPalette = visibleInPalette;
    }

    @XmlAttribute
    public String getDisplayNameAttribute()
    {
        return displayNameAttribute;
    }

    public void setDisplayNameAttribute(String displayNameAttribute)
    {
        this.displayNameAttribute = displayNameAttribute;
    }

    @XmlAttribute
    public String getTitleColor()
    {
        return titleColor;
    }

    public void setTitleColor(String titleColor)
    {
        this.titleColor = titleColor;
    }

    @XmlAttribute
    public Boolean getAllowsAllExceptInbounds()
    {
        return allowsAllExceptInbounds;
    }

    public void setAllowsAllExceptInbounds(Boolean allowsAllExceptInbounds)
    {
        this.allowsAllExceptInbounds = allowsAllExceptInbounds;
    }

    @Override
    public void accept(IEditorElementVisitor visitor)
    {
        visitor.visit(this);
    }

    @XmlAttribute
    public String getPathExpression()
    {
        return pathExpression;
    }

    public void setPathExpression(String pathExpression)
    {
        this.pathExpression = pathExpression;
    }


    @XmlAttribute
    public Boolean getForcesResponse()
    {
        return forcesResponse;
    }

    public void setForcesResponse(Boolean forcesResponse)
    {
        this.forcesResponse = forcesResponse;
    }

    @XmlElementRef
    public List<AbstractContainer> getInnerContainers()
    {
        if (innerContainers == null)
        {
            innerContainers = new ArrayList<AbstractContainer>();
        }
        return innerContainers;
    }

    public void setInnerContainers(List<AbstractContainer> innerContainers)
    {
        this.innerContainers = innerContainers;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (o == null || getClass() != o.getClass())
        {
            return false;
        }
        if (!super.equals(o))
        {
            return false;
        }

        Container container = (Container) o;

        if (allowsAllExceptInbounds != null ? !allowsAllExceptInbounds.equals(container.allowsAllExceptInbounds) : container.allowsAllExceptInbounds != null)
        {
            return false;
        }
        if (location != null ? !location.equals(container.location) : container.location != null)
        {
            return false;
        }
        if (acceptedByElements != null ? !acceptedByElements.equals(container.acceptedByElements) : container.acceptedByElements != null)
        {
            return false;
        }
        if (acceptsElements != null ? !acceptsElements.equals(container.acceptsElements) : container.acceptsElements != null)
        {
            return false;
        }
        if (inbound != null ? !inbound.equals(container.inbound) : container.inbound != null)
        {
            return false;
        }
        if (elementMatcher != null ? !elementMatcher.equals(container.elementMatcher) : container.elementMatcher != null)
        {
            return false;
        }
        if (defaultNestedContainer != null ? !defaultNestedContainer.equals(container.defaultNestedContainer) : container.defaultNestedContainer != null)
        {
            return false;
        }
        if (allowMulipleChildren != null ? !allowMulipleChildren.equals(container.allowMulipleChildren) : container.allowMulipleChildren != null)
        {
            return false;
        }
        if (containerBehavior != null ? !containerBehavior.equals(container.containerBehavior) : container.containerBehavior != null)
        {
            return false;
        }
        if (visibleInPalette != null ? !visibleInPalette.equals(container.visibleInPalette) : container.visibleInPalette != null)
        {
            return false;
        }
        if (displayNameAttribute != null ? !displayNameAttribute.equals(container.displayNameAttribute) : container.displayNameAttribute != null)
        {
            return false;
        }
        if (titleColor != null ? !titleColor.equals(container.titleColor) : container.titleColor != null)
        {
            return false;
        }
        if (returnType != null ? !returnType.equals(container.returnType) : container.returnType != null)
        {
            return false;
        }
        if (pathExpression != null ? !pathExpression.equals(container.pathExpression) : container.pathExpression != null)
        {
            return false;
        }
        if (forcesResponse != null ? !forcesResponse.equals(container.forcesResponse) : container.forcesResponse != null)
        {
            return false;
        }
        return !(innerContainers != null ? !innerContainers.equals(container.innerContainers) : container.innerContainers != null);

    }

    @Override
    public int hashCode()
    {
        int result = super.hashCode();
        result = 31 * result + (allowsAllExceptInbounds != null ? allowsAllExceptInbounds.hashCode() : 0);
        result = 31 * result + (location != null ? location.hashCode() : 0);
        result = 31 * result + (acceptedByElements != null ? acceptedByElements.hashCode() : 0);
        result = 31 * result + (acceptsElements != null ? acceptsElements.hashCode() : 0);
        result = 31 * result + (inbound != null ? inbound.hashCode() : 0);
        result = 31 * result + (elementMatcher != null ? elementMatcher.hashCode() : 0);
        result = 31 * result + (defaultNestedContainer != null ? defaultNestedContainer.hashCode() : 0);
        result = 31 * result + (allowMulipleChildren != null ? allowMulipleChildren.hashCode() : 0);
        result = 31 * result + (containerBehavior != null ? containerBehavior.hashCode() : 0);
        result = 31 * result + (visibleInPalette != null ? visibleInPalette.hashCode() : 0);
        result = 31 * result + (displayNameAttribute != null ? displayNameAttribute.hashCode() : 0);
        result = 31 * result + (titleColor != null ? titleColor.hashCode() : 0);
        result = 31 * result + (returnType != null ? returnType.hashCode() : 0);
        result = 31 * result + (pathExpression != null ? pathExpression.hashCode() : 0);
        result = 31 * result + (forcesResponse != null ? forcesResponse.hashCode() : 0);
        result = 31 * result + (innerContainers != null ? innerContainers.hashCode() : 0);
        return result;
    }
}

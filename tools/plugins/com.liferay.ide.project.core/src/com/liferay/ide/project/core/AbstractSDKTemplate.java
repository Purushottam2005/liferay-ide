/*******************************************************************************
 * Copyright (c) 2000-2013 Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 *******************************************************************************/

package com.liferay.ide.project.core;

import org.eclipse.wst.common.project.facet.core.IFacetedProjectTemplate;
import org.eclipse.wst.common.project.facet.core.IProjectFacet;
import org.eclipse.wst.common.project.facet.core.ProjectFacetsManager;

/**
 * @author Gregory Amerson
 */
public abstract class AbstractSDKTemplate implements ISDKTemplate
{

    protected String displayName;
    protected IFacetedProjectTemplate facetedProjectTemplate;
    protected String facetedProjectTemplateId;
    protected String facetId;
    protected int menuIndex;
    protected IProjectFacet projectFacet;
    protected String shortName;

    public AbstractSDKTemplate()
    {
        super();
    }

    public String getDisplayName()
    {
        return displayName;
    }

    public IProjectFacet getFacet()
    {
        return this.projectFacet;
    }

    public IFacetedProjectTemplate getFacetedProjectTemplate()
    {
        return facetedProjectTemplate;
    }

    public String getFacetedProjectTemplateId()
    {
        return facetedProjectTemplateId;
    }

    public String getFacetId()
    {
        return facetId;
    }

    public int getMenuIndex()
    {
        return menuIndex;
    }

    public String getShortName()
    {
        return shortName;
    }

    public void setDisplayName( String displayName )
    {
        this.displayName = displayName;
    }

    public void setFacetedProjectTemplateId( String facetedProjectTemplateId )
    {
        this.facetedProjectTemplateId = facetedProjectTemplateId;
        this.facetedProjectTemplate = ProjectFacetsManager.getTemplate( facetedProjectTemplateId );
    }

    public void setFacetId( String facetId )
    {
        this.facetId = facetId;
        this.projectFacet = ProjectFacetsManager.getProjectFacet( facetId );
    }

    public void setMenuIndex( int menuIndex )
    {
        this.menuIndex = menuIndex;
    }

    public void setShortName( String shortName )
    {
        this.shortName = shortName;
    }

}

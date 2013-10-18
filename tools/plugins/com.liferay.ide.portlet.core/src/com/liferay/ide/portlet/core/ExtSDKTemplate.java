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

package com.liferay.ide.portlet.core;

import com.liferay.ide.project.core.AbstractSDKTemplate;
import com.liferay.ide.project.core.facet.IPluginFacetConstants;
import com.liferay.ide.project.core.facet.IPluginProjectDataModelProperties;

import org.eclipse.jst.j2ee.project.facet.IJ2EEFacetConstants;
import org.eclipse.jst.j2ee.web.project.facet.IWebFacetInstallDataModelProperties;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.project.facet.core.IFacetedProjectWorkingCopy;

/**
 * @author Gregory Amerson
 */
public class ExtSDKTemplate extends AbstractSDKTemplate implements IPluginProjectDataModelProperties
{

    public ExtSDKTemplate()
    {
        super();
    }

    public void setupNewFacetedProject( IDataModel dataModel, IFacetedProjectWorkingCopy facetedProject )
    {
        FacetDataModelMap map = (FacetDataModelMap) dataModel.getProperty( FACET_DM_MAP );
        // IDataModel javaFacetModel = map.getFacetDataModel(
        // JavaFacetUtils.JAVA_FACET.getId() );
        IDataModel webFacetModel = map.getFacetDataModel( IJ2EEFacetConstants.DYNAMIC_WEB_FACET.getId() );
        webFacetModel.setStringProperty(
            IWebFacetInstallDataModelProperties.CONFIG_FOLDER, IPluginFacetConstants.EXT_PLUGIN_SDK_CONFIG_FOLDER );
    }

}

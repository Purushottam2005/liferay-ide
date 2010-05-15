/*******************************************************************************
 * Copyright (c) 2000-2010 Liferay, Inc. All rights reserved.
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

package com.liferay.ide.eclipse.project.core.facet;

import com.liferay.ide.eclipse.core.util.CoreUtil;
import com.liferay.ide.eclipse.core.util.FileListing;
import com.liferay.ide.eclipse.project.core.ProjectCorePlugin;
import com.liferay.ide.eclipse.project.core.util.ProjectUtil;
import com.liferay.ide.eclipse.project.core.util.WebXMLDescriptorHelper;
import com.liferay.ide.eclipse.sdk.ISDKConstants;
import com.liferay.ide.eclipse.sdk.SDK;
import com.liferay.ide.eclipse.sdk.SDKManager;
import com.liferay.ide.eclipse.server.util.ServerUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jst.common.project.facet.core.libprov.LibraryInstallDelegate;
import org.eclipse.jst.j2ee.jsp.JspFactory;
import org.eclipse.jst.j2ee.jsp.TagLibRefType;
import org.eclipse.jst.j2ee.project.facet.IJ2EEFacetConstants;
import org.eclipse.jst.j2ee.project.facet.IJ2EEModuleFacetInstallDataModelProperties;
import org.eclipse.ui.PlatformUI;
import org.eclipse.wst.common.componentcore.ComponentCore;
import org.eclipse.wst.common.componentcore.datamodel.FacetInstallDataModelProvider;
import org.eclipse.wst.common.componentcore.datamodel.properties.IFacetDataModelProperties;
import org.eclipse.wst.common.componentcore.datamodel.properties.IFacetProjectCreationDataModelProperties;
import org.eclipse.wst.common.componentcore.internal.util.IModuleConstants;
import org.eclipse.wst.common.componentcore.resources.IVirtualComponent;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.project.facet.core.IDelegate;
import org.eclipse.wst.common.project.facet.core.IFacetedProjectWorkingCopy;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;
import org.eclipse.wst.common.project.facet.core.IFacetedProject.Action;
import org.eclipse.wst.common.project.facet.core.runtime.IRuntime;
import org.osgi.service.prefs.BackingStoreException;

/**
 * @author Greg Amerson
 */
public class PluginFacetInstall implements IDelegate, IPluginProjectDataModelProperties {

	protected IDataModel masterModel = null;
	
	protected IDataModel model = null;
	
	protected IProgressMonitor monitor;
	
	protected IProject project;

	public void execute(IProject project, IProjectFacetVersion fv, Object config, IProgressMonitor monitor)
		throws CoreException {
		
		if (!(config instanceof IDataModel)) {
			return;
		}
		else {
			this.model = (IDataModel) config;
			
			this.masterModel = (IDataModel) this.model.getProperty(FacetInstallDataModelProvider.MASTER_PROJECT_DM);
			
			this.project = project;
			
			this.monitor = monitor;
		}

		installPluginLibraryDelegate();

		// if (masterModel.getBooleanProperty(PLUGIN_TYPE_THEME)) {
		// installThemeTemplate();
		// } else if
		// (masterModel.getBooleanProperty(PLUGIN_TYPE_LAYOUT_TEMPLATE)) {
		// installLayoutTplTemplate();
		// }

		// need to save SDK path to facet property
		try {
			ProjectUtil.setSDK(project, fv.getProjectFacet(), getSDK());
		}
		catch (BackingStoreException e) {
			ProjectCorePlugin.logError(e);
		}
	}

	private boolean isProjectInSDK() {
		return masterModel.getBooleanProperty(LIFERAY_USE_SDK_LOCATION);
	}

	protected void configWebXML() {
		WebXMLDescriptorHelper webXmlHelper = new WebXMLDescriptorHelper(this.project);
		
		TagLibRefType tagLibRefType = JspFactory.eINSTANCE.createTagLibRefType();
		
		tagLibRefType.setTaglibURI("http://java.sun.com/portlet_2_0");
		tagLibRefType.setTaglibLocation("/WEB-INF/tld/liferay-portlet.tld");
		
		webXmlHelper.addTagLib(tagLibRefType);
	}

	// protected void extractZipToProject(File zipFile) {
	// try {
	// ZipFile zip = ZipUtil.open(zipFile);
	// Enumeration<? extends ZipEntry> entries = zip.entries();
	// while (entries.hasMoreElements()) {
	// copyToProject(zip, entries.nextElement());
	// }
	// } catch (Exception e) {
	// ProjectCorePlugin.logError(e);
	// }
	// }
	//
	protected void copyToProject(IPath parent, File newFile, boolean prompt)
		throws CoreException, IOException {
		
		if (newFile == null || !shouldCopyToProject(newFile)) {
			return;
		}

		IResource projectEntry = null;
		
		IPath newFilePath = new Path(newFile.getPath());
		
		IPath newFileRelativePath = newFilePath.makeRelativeTo(parent);
		
		if (newFile.isDirectory()) {
			projectEntry = this.project.getFolder(newFileRelativePath);
		}
		else {
			projectEntry = this.project.getFile(newFileRelativePath);
		}
		
		if (projectEntry.exists()) {
			if (projectEntry instanceof IFolder) {
				// folder already exists, we can return
				return;
			}
			else if (projectEntry instanceof IFile) {
				if (prompt && !promptForOverwrite(projectEntry)) {
					return;
				}
				
				((IFile) projectEntry).setContents(new FileInputStream(newFile), IResource.FORCE, null);
			}
		}
		else if (projectEntry instanceof IFolder) {
			IFolder newProjectFolder = (IFolder) projectEntry;
			
			newProjectFolder.create(true, true, null);
		}
		else if (projectEntry instanceof IFile) {
			((IFile) projectEntry).create(new FileInputStream(newFile), IResource.FORCE, null);
		}
		
		// if (projectEntry instanceof IFile) {
		// String contents =
		// CoreUtil.readStreamToString(((IFile)projectEntry).getContents(true));
		// for (String token : ISDKConstants.PORTLET_PLUGIN_ZIP_REPLACE_TOKENS)
		// {
		// contents = contents.replaceAll(token, this.project.getName());
		// }
		// ((IFile)projectEntry).setContents(new
		// ByteArrayInputStream(contents.getBytes("UTF-8")),
		// IResource.FORCE,
		// null);
		// }
	}

	protected boolean deletePath(IPath path) {
		if (path != null && path.toFile().exists()) {
			return path.toFile().delete();
		}
		
		return false;
	}

	protected IDataModel getFacetDataModel(String facetId) {
		IFacetedProjectWorkingCopy fp = getFacetedProject();
		
		for (IProjectFacetVersion pfv : fp.getProjectFacets()) {
			if (pfv.getProjectFacet().getId().equals(facetId)) {
				Action action = fp.getProjectFacetAction(pfv.getProjectFacet());
				
				if (action != null) {
					Object config = action.getConfig();
					
					return (IDataModel) Platform.getAdapterManager().getAdapter(config, IDataModel.class);
				}
			}
		}
		
		return null;
	}

	protected IFacetedProjectWorkingCopy getFacetedProject() {
		return (IFacetedProjectWorkingCopy) this.model.getProperty(IFacetDataModelProperties.FACETED_PROJECT_WORKING_COPY);
	}

	// protected void copyTLDsFromPortal() throws CoreException {
	// IPath portalTLDFolder = getPortalRoot().append("WEB-INF/tld");
	// IFolder tldFolder = getWebRootFolder().getFolder("WEB-INF/tld");
	// if (!tldFolder.exists()) {
	// tldFolder.create(true, true, null);
	// }
	// for (String tldFile : ISDKConstants.PORTLET_PLUGIN_TLD_FILES) {
	// IPath portalTLDFilePath = portalTLDFolder.append(tldFile);
	// if (portalTLDFilePath.toFile().exists()) {
	// IFile projectTLDFile = tldFolder.getFile(tldFile);
	// if (!projectTLDFile.exists()) {
	// try {
	// projectTLDFile.create(new FileInputStream(portalTLDFilePath.toFile()),
	// true, null);
	// } catch (FileNotFoundException e) {
	// ProjectCorePlugin.logError(e);
	// }
	// }
	// }
	// }
	// }

	protected IPath getPortalRoot() {
		IRuntime serverRuntime;
		
		if (masterModel != null) {
			serverRuntime = (IRuntime) masterModel.getProperty(PluginFacetInstallDataModelProvider.FACET_RUNTIME);
		}
		else {
			serverRuntime = getFacetedProject().getPrimaryRuntime();
		}
		
		return ServerUtil.getPortalRoot(serverRuntime);
	}

	protected String getRuntimeLocation() {
		try {
			return ServerUtil.getRuntime(this.project).getLocation().toOSString();
		}
		catch (CoreException e) {
			e.printStackTrace();
		}
		
		return null;
	}

	protected SDK getSDK() {
		String sdkName = null;
		
		try {
			sdkName = masterModel.getStringProperty(IPluginProjectDataModelProperties.LIFERAY_SDK_NAME);
		}
		catch (Exception ex) {
		}
		
		if (sdkName == null) {
			try {
				sdkName = model.getStringProperty(IPluginProjectDataModelProperties.LIFERAY_SDK_NAME);
			}
			catch (Exception ex) {
			}
		}
		
		return SDKManager.getSDKByName(sdkName);
	}

	protected IFolder getWebRootFolder() {
		IDataModel webFacetDataModel = null;
		
		if (masterModel != null) {
			FacetDataModelMap map =
				(FacetDataModelMap) masterModel.getProperty(IFacetProjectCreationDataModelProperties.FACET_DM_MAP);
			
			webFacetDataModel = map.getFacetDataModel(IJ2EEFacetConstants.DYNAMIC_WEB_FACET.getId());
		}
		else {
			webFacetDataModel = getFacetDataModel(IModuleConstants.JST_WEB_MODULE);
		}

		IPath webrootFullPath = null;
		
		if (webFacetDataModel != null) {
			webrootFullPath =
				this.project.getFullPath().append(
					webFacetDataModel.getStringProperty(IJ2EEModuleFacetInstallDataModelProperties.CONFIG_FOLDER));
		}
		else {
			IVirtualComponent component = ComponentCore.createComponent(this.project);
			if (component != null) {
				webrootFullPath = component.getRootFolder().getUnderlyingFolder().getFullPath();
			}
		}
		
		return ResourcesPlugin.getWorkspace().getRoot().getFolder(webrootFullPath);
	}

	protected void installLayoutTplTemplate()
		throws CoreException {
		
		// get the template zip for portlets and extract into the project
		SDK sdk = getSDK();

		String layouttplName = this.masterModel.getStringProperty(LAYOUT_TEMPLATE_NAME);
		
		IPath newThemePath = sdk.createNewLayoutTemplate(layouttplName, layouttplName, layouttplName);
		
		processNewFiles(newThemePath.append(layouttplName + "-layouttpl"), false);
		// cleanup portlet files
		newThemePath.toFile().delete();

		this.project.refreshLocal(IResource.DEPTH_INFINITE, monitor);
	}

	protected void installPluginLibraryDelegate()
		throws CoreException {
		LibraryInstallDelegate libraryDelegate =
			(LibraryInstallDelegate) this.model.getProperty(IPluginProjectDataModelProperties.LIFERAY_PLUGIN_LIBRARY_DELEGATE);
		
		libraryDelegate.execute(monitor);
	}

	protected void installThemeTemplate()
		throws CoreException {
		// get the template zip for portlets and extract into the project
		SDK sdk = getSDK();

		String themeName = this.masterModel.getStringProperty(THEME_NAME);
		
		IPath newThemePath = sdk.createNewTheme(themeName, themeName);
		
		processNewFiles(newThemePath.append(themeName + "-theme"), false);
		
		// cleanup portlet files
		newThemePath.toFile().delete();

		this.project.refreshLocal(IResource.DEPTH_INFINITE, monitor);
	}

	protected void processNewFiles(IPath path, boolean prompt)
		throws CoreException {
		
		try {
			List<File> newFiles = FileListing.getFileListing(path.toFile());
			
			for (File file : newFiles) {
				try {
					copyToProject(path, file, prompt);
				}
				catch (Exception e) {
					ProjectCorePlugin.logError(e);
				}
			}
		}
		catch (FileNotFoundException e1) {
			throw new CoreException(ProjectCorePlugin.createErrorStatus(e1));
		}
	}

	protected boolean promptForOverwrite(final IResource projectEntryPath) {
		
		final boolean[] retval = new boolean[1];
		
		PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {

			public void run() {
				retval[0] =
					MessageDialog.openQuestion(
						PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), "Overwrite project file?",
						"Overwrite project file: " + projectEntryPath.getLocation());
			}
			
		});
		
		return retval[0];
	}

	protected void setupDefaultOutputLocation()
		throws CoreException {
		
		IJavaProject jProject = JavaCore.create(this.project);
		
		IFolder folder = this.project.getFolder(IPluginFacetConstants.PORTLET_PLUGIN_SDK_DEFAULT_OUTPUT_FOLDER);
		
		if (folder.getParent().exists()) {
			CoreUtil.prepareFolder(folder);
			
			IPath oldOutputLocation = jProject.getOutputLocation();
			
			IFolder oldOutputFolder = CoreUtil.getWorkspaceRoot().getFolder(oldOutputLocation);
			
			jProject.setOutputLocation(folder.getFullPath(), null);
			
			if (oldOutputFolder.exists()) {
				oldOutputFolder.delete(true, null);
			}
		}
	}

	protected boolean shouldCopyToProject(File file) {
		if (isProjectInSDK()) {
			return true;
		}

		for (String name : ISDKConstants.PORTLET_PLUGIN_ZIP_IGNORE_FILES) {
			if (file.getName().equals(name)) {
				return false;
			}
		}
		
		return true;
	}

	// protected void configWebXML() {
	// WebArtifactEdit webArtifactEdit =
	// WebArtifactEdit.getWebArtifactEditForWrite(this.project);
	// int j2eeVersion = webArtifactEdit.getJ2EEVersion();
	// WebApp webApp = webArtifactEdit.getWebApp();
	// webApp.setFileList(null);
	// JSPConfig jspConfig = webApp.getJspConfig();
	// if (jspConfig == null && webApp.getVersionID() != 23) {
	// jspConfig = JspFactory.eINSTANCE.createJSPConfig();
	// }
	// TagLibRefType tagLibRefType = JspFactory.eINSTANCE.createTagLibRefType();
	// tagLibRefType.setTaglibURI("http://java.sun.com/portlet_2_0");
	// tagLibRefType.setTaglibLocation("/WEB-INF/tld/liferay-portlet.tld");
	// if (jspConfig != null) {
	// jspConfig.getTagLibs().add(tagLibRefType);
	// } else {
	// EList tagLibs = webApp.getTagLibs();
	// tagLibs.add(tagLibRefType);
	// }
	// if (jspConfig != null) {
	// webApp.setJspConfig(jspConfig);
	// }
	// webArtifactEdit.saveIfNecessary(null);
	// webArtifactEdit.dispose();
	// }
}
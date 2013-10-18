
package com.liferay.ide.project.core.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.core.util.ZipUtil;
import com.liferay.ide.project.core.LiferayProjectCore;
import com.liferay.ide.project.core.model.NewLiferayPluginProjectOp;
import com.liferay.ide.project.core.model.PluginType;
import com.liferay.ide.sdk.core.SDK;
import com.liferay.ide.sdk.core.SDKManager;
import com.liferay.ide.sdk.core.SDKUtil;
import com.liferay.ide.server.tomcat.core.ILiferayTomcatRuntime;

import java.io.File;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.wst.common.componentcore.resources.IVirtualFile;
import org.eclipse.wst.common.componentcore.resources.IVirtualFolder;
import org.eclipse.wst.server.core.IRuntimeWorkingCopy;
import org.eclipse.wst.server.core.ServerCore;
import org.junit.Before;
import org.junit.Test;

public class PluginProjectCoreTests extends ProjectCoreBaseTests
{
    private static final String runtimeId = "com.liferay.ide.eclipse.server.tomcat.runtime.70";

    private static final String runtimeName = "6.2.0";

    final static IPath liferayPluginsSdkDir = LiferayProjectCore.getDefault().getStateLocation().append(
        "liferay-plugins-sdk-6.2.0" );

    final static IPath liferayRuntimeDir = LiferayProjectCore.getDefault().getStateLocation().append(
        "liferay-portal-6.2.0-ce-rc2" );

    final static IPath tempDownloadsPath = new Path( System.getProperty(
        "liferay.plugin.project.tests.tempdir", System.getProperty( "java.io.tmpdir" ) ) );

    final static IPath liferayPluginsSDKZip =
        tempDownloadsPath.append( "liferay-plugins-sdk-6.2.0-ce-rc2-with-ivy-cache.zip" );

    final static IPath liferayRuntimeZip =
        tempDownloadsPath.append( "liferay-portal-tomcat-6.2.0-ce-rc2-20130926150829138.zip" );

    final static String liferayPluginsSDKZipUrl =
        "http://vm-32.liferay.com/files/liferay-plugins-sdk-6.2.0-ce-rc2-with-ivy-cache.zip";

    final static String liferayRuntimeZipUrl =
        "http://vm-32.liferay.com/files/liferay-portal-tomcat-6.2.0-ce-rc2-20130926150829138.zip";

    // setup Plugins SDK to test with

    /**
     * @throws Exception
     */
    @Before
    public void setupPluginsSDKAndRuntime() throws Exception
    {
        final File liferayPluginsSdkDirFile = liferayPluginsSdkDir.toFile();

        if( ! liferayPluginsSdkDirFile.exists() )
        {
            final File liferayPluginsSDKZipFile = liferayPluginsSDKZip.toFile();

            if( ! liferayPluginsSDKZipFile.exists() )
            {
                FileUtil.downloadFile( liferayPluginsSDKZipUrl, liferayPluginsSDKZip.toFile() );
            }

            assertEquals( true, liferayPluginsSDKZipFile.exists() );

            ZipUtil.unzip(
                liferayPluginsSDKZipFile, LiferayProjectCore.getDefault().getStateLocation().toFile() );
        }

        assertEquals( true, liferayPluginsSdkDirFile.exists() );

        final SDK newSdk = SDKUtil.createSDKFromLocation( liferayPluginsSdkDir );

        assertNotNull( newSdk );

        newSdk.setDefault( true );

        final SDKManager sdkManager = SDKManager.getInstance();
        sdkManager.addSDK( newSdk );

        final File liferayRuntimeDirFile = liferayRuntimeDir.toFile();

        if( ! liferayRuntimeDirFile.exists() )
        {
            final File liferayRuntimeZipFile = liferayRuntimeZip.toFile();

            if( ! liferayRuntimeZipFile.exists() )
            {
                FileUtil.downloadFile( liferayRuntimeZipUrl, liferayRuntimeZip.toFile() );
            }

            assertEquals( true, liferayRuntimeZipFile.exists() );

            ZipUtil.unzip(
                liferayRuntimeZipFile, LiferayProjectCore.getDefault().getStateLocation().toFile() );
        }

        assertEquals( true, liferayRuntimeDirFile.exists() );

        final NullProgressMonitor npm = new NullProgressMonitor();

        final IRuntimeWorkingCopy runtimeWC =
            ServerCore.findRuntimeType( runtimeId ).createRuntime( runtimeName, npm );

        runtimeWC.setName( runtimeName );
        runtimeWC.setLocation( liferayRuntimeDir );

        runtimeWC.save( true, npm );

        final ILiferayTomcatRuntime liferayRuntime =
            (ILiferayTomcatRuntime) ServerCore.findRuntime( runtimeName ).loadAdapter( ILiferayTomcatRuntime.class, npm );

        assertNotNull( liferayRuntime );
    }

    @Test
    public void testCreateNewIvyProjects() throws Exception
    {
        // TODO finish ivy tests
    }

    @Test
    public void testCreateNewSDKProjectInEclipseWorkspace() throws Exception
    {
        // TODO finish this test
    }

    @Test
    public void testCreateNewSDKProjects() throws Exception
    {
        createAntProjectName( "test-name-1" );
        createAntProjectName( "Test With Spaces" );
        createAntProjectName( "test_name_1" );
    }

    @Test
    public void testCreateNewHookAntProject() throws Exception
    {
        final String projectName = "test-hook-project-sdk";
        final NewLiferayPluginProjectOp op = NewLiferayPluginProjectOp.TYPE.instantiate();
        op.setProjectName( projectName );
        op.setPluginType( PluginType.hook );

        IProject hookProject = createAntProject( op );

        final IVirtualFolder webappRoot = CoreUtil.getDocroot( hookProject );

        assertNotNull( webappRoot );

        final IVirtualFile hookXml = webappRoot.getFile( "WEB-INF/liferay-hook.xml" );

        assertEquals( true, hookXml.exists() );
    }

    @Test
    public void testCreateNewLayoutAntProject() throws Exception
    {
        final String projectName = "test-layouttpl-project-sdk";
        final NewLiferayPluginProjectOp op = NewLiferayPluginProjectOp.TYPE.instantiate();
        op.setProjectName( projectName );
        op.setPluginType( PluginType.layouttpl );

        IProject layouttplProject = createAntProject( op );

        final IVirtualFolder webappRoot = CoreUtil.getDocroot( layouttplProject );

        assertNotNull( webappRoot );

        final IVirtualFile layoutXml = webappRoot.getFile( "WEB-INF/liferay-layout-templates.xml" );

        assertEquals( true, layoutXml.exists() );
    }

    protected void createNewThemeAntProject(String themeParent, String themeFramework) throws Exception
    {
        final String projectName = "test-theme-project-sdk-" + themeParent + "-" + themeFramework;
        final NewLiferayPluginProjectOp op = NewLiferayPluginProjectOp.TYPE.instantiate();
        op.setProjectName( projectName );
        op.setPluginType( PluginType.theme );
        op.setThemeParent( themeParent );
        op.setThemeFramework( themeFramework );

        IProject themeProject = createAntProject( op );

        final IVirtualFolder webappRoot = CoreUtil.getDocroot( themeProject );

        assertNotNull( webappRoot );

        final IVirtualFile readme = webappRoot.getFile( "WEB-INF/src/resources-importer/readme.txt" );

        assertEquals( true, readme.exists() );

        final IFile buildXml = themeProject.getFile( "build.xml" );

        final String buildXmlContent = CoreUtil.readStreamToString( buildXml.getContents() );

        final String expectedbuildXmlContent =
            CoreUtil.readStreamToString( this.getClass().getResourceAsStream(
                "files/build-theme-" + themeParent + "-" + themeFramework + ".xml" ) );

        assertEquals(
            stripCarriageReturns( expectedbuildXmlContent ), stripCarriageReturns( buildXmlContent ) );
    }

    @Test
    public void testCreateNewThemeProjects() throws Exception
    {
        createNewThemeAntProject( "_unstyled", "Velocity" );
        createNewThemeAntProject( "_unstyled", "Freemarker" );
        createNewThemeAntProject( "_unstyled", "JSP" );
        createNewThemeAntProject( "_styled", "Velocity" );
        createNewThemeAntProject( "_styled", "Freemarker" );
        createNewThemeAntProject( "_styled", "JSP" );
        createNewThemeAntProject( "classic", "Velocity" );
        createNewThemeAntProject( "classic", "Freemarker" );
        createNewThemeAntProject( "classic", "JSP" );
    }

    @Test
    public void testCreateNewJsfAntProjects() throws Exception
    {
        createNewJsfAntProject( "jsf" );
        createNewJsfAntProject( "liferay_faces_alloy" );
        createNewJsfAntProject( "icefaces" );
        createNewJsfAntProject( "primefaces" );
        createNewJsfAntProject( "richfaces" );
    }

    @Test
    public void testCreateNewVaadinAntProject() throws Exception
    {
        final String projectName = "test-vaadin-project-sdk";
        final NewLiferayPluginProjectOp op = NewLiferayPluginProjectOp.TYPE.instantiate();
        op.setProjectName( projectName );
        op.setPluginType( PluginType.portlet );
        op.setPortletFramework( "vaadin" );

        IProject vaadinProject = createAntProject( op );

        final IVirtualFolder webappRoot = CoreUtil.getDocroot( vaadinProject );

        assertNotNull( webappRoot );

        final IVirtualFile application =
            webappRoot.getFile( "WEB-INF/src/testvaadinprojectsdk/TestVaadinProjectSdkApplication.java" );

        assertEquals( true, application.exists() );
    }

    protected IProject createAntProject( NewLiferayPluginProjectOp op ) throws Exception
    {
        IProject project = createProject( op );

        assertEquals( false, project.getFolder( "src" ).exists() );

        return project;
    }

    protected void createNewJsfAntProject( String jsfSuite ) throws Exception
    {
        final String projectName = "test-" + jsfSuite +  "-sdk-project";
        final NewLiferayPluginProjectOp op = NewLiferayPluginProjectOp.TYPE.instantiate();
        op.setProjectName( projectName );
        op.setPortletFramework( "jsf-2.x" );
        op.setPortletFrameworkAdvanced( jsfSuite );

        IProject jsfStandardProject = createAntProject( op );

        final IVirtualFolder webappRoot = CoreUtil.getDocroot( jsfStandardProject );

        assertNotNull( webappRoot );

        final IVirtualFile config = webappRoot.getFile( "WEB-INF/faces-config.xml" );

        assertEquals( true, config.exists() );

        final IFile ivyXml = jsfStandardProject.getFile( "ivy.xml" );

        final String ivyXmlContent = CoreUtil.readStreamToString( ivyXml.getContents() );

        final String expectedIvyXmlContent =
            CoreUtil.readStreamToString( this.getClass().getResourceAsStream( "files/ivy-" + jsfSuite + ".xml" ) );

        assertEquals(
            stripCarriageReturns( expectedIvyXmlContent ), stripCarriageReturns( ivyXmlContent ) );
    }

    //TODO re-enable Ext test
//    @Test
//    public void testCreateNewExtAntProject() throws Exception
//    {
//        final String projectName = "test-ext-project-sdk";
//        final NewLiferayPluginProjectOp op = NewLiferayPluginProjectOp.TYPE.instantiate();
//        op.setProjectName( projectName );
//        op.setPluginType( PluginType.ext );
//
//        IProject extProject = createAntProject( op );
//
//        final IVirtualFolder webappRoot = CoreUtil.getDocroot( extProject );
//
//        assertNotNull( webappRoot );
//
//        final IVirtualFile extFile = webappRoot.getFile( "WEB-INF/liferay-portlet-ext.xml" );
//
//        assertEquals( true, extFile.exists() );
//    }

    protected void createAntProjectName( final String projectName ) throws Exception
    {
        final NewLiferayPluginProjectOp op = NewLiferayPluginProjectOp.TYPE.instantiate();
        op.setProjectName( projectName );
        op.setProjectProvider( "ant" );

        createAntProject( op );
    }



}

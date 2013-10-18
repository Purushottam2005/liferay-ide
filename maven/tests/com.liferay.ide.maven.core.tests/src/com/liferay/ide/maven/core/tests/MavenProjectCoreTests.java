
package com.liferay.ide.maven.core.tests;

import static org.junit.Assert.assertEquals;

import com.liferay.ide.project.core.model.NewLiferayPluginProjectOp;
import com.liferay.ide.project.core.tests.ProjectCoreBaseTests;

import org.eclipse.core.resources.IProject;
import org.junit.Test;

public class MavenProjectCoreTests extends ProjectCoreBaseTests
{
    @Test
    public void testCreateNewxMavenProject() throws Exception
    {
        createMavenProjectName( "test-name-1" );
        createMavenProjectName( "Test With Spaces" );
        createMavenProjectName( "test_name_1" );
    }

    protected void createMavenProjectName( final String projectName ) throws Exception
    {
        final NewLiferayPluginProjectOp op = NewLiferayPluginProjectOp.TYPE.instantiate();
        op.setProjectName( projectName );
        op.setProjectProvider( "maven" );

        createMavenProject( op );
    }

    protected IProject createMavenProject( NewLiferayPluginProjectOp op ) throws Exception
    {
        IProject project = createProject( op );

        assertEquals( true, project.getFolder( "src" ).exists() );

        return project;
    }
}

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

package com.liferay.ide.core.tests;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.modeling.xml.RootXmlResource;
import org.eclipse.sapphire.modeling.xml.XmlResourceStore;

/**
 * @author Gregory Amerson
 */
public class BaseTests
{

    protected final IFile createFile( final IProject project, final String path ) throws Exception
    {
        return createFile( project, path, new byte[0] );
    }

    protected final IFile createFile( final IProject project, final String path, final byte[] content ) throws Exception
    {
        return createFile( project, path, new ByteArrayInputStream( content ) );
    }

    protected final IFile createFile( final IProject project, final String path, final InputStream content ) throws Exception
    {
        final IFile file = project.getFile( path );
        final IContainer parent = file.getParent();

        if( parent instanceof IFolder )
        {
            createFolder( (IFolder) parent );
        }

        file.create( content, true, null );

        return file;
    }

    protected final void createFolder( final IFolder folder ) throws Exception
    {
        if( !folder.exists() )
        {
            final IContainer parent = folder.getParent();

            if( parent instanceof IFolder )
            {
                createFolder( (IFolder) parent );
            }

            folder.create( true, true, null );
        }
    }

    protected final IFolder createFolder( final IProject project, final String path ) throws Exception
    {
        final IFolder folder = project.getFolder( path );
        createFolder( folder );
        return folder;
    }

    protected final IProject createProject( final String name ) throws Exception
    {
        String n = getClass().getName();

        if( name != null )
        {
            n = n + "." + name;
        }

        final IProject p = ResourcesPlugin.getWorkspace().getRoot().getProject( n );
        p.create( null );
        p.open( null );

        return p;
    }

    protected final void deleteProject( final String name ) throws Exception
    {
        String n = getClass().getName();

        if( name != null )
        {
            n = n + "." + name;
        }

        final IProject p = ResourcesPlugin.getWorkspace().getRoot().getProject( n );

        if( p.exists() )
        {
            p.delete( true, null );
        }
    }

    protected Element getElementFromFile( IProject project, IPath filePath, ElementType type ) throws Exception
    {
        final String filePathValue = filePath.toOSString();
        final IFile file = createFile( project, filePathValue, this.getClass().getResourceAsStream( filePathValue ) );

        assertEquals( file.getFullPath().lastSegment(), filePath.lastSegment() );

        final InputStream contents = file.getContents();
        final Element element = type.instantiate( new RootXmlResource( new XmlResourceStore( contents ) ) );

        contents.close();

        return element;
    }

    protected IProject project( final String name )
    {
        return workspaceRoot().getProject( name );
    }

    protected String stripCarriageReturns( String value )
    {
        return value.replaceAll( "\r", "" );
    }

    protected IWorkspace workspace()
    {
        return ResourcesPlugin.getWorkspace();
    }

    protected IWorkspaceRoot workspaceRoot()
    {
        return workspace().getRoot();
    }

}


package com.liferay.ide.portal.core.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import com.liferay.ide.core.tests.BaseTests;
import com.liferay.ide.portal.core.structures.model.DynamicElement;
import com.liferay.ide.portal.core.structures.model.DynamicElementMetadata;
import com.liferay.ide.portal.core.structures.model.Entry;
import com.liferay.ide.portal.core.structures.model.Root;
import com.liferay.ide.portal.core.structures.model.Structure;
import com.liferay.ide.portal.core.structures.model.StructureRoot;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.ElementList;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Gregory Amerson
 */
public class PortalCoreTests extends BaseTests
{

    private static final IPath DDM_STRUCTURE_BASIC_DOCUMENT = new Path( "structures/ddm_structure_basic_document.xml" );
    private static final IPath DDM_STRUCTURE_DDL = new Path( "structures/ddm_structure_ddl.xml" );
    private static final IPath DDMSTRUCTURE = new Path( "structures/ddmstructure.xml" );
    private static final IPath DOCUMENT_LIBRARY_STRUCTURES = new Path( "structures/document-library-structures.xml" );
    private static final IPath DYNAMIC_DATA_MAPPING_STRUCTURES = new Path(
        "structures/dynamic-data-mapping-structures.xml" );
    private static final IPath TEST_DDM_STRUCTURE_ALL_FIELDS =
        new Path( "structures/test-ddm-structure-all-fields.xml" );
    private static final IPath TEST_JOURNAL_CONTENT_BOOLEAN_REPEATABLE_FIELD = new Path(
        "structures/test-journal-content-boolean-repeatable-field.xml" );
    private static final IPath TEST_JOURNAL_CONTENT_DOC_LIBRARY_FIELD = new Path(
        "structures/test-journal-content-doc-library-field.xml" );
    private static final IPath TEST_JOURNAL_CONTENT_LINK_TO_PAGE_FIELD = new Path(
        "structures/test-journal-content-link-to-page-field.xml" );
    private static final IPath TEST_JOURNAL_CONTENT_LIST_FIELD = new Path(
        "structures/test-journal-content-list-field.xml" );
    private static final IPath TEST_JOURNAL_CONTENT_MULTI_LIST_FIELD = new Path(
        "structures/test-journal-content-multi-list-field.xml" );
    private static final IPath TEST_JOURNAL_CONTENT_NESTED_FIELDS = new Path(
        "structures/test-journal-content-nested-fields.xml" );
    private static final IPath TEST_JOURNAL_CONTENT_TEXT_AREA_FIELD = new Path(
        "structures/test-journal-content-text-area-field.xml" );
    private static final IPath TEST_JOURNAL_CONTENT_TEXT_BOX_REPEATABLE_FIELD = new Path(
        "structures/test-journal-content-text-box-repeatable-field.xml" );
    private static final IPath TEST_JOURNAL_CONTENT_TEXT_FIELD = new Path(
        "structures/test-journal-content-text-field.xml" );

    private IProject a;
    private Element currentElement;

    @Before
    public void createTestProject() throws Exception
    {
        this.a = createProject( "a" );
    }

    @After
    public void cleanup() throws Exception
    {
        if( this.currentElement != null )
        {
            if( ! this.currentElement.disposed() )
            {
                this.currentElement.dispose();
            }

            this.currentElement = null;
        }

        deleteProject( "a" );
    }

    @Test
    public void testDDMStructureBasicDocumentRead() throws Exception
    {
        final Element element = getElementFromFile( this.a, DDM_STRUCTURE_BASIC_DOCUMENT, StructureRoot.TYPE );

        setElement( element );

        final StructureRoot root = element.nearest( StructureRoot.class );

        assertNotNull( root );

        assertEquals( "en_US", root.getAvailableLocales().content() );

        assertEquals( "en_US", root.getDefaultLocale().content() );

        final DynamicElement dynamicElement = root.getDynamicElements().get( 1 );

        assertNotNull( dynamicElement );

        assertEquals( "string", dynamicElement.getDataType().content( false ) );

        assertEquals( "ClimateForcast_COMMAND_LINE", dynamicElement.getName().content( false ) );

        assertEquals( "text", dynamicElement.getType().content( false ) );

        final DynamicElementMetadata metaData = dynamicElement.getMetadata().content( false );

        assertNotNull( metaData );

        assertEquals( "en_US", metaData.getLocale().content( false ) );

        final ElementList<Entry> entries = metaData.getEntries();

        assertNotNull( entries );

        assertEquals( 4, entries.size() );

        final Entry entry = entries.get( 2 );

        assertNotNull( entry );

        assertEquals( "required", entry.getName().content( false ) );

        assertEquals( "false", entry.getValue().content( false ) );
    }

    private void setElement( Element element )
    {
        assertNotNull( element );

        this.currentElement = element;
    }

    @Test
    public void testDocumentLibraryStructuresRead() throws Exception
    {
        final Element element = getElementFromFile( this.a, DOCUMENT_LIBRARY_STRUCTURES, Root.TYPE );

        setElement( element );

        final Root root = element.nearest( Root.class );

        assertNotNull( root );

        final ElementList<Structure> structures = root.getStructures();

        assertNotNull( structures );
        assertEquals( 8, structures.size() );

        final Structure structure = structures.get( 2 );

        assertNotNull( structure );
        assertEquals( "Learning Module Metadata", structure.getName().content( false ) );
        assertEquals( "Learning Module Metadata", structure.getDescription().content( false ) );

        final StructureRoot structureRoot = structure.getRoot().content( false );

        assertNotNull( structureRoot );
        assertEquals( "[$LOCALE_DEFAULT$]", structureRoot.getAvailableLocales().content( false ) );
        assertEquals( "[$LOCALE_DEFAULT$]", structureRoot.getDefaultLocale().content( false ) );

        final ElementList<DynamicElement> dynamicElements = structureRoot.getDynamicElements();

        assertNotNull( dynamicElements );
        assertEquals( 4, dynamicElements.size() );

        final DynamicElement dynamicElement = dynamicElements.get( 1 );

        assertNotNull( dynamicElement );
        assertEquals( "string", dynamicElement.getDataType().content( false ) );
        assertEquals( "keyword", dynamicElement.getIndexType().content( false ) );
        assertEquals( true, dynamicElement.isMultiple().content( false ) );
        assertEquals( "select3212", dynamicElement.getName().content( false ) );
        assertEquals( false, dynamicElement.isReadOnly().content( false ) );
        assertEquals( false, dynamicElement.isRequired().content( false ) );
        assertEquals( true, dynamicElement.isShowLabel().content( false ) );
        assertEquals( "select", dynamicElement.getType().content( false ) );

        final DynamicElementMetadata metadata = dynamicElement.getMetadata().content( false );

        assertNotNull( metadata );
        assertEquals( "[$LOCALE_DEFAULT$]", metadata.getLocale().content( false ) );

        final ElementList<Entry> entries = metadata.getEntries();

        assertNotNull( entries );
        assertEquals( 3, entries.size() );

        final ElementList<DynamicElement> childDynamicElements = dynamicElement.getDynamicElements();

        assertNotNull( childDynamicElements );
        assertEquals( 3, childDynamicElements.size() );

        final DynamicElement childDynamicElement = childDynamicElements.get( 1 );

        assertNotNull( childDynamicElement );
        assertEquals( "2_0", childDynamicElement.getName().content( false ) );
        assertEquals( "option", childDynamicElement.getType().content( false ) );
        assertEquals( "2", childDynamicElement.getValue().content( false ) );

        final DynamicElementMetadata childMetadata = childDynamicElement.getMetadata().content( false );

        assertNotNull( childMetadata );
        assertEquals( "[$LOCALE_DEFAULT$]", childMetadata.getLocale().content( false ) );

        final ElementList<Entry> childEntries = childMetadata.getEntries();

        assertNotNull( childEntries );
        assertEquals( 1, childEntries.size() );

        final Entry childEntry = childEntries.get( 0 );

        assertNotNull( childEntry );
        assertEquals( "label", childEntry.getName().content( false ) );
        assertEquals( "2.0", childEntry.getValue().content( false ) );
    }

}

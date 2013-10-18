package com.liferay.ide.project.core.model.internal;

import java.util.Set;

import org.eclipse.sapphire.modeling.Status.Severity;
import org.eclipse.sapphire.services.PossibleValuesService;


/**
 * @author Gregory Amerson
 */
public class VersionPossibleValuesService extends PossibleValuesService
{

    @Override
    protected void fillPossibleValues( Set<String> values )
    {
        values.add( "6.0.5" ); //$NON-NLS-1$
        values.add( "6.0.6" ); //$NON-NLS-1$
        values.add( "6.1.0" ); //$NON-NLS-1$
        values.add( "6.1.1" ); //$NON-NLS-1$
        values.add( "6.1.2" ); //$NON-NLS-1$
        values.add( "6.2.0-RC1" ); //$NON-NLS-1$
        values.add( "6.2.0-RC2" ); //$NON-NLS-1$
        values.add( "6.2.0-RC3" ); //$NON-NLS-1$
        values.add( "6.2.0-RC4" ); //$NON-NLS-1$
        values.add( "6.2.0-SNAPSHOT" ); //$NON-NLS-1$
    }

    @Override
    public boolean ordered()
    {
        return true;
    }

    @Override
    public Severity getInvalidValueSeverity( String invalidValue )
    {
        return Severity.OK;
    }

}

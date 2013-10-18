package com.liferay.ide.project.core.model.internal;

import com.liferay.ide.sdk.core.ISDKListener;
import com.liferay.ide.sdk.core.SDK;
import com.liferay.ide.sdk.core.SDKManager;

import java.util.Set;

import org.eclipse.sapphire.modeling.Status.Severity;
import org.eclipse.sapphire.services.PossibleValuesService;


/**
 * @author Gregory Amerson
 */
public class PluginsSDKNamePossibleValuesService extends PossibleValuesService implements ISDKListener
{

    @Override
    protected void fillPossibleValues( Set<String> values )
    {
        SDK[] validSDKs = SDKManager.getInstance().getSDKs();

        if( validSDKs.length > 0 )
        {
            for( SDK validSDK : validSDKs )
            {
                values.add( validSDK.getName() );
            }
        }
    }

    @Override
    public void dispose()
    {
        SDKManager.getInstance().removeSDKListener( this );

        super.dispose();
    }

    @Override
    public Severity getInvalidValueSeverity( String invalidValue )
    {
        if( PluginsSDKNameDefaultValueService.NONE.equals( invalidValue ) ) //$NON-NLS-1$
        {
            return Severity.OK;
        }

        return super.getInvalidValueSeverity( invalidValue );
    }

    @Override
    protected void init()
    {
        super.init();

        SDKManager.getInstance().addSDKListener( this );
    }

    @Override
    public boolean ordered()
    {
        return true;
    }

    public void sdksAdded( SDK[] sdk )
    {
        broadcast();
    }

    public void sdksChanged( SDK[] sdk )
    {
        broadcast();
    }

    public void sdksRemoved( SDK[] sdk )
    {
        broadcast();
    }

}

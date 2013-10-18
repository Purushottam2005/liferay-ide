package com.liferay.ide.project.core.model.internal;

import com.liferay.ide.project.core.model.NewLiferayPluginProjectOp;
import com.liferay.ide.sdk.core.SDK;
import com.liferay.ide.sdk.core.SDKManager;

import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.services.ValidationService;


/**
 * @author Gregory Amerson
 */
public class PluginsSDKNameValidationService extends ValidationService
{

    @Override
    protected Status compute()
    {
        Status retval = Status.createOkStatus();

        final NewLiferayPluginProjectOp op = context( NewLiferayPluginProjectOp.class );

        if( "ant".equals( op.getProjectProvider().content().getShortName() ) ) //$NON-NLS-1$
        {
            final String sdkName = op.getPluginsSDKName().content();

            final SDK sdk = SDKManager.getInstance().getSDK( sdkName );

            if( sdk == null )
            {
                retval = Status.createErrorStatus( "Plugins SDK must be configured." ); //$NON-NLS-1$
            }
            else if( ! sdk.isValid() )
            {
                retval = Status.createErrorStatus( "Plugins SDK " + sdkName + " is invalid." ); //$NON-NLS-1$ //$NON-NLS-2$
            }
        }

        return retval;
    }

}

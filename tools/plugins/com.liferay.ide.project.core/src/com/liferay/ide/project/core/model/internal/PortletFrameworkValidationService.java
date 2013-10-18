package com.liferay.ide.project.core.model.internal;

import com.liferay.ide.core.ILiferayProjectProvider;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.project.core.IPortletFramework;
import com.liferay.ide.project.core.model.NewLiferayPluginProjectOp;
import com.liferay.ide.sdk.core.SDK;
import com.liferay.ide.sdk.core.SDKManager;

import org.eclipse.sapphire.FilteredListener;
import org.eclipse.sapphire.PropertyContentEvent;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.services.ValidationService;
import org.osgi.framework.Version;


/**
 * @author Gregory Amerson
 */
public class PortletFrameworkValidationService extends ValidationService
{
    private FilteredListener<PropertyContentEvent> listener;

    @Override
    protected void initValidationService()
    {
        super.initValidationService();

        this.listener = new FilteredListener<PropertyContentEvent>()
        {
            @Override
            protected void handleTypedEvent( PropertyContentEvent event )
            {
                refresh();
            }
        };

        op().property( NewLiferayPluginProjectOp.PROP_PLUGINS_SDK_NAME ).attach( this.listener );
    }

    @Override
    protected Status compute()
    {
        Status retval = Status.createOkStatus();

        final ILiferayProjectProvider projectProvider = op().getProjectProvider().content();
        final IPortletFramework portletFramework = op().getPortletFramework().content();
        final SDK sdk = SDKManager.getInstance().getSDK( op().getPluginsSDKName().content() );

        if( "ant".equals( projectProvider.getShortName() ) && portletFramework != null && sdk != null )
        {
            final Version requiredVersion = new Version( portletFramework.getRequiredSDKVersion() );
            final Version sdkVersion = new Version( sdk.getVersion() );

            if( CoreUtil.compareVersions( requiredVersion, sdkVersion ) > 0 )
            {
                retval =
                    Status.createErrorStatus( "Selected portlet framework requires SDK version at least " +
                        requiredVersion );
            }
        }

        return retval;
    }

    @Override
    public void dispose()
    {
        op().property( NewLiferayPluginProjectOp.PROP_PLUGINS_SDK_NAME ).detach( this.listener );

        super.dispose();
    }

    private NewLiferayPluginProjectOp op()
    {
        return context( NewLiferayPluginProjectOp.class );
    }
}

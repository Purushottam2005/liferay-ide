package com.liferay.ide.project.core.model.internal;

import com.liferay.ide.project.core.model.NewLiferayPluginProjectOp;
import com.liferay.ide.server.util.ServerUtil;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.platform.StatusBridge;
import org.eclipse.sapphire.services.ValidationService;
import org.eclipse.wst.server.core.IRuntime;


/**
 * @author Gregory Amerson
 */
public class RuntimeNameValidationService extends ValidationService
{

    @Override
    protected Status compute()
    {
        Status retval = Status.createOkStatus();

        final NewLiferayPluginProjectOp op = context( NewLiferayPluginProjectOp.class );

        if( "ant".equals( op.getProjectProvider().content( true ).getShortName() ) ) //$NON-NLS-1$
        {
            final String runtimeName = op.getRuntimeName().content( true );

            final IRuntime runtime = ServerUtil.getRuntime( runtimeName );

            if( runtime == null )
            {
                retval = Status.createErrorStatus( "Liferay runtime must be configured." ); //$NON-NLS-1$
            }
            else
            {
                retval = StatusBridge.create( runtime.validate( new NullProgressMonitor() ) );
            }
        }

        return retval;
    }

}

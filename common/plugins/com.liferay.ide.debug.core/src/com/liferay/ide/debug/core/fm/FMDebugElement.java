package com.liferay.ide.debug.core.fm;

import com.liferay.ide.debug.core.ILRDebugConstants;
import com.liferay.ide.debug.core.LiferayDebugCore;

import freemarker.debug.DebugModel;

import java.rmi.RemoteException;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.PlatformObject;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IDebugElement;


public class FMDebugElement extends PlatformObject implements IDebugElement
{

    private FMDebugTarget target;

    public FMDebugElement( FMDebugTarget target )
    {
        this.target = target;
    }

    @SuppressWarnings( "rawtypes" )
    public Object getAdapter(Class adapter)
    {
        if (adapter == IDebugElement.class)
        {
            return this;
        }

        return super.getAdapter(adapter);
    }

    public String getModelIdentifier()
    {
        return ILRDebugConstants.ID_FM_DEBUG_MODEL;
    }

    public FMDebugTarget getDebugTarget()
    {
        return this.target;
    }

    public ILaunch getLaunch()
    {
        return getDebugTarget().getLaunch();
    }

    protected void abort( String message, Throwable e ) throws DebugException
    {
        throw new DebugException( new Status(
            IStatus.ERROR, LiferayDebugCore.getDefault().getBundle().getSymbolicName(),
            DebugPlugin.INTERNAL_ERROR, message, e ) );
    }

    /**
     * Fires a debug event
     *
     * @param event
     *            the event to be fired
     */
    protected void fireEvent( DebugEvent event )
    {
        DebugPlugin.getDefault().fireDebugEventSet( new DebugEvent[] { event } );
    }

    /**
     * Fires a <code>CREATE</code> event for this element.
     */
    protected void fireCreationEvent()
    {
        fireEvent( new DebugEvent( this, DebugEvent.CREATE ) );
    }

    /**
     * Fires a <code>RESUME</code> event for this element with the given detail.
     *
     * @param detail
     *            event detail code
     */
    public void fireResumeEvent( int detail )
    {
        fireEvent( new DebugEvent( this, DebugEvent.RESUME, detail ) );
    }

    /**
     * Fires a <code>SUSPEND</code> event for this element with the given detail.
     *
     * @param detail
     *            event detail code
     */
    public void fireSuspendEvent( int detail )
    {
        fireEvent( new DebugEvent( this, DebugEvent.SUSPEND, detail ) );
    }

    /**
     * Fires a <code>TERMINATE</code> event for this element.
     */
    protected void fireTerminateEvent()
    {
        fireEvent( new DebugEvent( this, DebugEvent.TERMINATE ) );
    }

    protected static String getReferenceTypeName( DebugModel model ) throws DebugException
    {
        try
        {
            switch( model.getModelTypes() )
            {
                case DebugModel.TYPE_BOOLEAN:
                    return "boolean";
                case DebugModel.TYPE_COLLECTION:
                    return "collection";
                case DebugModel.TYPE_CONFIGURATION:
                    return "configuration";
                case DebugModel.TYPE_DATE:
                    return "date";
                case DebugModel.TYPE_ENVIRONMENT:
                    return "environment";
                case DebugModel.TYPE_HASH:
                    return "hash";
                case DebugModel.TYPE_HASH_EX:
                    return "hash_ex";
                case DebugModel.TYPE_METHOD:
                    return "method";
                case DebugModel.TYPE_METHOD_EX:
                    return "method_ex";
                case DebugModel.TYPE_NUMBER:
                    return "number";
                case DebugModel.TYPE_SCALAR:
                    return "scalar";
                case DebugModel.TYPE_SEQUENCE:
                    return "sequence";
                case DebugModel.TYPE_TEMPLATE:
                    return "template";
                case DebugModel.TYPE_TRANSFORM:
                    return "transform";
            }
        }
        catch( RemoteException e )
        {
            e.printStackTrace();
        }

        return "var";
    }
}

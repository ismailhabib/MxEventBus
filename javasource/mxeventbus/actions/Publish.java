// This file was generated by Mendix Modeler.
//
// WARNING: Only the following code will be retained when actions are regenerated:
// - the import list
// - the code between BEGIN USER CODE and END USER CODE
// - the code between BEGIN EXTRA CODE and END EXTRA CODE
// Other code you write will be lost the next time you deploy the project.
// Special characters, e.g., é, ö, à, etc. are supported in comments.

package mxeventbus.actions;

import com.mendix.core.Core;
import com.mendix.logging.ILogNode;
import com.mendix.systemwideinterfaces.core.IContext;
import com.mendix.webui.CustomJavaAction;
import com.mendix.systemwideinterfaces.core.IMendixObject;
import mxeventbus.Commons;
import mxeventbus.EventBus;
import mxeventbus.proxies.constants.Constants;

public class Publish extends CustomJavaAction<Boolean>
{
	private IMendixObject Obj;

	public Publish(IContext context, IMendixObject Obj)
	{
		super(context);
		this.Obj = Obj;
	}

	@Override
	public Boolean executeAction() throws Exception
	{
		// BEGIN USER CODE
		ILogNode logger = Core.getLogger(Constants.getLoggerName());

		if (!Constants.getEnabled()) {
			logger.debug("EventBus is disabled, not publishing.");
			return true;
		}

		logger.debug(Commons.prependWithThreadName("Publishing "+ this.Obj +" to: " + this.Obj.getType()));

		EventBus.getInstance().post(this.Obj);
		return true;
		// END USER CODE
	}

	/**
	 * Returns a string representation of this action
	 */
	@Override
	public String toString()
	{
		return "Publish";
	}

	// BEGIN EXTRA CODE
	// END EXTRA CODE
}

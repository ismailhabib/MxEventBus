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
import com.mendix.core.CoreException;
import com.mendix.logging.ILogNode;
import com.mendix.systemwideinterfaces.core.IContext;
import com.mendix.webui.CustomJavaAction;
import mxeventbus.Commons;
import mxeventbus.EventBus;
import mxeventbus.proxies.constants.Constants;
import rx.Observable;
import rx.schedulers.Schedulers;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import com.mendix.systemwideinterfaces.core.IMendixObject;

/**
 * Specifying debounce > 0 will cause the subscription to be done asynchronously.
 */
public class Subscribe extends CustomJavaAction<Boolean>
{
	private String EntityName;
	private String CallbackMicroflow;
	private Boolean IsAsync;
	private Long DebounceInMs;

	public Subscribe(IContext context, String EntityName, String CallbackMicroflow, Boolean IsAsync, Long DebounceInMs)
	{
		super(context);
		this.EntityName = EntityName;
		this.CallbackMicroflow = CallbackMicroflow;
		this.IsAsync = IsAsync;
		this.DebounceInMs = DebounceInMs;
	}

	@Override
	public Boolean executeAction() throws Exception
	{
		// BEGIN USER CODE

		ILogNode logger = Core.getLogger(Constants.getLoggerName());

		logger.debug(Commons.prependWithThreadName("Subscribing to " + this.EntityName));

        Observable<Object> observable = EventBus.getInstance().observe();

        if (this.IsAsync) {
            observable = observable.subscribeOn(Schedulers.newThread());
        }

        observable.map(obj -> (IMendixObject) obj)
                .filter(mendixObj -> mendixObj.getType().equals(this.EntityName));

		if (this.DebounceInMs > 0) {
			observable = observable.debounce(this.DebounceInMs, TimeUnit.MILLISECONDS);
		}

		observable.subscribe(mendixObj -> {
					logger.debug(Commons.prependWithThreadName("Received: " + mendixObj));
                    try {
						Commons.executeMf(getContext(), this.CallbackMicroflow, mendixObj);
                    } catch (CoreException e) {
						logger.error(e);
                    }
                });

        return true;
		// END USER CODE
	}

	/**
	 * Returns a string representation of this action
	 */
	@Override
	public String toString()
	{
		return "Subscribe";
	}

	// BEGIN EXTRA CODE
	// END EXTRA CODE
}
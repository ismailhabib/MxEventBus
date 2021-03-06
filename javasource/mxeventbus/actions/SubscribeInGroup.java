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
import com.mendix.systemwideinterfaces.core.IMendixObject;
import com.mendix.webui.CustomJavaAction;
import mxeventbus.Commons;
import mxeventbus.EventBus;
import mxeventbus.proxies.constants.Constants;
import rx.Observable;
import rx.exceptions.Exceptions;
import rx.schedulers.Schedulers;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Note than debounce will operate on the individual groups
 * 
 * Specifying debounce > 0 will cause the subscription to be done asynchronously.
 */
public class SubscribeInGroup extends CustomJavaAction<Boolean>
{
	private String EntityName;
	private String GroupByMicroflow;
	private String CallbackMicroflow;
	private String ErrorCallbackMicroflow;
	private Boolean IsAsync;
	private Long DebounceInMs;

	public SubscribeInGroup(IContext context, String EntityName, String GroupByMicroflow, String CallbackMicroflow, String ErrorCallbackMicroflow, Boolean IsAsync, Long DebounceInMs)
	{
		super(context);
		this.EntityName = EntityName;
		this.GroupByMicroflow = GroupByMicroflow;
		this.CallbackMicroflow = CallbackMicroflow;
		this.ErrorCallbackMicroflow = ErrorCallbackMicroflow;
		this.IsAsync = IsAsync;
		this.DebounceInMs = DebounceInMs;
	}

	@Override
	public Boolean executeAction() throws Exception
	{
		// BEGIN USER CODE
        ILogNode logger = Core.getLogger(Constants.getLoggerName());

        if (!Constants.getEnabled()) {
            logger.debug("EventBus is disabled, not subscribing.");
            return true;
        }

        logger.debug(Commons.prependWithThreadName("Subscribing to " + this.EntityName));

        Observable<Object> observable = EventBus.getInstance().observe();

        if (this.IsAsync) {
            observable = observable.subscribeOn(Schedulers.newThread());
        }

        observable.map(obj -> (IMendixObject) obj)
                .filter(mendixObj -> mendixObj.getType().equals(this.EntityName))
                .groupBy(mendixObj -> {
                    try {
                        return Commons.executeMf(Core.createSystemContext(), this.GroupByMicroflow, mendixObj);
                    } catch (Exception e) {
                        throw Exceptions.propagate(e);
                    }
                })
                .subscribe(newObservable -> {
                            logger.debug(Commons.prependWithThreadName("New grouped observable key: " + newObservable.getKey()));

                            Observable<IMendixObject> groupedObservable = newObservable;

                            if (this.DebounceInMs > 0) {
                                groupedObservable = groupedObservable.debounce(this.DebounceInMs, TimeUnit.MILLISECONDS);
                            }

                            groupedObservable.take(1).subscribe(mendixObj -> {
                                logger.debug(Commons.prependWithThreadName("Received: " + mendixObj));
                                try {
                                    Commons.executeMf(Core.createSystemContext(), this.CallbackMicroflow, mendixObj);
                                } catch (Exception e) {
                                    logger.error(e);
                                }
                            });
                        },
                        throwable -> {
                            logger.error(throwable);
                            try {
                                Core.execute(Core.createSystemContext(), this.ErrorCallbackMicroflow);
                            } catch (Exception e) {
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
		return "SubscribeInGroup";
	}

	// BEGIN EXTRA CODE
	// END EXTRA CODE
}

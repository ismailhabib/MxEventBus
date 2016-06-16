package mxeventbus;

import com.mendix.core.Core;
import com.mendix.logging.ILogNode;
import mxeventbus.proxies.constants.Constants;
import rx.Observable;
import rx.subjects.PublishSubject;
import rx.subjects.ReplaySubject;
import rx.subjects.SerializedSubject;
import rx.subjects.Subject;

/**
 * Created by imu on 14-Jun-16.
 */
public class EventBus {

    public EventBus() {
        ILogNode logger = Core.getLogger(Constants.getLoggerName());
        logger.info("MxEventBus is starting...");
    }

    private static EventBus INSTANCE = new EventBus();

    private final Subject<Object, Object> subject = new SerializedSubject<>(ReplaySubject.createWithSize(1000));

    public static EventBus getInstance() {
        return INSTANCE;
    }

    public Observable<Object> observe() {
        return subject;
    }

    public void post(Object event) {
        subject.onNext(event);
    }
}

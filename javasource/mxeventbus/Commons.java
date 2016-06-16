package mxeventbus;

import com.mendix.core.Core;
import com.mendix.core.CoreException;
import com.mendix.systemwideinterfaces.core.IContext;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by imu on 15-Jun-16.
 */
public class Commons {

    public static Object executeMf(IContext ctx, String mfName, Object param) throws CoreException {
        Map<String, Object> params = new HashMap<>();
        params.put("Object", param);
        return Core.execute(ctx, mfName, params);
    }

    public static String prependWithThreadName(String s) {
        return "[" + Thread.currentThread().getName() + "] " + s;
    }
}

package org.rmj.g3appdriver.lib.Account.gCircle;

import android.app.Application;
import android.util.Log;

import org.rmj.g3appdriver.lib.Account.Model.Auth;
import org.rmj.g3appdriver.lib.Account.Model.iAccount;
import org.rmj.g3appdriver.lib.Account.Model.iAuth;
import org.rmj.g3appdriver.lib.Account.gCircle.obj.EmployeeAuthentication;
import org.rmj.g3appdriver.lib.Account.gCircle.obj.ForgotPassword;
import org.rmj.g3appdriver.lib.Account.gCircle.obj.Register;
import org.rmj.g3appdriver.lib.Account.gCircle.obj.TerminateAccount;

public class gCircleAuth implements iAccount {
    private static final String TAG = gCircleAuth.class.getSimpleName();

    private final Application instance;

    public gCircleAuth(Application instance) {
        this.instance = instance;
    }

    @Override
    public iAuth getInstance(Auth params) {
        switch (params){
            case AUTHENTICATE:
                Log.d(TAG, "Initialize employee authentication.");
                return new EmployeeAuthentication(instance);
            case CREATE_ACCOUNT:
                Log.d(TAG, "Initialize account registration.");
                return new Register(instance);
            case CHANGE_PASSWORD:
                Log.d(TAG, "Initialize account update.");
                return new ForgotPassword(instance);
            case DEACTIVATE:
                Log.d(TAG, "Initialize account deactivate.");
                return new TerminateAccount(instance);
            default:
                Log.d(TAG, "Initialize forgot password.");
                return new ForgotPassword(instance);
        }
    }
}

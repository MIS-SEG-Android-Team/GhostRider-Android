package org.rmj.g3appdriver.AuthTest;


import static org.junit.Assert.assertTrue;

import android.app.Application;
import android.util.Log;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.rmj.g3appdriver.etc.AppConfigPreference;
import org.rmj.g3appdriver.GCircle.Account.EmployeeMaster;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(AndroidJUnit4.class)
public class TestLoginAccount {
    private static final String TAG = TestLoginAccount.class.getSimpleName();

    private Application instance;

    private EmployeeMaster poUser;

    private boolean isSuccess = false;
    private String message;

    @Before
    public void setup() throws Exception{
        instance = ApplicationProvider.getApplicationContext();
        poUser = new EmployeeMaster(instance);
        AppConfigPreference loConfig = AppConfigPreference.getInstance(instance);
        loConfig.setTestCase(true);
    }

    @Test
    public void test01ValidateEntries() throws Exception{
        EmployeeMaster.UserAuthInfo loAuth = new EmployeeMaster.UserAuthInfo("mikegarcia8748@gmail.com", "123456", "09171870011");
        assertTrue(loAuth.isAuthInfoValid());
    }

    @Test
    public void test02LoginAccount() throws Exception{
        EmployeeMaster.UserAuthInfo loAuth = new EmployeeMaster.UserAuthInfo("mikegarcia8748@gmail.com", "123456", "09171870011");
        assertTrue(poUser.AuthenticateUser(loAuth));
    }

    @Test
    public void test03GetAuthorizeFeatures() throws Exception{
        assertTrue(poUser.GetUserAuthorizeAccess());
    }

    @Test
    public void test04TestLoginWithValidation() throws Exception{
        EmployeeMaster.UserAuthInfo loAuth = new EmployeeMaster.UserAuthInfo("mikegarcia8748@gmail.com", "123456", "09171870011");
        if(!loAuth.isAuthInfoValid()){
            message = loAuth.getMessage();
            isSuccess = false;
        } else {
            if (!poUser.AuthenticateUser(loAuth)) {
                message = poUser.getMessage();
                return;
            }
        }


        Log.d(TAG, message);
        assertTrue(isSuccess);
    }
}

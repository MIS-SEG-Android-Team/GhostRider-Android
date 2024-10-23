package org.rmj.g3appdriver.AuthTest;

import static org.junit.Assert.assertTrue;

import android.app.Application;
import android.util.Log;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.rmj.g3appdriver.etc.AppConfigPreference;
import org.rmj.g3appdriver.lib.Account.AccountMaster;
import org.rmj.g3appdriver.lib.Account.Model.Auth;
import org.rmj.g3appdriver.lib.Account.Model.iAuth;
import org.rmj.g3appdriver.lib.Account.pojo.UserAuthInfo;


@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(AndroidJUnit4.class)
public class GCircleAuthenticationTest {
    private static final String TAG = GCircleAuthenticationTest.class.getSimpleName();

    private Application instance;

    private AccountMaster poAccount;
    private iAuth poSys;

    private boolean isSuccess = false;
    private String message;

    @Before
    public void setUp() throws Exception {
        this.instance = ApplicationProvider.getApplicationContext();
        AppConfigPreference.getInstance(instance).setProductID("gRider");
        AppConfigPreference.getInstance(instance).setTestCase(false);
        this.poAccount = new AccountMaster(instance);
        this.poSys = poAccount.initGuanzonApp().getInstance(Auth.AUTHENTICATE);
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void test01LoginAccount() {
        UserAuthInfo loInfo = new UserAuthInfo("mikegarcia8748@gmail.com", "123456");
        int lnResult = poSys.DoAction(loInfo);
        if(lnResult == 1){
            isSuccess = true;
        } else if(lnResult == 2){
            Log.e(TAG, poSys.getMessage());
        } else {
            Log.e(TAG, poSys.getMessage());
        }
        assertTrue(isSuccess);
    }
}
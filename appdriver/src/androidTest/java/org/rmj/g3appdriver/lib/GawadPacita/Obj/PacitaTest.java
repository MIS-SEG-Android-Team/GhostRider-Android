package org.rmj.g3appdriver.lib.GawadPacita.Obj;

import static org.junit.Assert.*;

import android.app.Application;
import android.util.Log;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.Observer;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.rmj.g3appdriver.GCircle.Apps.GawadPacita.Obj.Pacita;
import org.rmj.g3appdriver.GCircle.Apps.GawadPacita.Obj.PacitaRule;
import org.rmj.g3appdriver.GCircle.room.DataAccessObject.DPacita;
import org.rmj.g3appdriver.GCircle.room.Entities.EPacitaEvaluation;
import org.rmj.g3appdriver.GCircle.room.Entities.EPacitaRule;
import org.rmj.g3appdriver.etc.AppConfigPreference;
import org.rmj.g3appdriver.GCircle.Account.EmployeeMaster;
import org.rmj.g3appdriver.GCircle.Apps.GawadPacita.pojo.BranchRate;

import java.util.List;
import java.util.Random;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(AndroidJUnit4.class)
public class PacitaTest {
    private static final String TAG = PacitaTest.class.getSimpleName();

    private Application instance;

    private EmployeeMaster poUser;
    private Pacita poSys;

    private boolean isSuccess = false;

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Before
    public void setUp() throws Exception {
        instance = ApplicationProvider.getApplicationContext();
        poUser = new EmployeeMaster(instance);
        poSys = new Pacita(instance);
    }

    @Test
    public void test01ImportPacitaRule() {
        if(!poSys.ImportPacitaRules()){
            Log.e(TAG, poSys.getMessage());
        } else {
            isSuccess = true;
        }
        assertTrue(isSuccess);

        isSuccess = false;
    }

    @Test
    public void test02RetrievePacitaRules() {
        poSys.GetPacitaRules().observeForever(new Observer<List<EPacitaRule>>() {
            @Override
            public void onChanged(List<EPacitaRule> ePacitaRules) {
                if(ePacitaRules == null){
                    Log.e(TAG, "No pacita rules found.");
                    return;
                }



                if(ePacitaRules.size() == 0){
                    Log.e(TAG, "No pacita rules found.");
                    return;
                }

                Log.d(TAG, "Displaying the values of list");
                for(int x = 0; x < ePacitaRules.size(); x++){
                    Log.d(TAG, ePacitaRules.get(x).getFieldNmx());
                }

                isSuccess = true;
            }
        });

        assertTrue(isSuccess);

        isSuccess = false;
    }

    @Test
    public void test03BranchEvaluation() {
        String lsResult = poSys.InitializePacitaEvaluation("M050");
        if(lsResult == null){
            Log.e(TAG, poSys.getMessage());
        } else {
            poSys.GetEvaluationRecord(lsResult).observeForever(new Observer<EPacitaEvaluation>() {
                @Override
                public void onChanged(EPacitaEvaluation record) {
                    if(record == null){
                        Log.e(TAG, "No evaluation record found.");
                        return;
                    }

                    Log.d(TAG, record.getBranchCD());
                    Log.d(TAG, record.getTransact());
                    Log.d(TAG, record.getEvalType());

                    poSys.GetPacitaRules().observeForever(new Observer<List<EPacitaRule>>() {
                        @Override
                        public void onChanged(List<EPacitaRule> ePacitaRules) {
                            if(ePacitaRules == null){
                                Log.e(TAG, "No pacita rules found.");
                                return;
                            }

                            if(ePacitaRules.size() == 0){
                                Log.e(TAG, "No pacita rules found.");
                                return;
                            }

                            Log.d(TAG, "Displaying the values of list");
                            for(int x = 0; x < ePacitaRules.size(); x++){
                                Log.d(TAG, ePacitaRules.get(x).getFieldNmx());
                            }

                            String lsPayload = record.getPayloadx();
                            Log.d(TAG, "Evaluation Payload: " + lsPayload);
                            List<BranchRate> loRate = PacitaRule.ParseBranchRate(lsPayload, ePacitaRules);

                            if(loRate == null){
                                Log.e(TAG, "Unable to parse payload and pacita rules for UI.");
                                return;
                            }

                            if(loRate.size() == 0){
                                Log.e(TAG, "Unable to parse payload and pacita rules for UI.");
                                return;
                            }

                            for(int x = 0; x < loRate.size(); x++){
                                Log.d(TAG, "Entry No: " + loRate.get(x).getsRateIDxx() + ", Field Name: " + loRate.get(x).getsRateName() +", Rating: " + loRate.get(x).getcPasRatex());
                            }
                        }
                    });
                }
            });
        }
    }

    @Test
    public void test04EvaluateBranch() {
        String lsResult = poSys.InitializePacitaEvaluation("M001");
        if(lsResult == null){
            Log.e(TAG, poSys.getMessage());
        } else {
            poSys.GetEvaluationRecord(lsResult).observeForever(new Observer<EPacitaEvaluation>() {
                @Override
                public void onChanged(EPacitaEvaluation ePacitaEvaluation) {
                    if(ePacitaEvaluation == null){
                        Log.e(TAG, "No evaluation record found.");
                        return;
                    }

                    poSys.GetPacitaRules().observeForever(new Observer<List<EPacitaRule>>() {
                        @Override
                        public void onChanged(List<EPacitaRule> ePacitaRules) {
                            if(ePacitaRules == null){
                                Log.e(TAG, "No pacita rules found.");
                                return;
                            }

                            if(ePacitaRules.size() == 0){
                                Log.e(TAG, "No pacita rules found.");
                                return;
                            }

                            Log.d(TAG, "Displaying the values of list");
                            for(int x = 0; x < ePacitaRules.size(); x++){
                                Log.d(TAG, ePacitaRules.get(x).getFieldNmx());
                            }

                            String lsPayload = ePacitaEvaluation.getPayloadx();
                            Log.d(TAG, "Evaluation Payload: " + lsPayload);
                            List<BranchRate> loRate = PacitaRule.ParseBranchRate(lsPayload, ePacitaRules);

                            if(loRate == null){
                                Log.e(TAG, "Unable to parse payload and pacita rules for UI.");
                                return;
                            }

                            if(loRate.size() == 0){
                                Log.e(TAG, "Unable to parse payload and pacita rules for UI.");
                                return;
                            }

                            for(int x = 0; x < loRate.size(); x++){
                                Log.d(TAG, "Entry No: " + loRate.get(x).getsRateIDxx() + ", Field Name: " + loRate.get(x).getsRateName() +", Rating: " + loRate.get(x).getcPasRatex());
                            }
                            Random random = new Random();
                            int randomNumber;

                            for(int x = 0; x < loRate.size(); x++){
                                String lsTransNo = ePacitaEvaluation.getTransNox();
                                randomNumber = random.nextInt(2); // generates a random integer either 0 or 1
                                //poSys.UpdateBranchRate(lsTransNo, loRate.get(x).getsRateIDxx(), String.valueOf(randomNumber));
                            }
                        }
                    });
                }
            });
        }
    }

    @Test
    public void test05PostEvaluation() {
        String lsResult = poSys.InitializePacitaEvaluation("M050");
        if(lsResult == null){
            Log.e(TAG, poSys.getMessage());
            assertTrue(isSuccess);
            isSuccess = false;

            return;
        }

        if(!poSys.SaveBranchRatings(lsResult)){
            Log.e(TAG, poSys.getMessage());
        } else {
            isSuccess = true;
        }
        assertTrue(isSuccess);

        isSuccess = false;
    }

    @Test
    public void test06ImportEvaluations() {
        if(!poSys.ImportPacitaEvaluations("M001")){
            Log.e(TAG, poSys.getMessage());
        } else {
            isSuccess = true;
        }
        assertTrue(isSuccess);

        isSuccess = false;
    }

    @Test
    public void test07GetBranchRecords() {
        poSys.GetBranchRecords("M001").observeForever(new Observer<List<DPacita.BranchRecords>>() {
            @Override
            public void onChanged(List<DPacita.BranchRecords> branchRecords) {
                if(branchRecords == null){
                    Log.e(TAG, "No evaluation record found.");
                    return;
                }

                if(branchRecords.size() == 0){
                    Log.e(TAG, "No evaluation record found.");
                    return;
                }

                for(int x = 0; x < branchRecords.size(); x++){
                    Log.e(TAG, "TransNox: "+ branchRecords.get(x).sTransNox +", Date: " + branchRecords.get(x).dTransact + ", Rate: " + branchRecords.get(x).nRatingxx);
                }

                poSys.GetEvaluationRecord(branchRecords.get(0).sTransNox).observeForever(new Observer<EPacitaEvaluation>() {
                    @Override
                    public void onChanged(EPacitaEvaluation ePacitaEvaluation) {
                        if(ePacitaEvaluation == null){
                            Log.e(TAG, "No evaluation record found.");
                            return;
                        }

                        poSys.GetPacitaRules().observeForever(new Observer<List<EPacitaRule>>() {
                            @Override
                            public void onChanged(List<EPacitaRule> ePacitaRules) {
                                Log.d(TAG, ePacitaEvaluation.getPayloadx());
                                List<BranchRate> loList = PacitaRule.ParseBranchRate(ePacitaEvaluation.getPayloadx(), ePacitaRules);
                                for(int x = 0; x < loList.size(); x++){
                                    BranchRate loRate = loList.get(x);
                                    Log.d(TAG, "Criteria: " + loRate.getsRateName() + ", Rate: " + loRate.getcPasRatex());
                                }
                            }
                        });
                    }
                });
            }
        });
    }

    @Test
    public void test08ParseBranchRate() {
        String lsPayload = "{\"sEvalType\":\"026\",\"sPayloadx\":[{\"nEntryNox\":1,\"xRatingxx\":\"\"},{\"nEntryNox\":2,\"xRatingxx\":\"\"},{\"nEntryNox\":3,\"xRatingxx\":\"\"},{\"nEntryNox\":4,\"xRatingxx\":\"\"},{\"nEntryNox\":5,\"xRatingxx\":\"\"},{\"nEntryNox\":6,\"xRatingxx\":\"\"},{\"nEntryNox\":7,\"xRatingxx\":\"\"},{\"nEntryNox\":8,\"xRatingxx\":\"\"}]}";
        poSys.GetPacitaRules().observeForever(new Observer<List<EPacitaRule>>() {
            @Override
            public void onChanged(List<EPacitaRule> ePacitaRules) {
                List<BranchRate> loList = PacitaRule.ParseBranchRate(lsPayload, ePacitaRules);
                for(int x = 0; x < loList.size(); x++){
                    BranchRate loRate = loList.get(x);
                    Log.d(TAG, "Criteria: " + loRate.getsRateName() + ", Rate: " + loRate.getcPasRatex());
                }
//                assertNotNull(loList);
            }
        });
    }
}
/*
 * Created by Android Team MIS-SEG Year 2021
 * Copyright (c) 2021. Guanzon Central Office
 * Guanzon Bldg., Perez Blvd., Dagupan City, Pangasinan 2400
 * Project name : GhostRider_Android
 * Module : GhostRider_Android.dailyCollectionPlan
 * Electronic Personnel Access Control Security System
 * project file created : 6/7/21 10:36 AM
 * project file last modified : 6/7/21 10:36 AM
 */

package org.rmj.guanzongroup.ghostrider.dailycollectionplan.Model;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.rmj.g3appdriver.etc.AppConstants;

public class CollectionRemittanceInfoModelTest {
    private String TransNox;

    private String EntryNox;
    private String Transact = new AppConstants().CURRENT_DATE();
    private String PaymForm;
    private String RemitTyp;
    private String CompnyNm;
    private String BankAcct;
    private String ReferNox;
    private String Amountxx;
    private String SendStat;
    private String DateSent;
    private String TimeStmp;
    private String message;

    private String psBranch;
    private String psCltCashx;
    private String psCltCheck;
    private boolean isCheck;
    CollectionRemittanceInfoModel infoModel;
    @Before
    public void setUp() throws Exception {
        psBranch = "Guanzon Merchandising Corp.";
        Amountxx = "2500";
        psCltCashx = "2500";
        psCltCheck = "2500";
        isCheck = false;
        ReferNox = "0123456789";
        BankAcct = "M023GK1946";
//        008588003158, The Monarch Hospitality & Tourism Corp.
        CompnyNm = "Guanzon Merchandising Corp.";
        infoModel = new CollectionRemittanceInfoModel();
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void test_isDataValidCashBranch(){

        RemitTyp = "0";
        infoModel.setRemitTyp(RemitTyp);
        infoModel.setAmountxx(Amountxx);
        infoModel.setCheck(isCheck);
        infoModel.setPsBranch(psBranch);
        infoModel.setPsCltCashx(psCltCashx);
        infoModel.setPsCltCheck(psCltCheck);
        Assert.assertTrue(infoModel.isDataValid());
    }
    @Test
    public void test_isDataValidCheckBranch(){
        isCheck = true;

        RemitTyp = "0";
        infoModel.setRemitTyp(RemitTyp);
        infoModel.setAmountxx(Amountxx);
        infoModel.setCheck(isCheck);
        infoModel.setPsBranch(psBranch);
        infoModel.setPsCltCashx(psCltCashx);
        infoModel.setPsCltCheck(psCltCheck);
        Assert.assertTrue(infoModel.isDataValid());
    }
    @Test
    public void test_isDataValidCashRemittance(){

        RemitTyp = "0";
        infoModel.setCompnyNm(CompnyNm);
        infoModel.setReferNox(ReferNox);
        infoModel.setBankAcct(BankAcct);
        infoModel.setRemitTyp(RemitTyp);
        infoModel.setAmountxx(Amountxx);
        infoModel.setCheck(isCheck);
        infoModel.setPsBranch(psBranch);
        infoModel.setPsCltCashx(psCltCashx);
        infoModel.setPsCltCheck(psCltCheck);
        Assert.assertTrue(infoModel.isDataValid());
    }
    @Test
    public void test_isDataValidCheckRemittance(){
        isCheck = true;
        RemitTyp = "1";
        infoModel.setCompnyNm(CompnyNm);
        infoModel.setReferNox(ReferNox);
        infoModel.setBankAcct(BankAcct);
        infoModel.setRemitTyp(RemitTyp);
        infoModel.setAmountxx(Amountxx);
        infoModel.setCheck(isCheck);
//        infoModel.setPsBranch(psBranch);
        infoModel.setPsCltCashx(psCltCashx);
        infoModel.setPsCltCheck(psCltCheck);
        Assert.assertTrue(infoModel.isDataValid());
    }
    @Test
    public void test_isDataValidCashOtherRemittance(){

        RemitTyp = "0";
        infoModel.setCompnyNm(CompnyNm);
        infoModel.setReferNox(ReferNox);
        infoModel.setBankAcct(BankAcct);
        infoModel.setRemitTyp(RemitTyp);
        infoModel.setAmountxx(Amountxx);
        infoModel.setCheck(isCheck);
        infoModel.setPsBranch(psBranch);
        infoModel.setPsCltCashx(psCltCashx);
        infoModel.setPsCltCheck(psCltCheck);
        Assert.assertTrue(infoModel.isDataValid());
    }
    @Test
    public void test_isDataValidCheckOtherRemittance(){
        isCheck = true;
        RemitTyp = "1";
        infoModel.setCompnyNm(CompnyNm);
        infoModel.setReferNox(ReferNox);
        infoModel.setBankAcct(BankAcct);
        infoModel.setRemitTyp(RemitTyp);
        infoModel.setAmountxx(Amountxx);
        infoModel.setCheck(isCheck);
//        infoModel.setPsBranch(psBranch);
        infoModel.setPsCltCashx(psCltCashx);
        infoModel.setPsCltCheck(psCltCheck);
        Assert.assertTrue(infoModel.isDataValid());
    }
}
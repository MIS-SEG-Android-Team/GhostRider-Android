/*
 * Created by Android Team MIS-SEG Year 2021
 * Copyright (c) 2021. Guanzon Central Office
 * Guanzon Bldg., Perez Blvd., Dagupan City, Pangasinan 2400
 * Project name : GhostRider_Android
 * Module : GhostRider_Android.creditApp
 * Electronic Personnel Access Control Security System
 * project file created : 4/24/21 3:19 PM
 * project file last modified : 4/24/21 3:17 PM
 */

package org.rmj.g3appdriver.GCircle.Apps.CreditApp;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.simple.parser.JSONParser;
import org.rmj.g3appdriver.GCircle.room.Entities.ECreditApplicantInfo;
import org.rmj.gocas.base.GOCASApplication;

import java.util.Objects;

public class GoCasBuilder {
    private static final String TAG = GoCasBuilder.class.getSimpleName();

    private final GOCASApplication poGOCas;
    private final ECreditApplicantInfo poInfo;

    public GoCasBuilder(ECreditApplicantInfo foInfo){
        this.poInfo = foInfo;
        this.poGOCas = new GOCASApplication();
    }

    public void createDetailInfo(){
        try {
            setupPurchaseInfo();
            setupApplicantInfo();
            setupResidenceInfo();
            setupMeansInfo();
            setupSpouseInfo();
            setupSpouseResidence();
            setupSpsEmployment();
            setupSpsSelfEmployed();
            setupSpsPension();
            setupDisbursementInfo();
            setupDependentInfo();
            setupProperty();
            setupOtherInfo();
            setupCoMaker();
            setupComakerResidence();

            Log.e(TAG, poGOCas.toJSONString());
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public String getConstructedDetailedInfo(){
        createDetailInfo();
        return poGOCas.toJSONString();
    }

    private void setupPurchaseInfo() throws Exception {
        JSONObject loPurchase = new JSONObject(poInfo.getPurchase());
        poGOCas.PurchaseInfo().setAppliedFor("0");
        poGOCas.PurchaseInfo().setCustomerType(loPurchase.getString("cApplType"));
        poGOCas.PurchaseInfo().setPreferedBranch(loPurchase.getString("sBranchCd"));
        poGOCas.PurchaseInfo().setBrandName(loPurchase.getString("sUnitAppl"));
        poGOCas.PurchaseInfo().setModelID(loPurchase.getString("sModelIDx"));
        poGOCas.PurchaseInfo().setDownPayment(Double.parseDouble(loPurchase.getString("nDownPaym")));
        poGOCas.PurchaseInfo().setAccountTerm(Integer.parseInt(loPurchase.getString("nAcctTerm")));
        poGOCas.PurchaseInfo().setDateApplied(loPurchase.getString("dAppliedx"));
        poGOCas.PurchaseInfo().setMonthlyAmortization(Double.parseDouble(loPurchase.getString("nMonAmort")));
        poGOCas.PurchaseInfo().setRemarks(loPurchase.getString("sRemarksx"));
    }

    private void setupApplicantInfo() throws Exception{
        JSONObject loAppl = new JSONObject(poInfo.getApplInfo());
        poGOCas.ApplicantInfo().setLastName(loAppl.getString("sLastName"));
        poGOCas.ApplicantInfo().setFirstName(loAppl.getString("sFrstName"));
        poGOCas.ApplicantInfo().setMiddleName(loAppl.getString("sMiddName"));
        poGOCas.ApplicantInfo().setSuffixName(loAppl.getString("sSuffixNm"));
        poGOCas.ApplicantInfo().setNickName(loAppl.getString("sNickName"));
        poGOCas.ApplicantInfo().setBirthdate(loAppl.getString("dBirthDte"));
        poGOCas.ApplicantInfo().setBirthPlace(loAppl.getString("sBirthPlc"));
        poGOCas.ApplicantInfo().setGender(loAppl.getString("cGenderCd"));
        poGOCas.ApplicantInfo().setCivilStatus(loAppl.getString("cCvilStat"));
        poGOCas.ApplicantInfo().setCitizenship(loAppl.getString("sCitizenx"));
        poGOCas.ApplicantInfo().setMaidenName(loAppl.getString("sMaidenNm"));

        JSONArray loMobile = loAppl.getJSONArray("mobile_number");
        for (int x = 0; x < loMobile.length(); x++) {
            JSONObject mobile = loMobile.getJSONObject(x);
            poGOCas.ApplicantInfo().setMobileNoQty(x+1);
            poGOCas.ApplicantInfo().setMobileNo(x, mobile.getString("sMobileNo"));
            poGOCas.ApplicantInfo().IsMobilePostpaid(x, mobile.getString("cPostPaid"));
            poGOCas.ApplicantInfo().setPostPaidYears(x, Integer.parseInt(mobile.getString("nPostYear")));
        }
        JSONArray loPhone = loAppl.getJSONArray("landline");
        poGOCas.ApplicantInfo().setPhoneNoQty(1);
        poGOCas.ApplicantInfo().setPhoneNo(0, loPhone.getJSONObject(0).getString("sPhoneNox"));

        JSONArray loEmail = loAppl.getJSONArray("email_address");
        poGOCas.ApplicantInfo().setEmailAddQty(1);
        poGOCas.ApplicantInfo().setEmailAddress(0, loEmail.getJSONObject(0).getString("sEmailAdd"));

        JSONObject loFacebook = loAppl.getJSONObject("facebook");
        poGOCas.ApplicantInfo().setFBAccount(loFacebook.getString("sFBAcctxx"));
        poGOCas.ApplicantInfo().setViberAccount(loAppl.getString("sVibeAcct"));
    }

    private void setupResidenceInfo() throws Exception {
        JSONObject loResidence = new JSONObject(poInfo.getResidnce());

        JSONObject loPresent = loResidence.getJSONObject("present_address");
        poGOCas.ResidenceInfo().PresentAddress().setLandMark(loPresent.getString("sLandMark"));
        poGOCas.ResidenceInfo().PresentAddress().setHouseNo(loPresent.getString("sHouseNox"));
        poGOCas.ResidenceInfo().PresentAddress().setAddress1(loPresent.getString("sAddress1"));
        poGOCas.ResidenceInfo().PresentAddress().setAddress2(loPresent.getString("sAddress2"));
        poGOCas.ResidenceInfo().PresentAddress().setTownCity(loPresent.getString("sTownIDxx"));
        poGOCas.ResidenceInfo().PresentAddress().setBarangay(loPresent.getString("sBrgyIDxx"));

        poGOCas.ResidenceInfo().setOwnership(loResidence.getString("cOwnershp"));

        if(loResidence.getString("cOwnershp").equalsIgnoreCase("1")){
            JSONObject loRent = loResidence.getJSONObject("rent_others");
            poGOCas.ResidenceInfo().setRentedResidenceInfo(loRent.getString("cRntOther"));
            poGOCas.ResidenceInfo().setRentExpenses(Double.parseDouble(loRent.getString("nRentExps")));
            poGOCas.ResidenceInfo().setRentNoYears(Double.parseDouble(loRent.getString("nLenStayx")));
        }

        if(loResidence.has("cOwnOther")){
            poGOCas.ResidenceInfo().setOwnedResidenceInfo(loResidence.getString("cOwnOther"));
        }
        poGOCas.ResidenceInfo().setCareTakerRelation(loResidence.getString("sCtkReltn"));
        poGOCas.ResidenceInfo().setHouseType(loResidence.getString("cHouseTyp"));
        poGOCas.ResidenceInfo().hasGarage(loResidence.getString("cGaragexx"));

        JSONObject loPermanent = loResidence.getJSONObject("permanent_address");
        poGOCas.ResidenceInfo().PermanentAddress().setLandMark(loPermanent.getString("sLandMark"));
        poGOCas.ResidenceInfo().PermanentAddress().setHouseNo(loPermanent.getString("sHouseNox"));
        poGOCas.ResidenceInfo().PermanentAddress().setAddress1(loPermanent.getString("sAddress1"));
        poGOCas.ResidenceInfo().PermanentAddress().setAddress2(loPermanent.getString("sAddress2"));
        poGOCas.ResidenceInfo().PermanentAddress().setTownCity(loPermanent.getString("sTownIDxx"));
        poGOCas.ResidenceInfo().PermanentAddress().setBarangay(loPermanent.getString("sBrgyIDxx"));
    }

    private void setupMeansInfo() throws Exception{
//        JSONObject loMeans = new JSONObject(poInfo.getAppMeans());
//        poGOCas.MeansInfo().setIncomeSource(loMeans.getString("primay"));
//        JSONObject loMeans = new JSONObject(poInfo.getAppMeans());
        poGOCas.MeansInfo().setIncomeSource(poInfo.getAppMeans());

        setupEmploymentInfo();
        setupSelfEmployed();
        setupFinancier();
        setupPension();
    }

    private void setupEmploymentInfo() throws Exception {
        if(poInfo.getEmplymnt() != null){
            JSONObject loEmpl = new JSONObject(poInfo.getEmplymnt());
            poGOCas.MeansInfo().EmployedInfo().setEmploymentSector(loEmpl.getString("cEmpSectr"));
            poGOCas.MeansInfo().EmployedInfo().IsUniformedPersonel(loEmpl.getString("cUniforme"));
            poGOCas.MeansInfo().EmployedInfo().IsMilitaryPersonel(loEmpl.getString("cMilitary"));
            poGOCas.MeansInfo().EmployedInfo().setGovernmentLevel(loEmpl.getString("cGovtLevl"));
            poGOCas.MeansInfo().EmployedInfo().setOFWRegion(loEmpl.getString("cOFWRegnx"));
            poGOCas.MeansInfo().EmployedInfo().setCompanyLevel(loEmpl.getString("cCompLevl"));
            poGOCas.MeansInfo().EmployedInfo().setEmployeeLevel(loEmpl.getString("cEmpLevlx"));
            poGOCas.MeansInfo().EmployedInfo().setOFWCategory(loEmpl.getString("cOcCatgry"));
            poGOCas.MeansInfo().EmployedInfo().setOFWNation(loEmpl.getString("sOFWNatnx"));
            poGOCas.MeansInfo().EmployedInfo().setNatureofBusiness(loEmpl.getString("sIndstWrk"));
            poGOCas.MeansInfo().EmployedInfo().setCompanyName(loEmpl.getString("sEmployer"));
            poGOCas.MeansInfo().EmployedInfo().setCompanyAddress(loEmpl.getString("sWrkAddrx"));
            poGOCas.MeansInfo().EmployedInfo().setCompanyTown(loEmpl.getString("sWrkTownx"));
            poGOCas.MeansInfo().EmployedInfo().setPosition(loEmpl.getString("sPosition"));
            poGOCas.MeansInfo().EmployedInfo().setJobDescription(loEmpl.getString("sFunction"));
            poGOCas.MeansInfo().EmployedInfo().setEmployeeStatus(loEmpl.getString("cEmpStatx"));
            poGOCas.MeansInfo().EmployedInfo().setLengthOfService(Double.parseDouble(loEmpl.getString("nLenServc")));
            poGOCas.MeansInfo().EmployedInfo().setSalary(Long.parseLong(loEmpl.getString("nSalaryxx")));
            poGOCas.MeansInfo().EmployedInfo().setCompanyNo(loEmpl.getString("sWrkTelno"));
        }
    }

    private void setupSelfEmployed() throws Exception {
        if(poInfo.getBusnInfo() != null){
            JSONObject loSEmpl = new JSONObject(poInfo.getBusnInfo());
            poGOCas.MeansInfo().SelfEmployedInfo().setNatureOfBusiness(loSEmpl.getString("sIndstBus"));
            poGOCas.MeansInfo().SelfEmployedInfo().setNameOfBusiness(loSEmpl.getString("sBusiness"));
            poGOCas.MeansInfo().SelfEmployedInfo().setBusinessAddress(loSEmpl.getString("sBusAddrx"));
            poGOCas.MeansInfo().SelfEmployedInfo().setCompanyTown(loSEmpl.getString("sBusTownx"));
            poGOCas.MeansInfo().SelfEmployedInfo().setBusinessType(loSEmpl.getString("cBusTypex"));
            poGOCas.MeansInfo().SelfEmployedInfo().setOwnershipSize(loSEmpl.getString("cOwnSizex"));
            poGOCas.MeansInfo().SelfEmployedInfo().setBusinessLength(Double.parseDouble(loSEmpl.getString("nBusLenxx")));
            poGOCas.MeansInfo().SelfEmployedInfo().setMonthlyExpense(Long.parseLong(loSEmpl.getString("nMonExpns")));
            poGOCas.MeansInfo().SelfEmployedInfo().setIncome(Long.parseLong(loSEmpl.getString("nBusIncom")));
        }
    }

    private void setupFinancier() throws Exception{
        if(poInfo.getFinancex() != null){
            JSONObject loFinance = new JSONObject(poInfo.getFinancex());
            poGOCas.MeansInfo().FinancerInfo().setSource(loFinance.getString("sReltnCde"));
            poGOCas.MeansInfo().FinancerInfo().setFinancerName(loFinance.getString("sFinancer"));
            poGOCas.MeansInfo().FinancerInfo().setAmount(Long.parseLong(loFinance.getString("nEstIncme")));
            poGOCas.MeansInfo().FinancerInfo().setCountry(loFinance.getString("sNatnCode"));
            poGOCas.MeansInfo().FinancerInfo().setMobileNo(loFinance.getString("sMobileNo"));
            poGOCas.MeansInfo().FinancerInfo().setFBAccount(loFinance.getString("sFBAcctxx"));
            poGOCas.MeansInfo().FinancerInfo().setEmailAddress(loFinance.getString("sEmailAdd"));
        }
    }

    private void setupPension() throws Exception{
        if(poInfo.getPensionx() != null){
            JSONObject loPension = new JSONObject(poInfo.getPensionx());
//            JSONObject loPension = loMeans.getJSONObject("pensioner");
            poGOCas.MeansInfo().PensionerInfo().setSource(loPension.getString("cPenTypex"));
            poGOCas.MeansInfo().PensionerInfo().setAmount(Long.parseLong(loPension.getString("nPensionx")));
            poGOCas.MeansInfo().PensionerInfo().setYearRetired(Integer.parseInt(loPension.getString("nRetrYear")));
            JSONParser loParser = new JSONParser();
            org.json.simple.JSONObject loMeans = (org.json.simple.JSONObject) loParser.parse(poInfo.getOtherInc());
            JSONObject loOther = new JSONObject(loMeans.get("other_income").toString());
            try {
                poGOCas.MeansInfo().setOtherIncomeNature(loOther.getString("sOthrIncm"));
                // poGOCas.MeansInfo().setOtherIncomeAmount(Long.parseLong(loOther.getString("nOthrIncm")));
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    private void setupSpouseInfo() throws Exception{
        if(poInfo.getSpousexx() != null){
            JSONObject loSpouse = new JSONObject(poInfo.getSpousexx());
            poGOCas.SpouseInfo().PersonalInfo().setLastName(loSpouse.getString("sLastName"));
            poGOCas.SpouseInfo().PersonalInfo().setFirstName(loSpouse.getString("sFrstName"));
            poGOCas.SpouseInfo().PersonalInfo().setMiddleName(loSpouse.getString("sMiddName"));
            poGOCas.SpouseInfo().PersonalInfo().setSuffixName(loSpouse.getString("sSuffixNm"));
            poGOCas.SpouseInfo().PersonalInfo().setNickName(loSpouse.getString("sNickName"));
            poGOCas.SpouseInfo().PersonalInfo().setBirthdate(loSpouse.getString("dBirthDte"));
            poGOCas.SpouseInfo().PersonalInfo().setBirthPlace(loSpouse.getString("sBirthPlc"));
            poGOCas.SpouseInfo().PersonalInfo().setCitizenship(loSpouse.getString("sCitizenx"));

            JSONArray loMobile = loSpouse.getJSONArray("mobile_number");
            for(int x = 0; x < loMobile.length(); x++){
                JSONObject mobile = loMobile.getJSONObject(x);
                poGOCas.SpouseInfo().PersonalInfo().setMobileNoQty(x+1);
                poGOCas.SpouseInfo().PersonalInfo().setMobileNo(x, mobile.getString("sMobileNo"));
                poGOCas.SpouseInfo().PersonalInfo().IsMobilePostpaid(x, mobile.getString("cPostPaid"));
                poGOCas.SpouseInfo().PersonalInfo().setPostPaidYears(x, Integer.parseInt(mobile.getString("nPostYear")));
            }

            JSONArray loPhone = loSpouse.getJSONArray("landline");
            poGOCas.SpouseInfo().PersonalInfo().setPhoneNoQty(1);
            for(int x = 0; x < loPhone.length(); x++) {
                JSONObject phone = loPhone.getJSONObject(x);
                poGOCas.SpouseInfo().PersonalInfo().setPhoneNo(x, phone.getString("sPhoneNox"));
            }

            JSONArray loEmail = loSpouse.getJSONArray("email_address");
            poGOCas.SpouseInfo().PersonalInfo().setEmailAddQty(loEmail.length());
            for(int x = 0; x < loEmail.length(); x++) {
                JSONObject email = loEmail.getJSONObject(x);
                poGOCas.SpouseInfo().PersonalInfo().setEmailAddress(x, email.getString("sEmailAdd"));
            }

            JSONObject loFacebook = loSpouse.getJSONObject("facebook");
            poGOCas.ApplicantInfo().setFBAccount(loFacebook.getString("sFBAcctxx"));
            poGOCas.SpouseInfo().PersonalInfo().setViberAccount(loSpouse.getString("sVibeAcct"));
        }
    }

    private void setupSpouseResidence() throws Exception{
        if(poInfo.getSpsResdx() != null){
            JSONObject loResd = new JSONObject(poInfo.getSpsResdx());
            JSONObject loPresent = loResd.getJSONObject("present_address");
            poGOCas.SpouseInfo().ResidenceInfo().PresentAddress().setLandMark(loPresent.getString("sLandMark"));
            poGOCas.SpouseInfo().ResidenceInfo().PresentAddress().setHouseNo(loPresent.getString("sHouseNox"));
            poGOCas.SpouseInfo().ResidenceInfo().PresentAddress().setAddress1(loPresent.getString("sAddress1"));
            poGOCas.SpouseInfo().ResidenceInfo().PresentAddress().setAddress2(loPresent.getString("sAddress2"));
            poGOCas.SpouseInfo().ResidenceInfo().PresentAddress().setTownCity(loPresent.getString("sTownIDxx"));
            poGOCas.SpouseInfo().ResidenceInfo().PresentAddress().setBarangay(loPresent.getString("sBrgyIDxx"));
        }
    }

    private void setupSpsEmployment() throws Exception{
        if(poInfo.getSpsEmplx() != null){
            JSONObject loEmpl = new JSONObject(poInfo.getSpsEmplx());
            poGOCas.SpouseMeansInfo().EmployedInfo().setEmploymentSector(loEmpl.getString("cEmpSectr"));
            poGOCas.SpouseMeansInfo().EmployedInfo().IsUniformedPersonel(loEmpl.getString("cUniforme"));
            poGOCas.SpouseMeansInfo().EmployedInfo().IsMilitaryPersonel(loEmpl.getString("cMilitary"));
            poGOCas.SpouseMeansInfo().EmployedInfo().setGovernmentLevel(loEmpl.getString("cGovtLevl"));
            poGOCas.SpouseMeansInfo().EmployedInfo().setOFWRegion(loEmpl.getString("cOFWRegnx"));
            poGOCas.SpouseMeansInfo().EmployedInfo().setCompanyLevel(loEmpl.getString("cCompLevl"));
            poGOCas.SpouseMeansInfo().EmployedInfo().setEmployeeLevel(loEmpl.getString("cEmpLevlx"));
            poGOCas.SpouseMeansInfo().EmployedInfo().setOFWCategory(loEmpl.getString("cOcCatgry"));
            poGOCas.SpouseMeansInfo().EmployedInfo().setOFWNation(loEmpl.getString("sOFWNatnx"));
            poGOCas.SpouseMeansInfo().EmployedInfo().setNatureofBusiness(loEmpl.getString("sIndstWrk"));
            poGOCas.SpouseMeansInfo().EmployedInfo().setCompanyName(loEmpl.getString("sEmployer"));
            poGOCas.SpouseMeansInfo().EmployedInfo().setCompanyAddress(loEmpl.getString("sWrkAddrx"));
            poGOCas.SpouseMeansInfo().EmployedInfo().setCompanyTown(loEmpl.getString("sWrkTownx"));
            poGOCas.SpouseMeansInfo().EmployedInfo().setPosition(loEmpl.getString("sPosition"));
            poGOCas.SpouseMeansInfo().EmployedInfo().setJobDescription(loEmpl.getString("sFunction"));
            poGOCas.SpouseMeansInfo().EmployedInfo().setEmployeeStatus(loEmpl.getString("cEmpStatx"));
            poGOCas.SpouseMeansInfo().EmployedInfo().setLengthOfService(Double.parseDouble(loEmpl.getString("nLenServc")));
            poGOCas.SpouseMeansInfo().EmployedInfo().setSalary(Long.parseLong(loEmpl.getString("nSalaryxx")));
            poGOCas.SpouseMeansInfo().EmployedInfo().setCompanyNo(loEmpl.getString("sWrkTelno"));
        }
    }

    private void setupSpsSelfEmployed() throws Exception{
        if(poInfo.getSpsBusnx() != null){
            JSONObject loSEmpl = new JSONObject(poInfo.getSpsBusnx());
            poGOCas.SpouseMeansInfo().SelfEmployedInfo().setNatureOfBusiness(loSEmpl.getString("sIndstBus"));
            poGOCas.SpouseMeansInfo().SelfEmployedInfo().setNameOfBusiness(loSEmpl.getString("sBusiness"));
            poGOCas.SpouseMeansInfo().SelfEmployedInfo().setBusinessAddress(loSEmpl.getString("sBusAddrx"));
            poGOCas.SpouseMeansInfo().SelfEmployedInfo().setCompanyTown(loSEmpl.getString("sBusTownx"));
            poGOCas.SpouseMeansInfo().SelfEmployedInfo().setBusinessType(loSEmpl.getString("cBusTypex"));
            poGOCas.SpouseMeansInfo().SelfEmployedInfo().setOwnershipSize(loSEmpl.getString("cOwnSizex"));
            poGOCas.SpouseMeansInfo().SelfEmployedInfo().setBusinessLength(Double.parseDouble(loSEmpl.getString("nBusLenxx")));
            poGOCas.SpouseMeansInfo().SelfEmployedInfo().setMonthlyExpense(Long.parseLong(loSEmpl.getString("nMonExpns")));
            poGOCas.SpouseMeansInfo().SelfEmployedInfo().setIncome(Long.parseLong(loSEmpl.getString("nBusIncom")));
        }
    }

    private void setupSpsPension() throws Exception {
        if(poInfo.getSpsPensn() != null){
            JSONObject loPension = new JSONObject(poInfo.getSpsPensn());
            poGOCas.SpouseMeansInfo().PensionerInfo().setSource(loPension.getString("cPenTypex"));
            poGOCas.SpouseMeansInfo().PensionerInfo().setAmount(Long.parseLong(loPension.getString("nPensionx")));
            poGOCas.SpouseMeansInfo().PensionerInfo().setYearRetired(Integer.parseInt(loPension.getString("nRetrYear")));

            if(loPension.has("other_income")) {
                JSONObject loOther = loPension.getJSONObject("other_income");
                try {
                    poGOCas.SpouseMeansInfo().setOtherIncomeNature(loOther.getString("sOthrIncm"));
                    //poGOCas.SpouseMeansInfo().setOtherIncomeAmount(Long.parseLong(loOther.getString("nOthrIncm")));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void setupDisbursementInfo() throws Exception{
        if(poInfo.getDisbrsmt() != null) {
            JSONObject loDisb = new JSONObject(poInfo.getDisbrsmt());
            JSONObject loExp = loDisb.getJSONObject("monthly_expenses");
            poGOCas.DisbursementInfo().Expenses().setElectricBill(Double.parseDouble(loExp.getString("nElctrcBl")));
            poGOCas.DisbursementInfo().Expenses().setFoodAllowance(Double.parseDouble(loExp.getString("nFoodAllw")));
            poGOCas.DisbursementInfo().Expenses().setWaterBill(Double.parseDouble(loExp.getString("nWaterBil")));
            poGOCas.DisbursementInfo().Expenses().setLoanAmount(Double.parseDouble((loExp.getString("nLoanAmtx"))));

            JSONObject loBank = loDisb.getJSONObject("bank_account");
            poGOCas.DisbursementInfo().BankAccount().setBankName(Objects.requireNonNull(loBank.getString("sBankName")));
            poGOCas.DisbursementInfo().BankAccount().setAccountType(Objects.requireNonNull(loBank.getString("sAcctType")));

            JSONObject loCrdit = loDisb.getJSONObject("credit_card");
            poGOCas.DisbursementInfo().CreditCard().setBankName(Objects.requireNonNull(loCrdit.getString("sBankName")));
            poGOCas.DisbursementInfo().CreditCard().setCreditLimit(Double.parseDouble(loCrdit.getString("nCrdLimit")));
            poGOCas.DisbursementInfo().CreditCard().setMemberSince(Integer.parseInt(loCrdit.getString("nSinceYrx")));
        }
    }

    private void setupDependentInfo() throws Exception {
        if(poInfo.getDependnt() != null) {
            JSONObject loDependent = new JSONObject(poInfo.getDependnt());
            Log.e(TAG, poInfo.getDependnt());
            JSONArray loDpd = loDependent.getJSONArray("children");
            for (int x = 0; x < loDpd.length(); x++) {
                JSONObject dpd = loDpd.getJSONObject(x);
                poGOCas.DisbursementInfo().DependentInfo().addDependent();
                poGOCas.DisbursementInfo().DependentInfo().setFullName(x, dpd.getString("sFullName"));
                poGOCas.DisbursementInfo().DependentInfo().setRelation(x, dpd.getString("sReltnCde"));
                poGOCas.DisbursementInfo().DependentInfo().setAge(x, Integer.parseInt(dpd.getString("nDepdAgex")));
                poGOCas.DisbursementInfo().DependentInfo().IsStudent(x, dpd.getString("cIsPupilx"));
                poGOCas.DisbursementInfo().DependentInfo().IsWorking(x, dpd.getString("cHasWorkx"));
                poGOCas.DisbursementInfo().DependentInfo().IsDependent(x, dpd.getString("cDependnt"));
                poGOCas.DisbursementInfo().DependentInfo().IsHouseHold(x, dpd.getString("cHouseHld"));
                poGOCas.DisbursementInfo().DependentInfo().IsMarried(x, dpd.getString("cIsMarrdx"));
                poGOCas.DisbursementInfo().DependentInfo().setSchoolName(x, dpd.getString("sSchlName"));
                poGOCas.DisbursementInfo().DependentInfo().setSchoolAddress(x, dpd.getString("sSchlAddr"));
                poGOCas.DisbursementInfo().DependentInfo().setSchoolTown(x, dpd.getString("sSchlTown"));
                poGOCas.DisbursementInfo().DependentInfo().setEducationalLevel(x, dpd.getString("sEducLevl"));
                poGOCas.DisbursementInfo().DependentInfo().IsPrivateSchool(x, dpd.getString("cIsPrivte"));
                poGOCas.DisbursementInfo().DependentInfo().IsScholar(x, dpd.getString("cIsSchlrx"));
                poGOCas.DisbursementInfo().DependentInfo().IsWorking(x, dpd.getString("cHasWorkx"));
                poGOCas.DisbursementInfo().DependentInfo().setWorkType(x, dpd.getString("cWorkType"));
                poGOCas.DisbursementInfo().DependentInfo().setCompany(x, dpd.getString("sCompanyx"));
            }
        }
    }

    private void setupProperty() throws Exception {
        if(poInfo.getProperty() != null) {
            JSONObject loProperty = new JSONObject(poInfo.getProperty());
            poGOCas.DisbursementInfo().PropertiesInfo().setLotName1(loProperty.getString("sProprty1"));
            poGOCas.DisbursementInfo().PropertiesInfo().setLotName2(loProperty.getString("sProprty2"));
            poGOCas.DisbursementInfo().PropertiesInfo().setLotName3(loProperty.getString("sProprty3"));
            poGOCas.DisbursementInfo().PropertiesInfo().Has4Wheels(loProperty.getString("cWith4Whl"));
            poGOCas.DisbursementInfo().PropertiesInfo().Has3Wheels(loProperty.getString("cWith3Whl"));
            poGOCas.DisbursementInfo().PropertiesInfo().Has2Wheels(loProperty.getString("cWith2Whl"));
            poGOCas.DisbursementInfo().PropertiesInfo().WithAirCon(loProperty.getString("cWithACxx"));
            poGOCas.DisbursementInfo().PropertiesInfo().WithRefrigerator(loProperty.getString("cWithRefx"));
            poGOCas.DisbursementInfo().PropertiesInfo().WithTelevision(loProperty.getString("cWithTVxx"));
        }
    }

    private void setupOtherInfo() throws Exception {
        JSONObject loOther = new JSONObject(poInfo.getOthrInfo());
        poGOCas.OtherInfo().setUnitPayor(loOther.getString("sPurposex"));
        poGOCas.OtherInfo().setUnitPayor(loOther.getString("sUnitPayr"));
        if(loOther.has("sPyr2Buyr")) {
            poGOCas.OtherInfo().setPayorRelation(loOther.getString("sPyr2Buyr"));
        }
        poGOCas.OtherInfo().setUnitUser(loOther.getString("sUnitUser"));
        poGOCas.OtherInfo().setPurpose(loOther.getString("sPurposex"));
        poGOCas.OtherInfo().setSourceInfo(loOther.getString("sSrceInfo"));
        poGOCas.OtherInfo().setSourceInfo(loOther.getString("sSrceInfo"));

        JSONArray loRef = loOther.getJSONArray("personal_reference");
        for(int x = 0; x < loRef.length(); x++){
            JSONObject reference = loRef.getJSONObject(x);
            poGOCas.OtherInfo().addReference();
            poGOCas.OtherInfo().setPRName(x, reference.getString("sRefrNmex"));
            poGOCas.OtherInfo().setPRTownCity(x, reference.getString("sRefrTown"));
            poGOCas.OtherInfo().setPRMobileNo(x, reference.getString("sRefrMPNx"));
            poGOCas.OtherInfo().setPRAddress(x, reference.getString("sRefrAddx"));
            Log.e(TAG, "Count = " + x);
        }
    }

    private void setupCoMaker() throws Exception {
        if(poInfo.getComakerx() != null){
            JSONObject loCoMker = new JSONObject(poInfo.getComakerx());
            poGOCas.CoMakerInfo().setLastName(loCoMker.getString("sLastName"));
            poGOCas.CoMakerInfo().setFirstName(loCoMker.getString("sFrstName"));
            poGOCas.CoMakerInfo().setMiddleName(loCoMker.getString("sMiddName"));
            poGOCas.CoMakerInfo().setSuffixName(loCoMker.getString("sSuffixNm"));
            poGOCas.CoMakerInfo().setNickName(loCoMker.getString("sNickName"));
            poGOCas.CoMakerInfo().setBirthdate(loCoMker.getString("dBirthDte"));
            poGOCas.CoMakerInfo().setBirthPlace(loCoMker.getString("sBirthPlc"));
            poGOCas.CoMakerInfo().setIncomeSource(loCoMker.getString("cIncmeSrc"));
            poGOCas.CoMakerInfo().setRelation(loCoMker.getString("sReltnCde"));

            JSONArray loMobile = loCoMker.getJSONArray("mobile_number");
            for (int x = 0; x < loMobile.length(); x++) {
                JSONObject mobile = loMobile.getJSONObject(x);
                poGOCas.CoMakerInfo().setMobileNoQty(x+1);
                poGOCas.CoMakerInfo().setMobileNo(x, mobile.getString("sMobileNo"));
                poGOCas.CoMakerInfo().IsMobilePostpaid(x, mobile.getString("cPostPaid"));
                poGOCas.CoMakerInfo().setPostPaidYears(x, Integer.parseInt(mobile.getString("nPostYear")));
            }
            poGOCas.CoMakerInfo().setFBAccount(loCoMker.getString("sFBAcctxx"));
        }
    }

    private void setupComakerResidence() throws Exception {
        if(poInfo.getCmResidx() != null){
            JSONObject loResidence = new JSONObject(poInfo.getCmResidx());

            JSONObject loPresent = loResidence.getJSONObject("present_address");
            poGOCas.CoMakerInfo().ResidenceInfo().PresentAddress().setLandMark(loPresent.getString("sLandMark"));
            poGOCas.CoMakerInfo().ResidenceInfo().PresentAddress().setHouseNo(loPresent.getString("sHouseNox"));
            poGOCas.CoMakerInfo().ResidenceInfo().PresentAddress().setAddress1(loPresent.getString("sAddress1"));
            poGOCas.CoMakerInfo().ResidenceInfo().PresentAddress().setAddress2(loPresent.getString("sAddress2"));
            poGOCas.CoMakerInfo().ResidenceInfo().PresentAddress().setTownCity(loPresent.getString("sTownIDxx"));
            poGOCas.CoMakerInfo().ResidenceInfo().PresentAddress().setBarangay(loPresent.getString("sBrgyIDxx"));

            poGOCas.ResidenceInfo().setOwnership(loResidence.getString("cOwnershp"));

            if(loResidence.getString("cOwnershp").equalsIgnoreCase("1")){
                JSONObject loRent = loResidence.getJSONObject("rent_others");
                poGOCas.CoMakerInfo().ResidenceInfo().setRentedResidenceInfo(loRent.getString("cRntOther"));
                poGOCas.CoMakerInfo().ResidenceInfo().setRentExpenses(Double.parseDouble(loRent.getString("nRentExps")));
                poGOCas.CoMakerInfo().ResidenceInfo().setRentNoYears(Double.parseDouble(loRent.getString("nLenStayx")));
            }

            if(loResidence.has("cOwnOther")){
                poGOCas.CoMakerInfo().ResidenceInfo().setOwnedResidenceInfo(loResidence.getString("cOwnOther"));
            }

            poGOCas.CoMakerInfo().ResidenceInfo().setCareTakerRelation(loResidence.getString("sCtkReltn"));
            poGOCas.CoMakerInfo().ResidenceInfo().setHouseType(loResidence.getString("cHouseTyp"));
            poGOCas.CoMakerInfo().ResidenceInfo().hasGarage(loResidence.getString("cGaragexx"));
        }
    }
}

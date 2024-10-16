package org.rmj.g3appdriver.GCircle.Apps.CreditApp.Obj;

import static org.rmj.g3appdriver.etc.AppConstants.getLocalMessage;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;

import org.rmj.g3appdriver.GCircle.Apps.CreditApp.CreditApp;
import org.rmj.g3appdriver.GCircle.Apps.CreditApp.CreditAppConstants;
import org.rmj.g3appdriver.GCircle.Apps.CreditApp.GoCasBuilder;
import org.rmj.g3appdriver.GCircle.Apps.CreditApp.model.ReviewAppDetail;
import org.rmj.g3appdriver.GCircle.room.DataAccessObject.DBarangayInfo;
import org.rmj.g3appdriver.GCircle.room.DataAccessObject.DCreditApplication;
import org.rmj.g3appdriver.GCircle.room.DataAccessObject.DTownInfo;
import org.rmj.g3appdriver.GCircle.room.Entities.EBarangayInfo;
import org.rmj.g3appdriver.GCircle.room.Entities.ECountryInfo;
import org.rmj.g3appdriver.GCircle.room.Entities.ECreditApplicantInfo;
import org.rmj.g3appdriver.GCircle.room.Entities.ECreditApplication;
import org.rmj.g3appdriver.GCircle.room.Entities.EMcModel;
import org.rmj.g3appdriver.GCircle.room.Entities.EOccupationInfo;
import org.rmj.g3appdriver.GCircle.room.GGC_GCircleDB;
import org.rmj.g3appdriver.lib.Etc.Barangay;
import org.rmj.g3appdriver.lib.Etc.Country;
import org.rmj.g3appdriver.GCircle.room.Repositories.RMcModel;
import org.rmj.g3appdriver.GCircle.room.Repositories.ROccupation;
import org.rmj.g3appdriver.lib.Etc.Town;
import org.rmj.g3appdriver.etc.AppConstants;
import org.rmj.g3appdriver.etc.FormatUIText;
import org.rmj.gocas.base.GOCASApplication;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ReviewLoanInfo implements CreditApp {
    private static final String TAG = ReviewLoanInfo.class.getSimpleName();

    private final DCreditApplication poDao;
    private final Town poTown;
    private final RMcModel poModel;
    private final Barangay poBarangay;
    private final Country poCountry;
    private final ROccupation poJobx;
    private List<ReviewAppDetail> loListDetail;
    private String message;

    public ReviewLoanInfo(Application instance) {
        this.poDao = GGC_GCircleDB.getInstance(instance).CreditApplicationDao();
        this.poTown = new Town(instance);
        this.poModel = new RMcModel(instance);
        this.poBarangay = new Barangay(instance);
        this.poCountry = new Country(instance);
        this.poJobx = new ROccupation(instance);
        this.loListDetail = new ArrayList<>();
    }

    @Override
    public LiveData<ECreditApplicantInfo> GetApplication(String args) {
        return poDao.GetApplicantInfo(args);
    }

    @Override
    public Object Parse(ECreditApplicantInfo args) {
        try {
            ECreditApplicantInfo poInfo = args;
            List<ReviewAppDetail> loListDetl = new ArrayList<>();
            GOCASApplication loGOCas = new GOCASApplication();
            loGOCas.setData(new GoCasBuilder(poInfo).getConstructedDetailedInfo());
            loListDetl.add(new ReviewAppDetail(true, "Purchase Info", "", ""));
            loListDetl.add(new ReviewAppDetail(false, "", "Branch", loGOCas.PurchaseInfo().getPreferedBranch()));

            loListDetl.add(new ReviewAppDetail(false, "", "Unit Applied", loGOCas.PurchaseInfo().getBrandName()));

            EMcModel loModel = poModel.getModelInfo(loGOCas.PurchaseInfo().getModelID());
            String lsModelNme = loModel.getModelNme();
            loListDetl.add(new ReviewAppDetail(false, "", "Model", lsModelNme));

            String lsDownpayment = String.valueOf(loGOCas.PurchaseInfo().getDownPayment());
            loListDetl.add(new ReviewAppDetail(false, "", "Downpayment", FormatUIText.getCurrencyUIFormat(lsDownpayment)));

            String lsTerm = String.valueOf(loGOCas.PurchaseInfo().getAccountTerm());
            loListDetl.add(new ReviewAppDetail(false, "", "Installment Term", lsTerm + " Months"));

            String lsAmort = String.valueOf(loGOCas.PurchaseInfo().getMonthlyAmortization());
            loListDetl.add(new ReviewAppDetail(false, "", "Monthly Amortization", FormatUIText.getCurrencyUIFormat(lsAmort)));

            loListDetl.add(new ReviewAppDetail(true, "Personal Info", "", ""));
            loListDetl.add(new ReviewAppDetail(false, "", "Full Name", loGOCas.ApplicantInfo().getClientName()));

            String lsBirthDate = FormatUIText.formatGOCasBirthdate(loGOCas.ApplicantInfo().getBirthdate());
            loListDetl.add(new ReviewAppDetail(false, "", "Birth Date", lsBirthDate));

            DTownInfo.TownProvinceName loBPlace = poTown.getTownProvinceName(loGOCas.ApplicantInfo().getBirthPlace());
            String lsBirthPlace = loBPlace.sTownName + ", " + loBPlace.sProvName;
            loListDetl.add(new ReviewAppDetail(false, "", "Birth Place", lsBirthPlace));
            loListDetl.add(new ReviewAppDetail(false, "", "Gender", parseGender(loGOCas.ApplicantInfo().getGender())));
            loListDetl.add(new ReviewAppDetail(false, "", "Civil Status", parseCivilStatus(loGOCas.ApplicantInfo().getCivilStatus())));
            loListDetl.add(new ReviewAppDetail(false, "", "Maiden Name", loGOCas.ApplicantInfo().getMaidenName()));

            ECountryInfo loCountry = poCountry.getCountryInfo(loGOCas.ApplicantInfo().getCitizenship());
            String lsCitizen = loCountry.getNational();
            loListDetl.add(new ReviewAppDetail(false, "", "Citizenship", lsCitizen));

            for (int x = 0; x < loGOCas.ApplicantInfo().getMobileNoQty(); x++) {
                loListDetl.add(new ReviewAppDetail(false, "", "Mobile No",

                        loGOCas.ApplicantInfo().getMobileNo(x) + ", " + parseSimType(loGOCas.ApplicantInfo().IsMobilePostpaid(x))));
            }

            loListDetl.add(new ReviewAppDetail(false, "", "Landline", loGOCas.ApplicantInfo().getPhoneNo(0)));
            loListDetl.add(new ReviewAppDetail(false, "", "Email", loGOCas.ApplicantInfo().getEmailAddress(0)));
            loListDetl.add(new ReviewAppDetail(false, "", "Facebook", loGOCas.ApplicantInfo().getFBAccount()));
            loListDetl.add(new ReviewAppDetail(false, "", "Viber", loGOCas.ApplicantInfo().getViberAccount()));

            loListDetl.add(new ReviewAppDetail(true, "Present Residence Info", "", ""));
            loListDetl.add(new ReviewAppDetail(false, "", "Landmark", loGOCas.ResidenceInfo().PresentAddress().getLandMark()));
            loListDetl.add(new ReviewAppDetail(false, "", "House No", loGOCas.ResidenceInfo().PresentAddress().getHouseNo()));
            loListDetl.add(new ReviewAppDetail(false, "", "Sitio/Lot No.", loGOCas.ResidenceInfo().PresentAddress().getAddress1()));
            loListDetl.add(new ReviewAppDetail(false, "", "Street", loGOCas.ResidenceInfo().PresentAddress().getAddress2()));

            DBarangayInfo.BrgyTownProvNames loPresent = poBarangay.getBrgyTownProvName(loGOCas.ResidenceInfo().PresentAddress().getBarangay());
            loListDetl.add(new ReviewAppDetail(false, "", "Barangay", loPresent.sBrgyName));
            loListDetl.add(new ReviewAppDetail(false, "", "Town", loPresent.sTownName));
            loListDetl.add(new ReviewAppDetail(false, "", "Province", loPresent.sProvName));

            loListDetl.add(new ReviewAppDetail(false, "", "House Ownership", parseHouseOwn(loGOCas.ResidenceInfo().getOwnership())));
            if (loGOCas.ResidenceInfo().getOwnership().equalsIgnoreCase("0")) {
                loListDetl.add(new ReviewAppDetail(false, "", "House Hold", parseHouseHold(loGOCas.ResidenceInfo().getOwnedResidenceInfo())));
            } else if (loGOCas.ResidenceInfo().getOwnership().equalsIgnoreCase("1")) {
                loListDetl.add(new ReviewAppDetail(false, "", "House Holds", parseHouseHold(loGOCas.ResidenceInfo().getRentedResidenceInfo())));

                String lsExp = String.valueOf(loGOCas.ResidenceInfo().getRentExpenses());
                loListDetl.add(new ReviewAppDetail(false, "", "Monthly Rent", lsExp));
                //loListDetl.add(new ReviewAppDetail(false, "", "Length of Stay", loGOCas.ResidenceInfo().getRentNoYears()));
            } else if (loGOCas.ResidenceInfo().getOwnership().equalsIgnoreCase("2")) {
                loListDetl.add(new ReviewAppDetail(false, "", "House Owner", loGOCas.ResidenceInfo().getCareTakerRelation()));
            }

            loListDetl.add(new ReviewAppDetail(false, "", "House Type", parseHouseType(loGOCas.ResidenceInfo().getHouseType())));
            loListDetl.add(new ReviewAppDetail(false, "", "Has Garage", parseGarage(loGOCas.ResidenceInfo().hasGarage())));

            /*
              Permanent Residence Info
             */
            loListDetl.add(new ReviewAppDetail(true, "Permanent Residence Info", "", ""));
            loListDetl.add(new ReviewAppDetail(false, "", "Landmark", loGOCas.ResidenceInfo().PermanentAddress().getLandMark()));
            loListDetl.add(new ReviewAppDetail(false, "", "House No", loGOCas.ResidenceInfo().PermanentAddress().getHouseNo()));
            loListDetl.add(new ReviewAppDetail(false, "", "Sitio/Lot No.", loGOCas.ResidenceInfo().PermanentAddress().getAddress1()));
            loListDetl.add(new ReviewAppDetail(false, "", "Street", loGOCas.ResidenceInfo().PermanentAddress().getAddress2()));

            DBarangayInfo.BrgyTownProvNames loPermanent = poBarangay.getBrgyTownProvName(loGOCas.ResidenceInfo().PermanentAddress().getBarangay());
            loListDetl.add(new ReviewAppDetail(false, "", "Barangay", loPermanent.sBrgyName));
            loListDetl.add(new ReviewAppDetail(false, "", "Town", loPermanent.sTownName));
            loListDetl.add(new ReviewAppDetail(false, "", "Province", loPermanent.sProvName));


            loListDetl.add(new ReviewAppDetail(true, "Means Info", "", ""));
            String lsPrimary = loGOCas.MeansInfo().getIncomeSource();
            loListDetl.add(new ReviewAppDetail(false, "", "Primary Income", parseMeansPrimary(lsPrimary)));

            /*
                Check if Employment info has data input.
             */
            if (!loGOCas.MeansInfo().EmployedInfo().getCompanyName().isEmpty() &&
                    !loGOCas.MeansInfo().EmployedInfo().getCompanyTown().isEmpty() &&
                    !loGOCas.MeansInfo().EmployedInfo().getCompanyTown().isEmpty()) {

                loListDetl.add(new ReviewAppDetail(true, "Employment Info", "", ""));
                String lsMSector = loGOCas.MeansInfo().EmployedInfo().getEmploymentSector();
                loListDetl.add(new ReviewAppDetail(false, "", "Employment Sector", parseSector(lsMSector)));
                /*
                    Display Info according to employment Sector.
                    1 -> Private
                    0 -> Public/Government
                    2 -> OFW
                 */
                if (lsMSector.equalsIgnoreCase("1")) {
                    loListDetl.add(new ReviewAppDetail(false, "", "Nature of Business", loGOCas.MeansInfo().EmployedInfo().getNatureofBusiness()));
                    loListDetl.add(new ReviewAppDetail(false, "", "Company Name", loGOCas.MeansInfo().EmployedInfo().getCompanyName()));
                    loListDetl.add(new ReviewAppDetail(false, "", "Company Level", parseCompLevel(loGOCas.MeansInfo().EmployedInfo().getCompanyLevel())));
                    loListDetl.add(new ReviewAppDetail(false, "", "Address", loGOCas.MeansInfo().EmployedInfo().getCompanyAddress()));

                    DTownInfo.TownProvinceName loTown = poTown.getTownProvinceName(loGOCas.MeansInfo().EmployedInfo().getCompanyTown());
                    String lsCompTown = loTown.sTownName + ", " + loTown.sProvName;
                    loListDetl.add(new ReviewAppDetail(false, "", "Town", lsCompTown));

                    String lsPosition = poJobx.getOccupationName(loGOCas.MeansInfo().EmployedInfo().getPosition());
                    loListDetl.add(new ReviewAppDetail(false, "", "Position", lsPosition));
                    loListDetl.add(new ReviewAppDetail(false, "", "Specific Job", loGOCas.MeansInfo().EmployedInfo().getJobDescription()));

                    double lnService = loGOCas.MeansInfo().EmployedInfo().getLengthOfService();
                    double lnValue = 0;
                    if (lnService % 1 == 0) {
                        lnValue = lnService;
                    } else {
                        lnValue = lnService * 12;
                    }
                    loListDetl.add(new ReviewAppDetail(false, "", "Length of Service", String.valueOf(lnValue) + " Year/s"));
                    loListDetl.add(new ReviewAppDetail(false, "", "Estimated Salary", FormatUIText.getCurrencyUIFormat(String.valueOf(loGOCas.MeansInfo().EmployedInfo().getSalary()))));
                    loListDetl.add(new ReviewAppDetail(false, "", "Company Contact", loGOCas.MeansInfo().EmployedInfo().getCompanyNo()));
                    loListDetl.add(new ReviewAppDetail(false, "", "Employee Status", parseEmployeeStatus(loGOCas.MeansInfo().EmployedInfo().getEmployeeStatus())));
                    loListDetl.add(new ReviewAppDetail(false, "", "Employee Level", parseEmployeeLevel(loGOCas.MeansInfo().EmployedInfo().getEmployeeLevel())));
                } else if (lsMSector.equalsIgnoreCase("0")) {
                    loListDetl.add(new ReviewAppDetail(false, "", "Government Sector", ""));
                    loListDetl.add(new ReviewAppDetail(false, "", "Military Personnel", parseMilitary(loGOCas.MeansInfo().EmployedInfo().IsMilitaryPersonel())));
                    loListDetl.add(new ReviewAppDetail(false, "", "Military Personnel", parseMilitary(loGOCas.MeansInfo().EmployedInfo().IsUniformedPersonel())));
                    loListDetl.add(new ReviewAppDetail(false, "", "Government Level", parseGovLevel(loGOCas.MeansInfo().EmployedInfo().getGovernmentLevel())));
                    loListDetl.add(new ReviewAppDetail(false, "", "Government Institution", loGOCas.MeansInfo().EmployedInfo().getCompanyName()));
                    loListDetl.add(new ReviewAppDetail(false, "", "Address", loGOCas.MeansInfo().EmployedInfo().getCompanyAddress()));

                    DTownInfo.TownProvinceName loTown = poTown.getTownProvinceName(loGOCas.MeansInfo().EmployedInfo().getCompanyTown());
                    String lsCompTown = loTown.sTownName + ", " + loTown.sProvName;
                    loListDetl.add(new ReviewAppDetail(false, "", "Town", lsCompTown));

                    String lsPosition = poJobx.getOccupationName(loGOCas.MeansInfo().EmployedInfo().getPosition());
                    loListDetl.add(new ReviewAppDetail(false, "", "Position", lsPosition));
                    loListDetl.add(new ReviewAppDetail(false, "", "Specific Job", loGOCas.MeansInfo().EmployedInfo().getJobDescription()));
                    loListDetl.add(new ReviewAppDetail(false, "", "Estimated Salary", String.valueOf(loGOCas.MeansInfo().EmployedInfo().getSalary())));
                    loListDetl.add(new ReviewAppDetail(false, "", "Contact", loGOCas.MeansInfo().EmployedInfo().getCompanyNo()));

                    double lnService = loGOCas.MeansInfo().EmployedInfo().getLengthOfService();
                    double lnValue = 0;
                    if (lnService % 1 == 0) {
                        lnValue = lnService;
                    } else {
                        lnValue = lnService * 12;
                    }
                    loListDetl.add(new ReviewAppDetail(false, "", "Length Of Service", String.valueOf(lnValue) + " Year/s"));
                } else if (lsMSector.equalsIgnoreCase("2")) {
                    loListDetl.add(new ReviewAppDetail(false, "", "OFW", ""));
                    loListDetl.add(new ReviewAppDetail(false, "", "Region", parseRegion(loGOCas.MeansInfo().EmployedInfo().getOFWRegion())));
                    loListDetl.add(new ReviewAppDetail(false, "", "Work", parseCategory(loGOCas.MeansInfo().EmployedInfo().getOFWCategory())));

                    ECountryInfo loCntryInfo = poCountry.getCountryInfo(loGOCas.MeansInfo().EmployedInfo().getOFWNation());
                    String lsCountry = loCntryInfo.getCntryNme();
                    loListDetl.add(new ReviewAppDetail(false, "", "Country", lsCountry));
                }
            }

            /*
                Check Business nature if data existed
             */
            if (!loGOCas.MeansInfo().SelfEmployedInfo().getNameOfBusiness().isEmpty() &&
                    !loGOCas.MeansInfo().SelfEmployedInfo().getBusinessTown().isEmpty() &&
                    loGOCas.MeansInfo().SelfEmployedInfo().getMonthlyExpense() != 0) {

                loListDetl.add(new ReviewAppDetail(true, "Business Info", "", ""));
                loListDetl.add(new ReviewAppDetail(false, "", "Business Nature", loGOCas.MeansInfo().SelfEmployedInfo().getNatureOfBusiness()));
                loListDetl.add(new ReviewAppDetail(false, "", "Business Name", loGOCas.MeansInfo().SelfEmployedInfo().getNameOfBusiness()));
                loListDetl.add(new ReviewAppDetail(false, "", "Address", loGOCas.MeansInfo().SelfEmployedInfo().getBusinessAddress()));

                DTownInfo.TownProvinceName loTown = poTown.getTownProvinceName(loGOCas.MeansInfo().SelfEmployedInfo().getBusinessTown());
                String lsTown = loTown.sTownName + ", " + loTown.sProvName;
                loListDetl.add(new ReviewAppDetail(false, "", "Town", lsTown));

                loListDetl.add(new ReviewAppDetail(false, "", "Business Type", parseBusinessType(loGOCas.MeansInfo().SelfEmployedInfo().getBusinessType())));
                loListDetl.add(new ReviewAppDetail(false, "", "Business Size", parseBusinessSize(loGOCas.MeansInfo().SelfEmployedInfo().getOwnershipSize())));
                loListDetl.add(new ReviewAppDetail(false, "", "Monthly Income", FormatUIText.getCurrencyUIFormat(String.valueOf(loGOCas.MeansInfo().SelfEmployedInfo().getIncome()))));
                loListDetl.add(new ReviewAppDetail(false, "", "Monthly Expenses", FormatUIText.getCurrencyUIFormat(String.valueOf(loGOCas.MeansInfo().SelfEmployedInfo().getMonthlyExpense()))));
            }

            if (!loGOCas.MeansInfo().FinancerInfo().getFinancerName().isEmpty() &&
                    loGOCas.MeansInfo().FinancerInfo().getAmount() != 0 &&
                    !loGOCas.MeansInfo().FinancerInfo().getMobileNo().isEmpty()) {

                loListDetl.add(new ReviewAppDetail(true, "Financier Info", "", ""));
                loListDetl.add(new ReviewAppDetail(false, "", "Source", parseFinancier(loGOCas.MeansInfo().FinancerInfo().getSource())));
                loListDetl.add(new ReviewAppDetail(false, "", "Financier Name", loGOCas.MeansInfo().FinancerInfo().getFinancerName()));
                loListDetl.add(new ReviewAppDetail(false, "", "Estimated Amount", FormatUIText.getCurrencyUIFormat(String.valueOf(loGOCas.MeansInfo().FinancerInfo().getAmount()))));

                ECountryInfo loCntryFn = poCountry.getCountryInfo(loGOCas.MeansInfo().FinancerInfo().getCountry());
                String lsCountry = loCntryFn.getCntryNme();
                loListDetl.add(new ReviewAppDetail(false, "", "Country", lsCountry));
                loListDetl.add(new ReviewAppDetail(false, "", "Contact", loGOCas.MeansInfo().FinancerInfo().getMobileNo()));
                loListDetl.add(new ReviewAppDetail(false, "", "Facebook", loGOCas.MeansInfo().FinancerInfo().getFBAccount()));
                loListDetl.add(new ReviewAppDetail(false, "", "Email", loGOCas.MeansInfo().FinancerInfo().getEmailAddress()));
            }

            if (!loGOCas.MeansInfo().PensionerInfo().getSource().isEmpty() &&
                    loGOCas.MeansInfo().PensionerInfo().getAmount() != 0) {
                loListDetl.add(new ReviewAppDetail(true, "Pension Info", "", ""));
                loListDetl.add(new ReviewAppDetail(false, "", "Source", parsePensionSrc(loGOCas.MeansInfo().PensionerInfo().getSource())));
                loListDetl.add(new ReviewAppDetail(false, "", "Estimated Amount", FormatUIText.getCurrencyUIFormat(String.valueOf(loGOCas.MeansInfo().PensionerInfo().getAmount()))));
                loListDetl.add(new ReviewAppDetail(false, "", "Other Info", ""));
                loListDetl.add(new ReviewAppDetail(false, "", "Other Source", loGOCas.MeansInfo().getOtherIncomeNature()));
                //loListDetl.add(new ReviewAppDetail(false, "", "Estimated Income",FormatUIText.getCurrencyUIFormat(String.valueOf(loGOCas.MeansInfo().getOtherIncomeAmount()))));
            }

            if (loGOCas.ApplicantInfo().getCivilStatus().equalsIgnoreCase("1") ||
                    loGOCas.ApplicantInfo().getCivilStatus().equalsIgnoreCase("5")) {

                // TODO: Spouse Info Start
                loListDetl.add(new ReviewAppDetail(true, "Spouse Information", "", ""));
                loListDetl.add(new ReviewAppDetail(false, "", "Last Name", loGOCas.SpouseInfo().PersonalInfo().getLastName()));
                loListDetl.add(new ReviewAppDetail(false, "", "First Name", loGOCas.SpouseInfo().PersonalInfo().getFirstName()));
                loListDetl.add(new ReviewAppDetail(false, "", "Middle Name", loGOCas.SpouseInfo().PersonalInfo().getMiddleName()));
                loListDetl.add(new ReviewAppDetail(false, "", "Suffix", loGOCas.SpouseInfo().PersonalInfo().getSuffixName()));
                loListDetl.add(new ReviewAppDetail(false, "", "Nickname", loGOCas.SpouseInfo().PersonalInfo().getNickName()));

                String lsBirthDte = FormatUIText.formatGOCasBirthdate(loGOCas.SpouseInfo().PersonalInfo().getBirthdate());
                loListDetl.add(new ReviewAppDetail(false, "", "Birthdate", lsBirthDte));

                DTownInfo.TownProvinceName loBPlaceS = poTown.getTownProvinceName(loGOCas.SpouseInfo().PersonalInfo().getBirthPlace());
                String lsSpouseBP = loBPlaceS.sTownName + ", " + loBPlaceS.sProvName;
                loListDetl.add(new ReviewAppDetail(false, "", "Birthplace", lsSpouseBP));

                ECountryInfo loCountryS = poCountry.getCountryInfo(loGOCas.SpouseInfo().PersonalInfo().getCitizenship());
                String lsCitizenS = loCountryS.getNational();
                loListDetl.add(new ReviewAppDetail(false, "", "Citizenship", lsCitizenS));

                int lnMobilQt = loGOCas.SpouseInfo().PersonalInfo().getMobileNoQty();
                String[] lsMobilTp = {"Primary", "2nd", "3rd"};
                for (int i = 0; i < lnMobilQt; i++) {
                    if(lsMobilTp[i] != null){
                        loListDetl.add(new ReviewAppDetail(false, "", lsMobilTp[i] + " Contact No.", loGOCas.SpouseInfo().PersonalInfo().getMobileNo(i)));
                    }
                }

                if (loGOCas.SpouseInfo().PersonalInfo().getPhoneNo(0) != null) {
                    loListDetl.add(new ReviewAppDetail(false, "", "Telephone No.", loGOCas.SpouseInfo().PersonalInfo().getPhoneNo(0)));
                }
                if (loGOCas.SpouseInfo().PersonalInfo().getEmailAddress(0) != null) {
                    loListDetl.add(new ReviewAppDetail(false, "", "Email Address", loGOCas.SpouseInfo().PersonalInfo().getEmailAddress(0)));
                }
                if (loGOCas.SpouseInfo().PersonalInfo().getFBAccount() != null) {
                    loListDetl.add(new ReviewAppDetail(false, "", "Facebook Account", loGOCas.SpouseInfo().PersonalInfo().getFBAccount()));
                }
                if (loGOCas.SpouseInfo().PersonalInfo().getViberAccount() != null) {
                    loListDetl.add(new ReviewAppDetail(false, "", "Viber Account", loGOCas.SpouseInfo().PersonalInfo().getViberAccount()));
                }
                //Spouse Info End

                // TODO: Spouse Residence Info Start
                if (!loGOCas.SpouseInfo().ResidenceInfo().PresentAddress().getLandMark().equalsIgnoreCase("") &&
                        loGOCas.SpouseInfo().ResidenceInfo().PresentAddress().getTownCity() != null &&
                        loGOCas.SpouseInfo().ResidenceInfo().PresentAddress().getBarangay() != null) {
                    loListDetl.add(new ReviewAppDetail(true, "Spouse Residence Information", "", ""));
                    loListDetl.add(new ReviewAppDetail(false, "", "Landmark", loGOCas.SpouseInfo().ResidenceInfo().PresentAddress().getLandMark()));
                    loListDetl.add(new ReviewAppDetail(false, "", "House No.", loGOCas.SpouseInfo().ResidenceInfo().PresentAddress().getHouseNo()));
                    loListDetl.add(new ReviewAppDetail(false, "", "Phase/Lot No./Sitio", loGOCas.SpouseInfo().ResidenceInfo().PresentAddress().getAddress1()));
                    loListDetl.add(new ReviewAppDetail(false, "", "Street", loGOCas.SpouseInfo().ResidenceInfo().PresentAddress().getAddress2()));

                    DTownInfo.TownProvinceName loSpousRs = poTown.getTownProvinceName(loGOCas.SpouseInfo().ResidenceInfo().PresentAddress().getTownCity());
                    loListDetl.add(new ReviewAppDetail(false, "", "Province.", loSpousRs.sProvName));
                    loListDetl.add(new ReviewAppDetail(false, "", "Town/City.", loSpousRs.sTownName));

                    DBarangayInfo.BrgyTownProvNames loBrgySps = poBarangay.getBrgyTownProvName(loGOCas.SpouseInfo().ResidenceInfo().PresentAddress().getBarangay());
                    loListDetl.add(new ReviewAppDetail(false, "", "Barangay.", loBrgySps.sBrgyName));
                }
                //Spouse Residence Info End

                // TODO: Spouse Employment Info Start
                String lsCompLvl = loGOCas.SpouseMeansInfo().EmployedInfo().getCompanyLevel();
                String lsEmpLvel = loGOCas.SpouseMeansInfo().EmployedInfo().getEmployeeLevel();
                if (lsCompLvl != null &&
                lsEmpLvel != null) {
                    loListDetl.add(new ReviewAppDetail(true, "Spouse Employment Information", "", ""));
                    if (loGOCas.SpouseMeansInfo().EmployedInfo().getEmploymentSector().equalsIgnoreCase("1")) {
                        // Private Sector
                        int lnCmpLvl = Integer.parseInt(loGOCas.SpouseMeansInfo().EmployedInfo().getCompanyLevel());
                        loListDetl.add(new ReviewAppDetail(false, "", "Company Level", CreditAppConstants.COMPANY_LEVEL[lnCmpLvl]));
                        int lmEmpLvl = Integer.parseInt(loGOCas.SpouseMeansInfo().EmployedInfo().getEmployeeLevel());
                        loListDetl.add(new ReviewAppDetail(false, "", "Employee Level", CreditAppConstants.EMPLOYEE_LEVEL[lmEmpLvl]));

                        loListDetl.add(new ReviewAppDetail(false, "", "Business Industry", loGOCas.SpouseMeansInfo().EmployedInfo().getNatureofBusiness()));
                        loListDetl.add(new ReviewAppDetail(false, "", "Company Name", loGOCas.SpouseMeansInfo().EmployedInfo().getCompanyName()));

                        DTownInfo.TownProvinceName loAddrs = poTown.getTownProvinceName(loGOCas.SpouseMeansInfo().EmployedInfo().getCompanyTown());
                        loListDetl.add(new ReviewAppDetail(false, "", "Street/Bldg./Barangay", loGOCas.SpouseMeansInfo().EmployedInfo().getCompanyAddress()));
                        loListDetl.add(new ReviewAppDetail(false, "", "Province", loAddrs.sProvName));
                        loListDetl.add(new ReviewAppDetail(false, "", "Town/City", loAddrs.sTownName));

                        // Concatenated Type of Address
                        // String lsBizTown = loAddrs.sTownName + ", " + loAddrs.sProvName;
                        // String lsBizAdrs = loGOCas.SpouseMeansInfo().EmployedInfo().getCompanyAddress() + ", " + lsBizTown;
                        // loListDetl.add(new ReviewAppDetail(false, "", "Business Address", lsBizAdrs));

                        String lsPosition = poJobx.getOccupationName(loGOCas.SpouseMeansInfo().EmployedInfo().getPosition());
                        loListDetl.add(new ReviewAppDetail(false, "", "Job Title", lsPosition));
                        loListDetl.add(new ReviewAppDetail(false, "", "Specific Job/Position", loGOCas.SpouseMeansInfo().EmployedInfo().getJobDescription()));

                        loListDetl.add(new ReviewAppDetail(false, "", "Employment Status", parseEmployeeStatus(loGOCas.SpouseMeansInfo().EmployedInfo().getEmployeeStatus())));

                        double lnService = loGOCas.SpouseMeansInfo().EmployedInfo().getLengthOfService();
                        double lnValue = 0;
                        if (lnService % 1 == 0) {
                            lnValue = lnService;
                        } else {
                            lnValue = lnService * 12;
                        }
                        loListDetl.add(new ReviewAppDetail(false, "", "Length of Service", String.valueOf(lnValue) + " Year/s"));
                        loListDetl.add(new ReviewAppDetail(false, "", "Gross Monthly Income", FormatUIText.getCurrencyUIFormat(String.valueOf(loGOCas.SpouseMeansInfo().EmployedInfo().getSalary()))));
                        loListDetl.add(new ReviewAppDetail(false, "", "Company Contact No.", String.valueOf(loGOCas.SpouseMeansInfo().EmployedInfo().getCompanyNo())));
                    } else if (loGOCas.SpouseMeansInfo().EmployedInfo().getEmploymentSector().equalsIgnoreCase("0")) {
                        // Government Sector
                        loListDetl.add(new ReviewAppDetail(false, "", "Government Sector", ""));
                        loListDetl.add(new ReviewAppDetail(false, "", "Military Personnel", parseMilitary(loGOCas.SpouseMeansInfo().EmployedInfo().IsMilitaryPersonel())));
                        loListDetl.add(new ReviewAppDetail(false, "", "Uniformed Personnel", parseMilitary(loGOCas.SpouseMeansInfo().EmployedInfo().IsUniformedPersonel())));
                        loListDetl.add(new ReviewAppDetail(false, "", "Government Level", parseGovLevel(loGOCas.SpouseMeansInfo().EmployedInfo().getGovernmentLevel())));
                        loListDetl.add(new ReviewAppDetail(false, "", "Government Institution", loGOCas.SpouseMeansInfo().EmployedInfo().getCompanyName()));
                        loListDetl.add(new ReviewAppDetail(false, "", "Address", loGOCas.SpouseMeansInfo().EmployedInfo().getCompanyAddress()));

                        DTownInfo.TownProvinceName loTown = poTown.getTownProvinceName(loGOCas.SpouseMeansInfo().EmployedInfo().getCompanyTown());
                        String lsCompTown = loTown.sTownName + ", " + loTown.sProvName;
                        loListDetl.add(new ReviewAppDetail(false, "", "Town", lsCompTown));

                        String lsPosition = poJobx.getOccupationName(loGOCas.SpouseMeansInfo().EmployedInfo().getPosition());
                        loListDetl.add(new ReviewAppDetail(false, "", "Position", lsPosition));
                        loListDetl.add(new ReviewAppDetail(false, "", "Specific Job", loGOCas.SpouseMeansInfo().EmployedInfo().getJobDescription()));
                        loListDetl.add(new ReviewAppDetail(false, "", "Estimated Salary", String.valueOf(loGOCas.SpouseMeansInfo().EmployedInfo().getSalary())));
                        loListDetl.add(new ReviewAppDetail(false, "", "Contact", loGOCas.SpouseMeansInfo().EmployedInfo().getCompanyNo()));

                        double lnService = loGOCas.SpouseMeansInfo().EmployedInfo().getLengthOfService();
                        double lnValue = 0;
                        if (lnService % 1 == 0) {
                            lnValue = lnService;
                        } else {
                            lnValue = lnService * 12;
                        }
                        loListDetl.add(new ReviewAppDetail(false, "", "Length Of Service", String.valueOf(lnValue) + " Year/s"));

                    } else {
                        // OFW
                        loListDetl.add(new ReviewAppDetail(false, "", "OFW", ""));
                        loListDetl.add(new ReviewAppDetail(false, "", "Region", parseRegion(loGOCas.SpouseMeansInfo().EmployedInfo().getOFWRegion())));
                        loListDetl.add(new ReviewAppDetail(false, "", "Work", parseCategory(loGOCas.SpouseMeansInfo().EmployedInfo().getOFWCategory())));

                        ECountryInfo loCntryInfo = poCountry.getCountryInfo(loGOCas.SpouseMeansInfo().EmployedInfo().getOFWNation());
                        String lsCountry = loCntryInfo.getCntryNme();
                        loListDetl.add(new ReviewAppDetail(false, "", "Country", lsCountry));

                    }
                }
                //Spouse Employment Info End

                // TODO: Spouse Self Employed Info Start
                if (!loGOCas.SpouseMeansInfo().SelfEmployedInfo().getNameOfBusiness().equalsIgnoreCase("") &&
                        loGOCas.SpouseMeansInfo().SelfEmployedInfo().getNatureOfBusiness() != null) {
                    Log.e("spsSlefEmplyed", loGOCas.SpouseMeansInfo().SelfEmployedInfo().toJSONString());
                    loListDetl.add(new ReviewAppDetail(true, "Spouse Business Information", "", ""));
                    loListDetl.add(new ReviewAppDetail(false, "", "Business Name", loGOCas.SpouseMeansInfo().SelfEmployedInfo().getNameOfBusiness()));
                    int lnBzNatur = Integer.parseInt(loGOCas.SpouseMeansInfo().SelfEmployedInfo().getNatureOfBusiness());
                    loListDetl.add(new ReviewAppDetail(false, "", "Nature of Business", CreditAppConstants.BUSINESS_NATURE[lnBzNatur]));

                    String lsTown = loGOCas.SpouseMeansInfo().SelfEmployedInfo().getBusinessTown();
                    DTownInfo.TownProvinceName loAddrs = poTown.getTownProvinceName(lsTown);
                    loListDetl.add(new ReviewAppDetail(false, "", "Street/Bldg./Barangay", loGOCas.SpouseMeansInfo().SelfEmployedInfo().getBusinessAddress()));
                    loListDetl.add(new ReviewAppDetail(false, "", "Province", loAddrs.sProvName));
                    loListDetl.add(new ReviewAppDetail(false, "", "Town/City", loAddrs.sTownName));

                    // Concatenated Type of Address
                    // String lsBizTown = loAddrs.sTownName + ", " + loAddrs.sProvName;
                    // String lsBizAdrs = loGOCas.SpouseMeansInfo().SelfEmployedInfo().getBusinessAddress() + ", " + lsBizTown;
                    // loListDetl.add(new ReviewAppDetail(false, "", "Business Address", lsBizAdrs));

                    int lnBizType = Integer.parseInt(loGOCas.SpouseMeansInfo().SelfEmployedInfo().getBusinessType());
                    loListDetl.add(new ReviewAppDetail(false, "", "Business Type", CreditAppConstants.BUSINESS_TYPE[lnBizType]));

                    int lnBizSize = Integer.parseInt(loGOCas.SpouseMeansInfo().SelfEmployedInfo().getOwnershipSize());
                    loListDetl.add(new ReviewAppDetail(false, "", "Business Size", CreditAppConstants.BUSINESS_SIZE[lnBizSize]));

                    double lnService = loGOCas.SpouseMeansInfo().SelfEmployedInfo().getBusinessLength();
                    double lnValue = 0;
                    if (lnService % 1 == 0) {
                        lnValue = lnService;
                    } else {
                        lnValue = lnService * 12;
                    }
                    loListDetl.add(new ReviewAppDetail(false, "", "Length of Service", String.valueOf(lnValue) + " Year/s"));

                    loListDetl.add(new ReviewAppDetail(false, "", "Gross Monthly Income", FormatUIText.getCurrencyUIFormat(String.valueOf(loGOCas.SpouseMeansInfo().SelfEmployedInfo().getIncome()))));
                    loListDetl.add(new ReviewAppDetail(false, "", "Monthly Expenses", FormatUIText.getCurrencyUIFormat(String.valueOf(loGOCas.SpouseMeansInfo().SelfEmployedInfo().getMonthlyExpense()))));
                }
                // Spouse Self Employed Info End

                // TODO: Spouse Pension Info Start
                if (loGOCas.SpouseMeansInfo().PensionerInfo().getSource() != null &&
                        loGOCas.SpouseMeansInfo().PensionerInfo().getAmount() != 0) {
                    Log.e("SpousePensionerVal", loGOCas.SpouseMeansInfo().PensionerInfo().toJSONString());
                    loListDetl.add(new ReviewAppDetail(true, "Spouse Pension Information", "", ""));
                    String[] lsSource = {"Government", "Private"};
                    int lnSource = Integer.parseInt(loGOCas.SpouseMeansInfo().PensionerInfo().getSource());
                    loListDetl.add(new ReviewAppDetail(false, "", "Pension Source", lsSource[lnSource]));
                    loListDetl.add(new ReviewAppDetail(false, "", "Pension Income", FormatUIText.getCurrencyUIFormat(String.valueOf(loGOCas.SpouseMeansInfo().PensionerInfo().getAmount()))));

//                        if(loGOCas.SpouseMeansInfo().getOtherIncomeNature() != null) {
//                            loListDetl.add(new ReviewAppDetail(true, "Other Source of Income", "", ""));
//                            loListDetl.add(new ReviewAppDetail(false, "", "Nature of Income", loGOCas.SpouseMeansInfo().getOtherIncomeNature()));
//                            loListDetl.add(new ReviewAppDetail(false, "", "Range of Income", String.valueOf(loGOCas.SpouseMeansInfo().getOtherIncomeAmount())));
//                        }
                }
                // Spouse Pension Info End

            }
            if (loGOCas.DisbursementInfo().Expenses().getElectricBill() > 0.0 || loGOCas.DisbursementInfo().Expenses().getFoodAllowance() > 0.0 ||
                    loGOCas.DisbursementInfo().Expenses().getWaterBill() > 0.0 || loGOCas.DisbursementInfo().Expenses().getLoanAmount() > 0.0 ||
                    !loGOCas.DisbursementInfo().BankAccount().getBankName().isEmpty() || !loGOCas.DisbursementInfo().CreditCard().getBankName().isEmpty()) {
                loListDetl.add(new ReviewAppDetail(true, "Disbursement", "", ""));
                if (loGOCas.DisbursementInfo().Expenses().getElectricBill() > 0.0) {
                    loListDetl.add(new ReviewAppDetail(false, "", "Electricity Bill", String.valueOf(loGOCas.DisbursementInfo().Expenses().getElectricBill())));
                }
                if (loGOCas.DisbursementInfo().Expenses().getFoodAllowance() > 0.0) {
                    loListDetl.add(new ReviewAppDetail(false, "", "Food Allowance", String.valueOf(loGOCas.DisbursementInfo().Expenses().getFoodAllowance())));
                }
                if (loGOCas.DisbursementInfo().Expenses().getWaterBill() > 0.0) {
                    loListDetl.add(new ReviewAppDetail(false, "", "Water Bill", String.valueOf(loGOCas.DisbursementInfo().Expenses().getWaterBill())));
                }
                if (loGOCas.DisbursementInfo().Expenses().getLoanAmount() > 0.0) {
                    loListDetl.add(new ReviewAppDetail(false, "", "Loan Amount", String.valueOf(loGOCas.DisbursementInfo().Expenses().getLoanAmount())));
                }
                if (!loGOCas.DisbursementInfo().BankAccount().getBankName().isEmpty()) {
                    loListDetl.add(new ReviewAppDetail(false, "", "Bank Name", loGOCas.DisbursementInfo().BankAccount().getBankName()));
                    loListDetl.add(new ReviewAppDetail(false, "", "Account Type", loGOCas.DisbursementInfo().BankAccount().getAccountType()));
                }
                if (!loGOCas.DisbursementInfo().CreditCard().getBankName().isEmpty()) {
                    loListDetl.add(new ReviewAppDetail(false, "", "Bank Name", loGOCas.DisbursementInfo().CreditCard().getBankName()));
                    loListDetl.add(new ReviewAppDetail(false, "", "Account Limit", String.valueOf(loGOCas.DisbursementInfo().CreditCard().getCreditLimit())));
                    //loListDetl.add(new ReviewAppDetail(false, "", "Length of Use",String.valueOf(loGOCas.DisbursementInfo().CreditCard().getMemberSince())));
                }
            }
//                DEPENDENT
            if(poInfo.getDependnt() != null){
                org.json.JSONObject loDependent = new org.json.JSONObject(poInfo.getDependnt());
                org.json.JSONArray loDeptArray = loDependent.getJSONArray("children");
                if (loDeptArray.length() > 0) {
                    loListDetl.add(new ReviewAppDetail(true, "Dependent Info", "", ""));
                    for (int x = 0; x < loDeptArray.length(); x++) {
                        int i = x;
                        org.json.JSONObject loExp = loDeptArray.getJSONObject(x);
                        loListDetl.add(new ReviewAppDetail(false, "", "Dependent " + (i + 1), ""));
                        loListDetl.add(new ReviewAppDetail(false, "", "Fullname ", loExp.get("sFullName").toString()));
                        loListDetl.add(new ReviewAppDetail(false, "", "Age ", loExp.get("nDepdAgex").toString()));
                        loListDetl.add(new ReviewAppDetail(false, "", "Relationship ", parseDpdntRelationship(loExp.get("sReltnCde").toString())));
                        if (loExp.get("cIsPupilx").toString().equalsIgnoreCase("1")) {

                            loListDetl.add(new ReviewAppDetail(false, "", "School Name ", loExp.get("sSchlName").toString()));
                            loListDetl.add(new ReviewAppDetail(false, "", "School Type ", parseDpdntSchoolType(loExp.get("cIsPrivte").toString())));
                            loListDetl.add(new ReviewAppDetail(false, "", "Educational Level ", parseDpdntEducLvl(loExp.get("sEducLevl").toString())));
                            if (loExp.get("cIsSchlrx").toString().equalsIgnoreCase("1")) {
                                loListDetl.add(new ReviewAppDetail(false, "", "Schoolar", "Yes"));
                            }
                            loListDetl.add(new ReviewAppDetail(false, "", "School Address", loExp.get("sSchlAddr").toString()));

                            DTownInfo.TownProvinceName SchooPlace = poTown.getTownProvinceName(loExp.get("sSchlTown").toString());
                            String SchooBirthPlace = SchooPlace.sTownName + ", " + SchooPlace.sProvName;
                            loListDetl.add(new ReviewAppDetail(false, "", "School Town ", SchooBirthPlace));

                        }

                        if (loExp.get("cHasWorkx").toString().equalsIgnoreCase("1")) {
                            loListDetl.add(new ReviewAppDetail(false, "", "Work Type", parseDpdntWorkType(loExp.get("cWorkType").toString())));
                            loListDetl.add(new ReviewAppDetail(false, "", "Company Name", loExp.get("sCompanyx").toString()));
                        }
                    }
                }
            }

//                OTHER INFO
            org.json.JSONObject loDisb = new org.json.JSONObject(poInfo.getOthrInfo());
            org.json.JSONArray loArray = loDisb.getJSONArray("personal_reference");

            loListDetl.add(new ReviewAppDetail(true, "Other Info", "", ""));

            loListDetl.add(new ReviewAppDetail(false, "", "Unit User", parseUnitUser(loDisb.get("sUnitUser").toString())));
            loListDetl.add(new ReviewAppDetail(false, "", "Unit Payer", parseUnitPayor(loDisb.get("sUnitPayr").toString())));
            loListDetl.add(new ReviewAppDetail(false, "", "Purpose ", parseUnitPurpose(loDisb.get("sPurposex").toString())));
            loListDetl.add(new ReviewAppDetail(false, "", "Source Info", loDisb.get("sSrceInfo").toString()));

            loListDetl.add(new ReviewAppDetail(true, "Personal References", "", ""));
            for (int x = 0; x < loArray.length(); x++) {
                int i = x;
                org.json.JSONObject loExp = loArray.getJSONObject(x);
                loListDetl.add(new ReviewAppDetail(false, "", "Reference " + (i + 1), ""));
                loListDetl.add(new ReviewAppDetail(false, "", "Fullname ", loExp.get("sRefrNmex").toString()));
                loListDetl.add(new ReviewAppDetail(false, "", "Mobile No. ", loExp.get("sRefrMPNx").toString()));
                String rfBirthPlace = loExp.get("sRefrTown").toString();

                DTownInfo.TownProvinceName loTown = poTown.getTownProvinceName(rfBirthPlace);
                String lsTown = loTown.sTownName + ", " + loTown.sProvName;
                loListDetl.add(new ReviewAppDetail(false, "", "Address ", loExp.get("sRefrAddx").toString()));
                loListDetl.add(new ReviewAppDetail(false, "", "Town ", lsTown));
            }

//                CO-MAKER
            org.json.JSONObject coDisb = new org.json.JSONObject(poInfo.getComakerx());
            loListDetl.add(new ReviewAppDetail(true, "Co-Maker", "", ""));
            loListDetl.add(new ReviewAppDetail(false, "", "Relationship", parseCoMakerRel(coDisb.get("sReltnCde").toString())));
            loListDetl.add(new ReviewAppDetail(false, "", "Full Name", coDisb.get("sLastName").toString() +
                    ", " + coDisb.get("sFrstName").toString() + " " + coDisb.get("sMiddName").toString() + " " + coDisb.get("sSuffixNm").toString()));
            loListDetl.add(new ReviewAppDetail(false, "", "Nickname", coDisb.get("sNickName").toString()));

            String coBirthDate = FormatUIText.formatGOCasBirthdate(coDisb.get("dBirthDte").toString());
            loListDetl.add(new ReviewAppDetail(false, "", "Birth Date", coBirthDate));

                DTownInfo.TownProvinceName coBPlace = poTown.getTownProvinceName(coDisb.get("sBirthPlc").toString());
                String coBirthPlace = coBPlace.sTownName + ", " + coBPlace.sProvName;
            loListDetl.add(new ReviewAppDetail(false, "", "Birth Place", coBirthPlace));
            loListDetl.add(new ReviewAppDetail(false, "", "Source of Income", parseCoMakerSrcIncom(coDisb.get("cIncmeSrc").toString())));

            org.json.JSONArray loCmContactArr = coDisb.getJSONArray("mobile_number");
            for (int x = 0; x < loCmContactArr.length(); x++) {
                org.json.JSONObject loCmContact = loCmContactArr.getJSONObject(x);
                loListDetl.add(new ReviewAppDetail(false, "", "Mobile No. ",
                        loCmContact.get("sMobileNo").toString() + ", " + parseSimType(loCmContact.get("cPostPaid").toString())));
            }
            loListDetl.add(new ReviewAppDetail(false, "", "Facebook Account", coDisb.get("sFBAcctxx").toString()));

//              CO-MAKER RESIDENCE INFO

            org.json.JSONObject coResIdx = new org.json.JSONObject(poInfo.getCmResidx());
            org.json.JSONObject coResIdxx = coResIdx.getJSONObject("present_address");
            loListDetl.add(new ReviewAppDetail(true, "CoMaker Present Residence Info", "", ""));
            loListDetl.add(new ReviewAppDetail(false, "", "Landmark", coResIdxx.get("sLandMark").toString()));
            loListDetl.add(new ReviewAppDetail(false, "", "House No", coResIdxx.get("sHouseNox").toString()));
            loListDetl.add(new ReviewAppDetail(false, "", "Sitio/Lot No.", coResIdxx.get("sAddress2").toString()));
            loListDetl.add(new ReviewAppDetail(false, "", "Street", coResIdxx.get("sAddress1").toString()));

            DBarangayInfo.BrgyTownProvNames coPresent = poBarangay.getBrgyTownProvName(coResIdxx.get("sBrgyIDxx").toString());
            loListDetl.add(new ReviewAppDetail(false, "", "Barangay", coPresent.sBrgyName));
            loListDetl.add(new ReviewAppDetail(false, "", "Town", coPresent.sTownName));
            loListDetl.add(new ReviewAppDetail(false, "", "Province", coPresent.sProvName));

            loListDetl.add(new ReviewAppDetail(false, "", "House Ownership", parseHouseOwn(coResIdx.get("cOwnershp").toString())));
            if(coResIdx.get("cOwnershp").toString().equalsIgnoreCase("0")){
                loListDetl.add(new ReviewAppDetail(false, "", "House Hold", parseHouseHold(coResIdx.get("cOwnOther").toString())));
            } else if (coResIdx.get("cOwnershp").toString().equalsIgnoreCase("1")) {
                org.json.JSONObject joRent = coResIdx.getJSONObject("rent_others");
                loListDetl.add(new ReviewAppDetail(false, "", "HouseHolds", parseHouseHold(joRent.get("cRntOther").toString())));

                String lsExp = joRent.get("nRentExps").toString();
                loListDetl.add(new ReviewAppDetail(false, "", "Monthly Rent", lsExp));
                loListDetl.add(new ReviewAppDetail(false, "", "Length of Stay", joRent.get("nLenStayx").toString()));
            } else if (coResIdx.get("cOwnershp").toString().equalsIgnoreCase("2")) {
                loListDetl.add(new ReviewAppDetail(false, "", "House Owner", loGOCas.CoMakerInfo().ResidenceInfo().getCareTakerRelation()));
            }

            loListDetl.add(new ReviewAppDetail(false, "", "House Type", parseHouseType(coResIdx.get("cHouseTyp").toString())));
            loListDetl.add(new ReviewAppDetail(false, "", "Has Garage", parseGarage(coResIdx.get("cGaragexx").toString())));
            loListDetail = loListDetl;
            return loListDetail;

        } catch (Exception e) {
            e.printStackTrace();
            message = getLocalMessage(e);
            return null;
        }
    }


    @Override
    public int Validate(Object args) {

        return 1;
    }

    @Override
    public String Save(Object args){
        try {
            ECreditApplicantInfo loInfo = (ECreditApplicantInfo) args;

            ECreditApplication loExist = poDao.CheckIfApplicantExist(
                    loInfo.getClientNm(),
                    loInfo.getBranchCd(),
                    loInfo.getAppliedx(),
                    loInfo.getDownPaym(),
                    loInfo.getCreatedx());

            if(loExist == null) {

                String lsTransNo = CreateUniqueID();

                ECreditApplication loDetail = new ECreditApplication();
                GoCasBuilder loModel = new GoCasBuilder(loInfo);

                loDetail.setTransNox(lsTransNo);
                loDetail.setBranchCd(loInfo.getBranchCd());
                loDetail.setClientNm(loInfo.getClientNm());
                loDetail.setUnitAppl(loInfo.getAppliedx());
                loDetail.setSourceCD("APP");
                loDetail.setDetlInfo(loModel.getConstructedDetailedInfo());
                loDetail.setDownPaym(loInfo.getDownPaym());
                loDetail.setCreatedx(poDao.GetUserID());
                loDetail.setDateCreatedx(loInfo.getCreatedx());
                loDetail.setTransact(loInfo.getTransact());
                loDetail.setTimeStmp(AppConstants.DATE_MODIFIED());
                loDetail.setTranStat("0");
                loDetail.setSendStat("0");
                loDetail.setRemarksx(loInfo.getRemarksx());

                poDao.Save(loDetail);

                return lsTransNo;
            }

            return loExist.getTransNox();
        } catch (Exception e) {
            e.printStackTrace();
            message = getLocalMessage(e);
            return null;
        }
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public LiveData<List<EBarangayInfo>> GetBarangayList(String args) {
        return null;
    }

    @Override
    public LiveData<List<DTownInfo.TownProvinceInfo>> GetTownProvinceList() {
        return poTown.getTownProvinceInfo();
    }

    @Override
    public LiveData<List<ECountryInfo>> GetCountryList() {
        return null;
    }

    @Override
    public LiveData<List<EOccupationInfo>> GetOccupations() {
        return null;
    }

    String parseGender(String value){
        if(value.equalsIgnoreCase("0")){
            return "Male";
        } else if(value.equalsIgnoreCase("1")){
            return "Female";
        } else {
            return "LGBT";
        }
    }

    String parseCivilStatus(String value){
        return CreditAppConstants.CIVIL_STATUS[Integer.parseInt(value)];
    }

    String parseSimType(String value){
        if(!"".equalsIgnoreCase(value)){
            return CreditAppConstants.MOBILE_NO_TYPE[Integer.parseInt(value)];
        }else{
            return "";
        }
    }

    String parseHouseOwn(String value){
        if(value.equalsIgnoreCase("0")){
            return "Owned";
        } else if(value.equalsIgnoreCase("1")){
            return "Rent";
        } else {
            return "Care-Taker";
        }
    }

    String parseHouseHold(String value){
        return CreditAppConstants.HOUSEHOLDS[Integer.parseInt(value)];
    }

    String parseHouseType(String value){
        return CreditAppConstants.HOUSE_TYPE[Integer.parseInt(value)];
    }

    String parseGarage(String value){
        if(value.equalsIgnoreCase("1")){
            return "Yes";
        } else {
            return "No";
        }
    }

    String parseMeansPrimary(String value){
        switch (value){
            case "0":
                return "Employment";
            case "1":
                return "Self Employed";
            case "2":
                return "Finance";
        }
        return "Pensioner";
    }

    String parseSector(String value){
        switch (value){
            case "0":
                return "Government";
            case "1":
                return "Private";
        }
        return "OFW";
    }

    String parseCompLevel(String value){
        return CreditAppConstants.COMPANY_LEVEL[Integer.parseInt(value)];
    }

    String parseEmployeeLevel(String value){
        return CreditAppConstants.EMPLOYEE_LEVEL[Integer.parseInt(value)];
    }

    String parseEmployeeStatus(String value){
        switch (value){
            case "R":
                return "Regular";
            case "P":
                return "Probationary";
            case "C":
                return "Contractual";
        }
        return "Seasonal";
    }

    String parseMilitary(String value){
        if(value.equalsIgnoreCase("N")){
            return "NO";
        } else {
            return "YES";
        }
    }

    String parseGovLevel(String value){
        return CreditAppConstants.GOVERMENT_LEVEL[Integer.parseInt(value)];
    }

    String parseRegion(String value){
        return CreditAppConstants.OFW_REGION[Integer.parseInt(value)];
    }

    String parseCategory(String value){
        return CreditAppConstants.OFW_CATEGORY[Integer.parseInt(value)];
    }

    String parseBusinessType(String value){
        return CreditAppConstants.BUSINESS_TYPE[Integer.parseInt(value)];
    }

    String parseBusinessSize(String value){
        return CreditAppConstants.BUSINESS_SIZE[Integer.parseInt(value)];
    }

    String parseFinancier(String value){
        return CreditAppConstants.FINANCE_SOURCE[Integer.parseInt(value)];
    }

    String parsePensionSrc(String value){
        return CreditAppConstants.PENSION_SECTOR[Integer.parseInt(value)];
    }
    String parseUnitUser(String value){
        if(!CreditAppConstants.UNIT_USER[Integer.parseInt(value)].equalsIgnoreCase("Others")) {
            return CreditAppConstants.UNIT_USER[Integer.parseInt(value)];
        } else {
            return CreditAppConstants.UNIT_USER_OTHERS[Integer.parseInt(value)];
        }
    }

    String parseUnitPayor(String value){
        if(!CreditAppConstants.UNIT_USER[Integer.parseInt(value)].equalsIgnoreCase("Others")) {
            return CreditAppConstants.UNIT_USER[Integer.parseInt(value)];
        } else {
            return CreditAppConstants.UNIT_PAYER[Integer.parseInt(value)];
        }
    }
    String parseUnitPurpose(String value){
        return CreditAppConstants.UNIT_PURPOSE[Integer.parseInt(value)];
    }
    String parseDpdntRelationship(String value){
        if(value.isEmpty()){
            return "";
        }
        return CreditAppConstants.DEPENDENT_RELATIONSHIP[Integer.parseInt(value)];
    }
    String parseDpdntSchoolType(String value){
        return CreditAppConstants.SCHOOL_TYPE[Integer.parseInt(value)];
    }
    String parseDpdntEducLvl(String value){
        return CreditAppConstants.SCHOOL_LEVEL[Integer.parseInt(value)];
    }
    String parseDpdntWorkType(String value){
        return CreditAppConstants.EMPLOYMENT_SECTOR[Integer.parseInt(value)];
    }
    String parseCoMakerRel(String value){
        return CreditAppConstants.CO_MAKER_RELATIONSHIP[Integer.parseInt(value)];
    }
    String parseCoMakerSrcIncom(String value){
        return CreditAppConstants.CO_MAKER_INCOME_SOURCE[Integer.parseInt(value)];
    }

    private String CreateUniqueID(){
        String lsUniqIDx = "";
        try{
            String lsBranchCd = "MX01";
            String lsCrrYear = new SimpleDateFormat("yy", Locale.getDefault()).format(new Date());
            StringBuilder loBuilder = new StringBuilder(lsBranchCd);
            loBuilder.append(lsCrrYear);

            int lnLocalID = poDao.GetRowsCountForID() + 1;
            String lsPadNumx = String.format("%05d", lnLocalID);
            loBuilder.append(lsPadNumx);
            lsUniqIDx = loBuilder.toString();
        } catch (Exception e){
            e.printStackTrace();
            Log.e(TAG, e.getMessage());
        }
        Log.d(TAG, lsUniqIDx);
        return lsUniqIDx;
    }
}


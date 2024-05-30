package org.rmj.g3appdriver.GCircle.Api;

import android.app.Application;
import android.util.Log;

import org.rmj.g3appdriver.dev.Api.WebApi;

public class GCircleApi extends WebApi {
    private static final String TAG = GCircleApi.class.getSimpleName();


    private static final String URL_AUTH_EMPLOYEE = "security/mlogin.php";
    private static final String URL_CREATE_ACCOUNT = "security/signup.php";
    private static final String URL_FORGOT_PASSWORD = "security/forgotpswd.php";
    private static final String URL_CHANGE_PASSWORD = "security/acctupdate.php";
    private static final String DEACTIVATE_ACCOUNT = "security/account_deactivate.php";
    private static final String URL_KNOX = "samsung/knox.php";
    private static final String IMPORT_BRANCH_PERFORMANCE = "integsys/bullseye/import_mc_branch_performance.php";
    private static final String IMPORT_AREA_PERFORMANCE = "integsys/bullseye/import_mc_area_performance.php";
    private static final String URL_IMPORT_BARANGAY = "integsys/param/download_barangay.php";
    private static final String URL_IMPORT_TOWN = "integsys/param/download_town.php";
    private static final String URL_IMPORT_PROVINCE = "integsys/param/download_province.php";
    private static final String URL_IMPORT_COUNTRY = "integsys/param/download_country.php";
    private static final String URL_IMPORT_MC_MODEL = "integsys/param/download_mc_model.php";
    private static final String URL_IMPORT_MC_COLOR = "integsys/param/download_mc_model_color.php";
    private static final String URL_IMPORT_MC_PRICES = "gcircle/ganado/download_cash_price.php";
    private static final String URL_IMPORT_MC_MODEL_PRICE = "integsys/param/download_mc_model_price.php";
    private static final String URL_IMPORT_BRAND = "integsys/param/download_brand.php";
    private static final String URL_IMPORT_MC_CATEGORY = "integsys/param/download_mc_category.php";
    private static final String URL_IMPORT_TERM_CATEGORY = "integsys/param/download_mc_term_category.php";
    private static final String URL_IMPORT_BRANCHES = "integsys/param/download_branch.php";
    private static final String URL_IMPORT_FILE_CODE = "integsys/param/download_edoc_file_type.php";
    private static final String URL_IMPORT_OCCUPATIONS = "integsys/param/download_occupation.php";
    private static final String URL_SUBMIT_ONLINE_APPLICATION = "integsys/gocas/gocas_save.php";
    private static final String URL_GET_INSTALLMENT_TERMS = "integsys/gocas/import_installment_terms.php";
    private static final String URL_IMPORT_RAFFLE_BASIS = "promo/param/download_raffle_entry_basis_all.php";
    private static final String URL_REQUEST_ONLINE_APPLICATIONS = "integsys/gocas/gocas_request_status.php";
    private static final String URL_IMPORT_ONLINE_APPLICATIONS = "integsys/gocas/gocas_request_application.php";

    private static final String URL_SUBMIT_CASHCOUNT = "integsys/cashcount/submit_cash_count.php";
    private static final String URL_QUICK_SEARCH = "integsys/paramqry/cash_count_rqst_officer.php";

    private static final String URL_SAVE_ITINERARY = "engineering/submit_itinerary.php";
    private static final String URL_DOWNLOAD_ITINERARY = "engineering/download_itinerary.php";
    private static final String URL_DOWNLOAD_ITINERARY_USERS = "engineering/get_itinerary_users.php";
    private static final String URL_SCA_REQUEST = "integsys/param/download_sca_request.php";
    private static final String URL_SAVE_APPROVAL = "integsys/codeapproval/save_approval.php";
    private static final String URL_LOAD_APPLICATION_APPROVAL = "integsys/codeapproval/code_load.php";
    private static final String URL_APPLICATION_APPROVE = "integsys/codeapproval/code_decide.php";
    private static final String URL_DOWNLOAD_DCP = "integsys/dcp/dcp_download.php";
    private static final String URL_DCP_SUBMIT = "integsys/dcp/dcp_submit.php";
    private static final String URL_POST_DCP_MASTER = "integsys/dcp/dcp_post.php";
    private static final String URL_GET_AR_CLIENT = "query/client/get_ar_client.php";
    private static final String URL_GET_REG_CLIENT = "query/client/get_reg_client.php";
    private static final String URL_UPDATE_ADDRESS = "integsys/dcp/request_address_update.php";
    private static final String URL_UPDATE_MOBILE = "integsys/dcp/request_mobile_update.php";
    private static final String URL_DOWNLOAD_BANK_INFO = "integsys/param/download_banks.php";
    private static final String URL_POST_SELFIELOG = "integsys/hcm/selfie_log.php";
    private static final String URL_BRANCH_LOAN_APP = "integsys/param/download_credit_online_application_list.php";
    private static final String URL_DCP_REMITTANCE = "integsys/dcp/dcp_remit.php";
    private static final String URL_DCP_LOCATION_REPORT = "integsys/dcp/dcp_sumbit_coordinates.php";
    private static final String LOCAL_COORDINATES_TRACKER = "integsys/dcp/dcp_coordinates_receiver.php";
    private static final String URL_BRANCH_REMITTANCE_ACC = "integsys/param/download_branch_bank_account.php";
    private static final String URL_IMPORT_SYS_CONFIG = "integsys/param/download_system_config.php";
    private static final String URL_DOWNLOAD_CREDIT_ONLINE_APP = "integsys/param/download_credit_online_application_list.php";
    private static final String URL_DOWNLOAD_RELATION = "gcircle/params/download_relation.php";
    private static final String URL_UPLOAD_CI_RESULT = "integsys/gocas/upload_ci_result.php";

    private static final String URL_SEND_RESPONSE = "nmm/send_response.php";
    private static final String URL_SEND_REQUEST = "nmm/send_request.php";
    private static final String URL_SEND_REQUEST_SYSTEM = "nmm/send_request_system.php";

    private static final String URL_KWIKSEARCH = "integsys/paramqry/cash_count_rqst_officer.php";
    private static final String URL_IMPORT_PAYSLIP = "petmgr/import_payslips.php";
    private static final String URL_SEND_LEAVE_APPLICATION = "petmgr/send_leave_application.php";
    private static final String URL_GET_LEAVE_APPLICATION = "petmgr/get_leave_application.php";
    private static final String URL_CONFIRM_LEAVE_APPLICATION = "petmgr/confirm_leave_application.php";
    private static final String URL_SEND_OB_APPLICATION = "petmgr/send_ob_application.php";
    private static final String URL_GET_OB_APPLICATION = "petmgr/get_ob_application.php";
    private static final String URL_CONFIRM_OB_APPLICATION = "petmgr/confirm_ob_application.php";
    private static final String URL_REQUEST_RANDOM_STOCK_INVENTORY = "integsys/bullseye/random_stock_inventory_request.php";
    private static final String URL_SUBMIT_RANDOM_STOCK_INVENTORY = "integsys/bullseye/random_stock_inventory_submit.php";

    private static final String URL_REQUEST_FOR_EVALUATIONS = "integsys/gocas/ci_request_for_evaluations.php";
    private static final String URL_ADD_FOR_EVALUATION = "integsys/gocas/ci_add_for_evaluation.php";
    private static final String URL_DOWNLOAD_CREDIT_APP = "integsys/gocas/download_credit_app.php";
    private static final String URL_SUBMIT_RESULT = "integsys/gocas/ci_submit_result.php";
    private static final String URL_POST_CI_APPROVAL = "integsys/gocas/ci_submit_approval.php";
    private static final String URL_DOWNLOAD_BH_PREVIEW = "integsys/gocas/bh_request_evaluation_preview.php";
    private static final String URL_POST_BH_APPROVAL = "integsys/gocas/bh_submit_approval.php";

    private static final String REQUEST_USER_ACCESS = "security/request_android_object.php";

    private static final String URL_SUBMIT_APP_VERSION = "security/updateUserAppVersion.php";
    private static final String URL_VERSION_LOG = "query/get_version_info.php";
    private static final String URL_CHECK_UPDATE = "query/check_update.php";
    private static final String GET_PANALO_REWARDS = "gconnect/upload/getUserPanalo.php";
    private static final String CLAIM_PANALO_REWARD = "gconnect/upload/getUserPanalo.php";
    private static final String GET_RAFFLE_PARTICIPANTS = "gconnect/upload/getUserPanalo.php";
    private static final String GET_PACITA_RULES = "gcircle/pacita/import_pacita_rules.php";
    private static final String GET_PACITA_EVALUATIONS = "gcircle/pacita/import_pacita_evaluations.php";
    private static final String SUBMIT_PACITA_RESULT = "gcircle/pacita/submit_pacita_result.php";
    private static final String URL_SUBMIT_INQUIRY = "gcircle/ganado/submit_inquiry.php";
    private static final String URL_DOWNLOAD_INQUIRIES = "gcircle/ganado/import_inquiries.php";
    private static final String URL_IMPORT_LOAN_TYPES = "integsys/param/download_loan_type.php";

    public GCircleApi(Application instance) {
        super(instance);
    }

    public String getUrlAuthEmployee() {
        if(isUnitTest()){
            Log.d(TAG, "Initialize api:" + LOCAL + URL_AUTH_EMPLOYEE);
            return LOCAL + URL_AUTH_EMPLOYEE;
        }
        Log.d(TAG, "Initialize api:" + LIVE + URL_AUTH_EMPLOYEE);
        return LIVE + URL_AUTH_EMPLOYEE;
    }

    public String getUrlCreateAccount() {
        if(isUnitTest()){
            Log.d(TAG, "Initialize api:" + LOCAL + URL_CREATE_ACCOUNT);
            return LOCAL + URL_CREATE_ACCOUNT;
        }
        Log.d(TAG, "Initialize api:" + LIVE + URL_CREATE_ACCOUNT);
        return LIVE + URL_CREATE_ACCOUNT;
    }

    public String getUrlForgotPassword() {
        if(isUnitTest()){
            Log.d(TAG, "Initialize api:" + LOCAL + URL_FORGOT_PASSWORD);
            return LOCAL + URL_FORGOT_PASSWORD;
        }
        Log.d(TAG, "Initialize api:" + LIVE + URL_FORGOT_PASSWORD);
        return LIVE + URL_FORGOT_PASSWORD;
    }

    public String getUrlDeactivateAccount() {
        if(isUnitTest()){
            Log.d(TAG, "Initialize api:" + LOCAL + DEACTIVATE_ACCOUNT);
            return LOCAL + DEACTIVATE_ACCOUNT;
        }
        Log.d(TAG, "Initialize api:" + LIVE + DEACTIVATE_ACCOUNT);
        return LIVE + DEACTIVATE_ACCOUNT;
    }

    public String getUrlKnox() {
        if(isUnitTest()){
            Log.d(TAG, "Initialize api:" + LOCAL + URL_KNOX);
            return LOCAL + URL_KNOX;
        }
        Log.d(TAG, "Initialize api:" + LIVE + URL_KNOX);
        return LIVE + URL_KNOX;
    }

    public String getUrlChangePassword() {
        if(isUnitTest()){
            Log.d(TAG, "Initialize api:" + LOCAL + URL_CHANGE_PASSWORD);
            return LOCAL + URL_CHANGE_PASSWORD;
        }
        Log.d(TAG, "Initialize api:" + LIVE + URL_CHANGE_PASSWORD);
        return LIVE + URL_CHANGE_PASSWORD;
    }

    public String getImportBranchPerformance() {
        if(isUnitTest()){
            Log.d(TAG, "Initialize api:" + LOCAL + IMPORT_BRANCH_PERFORMANCE);
            return LOCAL + IMPORT_BRANCH_PERFORMANCE;
        }
        Log.d(TAG, "Initialize api:" + LIVE + IMPORT_BRANCH_PERFORMANCE);
        return LIVE + IMPORT_BRANCH_PERFORMANCE;
    }

    public String getImportAreaPerformance() {
        if(isUnitTest()){
            Log.d(TAG, "Initialize api:" + LOCAL + IMPORT_AREA_PERFORMANCE);
            return LOCAL + IMPORT_AREA_PERFORMANCE;
        }
        Log.d(TAG, "Initialize api:" + LIVE + IMPORT_AREA_PERFORMANCE);
        return LIVE + IMPORT_AREA_PERFORMANCE;
    }

    public String getUrlImportBarangay() {
        if(isUnitTest()){
            Log.d(TAG, "Initialize api:" + LOCAL + URL_IMPORT_BARANGAY);
            return LOCAL + URL_IMPORT_BARANGAY;
        }
        Log.d(TAG, "Initialize api:" + LIVE + URL_IMPORT_BARANGAY);
        return LIVE + URL_IMPORT_BARANGAY;
    }

    public String getUrlImportTown() {
        if(isUnitTest()){
            Log.d(TAG, "Initialize api:" + LOCAL + URL_IMPORT_TOWN);
            return LOCAL + URL_IMPORT_TOWN;
        }
        Log.d(TAG, "Initialize api:" + LIVE + URL_IMPORT_TOWN);
        return LIVE + URL_IMPORT_TOWN;
    }

    public String getUrlImportProvince() {
        if(isUnitTest()){
            Log.d(TAG, "Initialize api:" + LOCAL + URL_IMPORT_PROVINCE);
            return LOCAL + URL_IMPORT_PROVINCE;
        }
        Log.d(TAG, "Initialize api:" + LIVE + URL_IMPORT_PROVINCE);
        return LIVE + URL_IMPORT_PROVINCE;
    }

    public String getUrlImportCountry() {
        if(isUnitTest()){
            Log.d(TAG, "Initialize api:" + LOCAL + URL_IMPORT_COUNTRY);
            return LOCAL + URL_IMPORT_COUNTRY;
        }
        Log.d(TAG, "Initialize api:" + LIVE + URL_IMPORT_COUNTRY);
        return LIVE + URL_IMPORT_COUNTRY;
    }

    public String getUrlImportMcModel() {
        if(isUnitTest()){
            Log.d(TAG, "Initialize api:" + LOCAL + URL_IMPORT_MC_MODEL);
            return LOCAL + URL_IMPORT_MC_MODEL;
        }
        Log.d(TAG, "Initialize api:" + LIVE + URL_IMPORT_MC_MODEL);
        return LIVE + URL_IMPORT_MC_MODEL;
    }

    public String getUrlImportMcModelColor() {
        if(isUnitTest()){
            Log.d(TAG, "Initialize api:" + LOCAL + URL_IMPORT_MC_COLOR);
            return LOCAL + URL_IMPORT_MC_COLOR;
        }
        Log.d(TAG, "Initialize api:" + LIVE + URL_IMPORT_MC_COLOR);
        return LIVE + URL_IMPORT_MC_COLOR;
    }

    public String getUrlImportMcModelPrices() {
        if(isUnitTest()){
            Log.d(TAG, "Initialize api:" + LOCAL + URL_IMPORT_MC_PRICES);
            return LOCAL + URL_IMPORT_MC_PRICES;
        }
        Log.d(TAG, "Initialize api:" + LIVE + URL_IMPORT_MC_PRICES);
        return LIVE + URL_IMPORT_MC_PRICES;
    }

    public String getUrlImportMcModelPrice() {
        if(isUnitTest()){
            Log.d(TAG, "Initialize api:" + LOCAL + URL_IMPORT_MC_MODEL_PRICE);
            return LOCAL + URL_IMPORT_MC_MODEL_PRICE;
        }
        Log.d(TAG, "Initialize api:" + LIVE + URL_IMPORT_MC_MODEL_PRICE);
        return LIVE + URL_IMPORT_MC_MODEL_PRICE;
    }

    public String getUrlImportBrand() {
        if(isUnitTest()){
            Log.d(TAG, "Initialize api:" + LOCAL + URL_IMPORT_BRAND);
            return LOCAL + URL_IMPORT_BRAND;
        }
        Log.d(TAG, "Initialize api:" + LIVE + URL_IMPORT_BRAND);
        return LIVE + URL_IMPORT_BRAND;
    }

    public String getUrlImportMcCategory() {
        if(isUnitTest()){
            Log.d(TAG, "Initialize api:" + LOCAL + URL_IMPORT_MC_CATEGORY);
            return LOCAL + URL_IMPORT_MC_CATEGORY;
        }
        Log.d(TAG, "Initialize api:" + LIVE + URL_IMPORT_MC_CATEGORY);
        return LIVE + URL_IMPORT_MC_CATEGORY;
    }

    public String getUrlImportTermCategory() {
        if(isUnitTest()){
            Log.d(TAG, "Initialize api:" + LOCAL + URL_IMPORT_TERM_CATEGORY);
            return LOCAL + URL_IMPORT_TERM_CATEGORY;
        }
        Log.d(TAG, "Initialize api:" + LIVE + URL_IMPORT_TERM_CATEGORY);
        return LIVE + URL_IMPORT_TERM_CATEGORY;
    }

    public String getUrlImportBranches() {
        if(isUnitTest()){
            Log.d(TAG, "Initialize api:" + LOCAL + URL_IMPORT_BRANCHES);
            return LOCAL + URL_IMPORT_BRANCHES;
        }
        Log.d(TAG, "Initialize api:" + LIVE + URL_IMPORT_BRANCHES);
        return LIVE + URL_IMPORT_BRANCHES;
    }

    public String getUrlImportFileCode() {
        if(isUnitTest()){
            Log.d(TAG, "Initialize api:" + LOCAL + URL_IMPORT_FILE_CODE);
            return LOCAL + URL_IMPORT_FILE_CODE;
        }
        Log.d(TAG, "Initialize api:" + LIVE + URL_IMPORT_FILE_CODE);
        return LIVE + URL_IMPORT_FILE_CODE;
    }

    public String getUrlImportOccupations() {
        if(isUnitTest()){
            Log.d(TAG, "Initialize api:" + LOCAL + URL_IMPORT_OCCUPATIONS);
            return LOCAL + URL_IMPORT_OCCUPATIONS;
        }
        Log.d(TAG, "Initialize api:" + LIVE + URL_IMPORT_OCCUPATIONS);
        return LIVE + URL_IMPORT_OCCUPATIONS;
    }

    public String getUrlSubmitOnlineApplication() {
        if(isUnitTest()){
            Log.d(TAG, "Initialize api:" + LOCAL + URL_SUBMIT_ONLINE_APPLICATION);
            return LOCAL + URL_SUBMIT_ONLINE_APPLICATION;
        }
        Log.d(TAG, "Initialize api:" + LIVE + URL_SUBMIT_ONLINE_APPLICATION);
        return LIVE + URL_SUBMIT_ONLINE_APPLICATION;
    }

    public String getUrlImportRaffleBasis() {
        if(isUnitTest()){
            Log.d(TAG, "Initialize api:" + LOCAL + URL_IMPORT_RAFFLE_BASIS);
            return LOCAL + URL_IMPORT_RAFFLE_BASIS;
        }
        Log.d(TAG, "Initialize api:" + LIVE + URL_IMPORT_RAFFLE_BASIS);
        return LIVE + URL_IMPORT_RAFFLE_BASIS;
    }

    public String getUrlRequestOnlineApplications() {
        if(isUnitTest()){
            Log.d(TAG, "Initialize api:" + LOCAL + URL_REQUEST_ONLINE_APPLICATIONS);
            return LOCAL + URL_REQUEST_ONLINE_APPLICATIONS;
        }
        Log.d(TAG, "Initialize api:" + LIVE + URL_REQUEST_ONLINE_APPLICATIONS);
        return LIVE + URL_REQUEST_ONLINE_APPLICATIONS;
    }

    public String getUrlImportOnlineApplications() {
        if(isUnitTest()){
            Log.d(TAG, "Initialize api:" + LOCAL + URL_IMPORT_ONLINE_APPLICATIONS);
            return LOCAL + URL_IMPORT_ONLINE_APPLICATIONS;
        }
        Log.d(TAG, "Initialize api:" + LIVE + URL_IMPORT_ONLINE_APPLICATIONS);
        return LIVE + URL_IMPORT_ONLINE_APPLICATIONS;
    }

    public String getUrlSubmitCashcount() {
        if(isUnitTest()){
            Log.d(TAG, "Initialize api:" + LOCAL + URL_SUBMIT_CASHCOUNT);
            return LOCAL + URL_SUBMIT_CASHCOUNT;
        }
        Log.d(TAG, "Initialize api:" + LIVE + URL_SUBMIT_CASHCOUNT);
        return LIVE + URL_SUBMIT_CASHCOUNT;
    }

    public String getUrlQuickSearch() {
        if(isUnitTest()){
            Log.d(TAG, "Initialize api:" + LOCAL + URL_QUICK_SEARCH);
            return LOCAL + URL_QUICK_SEARCH;
        }
        Log.d(TAG, "Initialize api:" + LIVE + URL_QUICK_SEARCH);
        return LIVE + URL_QUICK_SEARCH;
    }

    public String getUrlScaRequest() {
        if(isUnitTest()){
            Log.d(TAG, "Initialize api:" + LOCAL + URL_SCA_REQUEST);
            return LOCAL + URL_SCA_REQUEST;
        }
        Log.d(TAG, "Initialize api:" + LIVE + URL_SCA_REQUEST);
        return LIVE + URL_SCA_REQUEST;
    }

    public String getUrlSaveApproval() {
        if(isUnitTest()){
            Log.d(TAG, "Initialize api:" + LOCAL + URL_SAVE_APPROVAL);
            return LOCAL + URL_SAVE_APPROVAL;
        }
        Log.d(TAG, "Initialize api:" + LIVE + URL_SAVE_APPROVAL);
        return LIVE + URL_SAVE_APPROVAL;
    }

    public String getUrlLoadApplicationApproval() {
        if(isUnitTest()){
            Log.d(TAG, "Initialize api:" + LOCAL + URL_LOAD_APPLICATION_APPROVAL);
            return LOCAL + URL_LOAD_APPLICATION_APPROVAL;
        }
        Log.d(TAG, "Initialize api:" + LIVE + URL_LOAD_APPLICATION_APPROVAL);
        return LIVE + URL_LOAD_APPLICATION_APPROVAL;
    }

    public String getUrlApplicationApprove() {
        if(isUnitTest()){
            Log.d(TAG, "Initialize api:" + LOCAL + URL_APPLICATION_APPROVE);
            return LOCAL + URL_APPLICATION_APPROVE;
        }
        Log.d(TAG, "Initialize api:" + LIVE + URL_APPLICATION_APPROVE);
        return LIVE + URL_APPLICATION_APPROVE;
    }

    public String getUrlDownloadDcp() {
        if(isUnitTest()){
            Log.d(TAG, "Initialize api:" + LOCAL + URL_DOWNLOAD_DCP);
            return LOCAL + URL_DOWNLOAD_DCP;
        }
        Log.d(TAG, "Initialize api:" + LIVE + URL_DOWNLOAD_DCP);
        return LIVE + URL_DOWNLOAD_DCP;
    }

    public String getUrlDcpSubmit() {
        if(isUnitTest()){
            Log.d(TAG, "Initialize api:" + LOCAL + URL_DCP_SUBMIT);
            return LOCAL + URL_DCP_SUBMIT;
        }
        Log.d(TAG, "Initialize api:" + LIVE + URL_DCP_SUBMIT);
        return LIVE + URL_DCP_SUBMIT;
    }

    public String getUrlPostDcpMaster() {
        if(isUnitTest()){
            Log.d(TAG, "Initialize api:" + LOCAL + URL_POST_DCP_MASTER);
            return LOCAL + URL_POST_DCP_MASTER;
        }
        Log.d(TAG, "Initialize api:" + LIVE + URL_POST_DCP_MASTER);
        return LIVE + URL_POST_DCP_MASTER;
    }

    public String getUrlGetArClient() {
        if(isUnitTest()){
            Log.d(TAG, "Initialize api:" + LOCAL + URL_GET_AR_CLIENT);
            return LOCAL + URL_GET_AR_CLIENT;
        }
        Log.d(TAG, "Initialize api:" + LIVE + URL_GET_AR_CLIENT);
        return LIVE + URL_GET_AR_CLIENT;
    }

    public String getUrlGetRegClient() {
        if(isUnitTest()){
            Log.d(TAG, "Initialize api:" + LOCAL + URL_GET_REG_CLIENT);
            return LOCAL + URL_GET_REG_CLIENT;
        }
        Log.d(TAG, "Initialize api:" + LIVE + URL_GET_REG_CLIENT);
        return LIVE + URL_GET_REG_CLIENT;
    }

    public String getUrlUpdateAddress() {
        if(isUnitTest()){
            Log.d(TAG, "Initialize api:" + LOCAL + URL_UPDATE_ADDRESS);
            return LOCAL + URL_UPDATE_ADDRESS;
        }
        Log.d(TAG, "Initialize api:" + LIVE + URL_UPDATE_ADDRESS);
        return LIVE + URL_UPDATE_ADDRESS;
    }

    public String getUrlUpdateMobile() {
        if(isUnitTest()){
            Log.d(TAG, "Initialize api:" + LOCAL + URL_UPDATE_MOBILE);
            return LOCAL + URL_UPDATE_MOBILE;
        }
        Log.d(TAG, "Initialize api:" + LIVE + URL_UPDATE_MOBILE);
        return LIVE + URL_UPDATE_MOBILE;
    }

    public String getUrlDownloadBankInfo() {
        if(isUnitTest()){
            Log.d(TAG, "Initialize api:" + LOCAL + URL_DOWNLOAD_BANK_INFO);
            return LOCAL + URL_DOWNLOAD_BANK_INFO;
        }
        Log.d(TAG, "Initialize api:" + LIVE + URL_DOWNLOAD_BANK_INFO);
        return LIVE + URL_DOWNLOAD_BANK_INFO;
    }

    public String getUrlPostSelfielog() {
        if(isUnitTest()){
            Log.d(TAG, "Initialize api:" + LOCAL + URL_POST_SELFIELOG);
            return LOCAL + URL_POST_SELFIELOG;
        }
        Log.d(TAG, "Initialize api:" + LIVE + URL_POST_SELFIELOG);
        return LIVE + URL_POST_SELFIELOG;
    }

    public String getUrlBranchLoanApp() {
        if(isUnitTest()){
            Log.d(TAG, "Initialize api:" + LOCAL + URL_BRANCH_LOAN_APP);
            return LOCAL + URL_BRANCH_LOAN_APP;
        }
        Log.d(TAG, "Initialize api:" + LIVE + URL_BRANCH_LOAN_APP);
        return LIVE + URL_BRANCH_LOAN_APP;
    }

    public String getUrlDcpRemittance() {
        if(isUnitTest()){
            Log.d(TAG, "Initialize api:" + LOCAL + URL_DCP_REMITTANCE);
            return LOCAL + URL_DCP_REMITTANCE;
        }
        Log.d(TAG, "Initialize api:" + LIVE + URL_DCP_REMITTANCE);
        return LIVE + URL_DCP_REMITTANCE;
    }

    public String getUrlDcpLocationReport() {
        if(isUnitTest()){
            Log.d(TAG, "Initialize api:" + LOCAL + URL_DCP_LOCATION_REPORT);
            return LOCAL + URL_DCP_LOCATION_REPORT;
        }
        Log.d(TAG, "Initialize api:" + LIVE + URL_DCP_LOCATION_REPORT);
        return LIVE + URL_DCP_LOCATION_REPORT;
    }

    public String getUrlBranchRemittanceAcc() {
        if(isUnitTest()){
            Log.d(TAG, "Initialize api:" + LOCAL + URL_BRANCH_REMITTANCE_ACC);
            return LOCAL + URL_BRANCH_REMITTANCE_ACC;
        }
        Log.d(TAG, "Initialize api:" + LIVE + URL_BRANCH_REMITTANCE_ACC);
        return LIVE + URL_BRANCH_REMITTANCE_ACC;
    }

    public String getUrlImportSysConfig() {
        if(isUnitTest()){
            Log.d(TAG, "Initialize api:" + LOCAL + URL_IMPORT_SYS_CONFIG);
            return LOCAL + URL_IMPORT_SYS_CONFIG;
        }
        Log.d(TAG, "Initialize api:" + LIVE + URL_IMPORT_SYS_CONFIG);
        return LIVE + URL_IMPORT_SYS_CONFIG;
    }

    public String getUrlDownloadCreditOnlineApp() {
        if(isUnitTest()){
            Log.d(TAG, "Initialize api:" + LOCAL + URL_DOWNLOAD_CREDIT_ONLINE_APP);
            return LOCAL + URL_DOWNLOAD_CREDIT_ONLINE_APP;
        }
        Log.d(TAG, "Initialize api:" + LIVE + URL_DOWNLOAD_CREDIT_ONLINE_APP);
        return LIVE + URL_DOWNLOAD_CREDIT_ONLINE_APP;
    }

    public String getUrlDownloadRelation() {
        if(isUnitTest()){
            Log.d(TAG, "Initialize api:" + LOCAL + URL_DOWNLOAD_RELATION);
            return LOCAL + URL_DOWNLOAD_RELATION;
        }
        Log.d(TAG, "Initialize api:" + LIVE + URL_DOWNLOAD_RELATION);
        return LIVE + URL_DOWNLOAD_RELATION;
    }

    public String getUrlUploadCiResult() {
        if(isUnitTest()){
            Log.d(TAG, "Initialize api:" + LOCAL + URL_UPLOAD_CI_RESULT);
            return LOCAL + URL_UPLOAD_CI_RESULT;
        }
        Log.d(TAG, "Initialize api:" + LIVE + URL_UPLOAD_CI_RESULT);
        return LIVE + URL_UPLOAD_CI_RESULT;
    }

    public String getUrlSendResponse() {
        if(isUnitTest()){
            Log.d(TAG, "Initialize api:" + LOCAL + URL_SEND_RESPONSE);
            return LOCAL + URL_SEND_RESPONSE;
        }
        Log.d(TAG, "Initialize api:" + LIVE + URL_SEND_RESPONSE);
        return LIVE + URL_SEND_RESPONSE;
    }

    public String getUrlSendRequest() {
        if(isUnitTest()){
            Log.d(TAG, "Initialize api:" + LOCAL + URL_SEND_REQUEST);
            return LOCAL + URL_SEND_REQUEST;
        }
        Log.d(TAG, "Initialize api:" + LIVE + URL_SEND_REQUEST);
        return LIVE + URL_SEND_REQUEST;
    }

    public String getUrlKwiksearch() {
        if(isUnitTest()){
            Log.d(TAG, "Initialize api:" + LOCAL + URL_KWIKSEARCH);
            return LOCAL + URL_KWIKSEARCH;
        }
        Log.d(TAG, "Initialize api:" + LIVE + URL_KWIKSEARCH);
        return LIVE + URL_KWIKSEARCH;
    }

    public String getUrlSendLeaveApplication() {
        if(isUnitTest()){
            Log.d(TAG, "Initialize api:" + LOCAL + URL_SEND_LEAVE_APPLICATION);
            return LOCAL + URL_SEND_LEAVE_APPLICATION;
        }
        Log.d(TAG, "Initialize api:" + LIVE + URL_SEND_LEAVE_APPLICATION);
        return LIVE + URL_SEND_LEAVE_APPLICATION;
    }

    public String getImportPayslip() {
        if(isUnitTest()){
            Log.d(TAG, "Initialize api:" + LOCAL + URL_IMPORT_PAYSLIP);
            return LOCAL + URL_IMPORT_PAYSLIP;
        }
        Log.d(TAG, "Initialize api:" + LIVE + URL_IMPORT_PAYSLIP);
        return LIVE + URL_IMPORT_PAYSLIP;
    }

    public String getUrlGetLeaveApplication() {
        if(isUnitTest()){
            Log.d(TAG, "Initialize api:" + LOCAL + URL_GET_LEAVE_APPLICATION);
            return LOCAL + URL_GET_LEAVE_APPLICATION;
        }
        Log.d(TAG, "Initialize api:" + LIVE + URL_GET_LEAVE_APPLICATION);
        return LIVE + URL_GET_LEAVE_APPLICATION;
    }

    public String getUrlConfirmLeaveApplication() {
        if(isUnitTest()){
            Log.d(TAG, "Initialize api:" + LOCAL + URL_CONFIRM_LEAVE_APPLICATION);
            return LOCAL + URL_CONFIRM_LEAVE_APPLICATION;
        }
        Log.d(TAG, "Initialize api:" + LIVE + URL_CONFIRM_LEAVE_APPLICATION);
        return LIVE + URL_CONFIRM_LEAVE_APPLICATION;
    }

    public String getUrlSendObApplication() {
        if(isUnitTest()){
            Log.d(TAG, "Initialize api:" + LOCAL + URL_SEND_OB_APPLICATION);
            return LOCAL + URL_SEND_OB_APPLICATION;
        }
        Log.d(TAG, "Initialize api:" + LIVE + URL_SEND_OB_APPLICATION);
        return LIVE + URL_SEND_OB_APPLICATION;
    }

    public String getUrlGetObApplication() {
        if(isUnitTest()){
            Log.d(TAG, "Initialize api:" + LOCAL + URL_GET_OB_APPLICATION);
            return LOCAL + URL_GET_OB_APPLICATION;
        }
        Log.d(TAG, "Initialize api:" + LIVE + URL_GET_OB_APPLICATION);
        return LIVE + URL_GET_OB_APPLICATION;
    }

    public String getUrlConfirmObApplication() {
        if(isUnitTest()){
            Log.d(TAG, "Initialize api:" + LOCAL + URL_CONFIRM_OB_APPLICATION);
            return LOCAL + URL_CONFIRM_OB_APPLICATION;
        }
        Log.d(TAG, "Initialize api:" + LIVE + URL_CONFIRM_OB_APPLICATION);
        return LIVE + URL_CONFIRM_OB_APPLICATION;
    }

    public String getUrlRequestRandomStockInventory() {
        if(isUnitTest()){
            Log.d(TAG, "Initialize api:" + LOCAL + URL_REQUEST_RANDOM_STOCK_INVENTORY);
            return LOCAL + URL_REQUEST_RANDOM_STOCK_INVENTORY;
        }
        Log.d(TAG, "Initialize api:" + LIVE + URL_REQUEST_RANDOM_STOCK_INVENTORY);
        return LIVE + URL_REQUEST_RANDOM_STOCK_INVENTORY;
    }

    public String getUrlSubmitRandomStockInventory() {
        if(isUnitTest()){
            Log.d(TAG, "Initialize api:" + LOCAL + URL_SUBMIT_RANDOM_STOCK_INVENTORY);
            return LOCAL + URL_SUBMIT_RANDOM_STOCK_INVENTORY;
        }
        Log.d(TAG, "Initialize api:" + LIVE + URL_SUBMIT_RANDOM_STOCK_INVENTORY);
        return LIVE + URL_SUBMIT_RANDOM_STOCK_INVENTORY;
    }

    public String getRequestUserAccess() {
        if(isUnitTest()){
            Log.d(TAG, "Initialize api:" + LOCAL + REQUEST_USER_ACCESS);
            return LOCAL + REQUEST_USER_ACCESS;
        }
        Log.d(TAG, "Initialize api:" + LIVE + REQUEST_USER_ACCESS);
        return LIVE + REQUEST_USER_ACCESS;
    }

    public String getUrlDownloadCIApplications() {
        if(isUnitTest()){
            Log.d(TAG, "Initialize api:" + LOCAL + URL_REQUEST_FOR_EVALUATIONS);
            return LOCAL + URL_REQUEST_FOR_EVALUATIONS;
        }
        Log.d(TAG, "Initialize api:" + LIVE + URL_REQUEST_FOR_EVALUATIONS);
        return LIVE + URL_REQUEST_FOR_EVALUATIONS;
    }

    public String getUrlSubmitCIResult() {
        if(isUnitTest()){
            Log.d(TAG, "Initialize api:" + LOCAL + URL_SUBMIT_RESULT);
            return LOCAL + URL_SUBMIT_RESULT;
        }
        Log.d(TAG, "Initialize api:" + LIVE + URL_SUBMIT_RESULT);
        return LIVE + URL_SUBMIT_RESULT;
    }

    public String getUrlPostCiApproval(){
        if(isUnitTest()) {
            Log.d(TAG, "Initialize api:" + LOCAL + URL_POST_CI_APPROVAL);
            return LOCAL + URL_POST_CI_APPROVAL;
        }
        Log.d(TAG, "Initialize api:" + LIVE + URL_POST_CI_APPROVAL);
        return LIVE + URL_POST_CI_APPROVAL;
    }

    public String getUrlPostBhApproval(){
        if(isUnitTest()) {
            Log.d(TAG, "Initialize api:" + LOCAL + URL_POST_BH_APPROVAL);
            return LOCAL + URL_POST_BH_APPROVAL;
        }
        Log.d(TAG, "Initialize api:" + LIVE + URL_POST_BH_APPROVAL);
        return LIVE + URL_POST_BH_APPROVAL;
    }

    public String getUrlDownloadBhPreview(){
        if(isUnitTest()) {
            Log.d(TAG, "Initialize api:" + LOCAL + URL_DOWNLOAD_BH_PREVIEW);
            return LOCAL + URL_DOWNLOAD_BH_PREVIEW;
        }
        Log.d(TAG, "Initialize api:" + LIVE + URL_DOWNLOAD_BH_PREVIEW);
        return LIVE + URL_DOWNLOAD_BH_PREVIEW;
    }

    public String getUrlAddForEvaluation() {
        if(isUnitTest()) {
            Log.d(TAG, "Initialize api:" + LOCAL + URL_REQUEST_FOR_EVALUATIONS);
            return LOCAL + URL_REQUEST_FOR_EVALUATIONS;
        }
        Log.d(TAG, "Initialize api:" + LIVE + URL_REQUEST_FOR_EVALUATIONS);
        return LIVE + URL_REQUEST_FOR_EVALUATIONS;
    }

    public String getUrlDownloadCreditAppForCI() {
        if(isUnitTest()) {
            Log.d(TAG, "Initialize api:" + LOCAL + URL_DOWNLOAD_CREDIT_APP);
            return LOCAL + URL_DOWNLOAD_CREDIT_APP;
        }
        Log.d(TAG, "Initialize api:" + LIVE + URL_DOWNLOAD_CREDIT_APP);
        return LIVE + URL_DOWNLOAD_CREDIT_APP;
    }
    public String getUrlSaveItinerary() {
        if(isUnitTest()) {
            Log.d(TAG, "Initialize api:" + LOCAL + URL_SAVE_ITINERARY);
            return LOCAL + URL_SAVE_ITINERARY;
        }
        Log.d(TAG, "Initialize api:" + LIVE + URL_SAVE_ITINERARY);
        return LIVE + URL_SAVE_ITINERARY;
    }

    public String getUrlDownloadItinerary() {
        if(isUnitTest()) {
            Log.d(TAG, "Initialize api:" + LOCAL + URL_DOWNLOAD_ITINERARY);
            return LOCAL + URL_DOWNLOAD_ITINERARY;
        }
        Log.d(TAG, "Initialize api:" + LIVE + URL_DOWNLOAD_ITINERARY);
        return LIVE + URL_DOWNLOAD_ITINERARY;
    }

    public String getUrlDownloadItineraryUsers() {
        if(isUnitTest()) {
            Log.d(TAG, "Initialize api:" + LOCAL + URL_DOWNLOAD_ITINERARY_USERS);
            return LOCAL + URL_DOWNLOAD_ITINERARY_USERS;
        }
        Log.d(TAG, "Initialize api:" + LIVE + URL_DOWNLOAD_ITINERARY_USERS);
        return LIVE + URL_DOWNLOAD_ITINERARY_USERS;
    }

    public String getUrlSubmitLocationTrack() {
        if(isUnitTest()) {
            Log.d(TAG, "Initialize api:" + LOCAL + LOCAL_COORDINATES_TRACKER);
            return LOCAL + LOCAL_COORDINATES_TRACKER;
        }
        Log.d(TAG, "Initialize api:" + LIVE + LOCAL_COORDINATES_TRACKER);
        return LIVE + LOCAL_COORDINATES_TRACKER;
    }

    public String getUrlSubmitAppVersion() {
        if(isUnitTest()) {
            Log.d(TAG, "Initialize api:" + LOCAL + URL_SUBMIT_APP_VERSION);
            return LOCAL + URL_SUBMIT_APP_VERSION;
        }
        Log.d(TAG, "Initialize api:" + LIVE + URL_SUBMIT_APP_VERSION);
        return LIVE + URL_SUBMIT_APP_VERSION;
    }

    public String getUrlVersionLog() {
        if(isUnitTest()) {
            Log.d(TAG, "Initialize api:" + LOCAL + URL_VERSION_LOG);
            return LOCAL + URL_VERSION_LOG;
        }
        Log.d(TAG, "Initialize api:" + LIVE + URL_VERSION_LOG);
        return LIVE + URL_VERSION_LOG;
    }

    public String getUrlCheckUpdate() {
        if(isUnitTest()) {
            Log.d(TAG, "Initialize api:" + LOCAL + URL_CHECK_UPDATE);
            return LOCAL + URL_CHECK_UPDATE;
        }
        Log.d(TAG, "Initialize api:" + LIVE + URL_CHECK_UPDATE);
        return LIVE + URL_CHECK_UPDATE;
    }

    public String getUrlGetPanaloRewards() {
        if(isUnitTest()) {
            Log.d(TAG, "Initialize api:" + LOCAL + GET_PANALO_REWARDS);
            return LOCAL + GET_PANALO_REWARDS;
        }
        Log.d(TAG, "Initialize api:" + LIVE + GET_PANALO_REWARDS);
        return LIVE + GET_PANALO_REWARDS;
    }

    public String getUrlGetRaffleParticipants() {
        if(isUnitTest()) {
            Log.d(TAG, "Initialize api:" + LOCAL + GET_RAFFLE_PARTICIPANTS);
            return LOCAL + GET_RAFFLE_PARTICIPANTS;
        }
        Log.d(TAG, "Initialize api:" + LIVE + GET_RAFFLE_PARTICIPANTS);
        return LIVE + GET_RAFFLE_PARTICIPANTS;
    }

    public String getUrlGetPacitaRules() {
        if(isUnitTest()) {
            Log.d(TAG, "Initialize api:" + LOCAL + GET_PACITA_RULES);
            return LOCAL + GET_PACITA_RULES;
        }
        Log.d(TAG, "Initialize api:" + LIVE + GET_PACITA_RULES);
        return LIVE + GET_PACITA_RULES;
    }

    public String getUrlGetPacitaEvaluations() {
        if(isUnitTest()) {
            Log.d(TAG, "Initialize api:" + LOCAL + GET_PACITA_EVALUATIONS);
            return LOCAL + GET_PACITA_EVALUATIONS;
        }
        Log.d(TAG, "Initialize api:" + LIVE + GET_PACITA_EVALUATIONS);
        return LIVE + GET_PACITA_EVALUATIONS;
    }

    public String getUrlSubmitPacitaResult() {
        if(isUnitTest()) {
            Log.d(TAG, "Initialize api:" + LOCAL + SUBMIT_PACITA_RESULT);
            return LOCAL + SUBMIT_PACITA_RESULT;
        }
        Log.d(TAG, "Initialize api:" + LIVE + SUBMIT_PACITA_RESULT);
        return LIVE + SUBMIT_PACITA_RESULT;
    }

    public String getGetInstallmentTerms() {
        if(isUnitTest()) {
            Log.d(TAG, "Initialize api:" + LOCAL + URL_GET_INSTALLMENT_TERMS);
            return LOCAL + URL_GET_INSTALLMENT_TERMS;
        }
        Log.d(TAG, "Initialize api:" + LIVE + URL_GET_INSTALLMENT_TERMS);
        return LIVE + URL_GET_INSTALLMENT_TERMS;
    }

    public String getClaimPanaloReward() {
        if(isUnitTest()) {
            Log.d(TAG, "Initialize api:" + LOCAL + CLAIM_PANALO_REWARD);
            return LOCAL + CLAIM_PANALO_REWARD;
        }
        Log.d(TAG, "Initialize api:" + LIVE + CLAIM_PANALO_REWARD);
        return LIVE + CLAIM_PANALO_REWARD;
    }

    public String getSubmitInquiry() {
        if(isUnitTest()) {
            Log.d(TAG, "Initialize api:" + LOCAL + URL_SUBMIT_INQUIRY);
            return LOCAL + URL_SUBMIT_INQUIRY;
        }
        Log.d(TAG, "Initialize api:" + LIVE + URL_SUBMIT_INQUIRY);
        return LIVE + URL_SUBMIT_INQUIRY;
    }

    public String getDownloadInquiries() {
        if(isUnitTest()) {
            Log.d(TAG, "Initialize api:" + LOCAL + URL_DOWNLOAD_INQUIRIES);
            return LOCAL + URL_DOWNLOAD_INQUIRIES;
        }
        Log.d(TAG, "Initialize api:" + LIVE + URL_DOWNLOAD_INQUIRIES);
        return LIVE + URL_DOWNLOAD_INQUIRIES;
    }

    public String getDownloadLoanTypes() {
        if(isUnitTest()) {
            Log.d(TAG, "Initialize api:" + LOCAL + URL_IMPORT_LOAN_TYPES);
            return LOCAL + URL_IMPORT_LOAN_TYPES;
        }
        Log.d(TAG, "Initialize api:" + LIVE + URL_IMPORT_LOAN_TYPES);
        return LIVE + URL_IMPORT_LOAN_TYPES;
    }

}

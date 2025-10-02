package org.rmj.g3appdriver.GConnect.Api;

import android.app.Application;
import android.util.Log;

import org.rmj.g3appdriver.dev.Api.WebApi;

public class GConnectApi extends WebApi {
    private static final String TAG = GConnectApi.class.getSimpleName();

    private static final String SIGN_IN = SECURITY + "signin.php";
    private static final String RESEND_OTP = SECURITY + "send_otp.php";
    private static final String REGISTRATION = SECURITY + "signup.php";
    private static final String RETRIEVE_PASSWORD = SECURITY + "forgotpswd.php";

    private static final String URL_CHECK_APP_VERSION = GCARD + "version_checker.php";
    private static final String URL_ADD_NEW_GCARD = GCARD + "add_gcardnumber.php";
    private static final String URL_IMPORT_PROMOLINK = GCARD + "import_promo_link.php";
    private static final String URL_IMPORT_PLACE_ORDER = GCARD + "import_placed_orders.php";
    private static final String URL_IMPORT_REDEEM_ITEMS = GCARD + "import_redeem_item.php";
    private static final String URL_IMPORT_TRANSACTIONS_OFFLINE = GCARD + "import_trans_offline.php";
    private static final String URL_IMPORT_TRANSACTIONS_ONLINE = GCARD + "import_trans_online.php";
    private static final String URL_IMPORT_TRANSACTIONS_PREORDER = GCARD + "import_trans_preorder.php";
    private static final String URL_IMPORT_TRANSACTIONS_REDEMPTION = GCARD + "import_trans_redemption.php";
    private static final String URL_IMPORT_MC_REGISTRATION = GCARD + "import_registration.php";
    private static final String URL_IMPORT_SERVICE = GCARD + "import_service.php";
    private static final String URL_IMPORT_BRANCH = GCARD + "import_branch.php";
    private static final String URL_IMPORT_GCARD = GCARD + "import_gcard.php";
    private static final String URL_REQUEST_AVAIL_POINTS = GCARD + "request_avl_points.php";
    private static final String URL_PLACE_ODER = GCARD + "place_order.php";
    private static final String URL_CANCEL_ORDER = GCARD + "cancel_order_item.php";
    private static final String URL_IMPORT_EVENTS = GCARDs + "import_events.php";
    private static final String URL_CHANGE_PASSWORD = SECURITY + "acctupdate.php";
    private static final String URL_REQUEST_DEVICES = SECURITY + "acctdevice.php";
    private static final String URL_CLIENT_LOGIN = SECURITY + "signin.php";
    private static final String IMPORT_BARANGAY = "integsys/param/download_barangay.php";
    private static final String IMPORT_TOWN = "integsys/param/download_town.php";
    private static final String IMPORT_PROVINCE = "integsys/param/download_province.php";
    private static final String IMPORT_COUNTRIES = "integsys/param/download_country.php";

    private static final String RETRIEVE_PROFILE_PICTURE = "";
    private static final String RETRIEVE_VERIFIED_ID = "";
    //    private static final String REGISTER_ACCOUNT = "integsys/marketplace/add_new_client.php";
    private static final String REGISTER_ACCOUNT = "integsys/marketplace/complete_account_detail.php";
    private static final String SUBMIT_SELFIE_VERIFICATION = "integsys/marketplace/submit_selfie_verification.php";
    private static final String SUBMIT_ID_VERIFICATION = "integsys/marketplace/submit_id_verification.php";
    private static final String IMPORT_ACCOUNT_INFO = "integsys/marketplace/get_client_info.php";
    private static final String IMPORT_VALID_ID_CODE = "integsys/marketplace/import_valid_id_code.php";
    //    private static final String IMPORT_ACCOUNT_INFO = "integsys/marketplace/import_client_detail.php";
    private static final String UPDATE_ACCOUNT_INFO = "integsys/marketplace/update_client_info.php";
    private static final String UPDATE_MOBILE_NO = "integsys/marketplace/request_mobile_update.php";
    private static final String UPDATE_CLIENT_MOBILE_NO = "integsys/marketplace/update_client_mobile.php";
    private static final String VERIFY_CLIENT_MOBILE_NO = "integsys/marketplace/verify_client_mobile.php";
    private static final String IMPORT_MOBILE_NO = "integsys/marketplace/import_client_mobile.php";
    private static final String UPDATE_ADDRESS = "integsys/marketplace/request_address_update.php";
    private static final String URL_UPDATE_ADDRESS = "integsys/marketplace/update_client_address.php";
    private static final String URL_UPDATE_MOBILE = "integsys/marketplace/update_client_mobile.php";
    private static final String URL_UPDATE_EMAIL = "integsys/marketplace/update_client_email.php";
    private static final String VERIFY_UPDATE_EMAIL = "integsys/marketplace/verify_client_email.php";
    private static final String IMPORT_EMAILS = "integsys/marketplace/import_client_email.php";
    private static final String IMPORT_PAYMENT_METHODS = "";
    private static final String UPDATE_PAYMENT_METHOD = "";
    private static final String IMPORT_SHIPPING_INFO = "";
    private static final String IMPORT_TOP_SELLING_ITEMS = "";
    private static final String IMPORT_PROMO_ITEMS = "integsys/marketplace/download_item_list.php";
    private static final String IMPORT_PRICE_LIST = "integsys/marketplace/download_pricelist.php";
    private static final String IMPORT_SEARCH_RESULTS = "";
    private static final String IMPORT_PRODUCT_INFO = "";
    private static final String ADD_TO_CART_ITEM = "integsys/marketplace/add_to_cart.php";
    private static final String UPDATE_CART_ITEM = "integsys/marketplace/remove_to_cart.php";
    private static final String IMPORT_CART_ITEMS = "integsys/marketplace/download_cart.php";
    private static final String MKTPL_PLACE_ORDER = "integsys/marketplace/place_order.php";
    private static final String MKTPL_CANCEL_ORDER = "integsys/marketplace/cancel_order.php";
    private static final String IMPORT_MKTPL_CANCELLATIONS = "integsys/marketplace/import_cancellation_detail.php";
    private static final String MKTPL_ORDER_PAYMENT = "integsys/marketplace/place_order_payment.php";
    private static final String IMPORT_PURCHASES = "integsys/marketplace/download_purchases.php";
    private static final String IMPORT_RETRIEVE_SHIPPING_ADDRESS = "";
    private static final String UPDATE_SHIPPING_ADDRESS = "";
    private static final String REQUEST_ADDRESS_UPDATE = "integsys/marketplace/request_address_update.php";
    private static final String REQUEST_MOBILE_UPDATE = "integsys/marketplace/request_mobile_update.php";
    private static final String IMPORT_DEFAULT_PAYMENT_METHOD = "";
    private static final String UPDATE_DEFAULT_PAYMENT_METHOD = "";
    private static final String IMPORT_SHIPPING_STATUS = "";
    private static final String IMPORT_ORDER_SUMMARY = "";
    private static final String IMPORT_BANK_ACC = "integsys/marketplace/download_bank_account.php";
    private static final String IMPORT_REVIEWS = "integsys/marketplace/import_product_reviews.php";
    private static final String IMPORT_INQUIRIES = "integsys/marketplace/import_product_inquiries.php";
    private static final String SUBMIT_REVIEW = "integsys/marketplace/submit_product_review.php";
    private static final String SUBMIT_INQUIRY = "integsys/marketplace/submit_product_inquiry.php";

    private static final String SUBMIT_MEANS_INFO = "integsys/gocas/mp_submit_means_info.php";
    private static final String SUBMIT_OTHER_INFO = "integsys/gocas/mp_submit_other_info.php";
    private static final String GET_OTHER_APPLICATION_INFO = "integsys/gocas/mp_get_other_application_info.php";
    private static final String SUBMIT_LOAN_APPLICATION = "integsys/gocas/mp_submit_credit_app.php";

    private static final String GET_PANALO_REWARDS = "gconnect/upload/getUserPanalo.php";

    private static final String IMPORT_NOTIFICATIONS = "integsys/notification/request_previous_notifications.php";
    private static final String SEND_RESPONSE = "https://apps.guanzongroup.com.ph/notification/send_response.php";
    private static final String SEND_REQUEST = "https://apps.guanzongroup.com.ph/notification/send_request.php";
    private static final String CHECK_UPDATE_URL = "https://apps.guanzongroup.com.ph/gcard/ms/version_checker.php";

    public GConnectApi(Application instance) {
        super(instance);
    }


    public String getSIGN_IN() {
        if(isUnitTest()){
            Log.d(TAG, "Initialize api:" + LOCAL + SIGN_IN);
            return LOCAL + SIGN_IN;
        }
        Log.d(TAG, "Initialize api:" + LIVE + SIGN_IN);
        return LIVE + SIGN_IN;
    }

    public String getRegisterAcountAPI() {
        if(isUnitTest()){
            Log.d(TAG, "Initialize api:" + LOCAL + REGISTRATION);
            return LOCAL + REGISTRATION;
        }
        Log.d(TAG, "Initialize api:" + LIVE + REGISTRATION);
        return LIVE + REGISTRATION;
    }

    public String getRetrievePasswordAPI() {
        if(isUnitTest()){
            Log.d(TAG, "Initialize api:" + LOCAL + RETRIEVE_PASSWORD);
            return LOCAL + RETRIEVE_PASSWORD;
        }
        Log.d(TAG, "Initialize api:" + LIVE + RETRIEVE_PASSWORD);
        return LIVE + RETRIEVE_PASSWORD;
    }

    public String getCheckAppVersionAPI() {
        if(isUnitTest()){
            Log.d(TAG, "Initialize api:" + LOCAL + URL_CHECK_APP_VERSION);
            return LOCAL + URL_CHECK_APP_VERSION;
        }
        Log.d(TAG, "Initialize api:" + LIVE + URL_CHECK_APP_VERSION);
        return LIVE + URL_CHECK_APP_VERSION;
    }

    public String getAddNewGCardAPI() {
        if(isUnitTest()){
            Log.d(TAG, "Initialize api:" + LOCAL + URL_ADD_NEW_GCARD);
            return LOCAL + URL_ADD_NEW_GCARD;
        }
        Log.d(TAG, "Initialize api:" + LIVE + URL_ADD_NEW_GCARD);
        return LIVE + URL_ADD_NEW_GCARD;
    }

    public String getImportPromosAPI() {
        if(isUnitTest()){
            Log.d(TAG, "Initialize api:" + LOCAL + URL_IMPORT_PROMOLINK);
            return LOCAL + URL_IMPORT_PROMOLINK;
        }
        Log.d(TAG, "Initialize api:" + LIVE + URL_IMPORT_PROMOLINK);
        return LIVE + URL_IMPORT_PROMOLINK;
    }

    public String getImportPlaceOrderAPI() {
        if(isUnitTest()){
            Log.d(TAG, "Initialize api:" + LOCAL + URL_IMPORT_PLACE_ORDER);
            return LOCAL + URL_IMPORT_PLACE_ORDER;
        }
        Log.d(TAG, "Initialize api:" + LIVE + URL_IMPORT_PLACE_ORDER);
        return LIVE + URL_IMPORT_PLACE_ORDER;
    }

    public String getImportRedeemItemsAPI() {
        if(isUnitTest()){
            Log.d(TAG, "Initialize api:" + LOCAL + URL_IMPORT_REDEEM_ITEMS);
            return LOCAL + URL_IMPORT_REDEEM_ITEMS;
        }
        Log.d(TAG, "Initialize api:" + LIVE + URL_IMPORT_REDEEM_ITEMS);
        return LIVE + URL_IMPORT_REDEEM_ITEMS;
    }

    public String getImportOfflineTransAPI() {
        if(isUnitTest()){
            Log.d(TAG, "Initialize api:" + LOCAL + URL_IMPORT_TRANSACTIONS_OFFLINE);
            return LOCAL + URL_IMPORT_TRANSACTIONS_OFFLINE;
        }
        Log.d(TAG, "Initialize api:" + LIVE + URL_IMPORT_TRANSACTIONS_OFFLINE);
        return LIVE + URL_IMPORT_TRANSACTIONS_OFFLINE;
    }

    public String getImportOnlineTransAPI() {
        if(isUnitTest()){
            Log.d(TAG, "Initialize api:" + LOCAL + URL_IMPORT_TRANSACTIONS_ONLINE);
            return LOCAL + URL_IMPORT_TRANSACTIONS_ONLINE;
        }
        Log.d(TAG, "Initialize api:" + LIVE + URL_IMPORT_TRANSACTIONS_ONLINE);
        return LIVE + URL_IMPORT_TRANSACTIONS_ONLINE;
    }

    public String getImportPreOrderAPI() {
        if(isUnitTest()){
            Log.d(TAG, "Initialize api:" + LOCAL + URL_IMPORT_TRANSACTIONS_PREORDER);
            return LOCAL + URL_IMPORT_TRANSACTIONS_PREORDER;
        }
        Log.d(TAG, "Initialize api:" + LIVE + URL_IMPORT_TRANSACTIONS_PREORDER);
        return LIVE + URL_IMPORT_TRANSACTIONS_PREORDER;
    }

    public String getImportReedemptionsAPI() {
        if(isUnitTest()){
            Log.d(TAG, "Initialize api:" + LOCAL + URL_IMPORT_TRANSACTIONS_REDEMPTION);
            return LOCAL + URL_IMPORT_TRANSACTIONS_REDEMPTION;
        }
        Log.d(TAG, "Initialize api:" + LIVE + URL_IMPORT_TRANSACTIONS_REDEMPTION);
        return LIVE + URL_IMPORT_TRANSACTIONS_REDEMPTION;
    }

    public String getMCRegistrationAPI() {
        if(isUnitTest()){
            Log.d(TAG, "Initialize api:" + LOCAL + URL_IMPORT_MC_REGISTRATION);
            return LOCAL + URL_IMPORT_MC_REGISTRATION;
        }
        Log.d(TAG, "Initialize api:" + LIVE + URL_IMPORT_MC_REGISTRATION);
        return LIVE + URL_IMPORT_MC_REGISTRATION;
    }

    public String getServiceInfoAPI() {
        if(isUnitTest()){
            Log.d(TAG, "Initialize api:" + LOCAL + URL_IMPORT_SERVICE);
            return LOCAL + URL_IMPORT_SERVICE;
        }
        Log.d(TAG, "Initialize api:" + LIVE + URL_IMPORT_SERVICE);
        return LIVE + URL_IMPORT_SERVICE;
    }

    public String getImportBranchesAPI() {
        if(isUnitTest()){
            Log.d(TAG, "Initialize api:" + LOCAL + URL_IMPORT_BRANCH);
            return LOCAL + URL_IMPORT_BRANCH;
        }
        Log.d(TAG, "Initialize api:" + LIVE + URL_IMPORT_BRANCH);
        return LIVE + URL_IMPORT_BRANCH;
    }

    public String getImportGCardAPI() {
        if(isUnitTest()){
            Log.d(TAG, "Initialize api:" + LOCAL + URL_IMPORT_GCARD);
            return LOCAL + URL_IMPORT_GCARD;
        }
        Log.d(TAG, "Initialize api:" + LIVE + URL_IMPORT_GCARD);
        return LIVE + URL_IMPORT_GCARD;
    }

    public String getRequestAvlPointsAPI() {
        if(isUnitTest()){
            Log.d(TAG, "Initialize api:" + LOCAL + URL_REQUEST_AVAIL_POINTS);
            return LOCAL + URL_REQUEST_AVAIL_POINTS;
        }
        Log.d(TAG, "Initialize api:" + LIVE + URL_REQUEST_AVAIL_POINTS);
        return LIVE + URL_REQUEST_AVAIL_POINTS;
    }

    public String getPlaceOrderAPI() {
        if(isUnitTest()){
            Log.d(TAG, "Initialize api:" + LOCAL + URL_PLACE_ODER);
            return LOCAL + URL_PLACE_ODER;
        }
        Log.d(TAG, "Initialize api:" + LIVE + URL_PLACE_ODER);
        return LIVE + URL_PLACE_ODER;
    }

    public String getCancelOrderAPI() {
        if(isUnitTest()){
            Log.d(TAG, "Initialize api:" + LOCAL + URL_CANCEL_ORDER);
            return LOCAL + URL_CANCEL_ORDER;
        }
        Log.d(TAG, "Initialize api:" + LIVE + URL_CANCEL_ORDER);
        return LIVE + URL_CANCEL_ORDER;
    }

    public String getImportEventsAPI() {
        if(isUnitTest()){
            Log.d(TAG, "Initialize api:" + LOCAL + URL_IMPORT_EVENTS);
            return LOCAL + URL_IMPORT_EVENTS;
        }
        Log.d(TAG, "Initialize api:" + LIVE + URL_IMPORT_EVENTS);
        return LIVE + URL_IMPORT_EVENTS;
    }

    public String getChangePasswordAPI() {
        if(isUnitTest()){
            Log.d(TAG, "Initialize api:" + LOCAL + URL_CHANGE_PASSWORD);
            return LOCAL + URL_CHANGE_PASSWORD;
        }
        Log.d(TAG, "Initialize api:" + LIVE + URL_CHANGE_PASSWORD);
        return LIVE + URL_CHANGE_PASSWORD;
    }

    public String getDevicesInfoAPI() {
        if(isUnitTest()){
            Log.d(TAG, "Initialize api:" + LOCAL + URL_REQUEST_DEVICES);
            return LOCAL + URL_REQUEST_DEVICES;
        }
        Log.d(TAG, "Initialize api:" + LIVE + URL_REQUEST_DEVICES);
        return LIVE + URL_REQUEST_DEVICES;
    }

    public String getClientLoginAPI() {
        return URL_CLIENT_LOGIN;
    }

    public String getSendResponseAPI() {
        return SEND_RESPONSE;
    }

    public String getSendRequestAPI() {
        return SEND_REQUEST;
    }

    public String getCheckUpdateAPI() {
        if(isUnitTest()){
            Log.d(TAG, "Initialize api:" + LOCAL + CHECK_UPDATE_URL);
            return LOCAL + CHECK_UPDATE_URL;
        }
        Log.d(TAG, "Initialize api:" + LIVE + CHECK_UPDATE_URL);
        return LIVE + CHECK_UPDATE_URL;
    }

    public String getCreateNewClientAPI(){
        if(isUnitTest()){
            Log.d(TAG, "Initialize api:" + LOCAL + REGISTER_ACCOUNT);
            return LOCAL + REGISTER_ACCOUNT;
        }
        Log.d(TAG, "Initialize api:" + LIVE + REGISTER_ACCOUNT);
        return LIVE + REGISTER_ACCOUNT;
    }

    public String getImportAccountInfoAPI() {
        if(isUnitTest()){
            Log.d(TAG, "Initialize api:" + LOCAL + IMPORT_ACCOUNT_INFO);
            return LOCAL + IMPORT_ACCOUNT_INFO;
        }
        Log.d(TAG, "Initialize api:" + LIVE + IMPORT_ACCOUNT_INFO);
        return LIVE + IMPORT_ACCOUNT_INFO;
    }

    public String getUpdateAccountInfo() {
        if(isUnitTest()){
            Log.d(TAG, "Initialize api:" + LOCAL + UPDATE_ACCOUNT_INFO);
            return LOCAL + UPDATE_ACCOUNT_INFO;
        }
        Log.d(TAG, "Initialize api:" + LIVE + UPDATE_ACCOUNT_INFO);
        return LIVE + UPDATE_ACCOUNT_INFO;
    }

    public String getImportPaymentMethods() {
        if(isUnitTest()){
            Log.d(TAG, "Initialize api:" + LOCAL + IMPORT_PAYMENT_METHODS);
            return LOCAL + IMPORT_PAYMENT_METHODS;
        }
        Log.d(TAG, "Initialize api:" + LIVE + IMPORT_PAYMENT_METHODS);
        return LIVE + IMPORT_PAYMENT_METHODS;
    }

    public String getUpdatePaymentMethod() {
        if(isUnitTest()){
            Log.d(TAG, "Initialize api:" + LOCAL + UPDATE_PAYMENT_METHOD);
            return LOCAL + UPDATE_PAYMENT_METHOD;
        }
        Log.d(TAG, "Initialize api:" + LIVE + UPDATE_PAYMENT_METHOD);
        return LIVE + UPDATE_PAYMENT_METHOD;
    }

    public String getImportShippingInfo() {
        if(isUnitTest()){
            Log.d(TAG, "Initialize api:" + LOCAL + IMPORT_SHIPPING_INFO);
            return LOCAL + IMPORT_SHIPPING_INFO;
        }
        Log.d(TAG, "Initialize api:" + LIVE + IMPORT_SHIPPING_INFO);
        return LIVE + IMPORT_SHIPPING_INFO;
    }

    public String getImportTopSellingItems() {
        if(isUnitTest()){
            Log.d(TAG, "Initialize api:" + LOCAL + IMPORT_TOP_SELLING_ITEMS);
            return LOCAL + IMPORT_TOP_SELLING_ITEMS;
        }
        Log.d(TAG, "Initialize api:" + LIVE + IMPORT_TOP_SELLING_ITEMS);
        return LIVE + IMPORT_TOP_SELLING_ITEMS;
    }

    public String getImportProducts() {
        if(isUnitTest()){
            Log.d(TAG, "Initialize api:" + LOCAL + IMPORT_PROMO_ITEMS);
            return LOCAL + IMPORT_PROMO_ITEMS;
        }
        Log.d(TAG, "Initialize api:" + LIVE + IMPORT_PROMO_ITEMS);
        return LIVE + IMPORT_PROMO_ITEMS;
    }

    public String getImportSearchResults() {
        if(isUnitTest()){
            Log.d(TAG, "Initialize api:" + LOCAL + IMPORT_SEARCH_RESULTS);
            return LOCAL + IMPORT_SEARCH_RESULTS;
        }
        Log.d(TAG, "Initialize api:" + LIVE + IMPORT_SEARCH_RESULTS);
        return LIVE + IMPORT_SEARCH_RESULTS;
    }

    public String getImportProductInfo() {
        if(isUnitTest()){
            Log.d(TAG, "Initialize api:" + LOCAL + IMPORT_PRODUCT_INFO);
            return LOCAL + IMPORT_PRODUCT_INFO;
        }
        Log.d(TAG, "Initialize api:" + LIVE + IMPORT_PRODUCT_INFO);
        return LIVE + IMPORT_PRODUCT_INFO;
    }

    public String getRemoveCartItemAPI() {
        if(isUnitTest()){
            Log.d(TAG, "Initialize api:" + LOCAL + UPDATE_CART_ITEM);
            return LOCAL + UPDATE_CART_ITEM;
        }
        Log.d(TAG, "Initialize api:" + LIVE + UPDATE_CART_ITEM);
        return LIVE + UPDATE_CART_ITEM;
    }

    public String getImportCartItems() {
        if(isUnitTest()){
            Log.d(TAG, "Initialize api:" + LOCAL + IMPORT_CART_ITEMS);
            return LOCAL + IMPORT_CART_ITEMS;
        }
        Log.d(TAG, "Initialize api:" + LIVE + IMPORT_CART_ITEMS);
        return LIVE + IMPORT_CART_ITEMS;
    }

    public String getImportPurchasesAPI() {
        if(isUnitTest()){
            Log.d(TAG, "Initialize api:" + LOCAL + IMPORT_PURCHASES);
            return LOCAL + IMPORT_PURCHASES;
        }
        Log.d(TAG, "Initialize api:" + LIVE + IMPORT_PURCHASES);
        return LIVE + IMPORT_PURCHASES;
    }

    public String getImportRetrieveShippingAddress() {
        if(isUnitTest()){
            Log.d(TAG, "Initialize api:" + LOCAL + IMPORT_RETRIEVE_SHIPPING_ADDRESS);
            return LOCAL + IMPORT_RETRIEVE_SHIPPING_ADDRESS;
        }
        Log.d(TAG, "Initialize api:" + LIVE + IMPORT_RETRIEVE_SHIPPING_ADDRESS);
        return LIVE + IMPORT_RETRIEVE_SHIPPING_ADDRESS;
    }

    public String getUpdateShippingAddress() {
        if(isUnitTest()){
            Log.d(TAG, "Initialize api:" + LOCAL + UPDATE_SHIPPING_ADDRESS);
            return LOCAL + UPDATE_SHIPPING_ADDRESS;
        }
        Log.d(TAG, "Initialize api:" + LIVE + UPDATE_SHIPPING_ADDRESS);
        return LIVE + UPDATE_SHIPPING_ADDRESS;
    }

    public String getImportDefaultPaymentMethod() {
        if(isUnitTest()){
            Log.d(TAG, "Initialize api:" + LOCAL + IMPORT_DEFAULT_PAYMENT_METHOD);
            return LOCAL + IMPORT_DEFAULT_PAYMENT_METHOD;
        }
        Log.d(TAG, "Initialize api:" + LIVE + IMPORT_DEFAULT_PAYMENT_METHOD);
        return LIVE + IMPORT_DEFAULT_PAYMENT_METHOD;
    }

    public String getUpdateDefaultPaymentMethod() {
        if(isUnitTest()){
            Log.d(TAG, "Initialize api:" + LOCAL + UPDATE_DEFAULT_PAYMENT_METHOD);
            return LOCAL + UPDATE_DEFAULT_PAYMENT_METHOD;
        }
        Log.d(TAG, "Initialize api:" + LIVE + UPDATE_DEFAULT_PAYMENT_METHOD);
        return LIVE + UPDATE_DEFAULT_PAYMENT_METHOD;
    }

    public String getImportShippingStatus() {
        if(isUnitTest()){
            Log.d(TAG, "Initialize api:" + LOCAL + IMPORT_SHIPPING_STATUS);
            return LOCAL + IMPORT_SHIPPING_STATUS;
        }
        Log.d(TAG, "Initialize api:" + LIVE + IMPORT_SHIPPING_STATUS);
        return LIVE + IMPORT_SHIPPING_STATUS;
    }

    public String getImportOrderSummary() {
        if(isUnitTest()){
            Log.d(TAG, "Initialize api:" + LOCAL + IMPORT_ORDER_SUMMARY);
            return LOCAL + IMPORT_ORDER_SUMMARY;
        }
        Log.d(TAG, "Initialize api:" + LIVE + IMPORT_ORDER_SUMMARY);
        return LIVE + IMPORT_ORDER_SUMMARY;
    }

    public String getImportReviewsAPI() {
        if(isUnitTest()){
            Log.d(TAG, "Initialize api:" + LOCAL + IMPORT_REVIEWS);
            return LOCAL + IMPORT_REVIEWS;
        }
        Log.d(TAG, "Initialize api:" + LIVE + IMPORT_REVIEWS);
        return LIVE + IMPORT_REVIEWS;
    }

    public String getQuestionsAndAnswersAPI() {
        if(isUnitTest()){
            Log.d(TAG, "Initialize api:" + LOCAL + IMPORT_INQUIRIES);
            return LOCAL + IMPORT_INQUIRIES;
        }
        Log.d(TAG, "Initialize api:" + LIVE + IMPORT_INQUIRIES);
        return LIVE + IMPORT_INQUIRIES;
    }

    public String getUpdateMobileNo() {
        if(isUnitTest()){
            Log.d(TAG, "Initialize api:" + LOCAL + UPDATE_MOBILE_NO);
            return LOCAL + UPDATE_MOBILE_NO;
        }
        Log.d(TAG, "Initialize api:" + LIVE + UPDATE_MOBILE_NO);
        return LIVE + UPDATE_MOBILE_NO;
    }

    public String getUpdateAddressAPI() {
        if(isUnitTest()){
            Log.d(TAG, "Initialize api:" + LOCAL + UPDATE_ADDRESS);
            return LOCAL + UPDATE_ADDRESS;
        }
        Log.d(TAG, "Initialize api:" + LIVE + UPDATE_ADDRESS);
        return LIVE + UPDATE_ADDRESS;
    }

    public String getImportBarangayAPI(){
        if(isUnitTest()){
            Log.d(TAG, "Initialize api:" + LOCAL + IMPORT_BARANGAY);
            return LOCAL + IMPORT_BARANGAY;
        }
        Log.d(TAG, "Initialize api:" + LIVE + IMPORT_BARANGAY);
        return LIVE + IMPORT_BARANGAY;
    }

    public String getImportTownAPI(){
        if(isUnitTest()){
            Log.d(TAG, "Initialize api:" + LOCAL + IMPORT_TOWN);
            return LOCAL + IMPORT_TOWN;
        }
        Log.d(TAG, "Initialize api:" + LIVE + IMPORT_TOWN);
        return LIVE + IMPORT_TOWN;
    }

    public String getImportProvinceAPI(){
        if(isUnitTest()){
            Log.d(TAG, "Initialize api:" + LOCAL + IMPORT_PROVINCE);
            return LOCAL + IMPORT_PROVINCE;
        }
        Log.d(TAG, "Initialize api:" + LIVE + IMPORT_PROVINCE);
        return LIVE + IMPORT_PROVINCE;
    }

    public String getImportCountriesAPI(){
        if(isUnitTest()){
            Log.d(TAG, "Initialize api:" + LOCAL + IMPORT_COUNTRIES);
            return LOCAL + IMPORT_COUNTRIES;
        }
        Log.d(TAG, "Initialize api:" + LIVE + IMPORT_COUNTRIES);
        return LIVE + IMPORT_COUNTRIES;
    }

    public String getAddressUpdateAPI(){
        if(isUnitTest()){
            Log.d(TAG, "Initialize api:" + LOCAL + URL_UPDATE_ADDRESS);
            return LOCAL + URL_UPDATE_ADDRESS;
        }
        Log.d(TAG, "Initialize api:" + LIVE + URL_UPDATE_ADDRESS);
        return LIVE + URL_UPDATE_ADDRESS;
    }

    public String getMobileUpdateAPI(){
        if(isUnitTest()){
            Log.d(TAG, "Initialize api:" + LOCAL + URL_UPDATE_MOBILE);
            return LOCAL + URL_UPDATE_MOBILE;
        }
        Log.d(TAG, "Initialize api:" + LIVE + URL_UPDATE_MOBILE);
        return LIVE + URL_UPDATE_MOBILE;
    }

    public String getEmailUpdateAPI(){
        if(isUnitTest()){
            Log.d(TAG, "Initialize api:" + LOCAL + URL_UPDATE_EMAIL);
            return LOCAL + URL_UPDATE_EMAIL;
        }
        Log.d(TAG, "Initialize api:" + LIVE + URL_UPDATE_EMAIL);
        return LIVE + URL_UPDATE_EMAIL;
    }
    public String getVerifyEmailUpdateAPI(){
        if(isUnitTest()){
            Log.d(TAG, "Initialize api:" + LOCAL + VERIFY_UPDATE_EMAIL);
            return LOCAL + VERIFY_UPDATE_EMAIL;
        }
        Log.d(TAG, "Initialize api:" + LIVE + VERIFY_UPDATE_EMAIL);
        return LIVE + VERIFY_UPDATE_EMAIL;
    }

    public String getAddToCartAPI(){
        if(isUnitTest()){
            Log.d(TAG, "Initialize api:" + LOCAL + ADD_TO_CART_ITEM);
            return LOCAL + ADD_TO_CART_ITEM;
        }
        Log.d(TAG, "Initialize api:" + LIVE + ADD_TO_CART_ITEM);
        return LIVE + ADD_TO_CART_ITEM;
    }

    public String getMarketPlaceOrderAPI(){
        if(isUnitTest()){
            Log.d(TAG, "Initialize api:" + LOCAL + MKTPL_PLACE_ORDER);
            return LOCAL + MKTPL_PLACE_ORDER;
        }
        Log.d(TAG, "Initialize api:" + LIVE + MKTPL_PLACE_ORDER);
        return LIVE + MKTPL_PLACE_ORDER;
    }

    public String getOrderPaymentAPI(){
        if(isUnitTest()){
            Log.d(TAG, "Initialize api:" + LOCAL + MKTPL_ORDER_PAYMENT);
            return LOCAL + MKTPL_ORDER_PAYMENT;
        }
        Log.d(TAG, "Initialize api:" + LIVE + MKTPL_ORDER_PAYMENT);
        return LIVE + MKTPL_ORDER_PAYMENT;
    }

    public String getImportNotificationsAPI(){
        if(isUnitTest()){
            Log.d(TAG, "Initialize api:" + LOCAL + IMPORT_NOTIFICATIONS);
            return LOCAL + IMPORT_NOTIFICATIONS;
        }
        Log.d(TAG, "Initialize api:" + LIVE + IMPORT_NOTIFICATIONS);
        return LIVE + IMPORT_NOTIFICATIONS;
    }

    public String getAddressUpdateRequestAPI(){
        if(isUnitTest()){
            Log.d(TAG, "Initialize api:" + LOCAL + REQUEST_ADDRESS_UPDATE);
            return LOCAL + REQUEST_ADDRESS_UPDATE;
        }
        Log.d(TAG, "Initialize api:" + LIVE + REQUEST_ADDRESS_UPDATE);
        return LIVE + REQUEST_ADDRESS_UPDATE;
    }

    public String getMobileUpdateRequestAPI(){
        if(isUnitTest()){
            Log.d(TAG, "Initialize api:" + LOCAL + REQUEST_MOBILE_UPDATE);
            return LOCAL + REQUEST_MOBILE_UPDATE;
        }
        Log.d(TAG, "Initialize api:" + LIVE + REQUEST_MOBILE_UPDATE);
        return LIVE + REQUEST_MOBILE_UPDATE;
    }

    public String getSubmitInquiryAPI(){
        if(isUnitTest()){
            Log.d(TAG, "Initialize api:" + LOCAL + SUBMIT_INQUIRY);
            return LOCAL + SUBMIT_INQUIRY;
        }
        Log.d(TAG, "Initialize api:" + LIVE + SUBMIT_INQUIRY);
        return LIVE + SUBMIT_INQUIRY;
    }

    public String getSubmitReviewAPI(){
        if(isUnitTest()){
            Log.d(TAG, "Initialize api:" + LOCAL + SUBMIT_REVIEW);
            return LOCAL + SUBMIT_REVIEW;
        }
        Log.d(TAG, "Initialize api:" + LIVE + SUBMIT_REVIEW);
        return LIVE + SUBMIT_REVIEW;
    }

    public String getSendOtpAPI(){
        if(isUnitTest()){
            Log.d(TAG, "Initialize api:" + LOCAL + RESEND_OTP);
            return LOCAL + RESEND_OTP;
        }
        Log.d(TAG, "Initialize api:" + LIVE + RESEND_OTP);
        return LIVE + RESEND_OTP;
    }

    public String getCancelMarketplaceOrderAPI(){
        if(isUnitTest()){
            Log.d(TAG, "Initialize api:" + LOCAL + MKTPL_CANCEL_ORDER);
            return LOCAL + MKTPL_CANCEL_ORDER;
        }
        Log.d(TAG, "Initialize api:" + LIVE + MKTPL_CANCEL_ORDER);
        return LIVE + MKTPL_CANCEL_ORDER;
    }

    public String getDownloadBankAccountAPI(){
        if(isUnitTest()){
            Log.d(TAG, "Initialize api:" + LOCAL + IMPORT_BANK_ACC);
            return LOCAL + IMPORT_BANK_ACC;
        }
        Log.d(TAG, "Initialize api:" + LIVE + IMPORT_BANK_ACC);
        return LIVE + IMPORT_BANK_ACC;
    }

    public String getDownloadCancellationsAPI(){
        if(isUnitTest()){
            Log.d(TAG, "Initialize api:" + LOCAL + IMPORT_MKTPL_CANCELLATIONS);
            return LOCAL + IMPORT_MKTPL_CANCELLATIONS;
        }
        Log.d(TAG, "Initialize api:" + LIVE + IMPORT_MKTPL_CANCELLATIONS);
        return LIVE + IMPORT_MKTPL_CANCELLATIONS;
    }

    public String getRetrieveProfilePictureAPI(){
        if(isUnitTest()){
            Log.d(TAG, "Initialize api:" + LOCAL + RETRIEVE_PROFILE_PICTURE);
            return LOCAL + RETRIEVE_PROFILE_PICTURE;
        }
        Log.d(TAG, "Initialize api:" + LIVE + RETRIEVE_PROFILE_PICTURE);
        return LIVE + RETRIEVE_PROFILE_PICTURE;
    }

    public String getRetrieveVerifiedIDAPI(){
        if(isUnitTest()){
            Log.d(TAG, "Initialize api:" + LOCAL + RETRIEVE_VERIFIED_ID);
            return LOCAL + RETRIEVE_VERIFIED_ID;
        }
        Log.d(TAG, "Initialize api:" + LIVE + RETRIEVE_VERIFIED_ID);
        return LIVE + RETRIEVE_VERIFIED_ID;
    }

    public String getImportEmailsAPI(){
        if(isUnitTest()){
            Log.d(TAG, "Initialize api:" + LOCAL + IMPORT_EMAILS);
            return LOCAL + IMPORT_EMAILS;
        }
        Log.d(TAG, "Initialize api:" + LIVE + IMPORT_EMAILS);
        return LIVE + IMPORT_EMAILS;
    }

    public String getImportMobileNoAPI(){
        if(isUnitTest()){
            Log.d(TAG, "Initialize api:" + LOCAL + IMPORT_MOBILE_NO);
            return LOCAL + IMPORT_MOBILE_NO;
        }
        Log.d(TAG, "Initialize api:" + LIVE + IMPORT_MOBILE_NO);
        return LIVE + IMPORT_MOBILE_NO;
    }


    public String getSubmitSelfieVerificationAPI(){
        if(isUnitTest()){
            Log.d(TAG, "Initialize api:" + LOCAL + SUBMIT_SELFIE_VERIFICATION);
            return LOCAL + SUBMIT_SELFIE_VERIFICATION;
        }
        Log.d(TAG, "Initialize api:" + LIVE + SUBMIT_SELFIE_VERIFICATION);
        return LIVE + SUBMIT_SELFIE_VERIFICATION;
    }

    public String getSubmitIdVerificationAPI(){
        if(isUnitTest()){
            Log.d(TAG, "Initialize api:" + LOCAL + SUBMIT_ID_VERIFICATION);
            return LOCAL + SUBMIT_ID_VERIFICATION;
        }
        Log.d(TAG, "Initialize api:" + LIVE + SUBMIT_ID_VERIFICATION);
        return LIVE + SUBMIT_ID_VERIFICATION;
    }

    public String getImportValidIdCodeAPI(){
        if(isUnitTest()){
            Log.d(TAG, "Initialize api:" + LOCAL + IMPORT_VALID_ID_CODE);
            return LOCAL + IMPORT_VALID_ID_CODE;
        }
        Log.d(TAG, "Initialize api:" + LIVE + IMPORT_VALID_ID_CODE);
        return LIVE + IMPORT_VALID_ID_CODE;
    }

    public String getSubmitMeansInfo(){
        if(isUnitTest()){
            Log.d(TAG, "Initialize api:" + LOCAL + SUBMIT_MEANS_INFO);
            return LOCAL + SUBMIT_MEANS_INFO;
        }
        Log.d(TAG, "Initialize api:" + LIVE + SUBMIT_MEANS_INFO);
        return LIVE + SUBMIT_MEANS_INFO;
    }

    public String getSubmitOtherInfo(){
        if(isUnitTest()){
            Log.d(TAG, "Initialize api:" + LOCAL + SUBMIT_OTHER_INFO);
            return LOCAL + SUBMIT_OTHER_INFO;
        }
        Log.d(TAG, "Initialize api:" + LIVE + SUBMIT_OTHER_INFO);
        return LIVE + SUBMIT_OTHER_INFO;
    }

    public String getOtherApplicationInfo(){
        if(isUnitTest()){
            Log.d(TAG, "Initialize api:" + LOCAL + GET_OTHER_APPLICATION_INFO);
            return LOCAL + GET_OTHER_APPLICATION_INFO;
        }
        Log.d(TAG, "Initialize api:" + LIVE + GET_OTHER_APPLICATION_INFO);
        return LIVE + GET_OTHER_APPLICATION_INFO;
    }

    public String getSubmitLoanApplication(){
        if(isUnitTest()){
            Log.d(TAG, "Initialize api:" + LOCAL + SUBMIT_LOAN_APPLICATION);
            return LOCAL + SUBMIT_LOAN_APPLICATION;
        }
        Log.d(TAG, "Initialize api:" + LIVE + SUBMIT_LOAN_APPLICATION);
        return LIVE + SUBMIT_LOAN_APPLICATION;
    }

    public String getDownloadPriceListAPI(){
        if(isUnitTest()){
            Log.d(TAG, "Initialize api:" + LOCAL + IMPORT_PRICE_LIST);
            return LOCAL + IMPORT_PRICE_LIST;
        }
        Log.d(TAG, "Initialize api:" + LIVE + IMPORT_PRICE_LIST);
        return LIVE + IMPORT_PRICE_LIST;
    }

    public String getUserPanaloRewardsAPI(){
        if(isUnitTest()){
            Log.d(TAG, "Initialize api:" + LOCAL + GET_PANALO_REWARDS);
            return LOCAL + GET_PANALO_REWARDS;
        }
        Log.d(TAG, "Initialize api:" + LIVE + GET_PANALO_REWARDS);
        return LIVE + GET_PANALO_REWARDS;
    }

    public String getUpdateClientMobileAPI(){
        if(isUnitTest()){
            Log.d(TAG, "Initialize api:" + LOCAL + UPDATE_CLIENT_MOBILE_NO);
            return LOCAL + UPDATE_CLIENT_MOBILE_NO;
        }
        Log.d(TAG, "Initialize api:" + LIVE + UPDATE_CLIENT_MOBILE_NO);
        return LIVE + UPDATE_CLIENT_MOBILE_NO;
    }

    public String getVerifyClientMobileAPI(){
        if(isUnitTest()){
            Log.d(TAG, "Initialize api:" + LOCAL + VERIFY_CLIENT_MOBILE_NO);
            return LOCAL + VERIFY_CLIENT_MOBILE_NO;
        }
        Log.d(TAG, "Initialize api:" + LIVE + VERIFY_CLIENT_MOBILE_NO);
        return LIVE + VERIFY_CLIENT_MOBILE_NO;
    }
}

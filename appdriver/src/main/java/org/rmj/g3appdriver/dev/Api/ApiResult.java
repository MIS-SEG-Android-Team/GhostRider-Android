package org.rmj.g3appdriver.dev.Api;

import org.json.JSONObject;
public class ApiResult {


    public static String SERVER_NO_RESPONSE = "Apologies, the server is not responding at the moment. Please try again later.";
    public static String NOT_CONNECTED = "Connectivity required. Please enable WiFi or enable mobile data to proceed.";
    public static String UNABLE_TO_REACH_LOCAL = "We're experiencing difficulties reaching the local server. Please ensure it is accessible and try again.";
    public static String UNABLE_TO_REACH_SERVER = "We're unable to establish a connection with our servers at the moment. Please check your internet connection and try again.";

    public static String getErrorMessage(JSONObject args) throws Exception{
        String lsCode = args.getString("code");
        switch (lsCode){
            case "40020":
                return "KAY Gandang Araw!\n\nMaaaring pakisara muna ang iyong Guanzon Circle app at muling buksan para maglogin.\nSalamat.";

            default:
                String lsMessage = args.getString("message");
                return getMessage(lsMessage);
        }
    }

    private static String getMessage(String lsMessage){
        switch (lsMessage){
            case "Reload failed...":
                return "We're currently experiencing high network traffic. Please try your request again in a few moments.";

            case "Invalid password detected.":
                return "Authentication Failed. Please check your Password and try again.";

            case "Invalid email detected.":
                return "Email Error. The provided email is not recognized. Please review your entry.";

            case "No inquiries found":
                return "Oops! No updated inquiries found.";

            default:
                return lsMessage;
        }
    }
}

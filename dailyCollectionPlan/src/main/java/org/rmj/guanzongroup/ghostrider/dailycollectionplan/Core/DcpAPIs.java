package org.rmj.guanzongroup.ghostrider.dailycollectionplan.Core;

public class DcpAPIs {

    private boolean isUnitTest = false;

    private static final String LIVE = "https://apps.guanzongroup.com.ph/";
    private static final String LOCAL = "http://192.168.10.141/";

    private String URL_DOWNLOAD_DCP = "integsys/dcp/dcp_download.php";
    private String URL_DCP_SUBMIT = "integsys/dcp/dcp_submit.php";
    private String URL_POST_DCP_MASTER = "integsys/dcp/dcp_post.php";

    public DcpAPIs(boolean isUnitTest) {
        this.isUnitTest = isUnitTest;
    }

    public String getUrlDownloadDcp() {
        if(isUnitTest){
            return LOCAL + URL_DOWNLOAD_DCP;
        }
        return LIVE + URL_DOWNLOAD_DCP;
    }

    public String getUrlDcpSubmit() {
        if(isUnitTest){
            return LOCAL + URL_DCP_SUBMIT;
        }
        return LIVE + URL_DCP_SUBMIT;
    }

    public String getUrlPostDcp() {
        if(isUnitTest){
            return LOCAL + URL_POST_DCP_MASTER;
        }
        return LIVE + URL_POST_DCP_MASTER;
    }
}

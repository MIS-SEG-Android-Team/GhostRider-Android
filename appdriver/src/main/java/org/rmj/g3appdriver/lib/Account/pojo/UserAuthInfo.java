package org.rmj.g3appdriver.lib.Account.pojo;

public class UserAuthInfo {
    private final String Email;
    private final String Password;
    private String message;

    public UserAuthInfo(String email, String password) {
        Email = email;
        Password = password;
    }

    public String getEmail() {
        return Email;
    }

    public String getPassword() {
        return Password;
    }

    public String getMessage(){
        return message;
    }

    public boolean isAuthInfoValid(){
        if(!isEmailValid()){
            return false;
        }
        if(!isPasswordValid()){
            return false;
        }
        return true;
    }

    private boolean isEmailValid(){
        if(Email.isEmpty()){
            message = "Please enter your email";
            return false;
        }
        return true;
    }

    private boolean isPasswordValid(){
        if(Password.isEmpty()) {
            message = "Please enter password";
            return false;
        }
        return true;
    }
}
package backend.endpoints.requests;

public class ChangePasswordRequest {

    private String oldPassword;
    private String password;


    public ChangePasswordRequest() {
    }

    public ChangePasswordRequest(String oldPassword, String password) {
        this.oldPassword = oldPassword;
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }
}

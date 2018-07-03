package jp.co.leadinge.chat.entity;

public class Member {
    private String id;
    private String loginid;
    private String password;
    private String username;

    public String getId() {
        return id;
    }

    public String getLoginid() {
        return loginid;
    }

    public String getPassword() {
        return password;
    }

    public String getUsername() {
        return username;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setLoginid(String loginid) {
        this.loginid = loginid;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}

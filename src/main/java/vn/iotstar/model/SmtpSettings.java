package vn.iotstar.model;

/** SMTP configuration managed by an administrator for this local application. */
public class SmtpSettings {
    private String host;
    private int port = 587;
    private String username;
    private String password;
    private String fromEmail;
    private boolean startTls = true;

    public String getHost() { return host; }
    public void setHost(String host) { this.host = host; }
    public int getPort() { return port; }
    public void setPort(int port) { this.port = port; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getFromEmail() { return fromEmail; }
    public void setFromEmail(String fromEmail) { this.fromEmail = fromEmail; }
    public boolean isStartTls() { return startTls; }
    public void setStartTls(boolean startTls) { this.startTls = startTls; }
    public boolean isComplete() {
        return present(host) && port > 0 && present(username) && present(password) && present(fromEmail);
    }
    private boolean present(String value) { return value != null && !value.isBlank(); }
}

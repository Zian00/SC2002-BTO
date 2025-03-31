public interface IAuthentication {
    boolean login();
    void changePassword(String newPassword);
}

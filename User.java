public abstract class User implements IAuthentication {
    
    private String NRIC;
    private String password;
    private int age;
    private MaritalStatus maritalStatus;
    
    // Constructors, getters, setters can be added here

    @Override
    public boolean login() {
        // Implementation code here
        return true;
    }

    @Override
    public void changePassword(String newPassword) {
        // Change password logic here
        this.password = newPassword;
    }
}

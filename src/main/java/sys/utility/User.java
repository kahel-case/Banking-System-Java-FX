package sys.utility;

public class User extends Account {
    private double currentBalance;
    private String userName;
    private String userPassword;

    @Override
    int getCurrentBalance() {
        return 0;
    }

    @Override
    String getUserName() {
        return userName;
    }

    @Override
    String getUserPassword() {
        return userPassword;
    }
}

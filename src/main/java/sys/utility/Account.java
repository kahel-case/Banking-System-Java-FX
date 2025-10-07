package sys.utility;

public abstract class Account {
    double currentBalance;
    String userName;
    String userPassword;

    abstract int getCurrentBalance();
    abstract String getUserName();
    abstract String getUserPassword();
}

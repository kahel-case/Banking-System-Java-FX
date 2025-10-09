package sys.utility;

public abstract class Account {
    double currentBalance;
    String userName;

    abstract double getCurrentBalance();
    abstract String getUserName();
}

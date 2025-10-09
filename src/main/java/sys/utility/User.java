package sys.utility;

public class User extends Account {
    private double _currentBalance;
    private String _userName;

    public User(double currentBalance, String userName) {
        this._currentBalance = currentBalance;
        this._userName = userName;
    }

    @Override
    public double getCurrentBalance() {
        return _currentBalance;
    }

    @Override
    String getUserName() {
        return _userName;
    }
}

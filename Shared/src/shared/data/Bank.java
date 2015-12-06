package shared.data;

public enum Bank {
    Royal,
    National,
    Dominion,
    None;

    /**
     * maps an integer to each bank name.
     * @param bankId
     * @return
     */
    public static Bank fromInt(int bankId) {
        switch (bankId) {
            case 1:
                return Royal;

            case 2:
                return National;

            case 3:
                return Dominion;

            default:
                return None;
        }
    }

    public static Bank fromString(String bankName)
    {
        switch (bankName.toUpperCase()) {
            case "ROYAL":
                return Royal;

            case "NATIONAL":
                return National;

            case "DOMINION":
                return Dominion;

            default:
                return None;
        }
    }

    public static Bank[] getBanks() {
        return new Bank[]{
                Bank.Royal,
                Bank.National,
                Bank.Dominion
        };
    }

    /**
     * maps a bank name to an integer.
     * @return
     */
    public int toInt() {
        switch (this) {
            case Royal:
                return 1;

            case National:
                return 2;

            case Dominion:
                return 3;

            default:
                return 0;
        }
    }

}

/*
    Paulo Vasconcelos
    paulobvasconcelos@gmail.com
    2018 july
 */

public class PokerPlayer extends Player {
    // ----------------------------------------
    // Atributes
    public String status = null;

    // ----------------------------------------
    // Constructors
    public PokerPlayer(String name) { super(name); }

    public PokerPlayer(String name, int funds) { super(name, funds); }

    public PokerPlayer(String name, String status) {
        super(name);
        if(isValidStatus(status))  { this.status = status; }
        else                        { System.err.println("ERROR: Invalid player status."); System.exit(1); }
    }

    public PokerPlayer(String name, int funds, String status) {
        super(name, funds);
        if(isValidStatus(status))   { this.status = status; }
        else                        { System.err.println("ERROR: Invalid player status."); System.exit(1); }
    }

    // ----------------------------------------
    // Functions
    public String getStatus() { return status; }
    public void setStatus(String status) {
        if(status.equals("")) { this.status = null; }
        else {
            if(isValidStatus(status))   { this.status = status; }
            else                        { System.err.println("ERROR: Invalid player status."); System.exit(1); }
        }
    }

    public boolean isValidStatus(String str) {
        return ((str.equals("FOLD"))||(str.equals("CHECK"))||(str.equals("CALL"))||(str.equals("RAISE"))||(str.equals("ALLIN")));
    }

    public PokerAction play(int betToCover) {
        return null;
    }

    // ----------------------------------------
}
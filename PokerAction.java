/*
    Paulo Vasconcelos
    paulobvasconcelos@gmail.com
    2018 july
 */

public class PokerAction extends TableAction {

    // ----------------------------------------
    // Atributes
    // --- All inherited ---

    // ----------------------------------------
    // Constructors
    public PokerAction(String name) {
        super();
        name = name.toUpperCase();
        if(isValidAction(name) && !name.equals("RAISE"))    { this.name = name; }
        else                                                { System.err.println("ERROR: Invalid Poker Action."); System.exit(1); }
    }

    public PokerAction(String name, Integer amount) {
        super();
        name = name.toUpperCase();
        if(name.equals("RAISE") && amount>0)    { this.name = name; this.amount = amount; }
        else                                    { System.err.println("ERROR: Invalid Poker Action."); System.exit(1); }
    }

    // ----------------------------------------
    // Functions
    public String getName()     { return this.name; }
    public Integer getAmount()  { return this.amount; }

    private boolean isValidAction(String action) {
        return ((action.equals("FOLD"))||(action.equals("CHECK"))||(action.equals("CALL"))||(action.equals("RAISE"))||(action.equals("ALLIN")));
    }
    // ----------------------------------------
}
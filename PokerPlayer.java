/*
    Paulo Vasconcelos
    paulobvasconcelos@gmail.com
    2018 july
 */

import KarkaniusUtils.Menu;
import KarkaniusUtils.READ;

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
    public String getStatus() {
        try {
            this.status.equals(null);
        } catch(NullPointerException e) {
            return "";
        }
        return this.status;
    }
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
        System.out.println("Player:        "+super.name);
        System.out.println("Bet to conver: "+betToCover);
        System.out.println("Funds:         "+super.funds);
        System.out.println(super.cards);
        Menu menu;
        String title = "Actions";
        String[] options;
        // In case he is forced to either FOLD or go ALLIN
        if(this.funds<betToCover) { options = new String[] {"FOLD","ALLIN"}; }
        // In case there's no bet to cover, there's no reason to FOLD
        else if(betToCover==0) { options = new String[] {"CHECK","RAISE","ALLIN"}; }
        // Regular play
        else { options = new String[] {"FOLD","CALL","RAISE","ALLIN"}; }
        menu = new Menu(title, options);
        menu.printMenu();
        int op = READ.readInteger();;
        while(op<1||op>options.length) {
            System.out.println("Warning: Invalid option inserted.");
            op = READ.readInteger();
        }
        String actionName = options[op-1];
        if(actionName.equals("RAISE")) {
            String ans = "yes";
            int amount = 0;
            do {
                amount = READ.readInteger("Amount");
                while (amount <= 0 || amount > super.funds) {
                    System.out.println("Warning: Invalid amount inserted.");
                    amount = READ.readInteger("Amount");
                }
                if (amount == super.funds) {
                    ans = READ.readString("Do you wish to ALLIN instead? (y/n)");
                    while (ans.toLowerCase().equals("y")||ans.toLowerCase().equals("yes")||
                            ans.toLowerCase().equals("n")||ans.toLowerCase().equals("no")) {
                        System.out.println("Warning: Invalid answer inserted.");
                        ans = READ.readString("Do you wish to ALLIN instead? (y/n)");
                    }
                    if (ans.toLowerCase().equals("y")||ans.toLowerCase().equals("yes")) {
                        return new PokerAction("ALLIN");
                    }
                }
            } while(ans.toLowerCase().equals("")||ans.toLowerCase().equals("no"));
            return new PokerAction("RAISE", amount);
        } else { return new PokerAction(actionName); }
    }

    @Override
    public String stats() {
        String tempStatus = "";
        try {
            this.status.equals(null);
            tempStatus = this.status;
        } catch (NullPointerException e) { }
        return ("Name:   "+this.name+"\n"+
                "Funds:  "+this.funds+"\n"+
                "Status: "+tempStatus+"\n");
    }

    // ----------------------------------------
}
/*
    Paulo Vasconcelos
    paulobvasconcelos@gmail.com
    2018 july
 */

public class PokerBot_v1 extends PokerPlayer implements PokerBot {

    // ----------------------------------------
    // Atributes
    private BotStance stance = BotStance.NEUTRAL;

    // ----------------------------------------
    // Constructors
    public PokerBot_v1(String name) {
        super(name);
    }

    public PokerBot_v1(String name, int funds) {
        super(name, funds);
    }

    public PokerBot_v1(String name, BotStance stance) {
        super(name);
        this.stance = stance;
    }

    public PokerBot_v1(String name, int funds, BotStance stance) {
        super(name, funds);
        this.stance = stance;
    }

    // ----------------------------------------
    // Functions
    public String getName()         { return this.name; }
    public int getFunds()           { return this.funds; }
    public BotStance getStance()    { return this.stance; }
    public void setName(String name)        { this.name = name; }
    public void setFunds(int funds)         { this.funds = funds; }
    public void setStance(BotStance stance) { this.stance = stance; }

    public void withdrawFunds(int amount) {
        this.funds -= amount;
    }

    public void addFunds(int amount) {
        this.funds += amount;
    }

    @Override
    public PokerAction play(int betToCover) {
        double[] odds = {1,0,0,0};
        // In case he is forced to either FOLD or go ALLIN
        if(this.funds<=betToCover) {
            switch (this.stance) {
                case DEFENSIVE:
                    odds = new double[]{
                            2 / 3,    // Fold
                            0,      // Call
                            0,      // Raise
                            1 / 3     // Allin
                    };
                    break;
                case NEUTRAL:
                    odds = new double[]{
                            0.5,    // Fold
                            0,      // Call
                            0,      // Raise
                            0.5     // Allin
                    };
                    break;
                case OFFENSIVE:
                    odds = new double[]{
                            1 / 3,    // Fold
                            0,      // Call
                            0,      // Raise
                            2 / 3     // Allin
                    };
                    break;
            }
        }
        // In case there's no bet to cover, there's no reason to FOLD
        else if(betToCover==0) {
            switch (this.stance) {
                case DEFENSIVE:
                    odds = new double[] {
                            0,      // Fold
                            0.9,    // Call
                            0.1,    // Raise
                            0       // Allin
                    };
                    break;
                case NEUTRAL:
                    odds = new double[] {
                            0,      // Fold
                            0.75,   // Call
                            0.2,    // Raise
                            0.05    // Allin
                    };
                    break;
                case OFFENSIVE:
                    odds = new double[] {
                            0,      // Fold
                            0.6,    // Call
                            0.25,   // Raise
                            0.1     // Allin
                    };
                    break;
            }
        }
        // Regular play
        else {
            switch (this.stance) {
                case DEFENSIVE:
                    odds = new double[] {
                            0.3,    // Fold
                            0.6,    // Call
                            0.1,    // Raise
                            0       // Allin
                    };
                    break;
                case NEUTRAL:
                    odds = new double[] {
                            0.1,    // Fold
                            0.65,   // Call
                            0.2,    // Raise
                            0.05    // Allin
                    };
                    break;
                case OFFENSIVE:
                    odds = new double[] {
                            0.05,   // Fold
                            0.55,   // Call
                            0.25,   // Raise
                            0.1     // Allin
                    };
                    break;
            }
        }
        double foldThreshold  = odds[0];
        double callThreshold  = odds[0] + odds[1];
        double raiseThreshold = odds[0] + odds[1] + odds[2];
        double allinThreshold = odds[0] + odds[1] + odds[2] + odds[3];
        if(allinThreshold != 1) { System.err.println("ERROR: Invalid odds definition."); System.exit(1); }
        double play = Math.random();
        if(play<foldThreshold) { return new PokerAction("FOLD"); }
        if(play<callThreshold) {
            if(betToCover==0) return new PokerAction("CHECK");
            return new PokerAction("CALL");
        }
        if(play<raiseThreshold) { return new PokerAction("RAISE", (int)(Math.random()*(this.funds-betToCover))); }
        return new PokerAction("ALLIN",super.funds);
    }

    @Override
    public String stats() {
        String tempStatus = "";
        try {
            this.status.equals(null);
            tempStatus = this.status;
        } catch (NullPointerException e) { }
        return ("Name:   "+this.name+"\n"+
                "ID:     "+this.id+"\n"+
                "Funds:  "+this.funds+"\n"+
                "Stance: "+this.stance+"\n"+
                "Status: "+tempStatus+"\n");
    }

    // ----------------------------------------
}
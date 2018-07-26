/*
    Paulo Vasconcelos
    paulobvasconcelos@gmail.com
    2018 july
 */

import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;

public class Pot {
    // ----------------------------------------
    // Atributes
    private Set<Integer> players = new HashSet<>();
    private Map<Integer, Integer> deposited = new HashMap<>();
    private int amount = 0;
    private Integer playerAmount;
    private boolean closed = false;

    // ----------------------------------------
    // Constructors
    public Pot() { }

    // ----------------------------------------
    // Functions
    public Set<Integer> getPlayers()            { return this.players; }
    public int getPlayerAmount()                { return this.playerAmount; }
    public int getAmount()                      { return this.amount; }
    public Map<Integer, Integer> getDeposited() { return this.deposited; }

    public void setDeposited(Map<Integer, Integer> map) { this.deposited = map; }
    public void setPlayerAmount(int playerAmount)       { this.playerAmount = playerAmount; }

    public boolean isClosed()           { return this.closed; }

    public void addFunds(PokerPlayer player, int amount) { this.addFunds(player.getID(), amount); }
    public void addFunds(int playerID, int amount) {
        if(amount<=0) {
            System.err.println("ERROR: Invalid funds added to pot.");
            System.err.println("\tFunds: "+amount);
            System.exit(1);
        }
        this.amount += amount;
        try {
            this.deposited.put(playerID, this.deposited.get(playerID)+amount);
        } catch (NullPointerException e) {
            this.deposited.put(playerID, amount);
        }
        try {
            if (this.deposited.get(playerID) == playerAmount) {
                this.addPlayer(playerID);
            }
            if (this.deposited.get(playerID) > playerAmount) {
                System.err.println("ERROR: Player funds exceed pot.");
                System.err.println("\tPlayer funds: " + this.deposited.get(playerID));
                System.err.println("\tPot:          " + this.playerAmount);
                System.exit(1);
            }
        } catch (NullPointerException e) { }
    }

    public void withdrawFunds(PokerPlayer player, int amount) { this.withdrawFunds(player.getID(), amount); }
    public void withdrawFunds(int playerID, int amount) {
        if(amount<=0) { System.err.println("ERROR: Invalid funds withdrawed from pot."); System.exit(1); }
        this.amount -= amount;
        this.deposited.put(playerID, this.deposited.get(playerID)-amount);
        if(this.deposited.get(playerID)==playerAmount) { this.addPlayer(playerID); }
        if(this.deposited.get(playerID)>playerAmount) { System.err.println("ERROR: Player funds exceed pot."); System.exit(1); }
        }

    public void closePot(PokerPlayer player, int amount) { this.closePot(player.getID(), amount); }
    public void closePot(int playerID, int amount) {
        this.addFunds(playerID, amount);
        this.closed = true;
        this.addPlayer(playerID);
        this.playerAmount = this.deposited.get(playerID);
    }

    public void addPlayer(Integer playerID) { this.players.add(playerID); }

    public boolean potCompleted(PokerPlayer player)    { return this.potCompleted(player.getID()); }
    public boolean potCompleted(int playerID)          { return players.contains(playerID); }

    public int missing(PokerPlayer player)  { return this.missing(player.getID()); }
    public int missing(int playerID)        {
        try {
            return this.playerAmount-deposited.get(playerID);
        } catch (NullPointerException e) {
            return this.playerAmount;
        }
    }

    // ----------------------------------------
}
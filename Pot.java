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
    private int playerAmount;
    private boolean closed = false;

    // ----------------------------------------
    // Constructors
    public Pot() { }

    // ----------------------------------------
    // Functions
    public Set<Integer> getPlayers()    { return this.players; }
    public int getPlayerAmount()        { return this.playerAmount; }
    public int getAmount()              { return this.amount; }
    public boolean isClosed()           { return this.closed; }

    public void addFunds(PokerPlayer player, int amount) {
        if(amount<=0) { System.err.println("ERROR: Invalid funds added to pot."); System.exit(1); }
        this.amount += amount;
        this.deposited.put(player.getID(), this.deposited.get(player.getID())+amount);
        if(this.deposited.get(player.getID())==playerAmount) { this.addPlayer(player.getID()); }
    }
    public void addFunds(int playerID, int amount) {
        if(amount<=0) { System.err.println("ERROR: Invalid funds added to pot."); System.exit(1); }
        this.amount += amount;
        this.deposited.put(playerID, this.deposited.get(playerID)+amount);
        if(this.deposited.get(playerID)==playerAmount) { this.addPlayer(playerID); }
    }

    public void closePot(PokerPlayer player, int amount) {
        this.closed = true;
        this.addPlayer(player.getID());
    }
    public void closePot(int playerID, int amount) {
        this.closed = true;
        this.addPlayer(playerID);
    }

    private void addPlayer(Integer playerID) { this.players.add(playerID); }

    public boolean potCompleted(PokerPlayer player)    { return players.contains(player.getID()); }
    public boolean potCompleted(int playerID)          { return players.contains(playerID); }

    public int missing(PokerPlayer player)  { return playerAmount-deposited.get(player.getID()); }
    public int missing(int playerID)        { return playerAmount-deposited.get(playerID); }

    // ----------------------------------------
}
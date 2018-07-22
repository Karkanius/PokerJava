/*
    Paulo Vasconcelos
    paulobvasconcelos@gmail.com
    2018 july
 */

import java.util.List;
import java.util.Set;
import java.util.HashSet;

public class Table<T extends Player> {

    // ----------------------------------------
    // Atributes
    private static final int minPlayers = 2;
    private static final int maxPlayers = 6;
    private List<T> players;
    private int initialFunds = 1000;
    private char currency = '$';

    // ----------------------------------------
    // Constructors
    public Table(List<T> players) {
        if(validPlayerAmount((List<Player>)players))  { this.players = players; }
        else                            { System.err.println("ERROR: Invalid player amount."); System.exit(1); }
        setPlayersFunds(initialFunds);
    }

    public Table(List<T> players, int initialFunds) {
        boolean valid = true;
        if(validPlayerAmount((List<Player>)players))  { this.players = players; }
        else                            { System.err.println("ERROR: Invalid player amount."); valid = false; }
        if(initialFunds>0)  { this.initialFunds = initialFunds; }
        else                { System.err.println("ERROR: Invalid funds."); valid = false; }
        if(!valid) { System.exit(1); }
        setPlayersFunds(initialFunds);
    }

    // ----------------------------------------
    // Functions
    public static int getMinPlayers()   { return minPlayers; }
    public static int getMaxPlayers()   { return maxPlayers; }
    public List<T> getPlayers()         { return this.players; }
    public int getInitianFunds()        { return this.initialFunds; }
    public char getCurrency()           { return this.currency; }
    public void setPlayers(List<T> players)         { this.players = players; }
    public void setInitialFunds(int initialFunds)   { this.initialFunds = initialFunds; }
    public void setCurrency(char currency)          { this.currency = currency; }

    private static boolean validPlayerAmount(List<Player> players) { return((players.size()>=minPlayers)&&(players.size()<=maxPlayers)); }

    private void setPlayersFunds(int funds) {
        for(T p : players) { p.setFunds(funds); }
    }

    public void draw() {
        switch(players.size()) {
            case 2:
                break;
            case 3:
                break;
            case 4:
                break;
            case 5:
                break;
            case 6:
                break;
            default:

        }
    }

    public void printStat() { for(T p : players) { System.out.println(p.stats()); } }

    public void kickPlayers() {
        for(int i=0; i<players.size(); i++) {
            try {
                if(players.get(i).getFunds()==0) { players.set(i, null); }
            } catch (Exception e) { }
        }
    }

    public Set<T> activePlayers() {
        Set<T> retValue = new HashSet<>();
        for(int i=0; i<players.size(); i++) { if(players.get(i)!=null) { retValue.add(players.get(i)); } }
        return retValue;
    }

    public void updatePlayers(List<T> updated) {
        for(int i=0; i<this.players.size(); i++) {
            String playerName = this.players.get(i).getName();
            for(T player : updated) {
                if(player.getName().equals(playerName)) {
                    this.players.set(i, player);
                    break;
                }
            }
        }
    }

    // ----------------------------------------
}

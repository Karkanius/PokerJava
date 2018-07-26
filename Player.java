/*
    Paulo Vasconcelos
    paulobvasconcelos@gmail.com
    2018 july
 */

import java.util.Set;
import java.util.HashSet;
import java.util.Collection;

public class Player {

    // ----------------------------------------
    // Atributes
    public String name;
    public int funds = 0;
    public Set<Card> cards = new HashSet<>();
    public int id;

    private static Set<Integer> IDs = new HashSet<>();
    private final static int idRange = 100000;

    // ----------------------------------------
    // Constructors
    public Player(String name) {
        this.name = name;
        this.id = Player.generateID();
    }

    public Player(String name, int funds) {
        this.name = name;
        this.funds = funds;
        this.id = Player.generateID();
    }

    // ----------------------------------------
    // Functions
    public String getName()     { return this.name; }
    public int getFunds()       { return this.funds; }
    public Set<Card> getCards() { return cards; }
    public int getID()          { return this.id; }
    public void setName(String name)    { this.name = name; }
    public void setFunds(int funds)     { this.funds = funds; }
    public void setCards(Set<Card> cards) { this.cards = cards; }

    public void giveCard(Card card) { this.cards.add(card); }

    public void withdrawFunds(int amount) {
        this.funds -= amount;
    }
    public void addFunds(int amount) {
        this.funds += amount;
    }

    private static int generateID() {
        int tempID;
        do {
            tempID = (int)(idRange*Math.random());
        } while(IDs.contains(tempID));
        return tempID;
    }

    public boolean equals(Player player) { return this.id==player.getID(); }

    public static Set<Player> getPlayersFromID(Collection<Player> players, Collection<Integer> IDs) {
        Set<Player> retvalue = new HashSet<>();
        for (Integer id : IDs) {
            for (Player player : players) {
                if(player.getID()==id) {
                    retvalue.add(player);
                    break;
                }
            }
        }
        return retvalue;
    }

    public static Set<Integer> getIDFromPlayers(Collection<Player> players) {
        Set<Integer> retvalue = new HashSet<>();
        for (Player player : players) { retvalue.add(player.getID()); }
        return retvalue;
    }

    public void removeCards() { this.cards = new HashSet<>(); }

    public String stats() {
        return ("Name:  "+this.name+"\n"+
                "ID:    "+this.id+"\n"+
                "Funds: "+this.funds+"\n");
    }

    // ----------------------------------------
}
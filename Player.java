/*
    Paulo Vasconcelos
    paulobvasconcelos@gmail.com
    2018 july
 */

import java.util.Set;
import java.util.HashSet;

public class Player {

    // ----------------------------------------
    // Atributes
    public String name;
    public int funds = 0;
    public Set<Card> cards = new HashSet<>();

    // ----------------------------------------
    // Constructors
    public Player(String name) {
        this.name = name;
    }

    public Player(String name, int funds) {
        this.name = name;
        this.funds = funds;
    }

    // ----------------------------------------
    // Functions
    public String getName() { return this.name; }
    public int getFunds()   { return this.funds; }
    public Set<Card> getCards() { return cards; }
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

    public String stats() {
        return ("Name:  "+this.name+"\n"+
                "Funds: "+this.funds+"\n");
    }

    // ----------------------------------------
}
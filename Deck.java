/*
    Paulo Vasconcelos
    paulobvasconcelos@gmail.com
    2018 july
 */

import java.util.Set;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Collection;

public class Deck extends HashSet<Card> {

    // ----------------------------------------
    // Atributes

    // ----------------------------------------
    // Constructors
    public Deck() { this.generateDeckCards(); }

    public Deck(Set<Card> cards) { this.addAll(cards); }

    // ----------------------------------------
    // Functions
    private void setThis(Collection<Card> c) {
        this.clear();
        this.addAll(c);
    }

    private boolean remove(Card card) {
        if(!this.contains(card)) { return false; }
        List<Card> list = new ArrayList<>();
        for(Card c : this) { if(!c.equals(card)) { list.add(c); } }
        this.setThis(list);
        return true;
    }

    private void generateDeckCards() {
        String [] symbols = {"2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K", "A"};
        char [] suits = {'H', 'S', 'D', 'C'};

        for(char c : suits) {
            for(String str : symbols) { this.add(new Card(str, c)); }
        }
    }

    public Card draw() {
        String [] symbols = {"2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K", "A"};
        char [] suits = {'H', 'S', 'D', 'C'};
        Card retValue;
        do {
            retValue = new Card(symbols[(int) (Math.random() * symbols.length)], suits[(int) (Math.random() * suits.length)]);
        } while(!this.contains(retValue));
        this.remove(retValue);
        return retValue;
    }

    public void shuffle() {
        this.clear();
        this.generateDeckCards();
    }

    public boolean contains(Card object) {
        for(Card target : this) {
            if(target.getSymbol().equals(object.getSymbol())&&target.getSuit()==object.getSuit()) return true;
        }
        return false;
    }

    // ----------------------------------------
}
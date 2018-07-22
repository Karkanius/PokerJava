/*
    Paulo Vasconcelos
    paulobvasconcelos@gmail.com
    2018 july
 */

import java.util.Set;
import java.util.HashSet;

public class Deck {

    // ----------------------------------------
    // Atributes
    private Set<Card> cards = new HashSet<>();

    // ----------------------------------------
    // Constructors
    public Deck() { this.generateDeckCards(); }

    public Deck(Set<Card> cards) { this.cards = cards; }

    // ----------------------------------------
    // Functions
    public Set<Card> getCards() { return this.cards; }
    public void setCards(Set<Card> cards) { this.cards = cards; }

    private void generateDeckCards() {
        String [] symbols = {"2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K", "A"};
        char [] suits = {'H', 'S', 'D', 'C'};

        for(char c : suits) {
            for(String str : symbols) { cards.add(new Card(str, c)); }
        }
    }

    public Card draw() {
        String [] symbols = {"2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K", "A"};
        char [] suits = {'H', 'S', 'D', 'C'};
        Card retValue;
        do {
            retValue = new Card(symbols[(int) (Math.random() * symbols.length)], suits[(int) (Math.random() * suits.length)]);
        } while(!cards.contains(retValue));
        cards.remove(retValue);
        return retValue;
    }

    public void shuffle() {
        cards = new HashSet<>();
        this.generateDeckCards();
    }

    // ----------------------------------------
}
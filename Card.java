/*
    Paulo Vasconcelos
    paulobvasconcelos@gmail.com
    2018 july
 */

import java.util.Set;
import java.util.HashSet;
import java.util.Objects;

public class Card {

    // ----------------------------------------
    // Atributes
    private String symbol;
    private char suit;
    /*
    H - Heats
    S - Spades
    D - Diamonds
    C - Clubs
     */

    // ----------------------------------------
    // Constructors
    public Card(String symbol, char suit) {
        if(!validCardArguments(symbol,suit)) { System.err.println("ERROR: Unable to create card."); System.exit(1); }
        this.symbol = symbol;
        this.suit = suit;
    }

    public Card(String str) {
        if(isValidSuit(str.charAt(str.length()-1))) {
            String symbol = str.substring(0, str.length()-1);
            char suit = str.charAt(str.length()-1);
            if (!validCardArguments(symbol, suit)) { System.err.println("ERROR: Unable to create card."); System.exit(1); }
            this.symbol = symbol;
            this.suit = suit;
        }
        else { System.err.println("ERROR: Unable to create card."); System.exit(1); }
    }

    // ----------------------------------------
    // Functions
    public String getSymbol()   { return this.symbol; }
    public char getSuit()       { return this.suit; }
    public void setSymbol(String symbol)    { this.symbol = symbol; }
    public void setSuit(char suit)          { this.suit = suit; }

    public static boolean isValidSymbol(String symbol) {
        return (symbol.equals("2")
                || symbol.equals("3")
                || symbol.equals("4")
                || symbol.equals("5")
                || symbol.equals("6")
                || symbol.equals("7")
                || symbol.equals("8")
                || symbol.equals("9")
                || symbol.equals("10")
                || symbol.equals("J")
                || symbol.equals("Q")
                || symbol.equals("K")
                || symbol.equals("A"));
    }

    public static boolean isValidSuit(char suit) {
        switch (suit) {
            case 'H':   // Hearts
            case 'S':   // Spades
            case 'D':   // Diamonds
            case 'C':   // Clubs
                return true;
            default:
                return false;
        }
    }

    public static boolean validCardArguments(String symbol, char suit) {
        boolean retVal = true;
        if(!isValidSymbol(symbol))  { System.err.println("ERROR: Invalid card symbol - "+symbol); retVal = false; }
        if(!isValidSuit(suit))      { System.err.println("ERROR: Invalid card suit - "+suit); retVal = false; }
        return retVal;
    }

    public static String nextSymbol(String symbol) {
        if(!isValidSymbol(symbol))  { System.err.println("ERROR: Invalid card symbol - "+symbol); System.exit(1); }
        if(symbol.equals("2"))  { return "3"; }
        if(symbol.equals("3"))  { return "4"; }
        if(symbol.equals("4"))  { return "5"; }
        if(symbol.equals("5"))  { return "6"; }
        if(symbol.equals("6"))  { return "7"; }
        if(symbol.equals("7"))  { return "8"; }
        if(symbol.equals("8"))  { return "9"; }
        if(symbol.equals("9"))  { return "10"; }
        if(symbol.equals("10")) { return "J"; }
        if(symbol.equals("J"))  { return "Q"; }
        if(symbol.equals("Q"))  { return "K"; }
        if(symbol.equals("K"))  { return "A"; }
        if(symbol.equals("A"))  { return "2"; }
        System.err.println("ERROR: Unreachable statement reached.");
        System.exit(2);
        assert false;
        return "";
    }

    public static String previousSymbol(String symbol) {
        if(!isValidSymbol(symbol))  { System.err.println("ERROR: Invalid card symbol - "+symbol); System.exit(1); }
        if(symbol.equals("2"))  { return "A"; }
        if(symbol.equals("3"))  { return "2"; }
        if(symbol.equals("4"))  { return "3"; }
        if(symbol.equals("5"))  { return "4"; }
        if(symbol.equals("6"))  { return "5"; }
        if(symbol.equals("7"))  { return "6"; }
        if(symbol.equals("8"))  { return "7"; }
        if(symbol.equals("9"))  { return "8"; }
        if(symbol.equals("10")) { return "9"; }
        if(symbol.equals("J"))  { return "10"; }
        if(symbol.equals("Q"))  { return "J"; }
        if(symbol.equals("K"))  { return "Q"; }
        if(symbol.equals("A"))  { return "K"; }
        System.err.println("ERROR: Unreachable statement reached.");
        System.exit(2);
        assert false;
        return "";
    }

    public static int symbolToInt(String symbol) {
        if(!isValidSymbol(symbol))  { System.err.println("ERROR: Invalid card symbol - "+symbol); System.exit(1); }
        if(symbol.equals("2"))  { return 0; }
        if(symbol.equals("3"))  { return 1; }
        if(symbol.equals("4"))  { return 2; }
        if(symbol.equals("5"))  { return 3; }
        if(symbol.equals("6"))  { return 4; }
        if(symbol.equals("7"))  { return 5; }
        if(symbol.equals("8"))  { return 6; }
        if(symbol.equals("9"))  { return 7; }
        if(symbol.equals("10")) { return 8; }
        if(symbol.equals("J"))  { return 9; }
        if(symbol.equals("Q"))  { return 10; }
        if(symbol.equals("K"))  { return 11; }
        if(symbol.equals("A"))  { return 12; }
        System.err.println("ERROR: Unreachable statement reached.");
        System.exit(2);
        assert false;
        return -1;
    }

    public static String intToSymbol(int i) {
        if(i<0||i>12)  { System.err.println("ERROR: Invalid int to symbol conversion."); System.exit(1); }
        if(i==0)    { return "2"; }
        if(i==1)    { return "3"; }
        if(i==2)    { return "4"; }
        if(i==3)    { return "5"; }
        if(i==4)    { return "6"; }
        if(i==5)    { return "7"; }
        if(i==6)    { return "8"; }
        if(i==7)    { return "9"; }
        if(i==8)    { return "10"; }
        if(i==9)    { return "J"; }
        if(i==10)   { return "Q"; }
        if(i==11)   { return "K"; }
        if(i==12)   { return "A"; }
        System.err.println("ERROR: Unreachable statement reached.");
        System.exit(2);
        assert false;
        return "";
    }

    public static Set<Card> getCardsFromSuit(Set<Card> set, char suit) {
        if(!isValidSuit(suit))      { System.err.println("ERROR: Invalid card suit - "+suit); System.exit(1); }
        Set<Card> retValue = new HashSet<>();
        for(Card c : set) {
            if(c.getSuit()==suit) { retValue.add(c); }
        }
        return retValue;
    }

    @Override
    public boolean equals(Object object) {
        Card card = (Card) object;
        return this.suit==card.getSuit() && this.symbol.equals(card.getSymbol());
    }

    @Override
    public int hashCode() { return Objects.hash(super.hashCode(), symbol, suit); }

    @Override
    public String toString() { return this.symbol+"-"+this.suit; }

    // ----------------------------------------
}
/*
    Paulo Vasconcelos
    paulobvasconcelos@gmail.com
    2018 july
 */

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

    private static boolean isValidSymbol(String symbol) {
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

    private static boolean isValidSuit(char suit) {
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

    private static boolean validCardArguments(String symbol, char suit) {
        boolean retVal = true;
        if(!isValidSymbol(symbol))  { System.err.println("ERROR: Invalid card symbol - "+symbol); retVal = false; }
        if(!isValidSuit(suit))      { System.err.println("ERROR: Invalid card suit - "+suit); retVal = false; }
        return retVal;
    }
    // ----------------------------------------
}
/*
    Paulo Vasconcelos
    paulobvasconcelos@gmail.com
    2018 july
 */

import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import java.util.List;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Set;
import java.util.HashSet;
import java.util.Arrays;
import KarkaniusUtils.READ;

public class Game {

    static Scanner sc = new Scanner(System.in);
    static Table<PokerPlayer> table;
    static Deck cards = new Deck();
    static PokerPlayer first = null;
    static Card[] tableCards = new Card[5];
    static int pot = 0;
    static List<Integer> betToCover = new ArrayList<>();

    public static void main(String[] args) {
        System.out.println("==> POKER GAME <==");
        initTable();
        System.out.println("INITIALIZING POKER GAME");
        for(int i=0; i<5; i++) {
            delay(1000);
            System.out.print(".");
        }
        System.out.println("\n\n");
        while(table.activePlayers().size()>1) {
            handCards();
            // First bets
            handleBets();
            if(roundActivePlayers().size()==1) {
                endRound();
                continue;
            }
            flop();
            // Second bets
            handleBets();
            if(roundActivePlayers().size()==1) {
                endRound();
                continue;
            }
            turn();
            // Third bets
            handleBets();
            if(roundActivePlayers().size()==1) {
                endRound();
                continue;
            }
            river();
            // Fourth bets
            handleBets();
        }
    }

    public static void initTable() {
        List<PokerPlayer> playersList = new LinkedList<>();
        int numberOfPlayers = READ.readInteger("Seats ("+Table.getMinPlayers()+"-"+Table.getMaxPlayers()+"): ");
        while(numberOfPlayers<Table.getMinPlayers()||numberOfPlayers>Table.getMaxPlayers()) {
            System.err.println("WARNING: Invalid number of table seats.\n"+
                               "         Please introduce a number between "+Table.getMinPlayers()+" and "+Table.getMaxPlayers()+".");
            numberOfPlayers = READ.readInteger("Seats ("+Table.getMinPlayers()+"-"+Table.getMaxPlayers()+"): ");
        }
        int numberOfHumans = READ.readInteger("\nHumans (1-"+numberOfPlayers+"): ");
        while(numberOfHumans<1||numberOfHumans>numberOfPlayers) {
            System.err.println("WARNING: Invalid number of human players.\n"+
                               "         Please introduce a number between 1 and "+numberOfPlayers+".");
            numberOfHumans = READ.readInteger("Humans (1-"+numberOfPlayers+"): ");
        }
        System.out.println();
        for(int i=0; i<numberOfHumans; i++) {
            String name;
            boolean invalidName;
            do {
                name = READ.readString("Player "+(i+1)+"\nName: ");
                invalidName = false;
                for(PokerPlayer player : playersList) { if(player.getName().equals(name)) { invalidName = true; } }
            } while(invalidName);
            PokerPlayer aux = new PokerPlayer(name);
            playersList.add(aux);
            if(i==0) { first = aux; }
            System.out.println();
        }
        delay(500);
        for(int i=0; i<numberOfPlayers-numberOfHumans; i++) {
            System.out.println("Generating PokerBot " + (i + 1));
            playersList.add(new PokerPlayer("PokerBot " + (i + 1)));
            delay(1000);
        }
        System.out.println();
        table = new Table(playersList);
        table.printStat();
    }

    private static void delay(int ms) {
        try {
            TimeUnit.MILLISECONDS.sleep(ms);
        } catch (InterruptedException e) {}
    }

    private static Set<PokerPlayer> roundActivePlayers() {
        Set<PokerPlayer> roundActive = new HashSet<>();
        List<PokerPlayer> list = table.getPlayers();
        for(PokerPlayer player : list) {
            if(player!=null) {
                if(!player.getStatus().equals("FOLD")) { roundActive.add(player); }
            }
        }
        return roundActive;
    }

    private static void handCards() {
        for(PokerPlayer p : table.getPlayers()) { p.giveCard(cards.draw()); }    // First card
        for(PokerPlayer p : table.getPlayers()) { p.giveCard(cards.draw()); }    // Second card
    }

    private static void handleBets() {
        List<PokerPlayer> players = new ArrayList<>(table.activePlayers());
        for(int i=0; i<betToCover.size(); i++) { betToCover.set(i, 0); }
        int index = players.indexOf(first);
        // Circle trough players
        do {
            PokerPlayer player = players.get(index);
            if(player.getStatus().equals("FOLD")||player.getStatus().equals("ALLIN")) continue;
            int toCover = betToCover.get(index);
            PokerAction action = player.play(toCover);
            String status = action.getName();
            if(status.equals("ALLIN")) {
                pot += action.getAmount();
                if(toCover<action.getAmount()) {
                    int extra = action.getAmount()-betToCover.get(index);
                    betToCover.set(index, 0);
                    for(int i=0; i<betToCover.size(); i++) { if(i!=index) { betToCover.set(i, betToCover.get(i)+extra); } }
                }
                else { betToCover.set(index, betToCover.get(index)-action.getAmount()); }
            }
            else if(status.equals("RAISE")) {
                pot += action.getAmount();
                int extra = action.getAmount()-betToCover.get(index);
                betToCover.set(index, 0);
                for(int i=0; i<betToCover.size(); i++) { if(i!=index) { betToCover.set(i, betToCover.get(i)+extra); } }
                first = player;
            }
            else if(status.equals("CALL")) { betToCover.set(index, 0); }
            // In case status is FOLD or CHECK
            // nothing changes
            player.setStatus(status);
            // Updating temporary List within function
            players.set(index, player);
            index++;
            index = index%players.size();
        } while(players.get(index)!=first);
        // Player status clear
        for(int i=0; i<players.size(); i++) {
            // Keep status if FOLD or ALLIN
            if((players.get(i).getStatus().equals("FOLD"))||(players.get(i).getStatus().equals("ALLIN"))) continue;
            // Reset
            PokerPlayer aux = players.get(i);
            aux.setStatus("");
            players.set(i,aux);
        }
        // Updating table player List
        table.updatePlayers(players);
    }

    private static void flop() {
        tableCards[0] = cards.draw();
        tableCards[1] = cards.draw();
        tableCards[2] = cards.draw();
    }

    private static void turn() { tableCards[3] = cards.draw(); }

    private static void river() { tableCards[4] = cards.draw(); }

    private static void endRound() {
        handleWinnings(determineWinners());
        betToCover = new ArrayList<>();         // Reset betToCoverList
        table.kickPlayers();                    // Kick players with 0 funds
        resetPlayersStatus();                   // Remove all player status (including FOLD and ALLIN)
    }

    private static Set<PokerPlayer> determineWinners() {
        Set<PokerPlayer> winners = new HashSet<>();
        List<PokerPlayer> players = new ArrayList<>(table.activePlayers());
        Set<Card> bestSet = new HashSet<>();
        for(PokerPlayer player : players) {
            List<Card> available = new ArrayList<>();
            available.addAll(player.getCards());
            available.addAll(Arrays.asList(tableCards));
            // With 7 cards (5+2) there are 21 possible combinatios of 5 cards
            List<Set<Card>> combinations = new ArrayList<Set<Card>>();
            combinations = determineCombinations(available);
            for(Set<Card> hand : combinations) {
                // If hand isn't better than bestSet
                if(bestHand(bestSet, hand)>0) continue;
                // If hand is equivalent to bestSet
                if(bestHand(bestSet, hand)==0) { winners.add(player); continue; }
                // If hand beats bestSet
                if(bestHand(bestSet, hand)<0) {
                    bestSet = hand;
                    winners = new HashSet<>();
                    winners.add(player);
                }
            }
        }
        return winners;
    }

    private static List<Set<Card>> determineCombinations(List<Card> available) {
        List<Set<Card>> retValue = new ArrayList<Set<Card>>();
        for(int i=0; i<available.size()-1; i++) {
            for (int j = i; i < available.size(); j++) {
                Set<Card> auxSet = new HashSet<>();
                for (int k = 0; k < available.size(); k++) {
                    if ((k != i) && (k != j)) { auxSet.add(available.get(k)); }
                }
                retValue.add(auxSet);
            }
        }
        return retValue;
    }

    private static void handleWinnings(Set<PokerPlayer> winners) {
        //...
    }

    private static void resetPlayersStatus() {
        List<PokerPlayer> players = new ArrayList<>(table.activePlayers());
        for(PokerPlayer player : players) { player.setStatus(null); }
        table.updatePlayers(players);
    }

    private static int bestHand(Set<Card> setA, Set<Card> setB) {
        if(setA.equals(null)) return 1;
        if(setB.equals(null)) return -1;
        boolean validA = false;
        boolean validB = false;
        int scoreA = 0;
        int scoreB = 0;
        Card higherA;
        Card higherB;
        // --- PRIORITY AND SCORES ---
        // |  Royal Flush      -> 9  |
        // |  Straight Flush   -> 8  |
        // |  Four of a kind   -> 7  |
        // |  Full House       -> 6  |
        // |  Flush            -> 5  |
        // |  Straight         -> 4  |
        // |  Three of a kind  -> 3  |
        // |  Two pair         -> 2  |
        // |  Pair             -> 1  |
        // |  High card        -> 0  |
        // ---------------------------
        // Royal Flush
        if(isRoyalFlush(setA)) { scoreA=9; validA=true; }
        if(isRoyalFlush(setB)) { scoreB=9; validB=true; }

        // Straight Flush
        if(!validA) { if(isStraightFlush(setA)) { scoreA=8; validA=true; } }
        if(!validB) { if(isStraightFlush(setB)) { scoreB=8; validB=true; } }

        // Four of a Kind
        if(!validA) { if(isFOAK(setA)) { scoreA=7; validA=true; } }
        if(!validB) { if(isFOAK(setB)) { scoreB=7; validB=true; } }

        // Full House
        if(!validA) { if(isFullHouse(setA)) { scoreA=6; validA=true; } }
        if(!validB) { if(isFullHouse(setB)) { scoreB=6; validB=true; } }

        // Flush
        if(!validA) { if(isFlush(setA)) { scoreA=5; validA=true; } }
        if(!validB) { if(isFlush(setB)) { scoreB=5; validB=true; } }

        // Straight
        if(!validA) { if(isStraight(setA)) { scoreA=4; validA=true; } }
        if(!validB) { if(isStraight(setB)) { scoreB=4; validB=true; } }

        // Three of a kind
        if(!validA) { if(isTOAK(setA)) { scoreA=3; validA=true; } }
        if(!validB) { if(isTOAK(setB)) { scoreB=3; validB=true; } }

        // Two pair
        if(!validA) { if(isTwoPair(setA)) { scoreA=2; validA=true; } }
        if(!validB) { if(isTwoPair(setB)) { scoreB=2; validB=true; } }

        // Pair
        if(!validA) { if(isPair(setA)) { scoreA=1; validA=true; } }
        if(!validB) { if(isPair(setB)) { scoreB=1; validB=true; } }

        // Determine higher card
        higherA = determineHigherCard(setA);
        higherB = determineHigherCard(setB);

        if(scoreA!=scoreB) { return scoreA-scoreB; }
        return higherCard(higherA, higherB);
    }

    public static Card determineHigherCard(Set<Card> set) {
        List<Card> cards = new ArrayList<>(set);
        Card higher = cards.get(0);
        cards.remove(0);
        for(Card c : cards) {
            if(higherCard(higher, c)<0) { higher = c; }
        }
        return higher;
    }

    public static int higherCard(Card a, Card b) {
        String symbolA = a.getSymbol();
        String symbolB = b.getSymbol();
        boolean aIsNumber = false;
        boolean bIsNumber = false;
        try {
            Integer.parseInt(symbolA);
            aIsNumber = true;
        } catch (NumberFormatException e) { }
        try {
            Integer.parseInt(symbolB);
            bIsNumber = true;
        } catch (NumberFormatException e) { }
        if(aIsNumber && !bIsNumber) { return -1; }
        if(!aIsNumber && bIsNumber) { return 1; }
        if(aIsNumber && bIsNumber) { return Integer.parseInt(symbolA)-Integer.parseInt(symbolB); }
        // --- A is Ace ---
        if(symbolA=="A"&&symbolB=="A")  { return 0; }   // B is Ace
        if(symbolA=="A")                { return 1; }   // B isn't Ace
        // --- A is King ---
        if(symbolA=="K"&&symbolB=="A")  { return -1; }  // B is Ace
        if(symbolA=="K"&&symbolB=="K")  { return 0; }   // B is King
        if(symbolA=="K")                { return 1; }   // B isn't Ace nor King
        // --- A is Queen ---
        if(symbolA=="Q"&&symbolB=="J")  { return 1; }   // B is Ace
        if(symbolA=="Q"&&symbolB=="Q")  { return 0; }   // B is King
        if(symbolA=="Q")                { return -1; }  // B isn't Ace nor King
        // --- A is Jack ---
        if(symbolA=="J"&&symbolB=="J")  { return 0; }   // B is Jack
        if(symbolA=="J")                { return -1; }  // B isn't Jack
    }

}
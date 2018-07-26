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
import java.util.Map;
import java.util.HashMap;
import java.util.Arrays;
import java.util.Collection;
import java.util.ListIterator;
import KarkaniusUtils.READ;

public class Game {

    static Scanner sc = new Scanner(System.in);
    static final double timeMultiplier = 100;             // % of delay time
    static Table<PokerPlayer> table;
    static Deck deck = new Deck();
    static int firstID;
    static Card[] tableCards = new Card[5];
    static List<Pot> potList = new ArrayList<>();
    static Map<Integer, Integer> betToCover = new HashMap<>();

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
            potList.add(new Pot());
            handCards();
            // First bets
            handleBets();
            if(roundActivePlayers().size()==1) {
                endRound();
                continue;
            }
            delay(1000);
            flop();
            // Second bets
            handleBets();
            if(roundActivePlayers().size()==1) {
                endRound();
                continue;
            }
            delay(1000);
            turn();
            // Third bets
            handleBets();
            if(roundActivePlayers().size()==1) {
                endRound();
                continue;
            }
            delay(1000);
            river();
            // Fourth bets
            handleBets();
            endRound();
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
            betToCover.put(aux.getID(), 0);
            playersList.add(aux);
            if(i==0) { firstID = aux.getID(); }
            System.out.println();
        }
        delay(500);
        for(int i=0; i<numberOfPlayers-numberOfHumans; i++) {
            System.out.println("Generating PokerBot " + (i+1));
            PokerBot_v1 bot = new PokerBot_v1("PokerBot " + (i+1));
            playersList.add(bot);
            betToCover.put(bot.getID(), 0);
            delay(1000);
        }
        System.out.println();
        table = new Table(playersList);
        table.printStat();
    }

    private static List<PokerPlayer> getInGamePlayers() {
        List<PokerPlayer> players = new ArrayList<>(table.activePlayers());
        List<PokerPlayer> inGame = new ArrayList<>();
        for(PokerPlayer player : players) {
            if (player.getStatus().equals("FOLD")) continue;
            inGame.add(player);
        }
        return inGame;
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
        List<PokerPlayer> tempList = new ArrayList<>(table.activePlayers());
        for(PokerPlayer p : tempList) { p.giveCard(deck.draw()); }    // First card
        for(PokerPlayer p : tempList) { p.giveCard(deck.draw()); }    // Second card
        table.updatePlayers(tempList);
    }

    private static void printTableCards() {
        System.out.print("|  ");
        for(int i=0; i<5; i++) {
            if(tableCards[i] == null)   { System.out.print("?-?"); }
            else                        { System.out.print(tableCards[i].toString()); }
            System.out.print("  |  ");
        }
        System.out.println();
    }

    private static void handleBets() {
        printTableCards();
        List<PokerPlayer> players = getInGamePlayers();
        List<Integer> IDs = new ArrayList<>();
        for(PokerPlayer pp : players) { IDs.add(pp.getID()); }
        int index = IDs.indexOf(firstID);
        // Circle trough players
        do {
            PokerPlayer player = players.get(index);
            if(player.getStatus().equals("FOLD")||player.getStatus().equals("ALLIN")) continue;
            int toCover = betToCover.get(player.getID());
            PokerAction action = player.play(toCover);
            String status = action.getName();
            if(player instanceof PokerBot) {
                System.out.println(player.getName());
                System.out.print(status);
                if(status.equals("RAISE")) { System.out.print("    "+action.getAmount()+table.getCurrency()); }
                System.out.println("\n");
            }
            if(status.equals("ALLIN")) {
                player.setFunds(0);
                int remaining = action.getAmount();
                // --- Pots ---
                ListIterator<Pot> potIterator = potList.listIterator();
                while (potIterator.hasNext()) {
                    Pot p = potIterator.next();
                    int presentIndex = potList.indexOf(p);
                    Pot next;
                    try {
                        next = potList.get(presentIndex+1);
                    } catch(IndexOutOfBoundsException e) {
                        next = new Pot();
                    }
                    // Closed pots
                    if(p.isClosed()) {
                        if(p.potCompleted(player)) { continue; }
                        // He can cover what he's missing
                        if(p.missing(player)<=remaining) {
                            remaining -= p.missing(player);
                            p.addFunds(player, p.missing(player));
                        }
                        // Funds are not enough to cover
                        else {
                            p.closePot(player, remaining);
                            Map<Integer, Integer> deposited = p.getDeposited();
                            // Next pot isn't last pot
                            // (next pot is closed)
                            if(next.isClosed()) {
                                Pot newPot = new Pot();
                                Map<Integer, Integer> newMap = new HashMap<>();
                                for (Integer id : deposited.keySet()) {
                                    int val = deposited.get(id)-p.getPlayerAmount();
                                    if(val>0) {
                                        p.withdrawFunds(id, val);
                                        newMap.put(id, val);
                                    }
                                }
                                newPot.setDeposited(newMap);
                                potList.add(presentIndex+1, newPot);
                                break;
                            }
                            // Next pot is last pot
                            // (next pot is open)
                            else {
                                Map<Integer, Integer> update = next.getDeposited();
                                for (Integer id : deposited.keySet()) {
                                    int val = deposited.get(id)-p.getPlayerAmount();
                                    if(val>0) {
                                        p.withdrawFunds(id, val);
                                        try {
                                            update.put(id, update.get(id)+val);
                                        } catch (NullPointerException e) {
                                            update.put(id, val);
                                        }
                                    }
                                }
                                next.setDeposited(update);
                                potList.set(presentIndex+1, next);
                                break;
                            }
                        }
                    }
                    // Last pot (open pot)
                    else {
                        if(remaining==0) { continue; }
                        p.closePot(player, remaining);
                        Pot newPot = new Pot();
                        Map<Integer, Integer> newMap = new HashMap<>();
                        Map<Integer, Integer> deposited = p.getDeposited();
                        for (Integer id : deposited.keySet()) {
                            int val = deposited.get(id)-p.getPlayerAmount();
                            if(val>0) {
                                p.withdrawFunds(id, val);
                                newMap.put(id, val);
                            }
                        }
                        newPot.setDeposited(newMap);
                        potList.add(newPot);
                        break;
                    }
                }
                // --- betToCover ---
                // He can cover what he's missing
                if(toCover<=remaining) {
                    int extra = remaining-betToCover.get(player.getID());
                    betToCover.put(player.getID(), 0);
                    for(int i=0; i<players.size(); i++) {
                        if(i!=index) { betToCover.put(players.get(i).getID(), betToCover.get(players.get(i).getID())+extra); }
                    }
                }
                // Funds are not enough to cover
                else { betToCover.put(player.getID(), betToCover.get(player.getID())-action.getAmount()); }
            }
            else if(status.equals("RAISE")) {
                int remaining = action.getAmount()+betToCover.get(player.getID());
                player.withdrawFunds(remaining);
                // --- Pots ---
                for(Pot p : potList) {
                    // Closed pots
                    if(p.isClosed()&&!p.potCompleted(player)) {
                        // He can cover what he's missing
                        if(p.missing(player)<=remaining) {
                            remaining -= p.missing(player);
                            p.addFunds(player, p.missing(player));
                        }
                        // Funds are not enough to cover
                        else {
                            p.addFunds(player, remaining);
                            break;
                        }
                    }
                    // Last pot (open pot)
                    else { p.addFunds(player, remaining); }
                }
                // --- betToCover ---
                // He can cover what he's missing
                int raised = action.getAmount();
                betToCover.put(player.getID(), 0);
                for(int i=0; i<players.size(); i++) {
                    if(i!=index) { betToCover.put(players.get(i).getID(), betToCover.get(players.get(i).getID())+raised); }
                }
                firstID = player.getID();
            }
            else if(status.equals("CALL")) {
                int remaining = betToCover.get(player.getID());
                // --- Pots ---
                for(Pot p : potList) {
                    // Closed pots
                    if(p.isClosed()&&!p.potCompleted(player)) {
                        // He can cover what he's missing
                        if(p.missing(player)<=remaining) {
                            remaining -= p.missing(player);
                            p.addFunds(player, p.missing(player));
                        }
                        // Funds are not enough to cover
                        else {
                            p.addFunds(player, remaining);
                            break;
                        }
                    }
                    // Last pot (open pot)
                    else { p.addFunds(player, remaining); }
                }
                // --- betToCover ---
                player.withdrawFunds(remaining);
                betToCover.put(player.getID(), 0);
            }
            else if(status.equals("FOLD")) {
                if(player.getID()==firstID) {
                    List<PokerPlayer> tempList = getInGamePlayers();
                    int newIndex = players.indexOf(Player.getPlayersFromID((Collection)tempList, Arrays.asList(player.getID())));
                    firstID = (newIndex+players.size()-1)%players.size();
                }
            }
            // In case status is FOLD or CHECK nothing changes
            player.setStatus(status);
            // Updating temporary List within function
            players.set(index, player);
            index++;
            index = index%players.size();

//            System.out.print("Funds: [");
//            int j=0;
//            for(PokerPlayer pp : players) {
//                System.out.print(pp.getID()+"="+pp.getFunds());
//                j++;
//                if(j!=players.size()) { System.out.print(", "); }
//            }
//            System.out.println("]");
//            System.out.println("betToCover: "+betToCover);

        } while(players.get(index).getID()!=firstID);
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
        System.out.println("\n\n\n============");
        System.out.println("=== FLOP ===");
        System.out.println("============\n");
        tableCards[0] = deck.draw();
        tableCards[1] = deck.draw();
        tableCards[2] = deck.draw();
    }
    private static void turn() {
        System.out.println("\n\n\n============");
        System.out.println("=== TURN ===");
        System.out.println("============\n");
        tableCards[3] = deck.draw();
    }
    private static void river() {
        System.out.println("\n\n\n=============");
        System.out.println("=== RIVER ===");
        System.out.println("=============\n");
        tableCards[4] = deck.draw();
    }

    private static void endRound() {
        for(Pot p : potList) { handleWinnings(p); } // Distribute funds according to pots
        table.kickPlayers();                        // Kick players with 0 funds
        resetPlayersStatus();                       // Remove all player status (including FOLD and ALLIN)
        resetBetToCover();                          // Reset betToCoverList
        potList.clear();                            // Clear List of pots
        tableCards = new Card[5];                   // Clear table cards
        List<PokerPlayer> players = table.getPlayers();
        for(PokerPlayer pp : players) {
            try {
                pp.removeCards();
                System.out.println(pp.stats());
            } catch (NullPointerException e) { }
        }
        table.updatePlayers(players);
    }

    private static void resetBetToCover() {
        betToCover = new HashMap<>();                   // Reset betToCoverList
        for(PokerPlayer player : table.activePlayers()) {
            betToCover.put(player.getID(), 0);          // Re-add players with funds
        }
    }

    private static Set<PokerPlayer> determineWinners(Set<PokerPlayer> set) {
        if(set.size()==1) { return set; }
        System.out.println("Determining winners.");
        Set<PokerPlayer> winners = new HashSet<>();
        List<PokerPlayer> players = new ArrayList<>(set);
        Set<Card> bestSet = new HashSet<>();
        for(PokerPlayer player : players) {
            List<Card> available = new ArrayList<>();
            available.addAll(player.getCards());
            available.addAll(Arrays.asList(tableCards));
            // With 7 cards (5+2) there are 21 possible combinatios of 5 cards
            List<Set<Card>> combinations = new ArrayList<Set<Card>>();
            System.out.println("Determining combinations.");
            combinations = determineCombinations(available);
            System.out.println("Combinations determined.");
            for(Set<Card> hand : combinations) {
                // If hand isn't better than bestSet
                if(bestHand(bestSet, hand)>0) continue;
                // If hand is equivalent to bestSet
                if(bestHand(bestSet, hand)==0) { if(!winners.contains(player)) { winners.add(player); } continue; }
                // If hand beats bestSet
                if(bestHand(bestSet, hand)<0) {
                    bestSet = hand;
                    winners = new HashSet<>();
                    winners.add(player);
                }
            }
        }
        System.out.println("Winners determined.");
        return winners;
    }

    private static List<Set<Card>> determineCombinations(List<Card> available) {
        int combinationsAdded=0;
        List<Set<Card>> retValue = new ArrayList<Set<Card>>();
        for(int i=0; i<available.size()-1; i++) {
            for (int j = i; i < available.size(); j++) {
                Set<Card> auxSet = new HashSet<>();
                for (int k = 0; k < available.size(); k++) {
                    if ((k != i) && (k != j)) { auxSet.add(available.get(k)); }
                }
                combinationsAdded++;
                System.out.println("Combinations calculated: "+combinationsAdded);
                retValue.add(auxSet);
            }
        }
        return retValue;
    }

    private static void handleWinnings(Pot p) {
        if(p.getAmount()==0) { return; }
        List<PokerPlayer> winners = new ArrayList<>(determineWinners(Player.getPlayersFromID((Collection)table.getPlayers(),p.getPlayers())));
        int nWinners = winners.size();
        if(nWinners==0) { System.err.println("ERROR: Round without winners."); System.exit(1); }
        for(PokerPlayer pp : winners) { pp.addFunds((int)(p.getAmount()/winners.size())); }
        table.updatePlayers(winners);
    }

    private static void resetPlayersStatus() {
        List<PokerPlayer> players = new ArrayList<>(table.activePlayers());
        List<PokerPlayer> updated = new ArrayList<>();
        for(PokerPlayer player : players) { if(player.getFunds()>0) { player.resetStatus(); } }
        table.updatePlayers(updated);
    }

    private static int bestHand(Set<Card> setA, Set<Card> setB) {
        System.out.println("\tChecking best hand from "+setA+" and "+setB);
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
        if(isRoyalFlush(setA)) { return 1; }
        if(isRoyalFlush(setB)) { return -1; }

        // Straight Flush
        if(!validA) { if(isStraightFlush(setA)) { scoreA=8; validA=true; } }
        if(!validB) { if(isStraightFlush(setB)) { scoreB=8; validB=true; } }
        if(scoreA==8&&scoreB==8) { return Card.symbolToInt(StraightHighest(Card.getCardsFromSuit(setA,FlushSuit(setA))))
                -Card.symbolToInt(StraightHighest(Card.getCardsFromSuit(setB,FlushSuit(setB)))); }

        // Four of a Kind
        if(!validA) { if(isFOAK(setA)) { scoreA=7; validA=true; } }
        if(!validB) { if(isFOAK(setB)) { scoreB=7; validB=true; } }
        if(scoreA==7&&scoreB==7) {
            String symb = FOAKSymbol(setA);
            int retValue = Card.symbolToInt(symb)-Card.symbolToInt(FOAKSymbol(setB));
            if(retValue!=0) { return  retValue; }
            // Kicker
            for(Card c : setA) {
                if(c.getSymbol()==symb) { setA.remove(c); }
            }
            for(Card c : setB) {
                if(c.getSymbol()==symb) { setB.remove(c); }
            }
            return higherCard(determineHigherCard(setA), determineHigherCard(setB));
        }

        // Full House
        if(!validA) { if(isFullHouse(setA)) { scoreA=6; validA=true; } }
        if(!validB) { if(isFullHouse(setB)) { scoreB=6; validB=true; } }
        if(scoreA==6&&scoreB==6) {
            // Trio
            int retValue = Card.symbolToInt(TrioSymbol(setA))-Card.symbolToInt(TrioSymbol(setB));
            if(retValue!=0) { return retValue; }
            // (Highest) Pair
            return Card.symbolToInt(HighestPair(setA))-Card.symbolToInt(HighestPair(setB));
        }

        // Flush
        if(!validA) { if(isFlush(setA)) { scoreA=5; validA=true; } }
        if(!validB) { if(isFlush(setB)) { scoreB=5; validB=true; } }
        if(scoreA==5&&scoreB==5) { return Card.symbolToInt(FlushHighest(setA))-Card.symbolToInt(FlushHighest(setB)); }

        // Straight
        if(!validA) { if(isStraight(setA)) { scoreA=4; validA=true; } }
        if(!validB) { if(isStraight(setB)) { scoreB=4; validB=true; } }
        if(scoreA==4&&scoreB==4) {
            int retValue = Card.symbolToInt(StraightHighest(setA))-Card.symbolToInt(StraightHighest(setB));
            if(retValue!=0) { return retValue; }
            // Kicker
            setA = removeStraight(setA);
            setB = removeStraight(setB);
            return higherCard(determineHigherCard(setA), determineHigherCard(setB));
        }

        // Three of a kind
        if(!validA) { if(isTOAK(setA)) { scoreA=3; validA=true; } }
        if(!validB) { if(isTOAK(setB)) { scoreB=3; validB=true; } }
        if(scoreA==3&&scoreB==3) {
            int retValue = Card.symbolToInt(TrioSymbol(setA))-Card.symbolToInt(TrioSymbol(setB));
            if(retValue!=0) { return retValue; }
            // Kicker
            setA = removeTrio(setA);
            setB = removeTrio(setB);
            return higherCard(determineHigherCard(setA), determineHigherCard(setB));
        }

        // Two pair
        if(!validA) { if(isTwoPair(setA)) { scoreA=2; validA=true; } }
        if(!validB) { if(isTwoPair(setB)) { scoreB=2; validB=true; } }
        if(scoreA==2&&scoreB==2) {
            // Highest pair
            int retValue = Card.symbolToInt(HighestPair(setA))-Card.symbolToInt(HighestPair(setB));
            if(retValue!=0) { return retValue; }
            // Second highest pair
            retValue = Card.symbolToInt(HighestPair(setA))-Card.symbolToInt(HighestPair(setB));
            if(retValue!=0) { return retValue; }
            // Kicker
            setA = removeHighestPairs(setA);
            setB = removeHighestPairs(setB);
            return higherCard(determineHigherCard(setA), determineHigherCard(setB));
        }

        // Pair
        if(!validA) { if(isPair(setA)) { scoreA=1; validA=true; } }
        if(!validB) { if(isPair(setB)) { scoreB=1; validB=true; } }
        if(scoreA==1&&scoreB==1) {
            int retValue = Card.symbolToInt(HighestPair(setA))-Card.symbolToInt(HighestPair(setB));
            if(retValue!=0) { return retValue; }
            // Kicker
            setA = removeHighestPair(setA);
            setB = removeHighestPair(setB);
            return higherCard(determineHigherCard(setA), determineHigherCard(setB));
        }

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
        System.err.println("ERROR: Unreachable statement reached.");
        System.exit(2);
        assert false;
        return 0;
    }

    private static boolean setContainsSymbol(Set<Card> set, String symbol) {
        for(Card c : set) { if(c.getSymbol().equals(symbol)) { return true; } }
        return false;
    }

    private static boolean isRoyalFlush(Set<Card> set) {
        char suit = 'i';
        int[] suitAmount = {0,0,0,0};
        char[] suits = {'H','S','D','C'};
        // --- Flush verification ---
        // Amount of cards
        //  of each suit
        for(Card c : set) {
            char cSuit = c.getSuit();
            if(!Card.isValidSuit(cSuit)) { System.err.println("ERROR: Invalid card suit - "+suit); System.exit(1); }
            for(int i=0; i<4; i++) { if(cSuit==suits[i]) { suitAmount[i]++; break; } }
        }
        // Determine dominant suit
        for(int i=0; i<4; i++) { if(suitAmount[i]>=5) { suit=suits[i]; break; } }
        if(suit=='i') { return false; } // No suit with 5+ cards
        // --- Royal Flush verification ---
        return ((set.contains(new Card("A",suit)))&&
                (set.contains(new Card("K",suit)))&&
                (set.contains(new Card("Q",suit)))&&
                (set.contains(new Card("J",suit)))&&
                (set.contains(new Card("10",suit))));
    }

    private static boolean isStraightFlush(Set<Card> set) {
        char suit = 'i';
        int[] suitAmount = {0,0,0,0};
        char[] suits = {'H','S','D','C'};
        Set<Card> cardsOfDominantSuit = new HashSet<>();
        // --- Flush verification ---
        // Amount of cards
        //  of each suit
        for(Card c : set) {
            char cSuit = c.getSuit();
            if(!Card.isValidSuit(cSuit)) { System.err.println("ERROR: Invalid card suit - "+suit); System.exit(1); }
            for(int i=0; i<4; i++) { if(cSuit==suits[i]) { suitAmount[i]++; break; } }
        }
        // Determine dominant suit
        for(int i=0; i<4; i++) { if(suitAmount[i]>=5) { suit=suits[i]; break; } }
        if(suit=='i') { return false; } // No suit with 5+ cards
        // --- Straight Flush verification ---
        // Determine higher card
        //   of dominant suit
        for(Card c : set) { if(c.getSuit()==suit) { cardsOfDominantSuit.add(c); } }
        for(int i=0; i<cardsOfDominantSuit.size()-4; i++) {
            Card higher = determineHigherCard(cardsOfDominantSuit);
            String higherSymbol = higher.getSymbol();
            if((cardsOfDominantSuit.contains(new Card(Card.previousSymbol(higherSymbol),suit)))&&
                (cardsOfDominantSuit.contains(new Card(Card.previousSymbol(Card.previousSymbol(higherSymbol)),suit)))&&
                (cardsOfDominantSuit.contains(new Card(Card.previousSymbol(Card.previousSymbol(Card.previousSymbol(higherSymbol))),suit)))&&
                (cardsOfDominantSuit.contains(new Card(Card.previousSymbol(Card.previousSymbol(Card.previousSymbol(Card.previousSymbol(higherSymbol)))),suit)))) {
                return true;
            }
            cardsOfDominantSuit.remove(higher);
        }
        return false;
    }

    private static boolean isFOAK(Set<Card> set) {
        int[] symbolAmount = {0,0,0,0,0,0,0,0,0,0,0,0,0};
        for(Card c : set) { symbolAmount[Card.symbolToInt(c.getSymbol())]++; }
        for(int i=symbolAmount.length-1; i>=0; i--) {
            if(symbolAmount[i]==4) { return true; }
        }
        return false;
    }

    private static String FOAKSymbol(Set<Card> set) {
        int[] symbolAmount = {0,0,0,0,0,0,0,0,0,0,0,0,0};
        for(Card c : set) { symbolAmount[Card.symbolToInt(c.getSymbol())]++; }
        for(int i=symbolAmount.length-1; i>=0; i--) {
            if(symbolAmount[i]==4) { return Card.intToSymbol(i); }
        }
        return "";
    }

    private static boolean isFullHouse(Set<Card> set) {
        int[] symbolAmount = {0,0,0,0,0,0,0,0,0,0,0,0,0};
        for(Card c : set) { symbolAmount[Card.symbolToInt(c.getSymbol())]++; }
        boolean trio = false;
        boolean pair = true;
        for(int i=symbolAmount.length-1; i>=0; i--) {
            if(symbolAmount[i]==3) { trio = true; continue; }
            if(symbolAmount[i]==2) { pair = true; }
        }
        return trio&&pair;
    }

    private static String TrioSymbol(Set<Card> set) {
        int[] symbolAmount = {0,0,0,0,0,0,0,0,0,0,0,0,0};
        for(Card c : set) { symbolAmount[Card.symbolToInt(c.getSymbol())]++; }
        for(int i=symbolAmount.length-1; i>=0; i--) {
            if(symbolAmount[i]==3) { return Card.intToSymbol(i); }
        }
        return "";
    }

    private static boolean isFlush(Set<Card> set) {
        int[] suitAmount = {0,0,0,0};
        char[] suits = {'H','S','D','C'};
        // --- Flush verification ---
        // Amount of cards
        //  of each suit
        for(Card c : set) {
            char cSuit = c.getSuit();
            if(!Card.isValidSuit(cSuit)) { System.err.println("ERROR: Invalid card suit - "+cSuit); System.exit(1); }
            for(int i=0; i<4; i++) { if(cSuit==suits[i]) { suitAmount[i]++; break; } }
        }
        // Determine dominant suit
        for(int i=0; i<4; i++) { if(suitAmount[i]>=5) { return true; } }
        return false; // No suit with 5+ cards
    }

    private static String FlushHighest(Set<Card> set) {
        char suit = 'i';
        int[] suitAmount = {0,0,0,0};
        char[] suits = {'H','S','D','C'};
        Set<Card> cardsOfDominantSuit = new HashSet<>();
        // --- Flush verification ---
        // Amount of cards
        //  of each suit
        for(Card c : set) {
            char cSuit = c.getSuit();
            if(!Card.isValidSuit(cSuit)) { System.err.println("ERROR: Invalid card suit - "+suit); System.exit(1); }
            for(int i=0; i<4; i++) { if(cSuit==suits[i]) { suitAmount[i]++; break; } }
        }
        // Determine dominant suit
        for(int i=0; i<4; i++) { if(suitAmount[i]>=5) { suit=suits[i]; break; } }
        if(suit=='i') { return ""; } // No suit with 5+ cards
        for(Card c : set) {
            char cSuit = c.getSuit();
            if(cSuit==suit) { cardsOfDominantSuit.add(c); }
        }
        return(determineHigherCard(cardsOfDominantSuit).getSymbol());
    }

    private static char FlushSuit(Set<Card> set) {
        char suit = 'i';
        int[] suitAmount = {0,0,0,0};
        char[] suits = {'H','S','D','C'};
        Set<Card> cardsOfDominantSuit = new HashSet<>();
        // --- Flush verification ---
        // Amount of cards
        //  of each suit
        for(Card c : set) {
            char cSuit = c.getSuit();
            if(!Card.isValidSuit(cSuit)) { System.err.println("ERROR: Invalid card suit - "+suit); System.exit(1); }
            for(int i=0; i<4; i++) { if(cSuit==suits[i]) { suitAmount[i]++; break; } }
        }
        // Determine dominant suit
        for(int i=0; i<4; i++) { if(suitAmount[i]>=5) { return suits[i]; } }
        return 'i';
    }

    private static boolean isStraight(Set<Card> set) {
        char[] suits = {'H','S','D','C'};
        for(int i=0; i<set.size()-4; i++) {
            Card higher = determineHigherCard(set);
            String higherSymbol = higher.getSymbol();
            for(char suit : suits) {
                if ((set.contains(new Card(Card.previousSymbol(higherSymbol), suit))) &&
                    (set.contains(new Card(Card.previousSymbol(Card.previousSymbol(higherSymbol)), suit))) &&
                    (set.contains(new Card(Card.previousSymbol(Card.previousSymbol(Card.previousSymbol(higherSymbol))), suit))) &&
                    (set.contains(new Card(Card.previousSymbol(Card.previousSymbol(Card.previousSymbol(Card.previousSymbol(higherSymbol)))), suit)))) {
                    return true;
                }
            }
            set.remove(higher);
        }
        return false;
    }

    private static String StraightHighest(Set<Card> set) {
        char[] suits = {'H','S','D','C'};
        for(int i=0; i<set.size()-4; i++) {
            Card higher = determineHigherCard(set);
            String higherSymbol = higher.getSymbol();
            for(char suit : suits) {
                if ((set.contains(new Card(Card.previousSymbol(higherSymbol), suit))) &&
                    (set.contains(new Card(Card.previousSymbol(Card.previousSymbol(higherSymbol)), suit))) &&
                    (set.contains(new Card(Card.previousSymbol(Card.previousSymbol(Card.previousSymbol(higherSymbol))), suit))) &&
                    (set.contains(new Card(Card.previousSymbol(Card.previousSymbol(Card.previousSymbol(Card.previousSymbol(higherSymbol)))), suit)))) {
                    return higherSymbol;
                }
            }
            set.remove(higher);
        }
        return "";
    }

    private static Set<Card> removeStraight(Set<Card> set) {
        String symbol = StraightHighest(set);
        for(int i=0; i<5; i++) {
            for (Card c : set) {
                if (c.getSymbol().equals(symbol)) {
                    set.remove(c);
                    break;
                }
            }
            symbol = Card.previousSymbol(symbol);
        }
        return set;
    }

    private static boolean isTOAK(Set<Card> set) {
        int[] symbolAmount = {0,0,0,0,0,0,0,0,0,0,0,0,0};
        for(Card c : set) { symbolAmount[Card.symbolToInt(c.getSymbol())]++; }
        for(int i=symbolAmount.length-1; i>=0; i--) {
            if(symbolAmount[i]==3) { return true; }
        }
        return false;
    }

    private static Set<Card> removeTrio(Set<Card> set) {
        String symbol = TrioSymbol(set);
        for (Card c : set) {
            if (c.getSymbol().equals(symbol)) { set.remove(c); }
        }
        return set;
    }

    private static boolean isTwoPair(Set<Card> set) {
        int[] symbolAmount = {0,0,0,0,0,0,0,0,0,0,0,0,0};
        for(Card c : set) { symbolAmount[Card.symbolToInt(c.getSymbol())]++; }
        int nPairs = 0;
        for(int i=symbolAmount.length-1; i>=0; i--) {
            if(symbolAmount[i]==2) { nPairs++; }
        }
        return nPairs>=2;
    }

    private static String HighestPair(Set<Card> set) {
        int[] symbolAmount = {0,0,0,0,0,0,0,0,0,0,0,0,0};
        for(Card c : set) { symbolAmount[Card.symbolToInt(c.getSymbol())]++; }
        for(int i=symbolAmount.length-1; i>=0; i--) {
            if(symbolAmount[i]==2) { return Card.intToSymbol(i); }
        }
        return "";
    }

    private static String SecondHighestPair(Set<Card> set) {
        int[] symbolAmount = {0,0,0,0,0,0,0,0,0,0,0,0,0};
        for(Card c : set) { symbolAmount[Card.symbolToInt(c.getSymbol())]++; }
        int pairsAnalized = 0;
        for(int i=symbolAmount.length-1; i>=0; i--) {
            if(symbolAmount[i]==2) {
                pairsAnalized++;
                if(pairsAnalized==2) { return Card.intToSymbol(i); }
            }
        }
        return "";
    }

    private static Set<Card> removeHighestPairs(Set<Card> set) {
        String symbol_1 = HighestPair(set);
        String symbol_2 = SecondHighestPair(set);
        for (Card c : set) {
            if (c.getSymbol().equals(symbol_1)||c.getSymbol().equals(symbol_2)) { set.remove(c); }
        }
        return set;
    }

    private static Set<Card> removeHighestPair(Set<Card> set) {
        String symbol = HighestPair(set);
        for (Card c : set) {
            if (c.getSymbol().equals(symbol)) { set.remove(c); }
        }
        return set;
    }

    private static boolean isPair(Set<Card> set) {
        int[] symbolAmount = {0,0,0,0,0,0,0,0,0,0,0,0,0};
        for(Card c : set) { symbolAmount[Card.symbolToInt(c.getSymbol())]++; }
        for(int i=symbolAmount.length-1; i>=0; i--) {
            if(symbolAmount[i]==2) { return true; }
        }
        return false;
    }

    private static void delay(int ms) {
        try {
            TimeUnit.NANOSECONDS.sleep(Math.round(ms*10000*timeMultiplier));
        } catch (InterruptedException e) {}
    }

}
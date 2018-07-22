/*
    Paulo Vasconcelos
    paulobvasconcelos@gmail.com
    2018 july
 */

import java.util.LinkedList;
import java.util.Set;
import java.util.HashSet;

public class testFile {

    public static void main(String[] args) {

        Set<Card> deck = new HashSet<>();
        deck.add(new Card("10",'H'));
        System.out.println(deck);
        System.out.println(deck.contains(new Card("10",'H')));

    }

}
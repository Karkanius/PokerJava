/*
    Paulo Vasconcelos
    paulobvasconcelos@gmail.com
    2018 july
 */

import java.util.LinkedList;

public class testFile {

    public static void main(String[] args) {

        LinkedList<Integer> list = new LinkedList<>();
        list.add(1);
        list.add(2);
        list.add(3);
        list.add(4);
        System.out.println(list+"\n"+list.size()+"\n");

        for(int j=0; j<list.size(); j++) {
            if(list.get(j).equals(2)) { list.set(j, null); }
        }
        System.out.println(list+"\n"+list.size()+"\n");

    }

}
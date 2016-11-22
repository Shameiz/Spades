import java.util.*;

class Deck
{
    private ArrayList<Card> deck = new ArrayList<Card>(); //deck is card array

    public Deck(){
        Card card;
        for (int x = 0 ; x < 52 ; x++){
            card = new Card(x);
            deck.add(card); //add 52 cards to deck
        }
    }

    /* deals 13 cards */ 
    public Card[] deal(){
        int num;
        Card[] deal = new Card[13]; //deals 13 cards

        for (int x = 0; x < 13; x++){
            num = (int)(Math.random()*deck.size()); //get random cards
            deal[x] = deck.get(num);
            deck.remove(num);
        }

        return deal;
    }
}
import java.awt.*;
import java.util.*;

class Hand
{
    private ArrayList<Card> hand = new ArrayList<Card>(); //hand is array list of cards
    private int[] suitlength = {0,0,0,0}; //length of 4 suits
    private int bid, tricks = 0; //bid, number of tricks taken

    public Hand(Card[] cards){
        for (int x = 0; x < cards.length; x++){
            hand.add(cards[x]); //add card to hand
            suitlength[cards[x].getsuit()]++; //add suit to suit length
        }
    }
    
    public int getsize (){
        return hand.size();
    }

    /* display hand */ 
    public void showhand (Graphics g, int w, int h, int side){
        for (int x = 0; x < hand.size(); x++){
            if (side != 0)
                hand.get(x).setFace(false); //set face down if not user's hand
            hand.get(x).show (g, w, h, side); //display card
            if (side%2 == 0)
                w += 25;
            else 
                h+= 25;
        }
    }

    /* selection sort for organizing hand */ 
    public void sort(){
        Card temp;
        for (int x = 0 ; x < hand.size() - 1 ; x++){
            int lowPos = x;
            for (int y = x + 1 ; y < hand.size() ; y++){
                if (hand.get(y).getsuit()*13+hand.get(y).getrank() < hand.get(lowPos).getsuit()*13+hand.get(lowPos).getrank())
                    lowPos = y;
            }
            temp = hand.get(x);
            hand.set(x,hand.get(lowPos));
            hand.set(lowPos,temp);
        }
    }

    public void setbid(int yourbid){
        bid = yourbid;
    }

    /* bidding AI
     * uses the points system from this site: http://www.sharkfeeder.com/spadsbook/basicbidding.html */
    public int bid(){
        double points = 0; //for counting the points in the hand
        int cardsuit, cardrank; //for each individual card

        boolean[][] facecards = new boolean[4][4]; //specific face cards[suit][rank]
        for (int x = 0; x < 13; x++){
            if (hand.get(x).getrank() < 4){ //if face card
                facecards[hand.get(x).getsuit()][hand.get(x).getrank()] = true; //has that face card
            }
        }

        //points for individual cards
        for (int x = 0; x < 13; x++){
            cardsuit = hand.get(x).getsuit();
            cardrank = hand.get(x).getrank();

            //trump cards (spades)
            if (cardsuit == 0){
                if (cardrank == 0) points++; //ace is always +1 point
                else if (cardrank == 1){ //king
                    if (facecards[0][0] || suitlength[0] > 2) points++; //has ace or is well protected: +1
                    else if (suitlength[0] > 1) points += 0.75; //mostly protected: +0.75
                }
                else if (cardrank == 2){ //queen
                    if ((facecards[0][0]&&facecards[0][1]) || suitlength[0] > 3) points++; //well protected
                    else if (suitlength[0] > 2) points += 0.75; //mostly protected
                    else if (suitlength[0] > 1) points += 0.25; //partly protected
                }
                else if (cardrank == 3){ //jack
                    if ((facecards[0][0]&&facecards[0][1]&&facecards[0][2]) || suitlength[0] > 4) points++; //well protected
                    else if (suitlength[0] > 3) points += 0.75; //mostly protected
                    else if (suitlength[0] > 2) points += 0.25; //partly protected
                }
            }
            //non trump cards
            else{
                if (cardrank == 0){ //ace
                    if (suitlength[cardsuit] < 7) points++; //suit less than 7 cards long: +1
                    else if (suitlength[cardsuit] < 9) points += 0.5; //suit 8-9 cards long: +0.5
                }
                else if (cardrank == 1 && suitlength[cardsuit] < 6){ //king
                    if (suitlength[cardsuit] == 1 || suitlength[cardsuit] == 5) points += 0.25; //suit 1 or 5 cards long: +0.25
                    else points += 0.5; //suit 2-4 cards long: +0.5
                    if (facecards[cardsuit][0]||facecards[cardsuit][2]||facecards[cardsuit][3]) points += 0.25; //has another face card: +0.25
                }
                else if (cardrank == 2 && suitlength[cardsuit] <= 5){ //queen
                    points += 0.25; //suit 5 cards or shorter: +0.25
                    if (facecards[cardsuit][0] && facecards[cardsuit][1]) points += 0.25; //has AK: +0.25 more
                }
            }
        }
        // points for trump length
        if (suitlength[0] == 6) points += 0.5; //6 cards long: +0.5
        if (suitlength[0] > 6) points += suitlength[0]-6; //+1 for each trump over 6

        bid = (int)(Math.round(points)); //bid will be rounded points
        return bid;
    }
    
    public int getbid(){
        return bid;
    }

    /* user's input play when leading */ 
    public Card play(int index){
        Card play = hand.get(index); //get card from hand
        hand.remove(index); //remove card from hand
        suitlength[play.getsuit()]--; //decrement suit length
        return play;
    }
    
    /* user's input play when not leading */ 
    public Card play(int index, Card led){
        Card play = hand.get(index); //get card from hand
        if (play.getsuit() != led.getsuit() && suitlength[led.getsuit()] != 0) return new Card (-1); //if did not follow suit but can
        else{
            hand.remove(index); //remove card from hand
            suitlength[play.getsuit()]--; //decrement suit length
            return play;
        }
    }

    /* playing AI when leading: plays highest card of longest suit*/ 
    public Card play(){
        int posn = 0, longsuit = 0;
        for (int x = 1; x < 4; x++){
            if (suitlength[x] > suitlength[longsuit]) longsuit = x; //find longest suit
        }
        while (hand.get(posn).getsuit() != longsuit) posn++;
        Card play = hand.get(posn);
        hand.remove(posn);
        suitlength[play.getsuit()]--;
        return play;
    }

    /* playing AI when not leading */ 
    public Card play(ArrayList<Card> p){
        int leadingsuit = p.get(0).getsuit(); //suit of lead card
        int posn;

        Card highest = p.get(0); //find highest card that has already been played
        for (int x = 1; x < p.size(); x++){
            if (p.get(x).beats(highest)) highest = p.get(x);
        }

        if (suitlength[leadingsuit] > 0){ //can follow suit: play highest card in suit if it can beat highest played card, otherwise play lowest card in suit
            posn = 0;
            while (hand.get(posn).getsuit() != leadingsuit) posn++;
            if (!hand.get(posn).beats(highest)){
                posn = hand.size()-1;
                while (hand.get(posn).getsuit() != leadingsuit) posn--;
            }
        }
        else{ //cannot follow suit: play lowest card of longest suit
            int longsuit = 0;
            for (int x = 1; x < 4; x++){
                if (suitlength[x] > suitlength[longsuit]) longsuit = x;
            }
            posn = hand.size()-1;
            while (hand.get(posn).getsuit() != longsuit) posn--;
        }
        Card play = hand.get(posn);
        hand.remove(posn);
        suitlength[play.getsuit()]--;
        return play;
    }

    public void addtrick(){
        tricks++;
    }

    public int gettricks(){
        return tricks;
    }

    /* calculates and returns points */ 
    public int points(){
        if (tricks < bid) return -10*bid;
        else return bid*10+tricks-bid;
    }
}
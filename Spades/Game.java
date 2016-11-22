import java.awt.*;
import javax.swing.*;
import java.util.*;
import java.awt.event.*;

class Game
{
    /* declare variables */
    private Card notplayed = new Card(-1); //filler card for hands that have not played
    private Card[] played = new Card[4]; //array of played cards

    private Hand[] hand = new Hand[4]; //hands for 4 players
    private int[] points = new int[4]; //points for each player
    private String[] cardinal = {"South","West","North","East"}; //cardinal names of hands

    private int dealer, lead, index, choice = 0; //dealer position, first player position, index for user play, choice after each round
    private boolean prompt = false, clicked; //prompt: if to show input prompt, clicked: if user has inputted

    static Deck deck; //deck of cards
    static GUI window; //GUI

    public Game(){
        reset(); //initialize values
        window = new GUI(this); //create GUI
    }

    /* display */ 
    public void display(Graphics g){
        g.drawString("South",166,410); //display hand names
        g.drawString("West",40,62);
        g.drawString("North",166,10);
        g.drawString("East",570,62);

        if (prompt) g.drawString(": Please select a card to play",200,410);

        int w, h;
        for (int x = 0; x < 4; x++) {
            if (x==0){ //dimensions for South
                w = 166; h = 420;
            }
            else if (x==1){ //West
                w = 40; h = 60;
            }
            else if (x==2){ //North
                w = 154; h = 20;
            }
            else{ //East
                w = 568; h = 60;
            }
            hand[x].showhand(g, w, h, x); //display hands
        }

        for (int y = 0; y < 4; y++){
            if (played[y].getnum() != 0){ //if the hand has played
                if (y==0){ //South
                    w = 310; h = 260;
                }
                else if (y==1){ //West
                    w = 193; h = 200;
                }
                else if (y==2){ //North
                    w = 310; h = 150;
                }
                else { //East
                    w = 427; h = 200;
                }
                played[y].setFace(true); //turn card face up
                played[y].show(g, w, h, 0); //display cards in center
            }
        }
    }

    /* gives the position of the next player */ 
    public int nextplayer(int player){
        if (player == 3) return 0;
        else return player+1;
    }

    /* deals and sorts 4 hands */ 
    public void deal(){
        deck = new Deck();
        for (int x = 0; x < 4; x++) {
            hand[x] = new Hand(deck.deal());
            hand[x].sort();
        }
        window.repaint();
    }

    /* bidding */ 
    public void bidding(){
        int bidder = nextplayer(dealer); //player clockwise from dealer starts auction
        int bid;
        JOptionPane.showMessageDialog(null, ("The dealer is " + cardinal[dealer]), "", JOptionPane.PLAIN_MESSAGE); //show dealer

        for (int x = 0; x < 4; x++){            
            if (bidder == 0){ //user bid
                bid = -1;
                do{
                    String inputValue = JOptionPane.showInputDialog(null, "Enter your bid (0-13)", "", JOptionPane.PLAIN_MESSAGE);
                    try{bid = Integer.parseInt (inputValue);} catch(NumberFormatException ne){};
                } while (bid < 0 || bid > 13); //bid must be 0-13
                hand[0].setbid(bid);
            }
            else  {
                bid = hand[bidder].bid(); //use bidding AI
                JOptionPane.showMessageDialog(null, (cardinal[bidder] + " bids " + bid), "", JOptionPane.PLAIN_MESSAGE);
                hand[bidder].setbid(bid);
            }
            bidder = nextplayer(bidder); //go to next player
        }
    }

    public void setclicked (boolean c){
        clicked = c;
    }

    /* actual gameplay */ 
    public void gameplay() throws InterruptedException{
        ArrayList<Card> p; //array list of played cards so far
        int player, winner;

        for (int x = 1; x <= 13; x++){
            p = new ArrayList<Card>(); //reset p
            for (int a = 0; a < 4; a++) played[a] = notplayed; //reset played
            player = lead; //lead starts
            window.repaint();

            for (int y = 0; y < 4; y++){
                if (player == 0){
                    clicked = false; //reset clicked
                    prompt = true; //set prompt
                    window.repaint();
                    do{
                        if (clicked){
                            index = (window.getx()-166)/25; //find index
                            if (index >= hand[0].getsize()) index = hand[0].getsize()-1;

                            if (y == 0) played[0] = hand[0].play(index); //play card at index
                            else played[0] = hand[0].play(index,p.get(0)); //if not leading, check for following suit
                            
                            if (played[0].getnum() == 0){ //if did not follow suit but can
                                JOptionPane.showMessageDialog(null, "You must follow suit", "", JOptionPane.ERROR_MESSAGE);
                                clicked = false;
                            }
                        }
                    } while (!clicked); //wait for valid input
                    prompt = false; //remove prompt
                }
                else if (y == 0) played[player] = hand[player].play(); //use playing AI for leading
                else played[player] = hand[player].play(p); //use playing AI for not leading

                p.add(played[player]); //add to arraylist of played cards
                player = nextplayer(player); //move to next player
                window.repaint();
                Thread.sleep(1000);
            }

            Card highest = played[player];
            winner = player;
            for (int z = 0; z < 3; z++){
                player = nextplayer(player);
                if (played[player].beats(highest)){
                    highest = played[player];
                    winner = player;  //find winner by comparing cards
                }
            }

            JOptionPane.showMessageDialog(null, "The winner is " + cardinal[winner], "", JOptionPane.PLAIN_MESSAGE); //display winner
            hand[winner].addtrick(); //winner gets a trick
            lead = winner; //winner leads next round
        }

    }

    /* display results */ 
    public void showscore(){
        JPanel t = new JPanel(); //create display panel, set layout
        t.setLayout(new GridLayout(6,5));

        String[] headings = {"Player","Bid","Made","Points","Total"}; //add headings and scores to display panel
        for (int x = 0; x < 5; x++){
            t.add(new JLabel(headings[x]));
        }
        for (int x = 0; x < 5; x++) t.add(new JLabel("-------------"));
        for (int x = 0; x < 4; x++){
            points[x] += hand[x].points();
            t.add(new JLabel(cardinal[x]));
            t.add(new JLabel(""+hand[x].getbid()));
            t.add(new JLabel(""+hand[x].gettricks()));
            t.add(new JLabel(""+hand[x].points()));
            t.add(new JLabel(""+points[x]));
        }

        //show dialog box with score table, get choice
        Object[] options = {"Continue", "New Game", "Quit"};
        choice = JOptionPane.showOptionDialog(null, t, "Round results", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);
        if (choice == 0) keepgoing();
        else if (choice == 1) reset();
    }

    /* continue playing */ 
    public void keepgoing(){
        for (int x = 0; x < 4; x++) played[x] = notplayed;
        dealer = nextplayer(dealer); //dealer rotates by 1
        lead = nextplayer(dealer);
    }

    /* start over */ 
    public void reset(){
        for (int x = 0; x < 4; x++){
            points[x] = 0; //set points to 0
            played[x] = notplayed; //reset played cards
        }
        dealer = (int)(Math.random()*4); //random dealer
        lead = nextplayer(dealer); //lead is next player
    }

    public static void main (String[] args)
    {
        Game h = new Game (); //create game
        window.setVisible (true); //set window properties
        window.setResizable(false);

        do{
            //go through sections of gameplay
            h.deal();
            h.bidding();
            try{h.gameplay();} catch(InterruptedException ie){}
            h.showscore();
        } while (h.choice != 2);
        window.dispatchEvent(new WindowEvent(window, WindowEvent.WINDOW_CLOSING)); //close window when user chooses quit
    }
}
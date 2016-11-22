import java.awt.*;
import javax.imageio.*;
import java.io.*;
import java.awt.image.BufferedImage;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;

class Card
{
    private int rank; //rank 1 = A to 13 = 2
    private int suit; //suit 0 = spades, 1 = hearts, 2 = diamonds, 3 = clubs 
    private int num; //numerical value from 1-52 for file name
    private boolean faceup; //face up or not
    private BufferedImage img = null;
    private static int rotate;

    public Card (int num){
        suit = num/13;
        rank = num%13;
        this.num = num+1;
        faceup = true;
    }
    
    public int getnum(){
        return num;
    }

    /* displays card */ 
    public void show(Graphics g, int w, int h, int side){
        if (faceup) //if card is face-up
            try {img = ImageIO.read(new File("cards\\" + num + ".gif"));} catch (IOException e) {}
        else //if card is face-down
            try{img = ImageIO.read(new File("cards\\b.gif"));} catch (IOException e) {}

        if (side%2 != 0){ //rotate east and west
            Graphics2D g2 = (Graphics2D)g;

            double rotationRequired = Math.toRadians (270);
            double locationX = img.getWidth() / 2;
            double locationY = img.getHeight() / 2;
            AffineTransform tx = AffineTransform.getRotateInstance(rotationRequired, locationX, locationY);
            AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_BILINEAR);
            g2.drawImage(op.filter(img, null), w, h, null); //draw image
        }
        else
            g.drawImage(img, w, h, null);
    }

    /* for determining winner */ 
    public boolean beats(Card highest){
        //true if follows suit and is better rank OR if trumps highest
        if (((suit == highest.suit) && (rank < highest.rank)) || ((suit == 0) && (highest.suit != 0))) return true;
        else return false;
    }

    public int getrank(){
        return rank;
    }

    public int getsuit(){
        return suit;
    }

    public void setFace (boolean face){
        faceup = face;
    }
}


import java.awt.*;
import javax.swing.*;
import java.awt.event.*;

@SuppressWarnings("unchecked")
class GUI extends JFrame
{
    Game game;
    private int mousex;

    public GUI(Game m)
    {
        game = m;
        
        // create content pane and components
        JPanel content = new JPanel ();
        content.setLayout (new BorderLayout ());

        DrawArea board = new DrawArea (600, 517); // Area for cards to be displayed
        board.addMouseListener (new MouseAdapter(){
                public void mouseClicked (MouseEvent me){
                    mousex = me.getX();
                    if (me.getY() > 420 && me.getY() < 517 && mousex > 166 && mousex < 539){
                        game.setclicked(true); //mouse is clicked within area of user's hand
                    }
                }
            });

        // Add components to content pane
        content.add (board, "Center");
        content.add (new JLabel(" "), "North");

        // set window attributes
        setContentPane (content);
        content.setBackground(new Color(0, 160, 0));
        pack ();
        setTitle ("♠ Spades ♠"); //set title
        setSize (700, 580); //set size
        setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo (null); // Center window.
    }
    
    public int getx(){
        return mousex; //gives x position of mouse
    }

    class DrawArea extends JPanel
    {
        public DrawArea (int width, int height)
        {
            this.setPreferredSize (new Dimension (width, height)); // size
        }

        public void paintComponent (Graphics g)
        {
            game.display (g); //re-draws the display area
        }
    }
}   

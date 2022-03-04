import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.*;
import java.util.Timer;
import java.util.*;

public class BogglePanel extends JPanel implements ActionListener, KeyListener
{
    JTextArea gridTA, wordListTA;
    JTextField wordField;
    JButton resetButton;
    JButton startStopButton;
    JLabel timerLabel;
    JLabel scoreLabel;

    Set<String> usedWords;
    boolean isPlaying;
    Board brd;
    int score;
    Date startTime;
    Timer countdownTimer;
    TimeManager countdownManager;
    ArrayList<String> dictionary;


    public BogglePanel()
    {
        super();
        setupUI();

        usedWords = new TreeSet<String>();
        isPlaying = false;
        brd = new Board();
        gridTA.setText(brd.toString());
        score = 0;
        countdownTimer = new Timer();
        countdownManager = new TimeManager();
        loadDictionary();
    }

    /**
     * sets up the appearance of the window, and the connections between controls and this class.
     */
    public void setupUI()
    {
        gridTA = new JTextArea();
        gridTA.setBackground(Color.LIGHT_GRAY);
        gridTA.setForeground(Color.WHITE);
        gridTA.setEditable(false);
        wordListTA = new JTextArea();
        wordListTA.setEditable(false);
        JScrollPane wordScroller = new JScrollPane(wordListTA,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        resetButton = new JButton("Shuffle");
        startStopButton = new JButton("Start");
        timerLabel = new JLabel("3:00");
        wordField = new JTextField();
        scoreLabel = new JLabel("0");

        this.setLayout(new BorderLayout());
        JPanel top = new JPanel();
        top.setLayout(new GridLayout(1,2));

        top.add(gridTA);
        Box rightSide = Box.createVerticalBox();
        rightSide.add(scoreLabel);
        rightSide.add(wordScroller);
        top.add(rightSide);
        this.add(top,BorderLayout.CENTER);
        Box bottom = Box.createHorizontalBox();
        bottom.add(resetButton);
        bottom.add(wordField);
        bottom.add(startStopButton);
        bottom.add(timerLabel);
        bottom.add(Box.createHorizontalStrut(10));
        this.add(bottom,BorderLayout.SOUTH);

        resetButton.addActionListener(this);
        wordField.addActionListener(this);
        wordField.setFocusTraversalKeysEnabled(false);
        wordField.addKeyListener(this);
        startStopButton.addActionListener(this);

        gridTA.setFont(new Font(Font.MONOSPACED, Font.BOLD, 36));

        resetButton.setEnabled(false);
        wordField.setEnabled(false);


    }

    /**
     * loads the words from the dictionary file into memory.
     */
    public void loadDictionary()
    {
        dictionary = new ArrayList<>();
        try
        {
            BufferedReader reader = new BufferedReader(new FileReader(new File("word_list_moby_crossword.flat.txt")));
            String word;
            while((word = reader.readLine())!=null)
            {
                dictionary.add(word);
            }

        }catch (FileNotFoundException fnfExp)
        {
            fnfExp.printStackTrace();
        }
        catch (IOException ioExp)
        {
            ioExp.printStackTrace();
        }
        System.out.println("Dictionary Loaded. "+dictionary.size());
    }

    /**
     * the user has typed in a word, and now we need to decide whether to give credit for it.
     * @param word - a string in all upper case that we want to check.
     * postcondition: the score may change, the list of used words may be extended, the background of the text area may change.
     */
    public void handleWordEntry(String word)
    {
        if (isPlaying)
        {
            System.out.println(word);
            if (!isAWord(word))                             // is this actually a word?
            {
                System.out.println(word+" is not a word.");
                wordField.setBackground(Color.RED);
                return;
            }
            if (brd.checkWord(word))                        // is this word found in the grid?
            {
                if (usedWords.contains(word))               // have we already used this word?
                    wordField.setBackground(Color.BLUE);
                else
                {
                    usedWords.add(word);
                    score += word.length();
                    updateWordListTA();
                    scoreLabel.setText("" + score);
                    wordField.setBackground(Color.GREEN);
                }
            } else
            {
                System.out.println("not found in grid.");
                wordField.setBackground(Color.RED);
            }

        }

    }

    /**
     * helper method to see whether this word is found in our dictionary
     * @param target -the word to consider.
     * @return - wether the word was found in the dictionary or not
     */
    public boolean isAWord(String target)
    {

        return isAWord(target.toLowerCase(),0,dictionary.size()-1);

    }

    /**
     * recursive method performing binary search for word in the dictionary in memory
     * @param target - word to find (all lower case, to match our dictionary)
     * @param start - index of the begining index of range of dictionary to consider.
     * @param end - index of the end index (inclusive) of range of dictionary to consider
     * @return - whether the word was found or not.
     */
    private boolean isAWord(String target, int start, int end)
    {
        if (start>end)
            return false;
        int mid = (start+end)/2;
        if (dictionary.get(mid).equals(target))
            return true;
        if (dictionary.get(mid).compareTo(target)>0)
            return isAWord(target,start,mid-1);
        else
            return isAWord(target,mid+1,end);

    }

    /**
     * deal with the user clicking on the start/stop button.
     */
    public void handleStartStop()
    {
        if (isPlaying)
            endGame();
        else
            startGame();
    }

    /**
     * begin a game. Clear score, used words, shuffle board, change colors, and start timer.
     */
    public void startGame()
    {
        startTime = new Date();
        isPlaying = true;
        brd.randomize();
        gridTA.setText(brd.toString());
        usedWords.clear();

        score = 0;
        updateWordListTA();
        scoreLabel.setText(""+score);

        resetButton.setEnabled(true);
        wordField.setText("");
        wordField.setEnabled(true);

        startStopButton.setText("Stop");
        gridTA.setBackground(Color.BLACK);
        countdownTimer.schedule(countdownManager,0,1000);
    }

    /**
     * end the game, either by timer or by user clicking stop button.
     */
    public void endGame()
    {
        isPlaying = false;
        startStopButton.setText("Start");
        resetButton.setEnabled(false);
        wordField.setEnabled(false);
        gridTA.setBackground(Color.LIGHT_GRAY);
        countdownTimer.cancel();
    }

    /**
     * the player has requested that we shuffle the board; change the board, update the screen and subtract 5 points.
     */
    public void handleShuffle()
    {
        if (isPlaying)
        {
            brd.randomize();
            gridTA.setText(brd.toString());
            score -= 5;
            updateWordListTA();
            scoreLabel.setText(""+score);
            wordField.setBackground(Color.WHITE);
        }
    }

    /**
     * the list of used words has changed, so change the appearance of the wordlist text area on screen.
     */
    public void updateWordListTA()
    {
        StringBuilder wordList = new StringBuilder();
        for (String w:usedWords)
        {
            wordList.append(w);
            wordList.append("\n");
        }
        wordListTA.setText(wordList.toString());
    }

    //--------------------------------------------------------- EVENT HANDLING
    // +++++++++++++ Action events - button presses and hit return in text field.

    /**
     * deal with an event sent by a UI component that added this class as an ActionListener - the buttons and textfield in this case.
     * @param aEvt - a description of what the user did, including which component experienced the action.
     */
    public void actionPerformed(ActionEvent aEvt)
    {
        if (aEvt.getSource() == wordField)
        {
            handleWordEntry(wordField.getText().toUpperCase());
            wordField.setText("");
        }

        if (aEvt.getSource() == startStopButton)
        {
            handleStartStop();
            wordField.requestFocus();
        }

        if (aEvt.getSource() == resetButton)
        {
            handleShuffle();
            wordField.requestFocus();
        }
    }
    // ++++++++++++++ Key Listeners - when keys are pressed or released when focus is in the textfield.
    /**
     * Invoked when a key has been typed.
     * See the class description for {@link KeyEvent} for a definition of
     * a key typed event.
     *
     * @param e the event to be processed
     */
    @Override
    public void keyTyped(KeyEvent e)
    {
        ;  // ignoring this - we need to have this method to implemnent keylistener, but we aren't using it.
    }

    /**
     * Invoked when a key has been pressed.
     * See the class description for {@link KeyEvent} for a definition of
     * a key pressed event.
     *
     * @param e the event to be processed
     */
    @Override
    public void keyPressed(KeyEvent e)
    {
        if (e.getKeyCode() == KeyEvent.VK_TAB) // if the player hits <tab>, shuffle the board!
            handleShuffle();
        wordField.setBackground(Color.WHITE);
    }

    /**
     * Invoked when a key has been released.
     * See the class description for {@link KeyEvent} for a definition of
     * a key released event.
     *
     * @param e the event to be processed
     */
    @Override
    public void keyReleased(KeyEvent e)
    {
        ;  // ignoring this - we need to have this method to implemnent keylistener, but we aren't using it.
    }


    // --------------------------------------------------------------------- TIME MANAGER internal class

    /**
     * this class embodies an action that will be called about once a second by the Timer in the startGame method.
     */
    public class TimeManager extends TimerTask
    {
        /**
         * what this TimerTask should do every time the timer triggers it in its own thread.
         */
        public void run()
        {
            Date now = new Date();
            long diff = now.getTime() - startTime.getTime();
            long remaining = 3*60- diff/1000;
            if (remaining>0)
            {
                int mins = (int) remaining / 60;
                int secs = (int) remaining % 60;
                timerLabel.setText(String.format("%d:%02d", mins, secs)); // this lets the minutes start with 2 leading zeroes.
                timerLabel.repaint();
            }
            else
            {
                timerLabel.setText("0:00");
                endGame();
            }
        }
    }

}

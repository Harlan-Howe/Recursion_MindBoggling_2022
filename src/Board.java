public class Board
{
    private char[][] grid;
    private boolean[][] used;
    private int size;
    private final char[] vowels = {'A','E','I','O','U'};
    private final char[] consonants = {'B','C','D','F','G','H','J','K','L','M','N','P','Q','R','S','T','V','W','X','Y','Z'};


    public Board()
    {
        this(4);
    }

    public Board(int size)
    {
        this.size = size;
        grid = new char[size][size];
        used = new boolean[size][size];
        randomize();
    }

    /**
     * pick new letters for the board, with a given distribution of vowels and consonants.
     */
    public void randomize()
    {
        for (int i=0; i< size; i++)
            for(int j=0; j<size; j++)
                if (Math.random()<0.25)  // this is the percentage of vowels we expect to see on the board.
                    grid[i][j] = vowels[(int)(Math.random()*5)];
                else
                    grid[i][j] = consonants[(int)(Math.random()*21)];
    }

    public String toString()
    {
        String result = "";

        for (char[] row : grid)
        {
            for (char cell : row)
            {
                result+=" "+ cell + " ";
            }
            result+="\n";
        }
        return result;
    }

    /**
     * determines whether the given string can be found as a non-repeating sequence of adjacent letters in the board grid.
     * @param s - the (all uppercase) string we are looking for
     * @return - whether the string is found (true/false)
     */
    public boolean checkWord(String s)
    {
        // clear all "used" to false...
        for (int i=0; i<size; i++)
            for (int j=0; j<size; j++)
                used[i][j] = false;
        // TODO: You write this!
        return false; // might write return true elsewhere, if you find a valid path....
    }

    /**
     * checks whether the remainder of a string, starting with the index can be found in a non-repeating sequence of
     * adjacent letters in the grid, starting in the immediate neighborhood of the given (row, col).
     * @param s - the string (all upppercase, in its entirety) that we are looking for
     * @param index - the index in the string where we are starting to look in this recursion
     * @param row - the row where the previous (index-1) letter was found
     * @param col - the col where the previous (index-1) letter was found
     * @return - whether we find the string or not.
     */
    private boolean checkRemainderOfWord(String s, int index,int row, int col)
    {
        System.out.println("Checking "+s.substring(index)+" at "+row+" by "+col);
        // TODO: You write this!

        return false;// might write return true elsewhere, if you reach the base case successfully.
    }

}

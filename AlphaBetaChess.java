/*
 * Benjamin Yan
 * 6/10/2020
 * The purpose of this program is to create a chess GUI board with the code in the background.
 * NOTE: I do not take credit for this code, all credit goes to Logic Crazy Chess on YouTube.
 */

import javax.swing.*;
import java.util.*;
public class AlphaBetaChess {

    /*
     * Represents the chess board
     * WHITE / black
     * pawn = P/p
     * knight = K/k
     * bishop = B/b
     * rook = R/r
     * queen = Q/q
     * king = A/a
     */

    static String[][] chessBoard ={
            {"r","k","b","q","a","b","k","r"},
            {"p","p","p","p","p","p","p","p"},
            {" "," "," "," "," "," "," "," "},
            {" "," "," "," "," "," "," "," "},
            {" "," "," "," "," "," "," "," "},
            {" "," "," "," "," "," "," "," "},
            {"P","P","P","P","P","P","P","P"},
            {"R","K","B","Q","A","B","K","R"}};

    /*
     * Global variables to keep track of the position of kings for both sides
     * C is capital, L is lowercase aka C is white and L is black.
     */

    static int kingPositionC, kingPositionL;

    /* Global variable where 1 = human as white, 0 = human as black */

    static int humanAsWhite = -1;

    /* Depth that the engine will search for */

    static int globalDepth = 4;

    public static void main(String[] args) {

        /* Locate the positions of both kings initially */

        while (!"A".equals(chessBoard[kingPositionC / 8][kingPositionC % 8])) {
            kingPositionC++;
        }
        while (!"a".equals(chessBoard[kingPositionL / 8][kingPositionL % 8])) {
            kingPositionL++;
        }

        /* Set up the UI with swing */

        JFrame f = new JFrame("Chess Tutorial");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        UserInterface ui = new UserInterface();
        f.add(ui);
        f.setSize(256, 277);
        f.setVisible(true);

        Object[] option = {"Computer", "Human"};
        humanAsWhite = JOptionPane.showOptionDialog(null,
                "Who should play as white?", "ABC Options",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null,
                        option, option[1]);
        if (humanAsWhite == 0) {
            makeMove(alphaBeta(globalDepth, 1000000, -1000000, "", 0));
            flipBoard();
            f.repaint();
        }

        /* Print out the board for visualization */

        for (int i = 0; i < 8;i++) {
            System.out.println(Arrays.toString(chessBoard[i]));
        }
    } /* main() */

    /*
     * This method is the actual algorithm designed to find the best move for each player.
     * We will use the alpha-beta with pruning algorithm to search depth to work upwards to
     * find the best possible outcome.
     */

    public static String alphaBeta(int depth, int beta, int alpha, String move, int player) {
        String list = possibleMoves();
        if (depth == 0 || list.length() == 0) {
            return move + (Rating.rating(list.length(), depth) * (player * 2 - 1));
        }

        /* swap players since to 0 or 1 depending on what it wasn't before */

        player = (player == 0) ? 1 : 0;

        /*
         * We will recursively make moves and then call alphaBeta to get different return
         * values based on what path we choose, and from there we will pick the best move.
         */

        for (int i = 0; i < list.length(); i += 5) {
            makeMove(list.substring(i, i + 5));
            flipBoard();
            String returnString = alphaBeta(depth - 1, beta, alpha,
                    list.substring(i, i + 5), player);
            int value = Integer.valueOf(returnString.substring(5));
            flipBoard();
            undoMove(list.substring(i, i + 5));
            if (player == 0) {
                if (value <= beta) {
                    beta = value;
                    if (depth == globalDepth) {
                        move = returnString.substring(0, 5);
                    }
                }
            } else {
                if (value > alpha) {
                    alpha = value;
                    if (depth == globalDepth) {
                        move = returnString.substring(0, 5);
                    }
                }
            }
            if (alpha >= beta) {
                if (player == 0) {
                    return move + beta;
                } else {
                    return move + alpha;
                }
            }
        }
        if (player == 0) {
            return move + beta;
        } else {
            return move + alpha;
        }
    } /* alphaBeta() */

    /*
     * This method will flip the chess board so that we don't have to duplicate methods and
     * can play on one side of the board. This also makes the character capital if
     * lowercase and lowercase if capital.
     */

    public static void flipBoard() {
        String temp;
        for (int i = 0; i < 32; i++) {
            int r = i/8;
            int c = i % 8;
            if (Character.isUpperCase(chessBoard[r][c].charAt(0))) {
                temp = chessBoard[r][c].toLowerCase();
            } else {
                temp = chessBoard[r][c].toUpperCase();
            }
            if (Character.isUpperCase(chessBoard[7 - r][7 - c].charAt(0))) {
                chessBoard[r][c]=chessBoard[7 - r][7 - c].toLowerCase();
            } else {
                chessBoard[r][c]=chessBoard[7 - r][7 - c].toUpperCase();
            }
            chessBoard[7 - r][7 - c]=temp;
        }
        int kingTemp = kingPositionC;
        kingPositionC = 63 - kingPositionL;
        kingPositionL = 63 - kingTemp;
    } /* flipBoard() */

    /* This will be our primary method for actually making a move on the chess board */

    public static void makeMove(String move) {

        /* Not a pawn promotion */

        if (move.charAt(4)!='P') {
            /*
             * Replace the new spot in the move with the piece that should go there, replace
             * old spot with an empty space since no piece will be there anymore.
             */

            chessBoard[Character.getNumericValue(move.charAt(2))]
                    [Character.getNumericValue(move.charAt(3))] =
                    chessBoard[Character.getNumericValue(move.charAt(0))]
                            [Character.getNumericValue(move.charAt(1))];
            chessBoard[Character.getNumericValue(move.charAt(0))]
                    [Character.getNumericValue(move.charAt(1))] = " ";

            /* Update king position if the king is the one to move */

            if ("A".equals(chessBoard[Character.getNumericValue(move.charAt(2))]
                    [Character.getNumericValue(move.charAt(3))])) {
                kingPositionC = 8 * Character.getNumericValue(move.charAt(2)) +
                        Character.getNumericValue(move.charAt(3));
            }
        } else {
            /* If pawn promotion */

            chessBoard[1][Character.getNumericValue(move.charAt(0))] = " ";
            chessBoard[0][Character.getNumericValue(move.charAt(1))] =
                    String.valueOf(move.charAt(3));
        }
    } /* makeMove() */

    /* This will be our primary method for undoing a move on the chess board */

    public static void undoMove(String move) {
        if (move.charAt(4) != 'P') {
            /*
             * Similar to above, we take the new location and replace with old piece, replace
             * new location with space since nothing will be there.
             */

            chessBoard[Character.getNumericValue(move.charAt(0))]
                    [Character.getNumericValue(move.charAt(1))] =
                    chessBoard[Character.getNumericValue(move.charAt(2))]
                            [Character.getNumericValue(move.charAt(3))];
            chessBoard[Character.getNumericValue(move.charAt(2))]
                    [Character.getNumericValue(move.charAt(3))] =
                    String.valueOf(move.charAt(4));
            if ("A".equals(chessBoard[Character.getNumericValue(move.charAt(0))]
                    [Character.getNumericValue(move.charAt(1))])) {
                kingPositionC = 8 * Character.getNumericValue(move.charAt(0)) +
                        Character.getNumericValue(move.charAt(1));
            }
        } else {
            /* If pawn promotion */

            chessBoard[1][Character.getNumericValue(move.charAt(0))]="P";
            chessBoard[0][Character.getNumericValue(move.charAt(1))] =
                    String.valueOf(move.charAt(2));
        }
    } /* undoMove() */

    /*
     * This method will return a list of possible moves at any given time for the player
     * on the bottom.
     */

    public static String possibleMoves() {
        /*
         * Notation for capturing: 1234b means the piece on row 1, column 2 in the 2d array
         * representing the chessboard moves to the position at row 3, column 4 and captured
         * b, which in this case represents a bishop. If there is a space after, no capture
         * occurred.
         */

        String list = "";

        /* Loop through and get all the possible moves from each piece and add to list */

        for (int i = 0; i < 64; i++) {
            switch (chessBoard[i / 8][i % 8]) {
                case "P":
                    list += possibleP(i);
                    break;
                case "R":
                    list += possibleR(i);
                    break;
                case "K":
                    list += possibleK(i);
                    break;
                case "B":
                    list += possibleB(i);
                    break;
                case "Q":
                    list += possibleQ(i);
                    break;
                case "A":
                    list += possibleA(i);
                    break;
            }
        }
        return list;
    } /* possibleMoves() */

    /* This method takes into account pawn movement. */
    //TODO: add in en pessant
    public static String possibleP(int i) {
        String list = "";
        String oldPiece;
        int r = i / 8;
        int c = i % 8;
        for (int j = -1; j <= 1; j += 2) {
            /*
             * There are five possibilities for what a pawn can do. It can capture diagonally,
             * capture diagonally and promote on the 8th rank, promote by moving forward on the
             * 8th rank, move one forward, or move two forward if it's on the 2nd rank and
             * there are no pieces in front. This loop takes into account the first two cases.
             */

            try { //capture
                if (Character.isLowerCase(chessBoard[r - 1][c + j].charAt(0)) && i >= 16) {
                    oldPiece=chessBoard[r - 1][c + j];
                    chessBoard[r][c] = " ";
                    chessBoard[r - 1][c + j] = "P";
                    if (kingSafe()) {
                        list = list + r + c + (r - 1) + (c + j) + oldPiece;
                    }
                    chessBoard[r][c] = "P";
                    chessBoard[r - 1][c + j] = oldPiece;
                }
            } catch (Exception e) {
                // do nothing
            }

            try { //promotion && capture
                if (Character.isLowerCase(chessBoard[r - 1][c + j].charAt(0)) && i < 16) {
                    String[] temp = {"Q", "R", "B", "K"};
                    for (int k = 0; k < 4; k++) {
                        oldPiece=chessBoard[r - 1][c + j];
                        chessBoard[r][c] = " ";
                        chessBoard[r - 1][c + j] = temp[k];
                        if (kingSafe()) {
                            //column1,column2,captured-piece,new-piece,P
                            list = list + c + (c + j) + oldPiece + temp[k] + "P";
                        }
                        chessBoard[r][c]="P";
                        chessBoard[r - 1][c + j] = oldPiece;
                    }
                }
            } catch (Exception e) {
                // do nothing
            }
        }

        /* The rest of these take into account the other 3 possibilities */

        try { //move one up
            if (" ".equals(chessBoard[r - 1][c]) && i >= 16) {
                oldPiece = chessBoard[r - 1][c];
                chessBoard[r][c]=" ";
                chessBoard[r - 1][c]="P";
                if (kingSafe()) {
                    list = list + r + c + (r - 1) + c + oldPiece;
                }
                chessBoard[r][c]="P";
                chessBoard[r - 1][c] = oldPiece;
            }
        } catch (Exception e) {
            // do nothing
        }

        try { //promotion && no capture
            if (" ".equals(chessBoard[r - 1][c]) && i < 16) {
                String[] temp={"Q", "R", "B", "K"};
                for (int k = 0; k < 4; k++) {
                    oldPiece = chessBoard[r - 1][c];
                    chessBoard[r][c]=" ";
                    chessBoard[r - 1][c]=temp[k];
                    if (kingSafe()) {
                        //column1,column2,captured-piece,new-piece,P
                        list = list + c + c + oldPiece + temp[k] + "P";
                    }
                    chessBoard[r][c]="P";
                    chessBoard[r - 1][c] = oldPiece;
                }
            }
        } catch (Exception e) {
            // do nothing
        }

        try { //move two up
            if (" ".equals(chessBoard[r - 1][c]) && " ".equals(chessBoard[r - 2]
                    [c]) && i >= 48) {
                oldPiece=chessBoard[r-2][c];
                chessBoard[r][c]=" ";
                chessBoard[r - 2][c] = "P";
                if (kingSafe()) {
                    list = list + r + c + (r - 2) + c + oldPiece;
                }
                chessBoard[r][c]="P";
                chessBoard[r - 2][c] = oldPiece;
            }
        } catch (Exception e) {
            // do nothing
        }

        return list;
    } /* possibleP() */

    /* Rest of the methods follow the same logic as the pawn method */

    /* This method represents rook movement */

    public static String possibleR(int i) {
        String list = "";
        String oldPiece;
        int r = i / 8, c = i % 8;
        int temp = 1;
        for (int j = -1; j <= 1; j += 2) {
            try {
                while (" ".equals(chessBoard[r][c + temp * j]))
                {
                    oldPiece=chessBoard[r][c + temp * j];
                    chessBoard[r][c]=" ";
                    chessBoard[r][c+temp*j] = "R";
                    if (kingSafe()) {
                        list = list + r + c + r + (c + temp * j) + oldPiece;
                    }
                    chessBoard[r][c] = "R";
                    chessBoard[r][c + temp * j] = oldPiece;
                    temp++;
                }
                if (Character.isLowerCase(chessBoard[r][c + temp * j].charAt(0))) {
                    oldPiece=chessBoard[r][c + temp * j];
                    chessBoard[r][c] = " ";
                    chessBoard[r][c + temp * j]="R";
                    if (kingSafe()) {
                        list = list + r + c + r + (c + temp * j) +oldPiece;
                    }
                    chessBoard[r][c] = "R";
                    chessBoard[r][c + temp * j] = oldPiece;
                }
            } catch (Exception e) {
                // do nothing
            }

            temp = 1;
            try {
                while (" ".equals(chessBoard[r + temp * j][c]))
                {
                    oldPiece = chessBoard[r + temp * j][c];
                    chessBoard[r][c] = " ";
                    chessBoard[r + temp * j][c] = "R";
                    if (kingSafe()) {
                        list = list + r + c + (r + temp * j) + c + oldPiece;
                    }
                    chessBoard[r][c] = "R";
                    chessBoard[r + temp * j][c] = oldPiece;
                    temp++;
                }
                if (Character.isLowerCase(chessBoard[r + temp * j][c].charAt(0))) {
                    oldPiece = chessBoard[r + temp * j][c];
                    chessBoard[r][c] = " ";
                    chessBoard[r + temp * j][c] = "R";
                    if (kingSafe()) {
                        list = list + r + c + (r + temp * j) + c + oldPiece;
                    }
                    chessBoard[r][c] = "R";
                    chessBoard[r + temp * j][c] = oldPiece;
                }
            } catch (Exception e) {
                // do nothing
            }
            temp=1;
        }
        return list;
    } /* possibleR() */

    /* This method describes knight movement and returns possible knight moves */

    public static String possibleK(int i) {
        String list = "";
        String oldPiece;
        int r = i / 8;
        int c = i % 8;
        for (int j = -1; j <= 1; j += 2) {
            for (int k = -1; k <= 1; k += 2) {
                try {
                    if (Character.isLowerCase(chessBoard[r + j][c + k * 2].charAt(0)) ||
                            " ".equals(chessBoard[r + j][c + k * 2])) {
                        oldPiece = chessBoard[r + j][c + k * 2];
                        chessBoard[r][c] = " ";
                        if (kingSafe()) {
                            list = list + r + c +(r + j) + (c + k * 2) + oldPiece;
                        }
                        chessBoard[r][c] = "K";
                        chessBoard[r + j][c + k * 2] = oldPiece;
                    }
                } catch (Exception e) {
                    // do nothing
                }
                try {
                    if (Character.isLowerCase(chessBoard[r + j * 2][c + k].charAt(0)) ||
                            " ".equals(chessBoard[r + j * 2][c + k])) {
                        oldPiece = chessBoard[r + j *2][c + k];
                        chessBoard[r][c] = " ";
                        if (kingSafe()) {
                            list = list + r + c + (r + j * 2) + (c + k) + oldPiece;
                        }
                        chessBoard[r][c] = "K";
                        chessBoard[r + j * 2][c + k] = oldPiece;
                    }
                } catch (Exception e) {
                    // do nothing
                }
            }
        }
        return list;
    } /* possibleK() */

    /* This method describes bishop movement and possible bishop moves */

    public static String possibleB(int i) {
        String list = "";
        String oldPiece;
        int r = i / 8;
        int c = i % 8;
        int temp = 1;
        for (int j = -1; j <= 1; j += 2) {
            for (int k = -1; k <= 1; k += 2) {
                try {
                    while (" ".equals(chessBoard[r + temp * j][c + temp * k])) {
                        oldPiece = chessBoard[r + temp * j][c + temp * k];
                        chessBoard[r][c] = " ";
                        chessBoard[r + temp * j][c + temp * k] = "B";
                        if (kingSafe()) {
                            list = list + r + c + (r + temp * j) + (c + temp * k) + oldPiece;
                        }
                        chessBoard[r][c] = "B";
                        chessBoard[r + temp * j][c + temp * k] = oldPiece;
                        temp++;
                    }
                    if (Character.isLowerCase(chessBoard[r + temp * j]
                            [c + temp * k].charAt(0))) {
                        oldPiece = chessBoard[r + temp * j][c + temp * k];
                        chessBoard[r][c] = " ";
                        chessBoard[r + temp * j][c + temp * k] = "B";
                        if (kingSafe()) {
                            list = list + r + c + (r + temp * j) + (c + temp * k) + oldPiece;
                        }
                        chessBoard[r][c] = "B";
                        chessBoard[r + temp * j][c + temp * k] = oldPiece;
                    }
                } catch (Exception e) {
                    // do nothing
                }
                temp = 1;
            }
        }
        return list;
    } /* possibleB() */

    /* This method describes queen movement and lists possible queen moves */

    public static String possibleQ(int i) {
        String list = "";
        String oldPiece;
        int r = i / 8;
        int c = i % 8;
        int temp = 1;
        for (int j = -1; j <= 1; j++) {
            for (int k =- 1; k <= 1; k++) {
                if (j != 0 || k != 0) {
                    try {
                        while (" ".equals(chessBoard[r + temp * j][c + temp * k])) {
                            oldPiece = chessBoard[r + temp * j][c + temp * k];
                            chessBoard[r][c] = " ";
                            chessBoard[r + temp * j][c + temp * k] = "Q";
                            if (kingSafe()) {
                                list = list + r + c + (r + temp * j) + (c + temp * k) +
                                        oldPiece;
                            }
                            chessBoard[r][c] = "Q";
                            chessBoard[r + temp * j][c + temp * k] = oldPiece;
                            temp++;
                        }
                        if (Character.isLowerCase(chessBoard[r + temp * j]
                                [c + temp * k].charAt(0))) {
                            oldPiece = chessBoard[r + temp * j][c + temp * k];
                            chessBoard[r][c] = " ";
                            chessBoard[r + temp * j][c + temp * k] = "Q";
                            if (kingSafe()) {
                                list = list + r + c +(r + temp * j) + (c + temp * k) +
                                        oldPiece;
                            }
                            chessBoard[r][c] = "Q";
                            chessBoard[r + temp * j][c + temp * k] = oldPiece;
                        }
                    } catch (Exception e) {
                        // do nothing
                    }
                    temp = 1;
                }
            }
        }
        return list;
    } /* possibleQ() */

    /* This method describes king movement and possible king moves */

    public static String possibleA(int i) {
        String list= "";
        String oldPiece;
        int r = i / 8;
        int c = i % 8;
        for (int j = 0; j < 9; j++) {
            if (j != 4) {
                try {
                    if (Character.isLowerCase(chessBoard[r - 1 + j / 3][c - 1 + j % 3]
                            .charAt(0)) ||
                            " ".equals(chessBoard[r - 1 + j / 3][c - 1 + j % 3])) {
                        oldPiece = chessBoard[r - 1 + j / 3][c - 1 + j % 3];
                        chessBoard[r][c] = " ";
                        chessBoard[r - 1 + j / 3][c- 1 +  j % 3] = "A";
                        int kingTemp = kingPositionC;
                        kingPositionC = i + (j / 3) * 8 + j % 3 - 9;
                        if (kingSafe()) {
                            list = list + r + c +(r - 1 + j / 3) + (c - 1 + j % 3) + oldPiece;
                        }
                        chessBoard[r][c] = "A";
                        chessBoard[r - 1 + j / 3][c - 1 + j % 3] = oldPiece;
                        kingPositionC = kingTemp;
                    }
                } catch (Exception e) {
                    // do nothing
                }
            }
        }
        //TODO: add castling later
        return list;
    } /* possibleA() */

    /* This method returns whether or not the king is in check or not in a given position */

    public static boolean kingSafe() {
        /* check diagonally in check */

        int temp = 1;
        for (int i = -1; i <= 1; i += 2) {
            for (int j = -1; j <= 1; j += 2) {
                try {
                    while (" ".equals(chessBoard[kingPositionC / 8 + temp * i]
                            [kingPositionC % 8 + temp * j])) {
                        temp++;
                    }
                    if ("b".equals(chessBoard[kingPositionC / 8 + temp * i]
                            [kingPositionC % 8 + temp * j]) ||
                            "q".equals(chessBoard[kingPositionC / 8 + temp * i]
                                    [kingPositionC % 8+ temp * j])) {
                        return false;
                    }
                } catch (Exception e) {
                    // do nothing
                }
                temp = 1;
            }
        }

        /* check horizontally/vertically in check */

        for (int i = -1; i <= 1; i += 2) {
            try {
                while (" ".equals(chessBoard[kingPositionC / 8]
                        [kingPositionC % 8 + temp * i])) {
                    temp++;
                }
                if ("r".equals(chessBoard[kingPositionC / 8][kingPositionC % 8+ temp * i]) ||
                        "q".equals(chessBoard[kingPositionC / 8]
                                [kingPositionC % 8 + temp * i])) {
                    return false;
                }
            } catch (Exception e) {
                // do nothing
            }

            temp = 1;

            try {
                while (" ".equals(chessBoard[kingPositionC / 8 + temp * i]
                        [kingPositionC % 8])) {
                    temp++;
                }
                if ("r".equals(chessBoard[kingPositionC / 8 + temp * i][kingPositionC%8]) ||
                        "q".equals(chessBoard[kingPositionC / 8 + temp * i]
                                [kingPositionC % 8])) {
                    return false;
                }
            } catch (Exception e) {
                // do nothing
            }
            temp = 1;
        }

        /* any checks from the knight */

        for (int i = -1; i <= 1; i += 2) {
            for (int j =- 1; j <= 1; j += 2) {
                try {
                    if ("k".equals(chessBoard[kingPositionC / 8 + i]
                            [kingPositionC % 8 + j * 2])) {
                        return false;
                    }
                } catch (Exception e) {
                    // do nothing
                }
                try {
                    if ("k".equals(chessBoard[kingPositionC / 8 + i * 2]
                            [kingPositionC % 8+ j])) {
                        return false;
                    }
                } catch (Exception e) {
                    // do nothing
                }
            }
        }

        /*
         * diagonal checks from the pawn, only have to check after 16 because the pawn cannot
         * legally check before then
         */

        if (kingPositionC >= 16) {
            try {
                if ("p".equals(chessBoard[kingPositionC / 80 - 1][kingPositionC % 8 - 1])) {
                    return false;
                }
            } catch (Exception e) {
                // do nothing
            }
            try {
                if ("p".equals(chessBoard[kingPositionC / 80 - 1][kingPositionC % 8 + 1])) {
                    return false;
                }
            } catch (Exception e) {
                // do nothing
            }

            //king
            for (int i =- 1; i <= 1; i++) {
                for (int j = -1; j <= 1; j++) {
                    if (i != 0 || j != 0) {
                        try {
                            if ("a".equals(chessBoard[kingPositionC / 8 + i]
                                    [kingPositionC % 8 + j])) {
                                return false;
                            }
                        } catch (Exception e) {
                            // do nothing
                        }
                    }
                }
            }
        }
        return true;
    } /* kingSafe() */
} /* AlphaBetaChess.java */



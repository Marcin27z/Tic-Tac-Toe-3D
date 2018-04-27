package main.java.model;

import java.io.Serializable;


public class Model implements Serializable {

    public enum Mode {
        LOCAL, ONLINE
    }
    public enum Turn {
        LOCALPLAYERONE, LOCALPLAYERTWO, LOCALPLAYERTURN, REMOTEPLAYERTURN, GAMEOVER
    }
    public enum Identity {
        SERVER, CLIENT, NONE
    }
    private Turn turn = Turn.LOCALPLAYERONE;
    private Mode mode = Mode.LOCAL;
    //final static boolean LOCAL = false;
    //final static boolean ONLINE = true;
/*    final static boolean SERVER = false;
    final static boolean CLIENT = true;
    final private int localPlayerOne = 1;
    final private int localPlayerTwo = 2;
    final private int localPlayerTurn = 3;
    final private int remotePlayerTurn = 4;*/
    private Identity me = Identity.NONE;
    private int latestMoveY, latestMoveF;
    public Player[] player;
    public int[][] board = {{0}, {0}};

    public Model() {
        player = new Player[2];
        player[0] = new Player("PlayerO", -1);
        player[1] = new Player("PlayerX", 1);
    }

    public void changeTurn() {
        if (mode == Mode.ONLINE && turn == Turn.REMOTEPLAYERTURN)
            turn = Turn.LOCALPLAYERTURN;
        else if (mode == Mode.ONLINE && turn == Turn.LOCALPLAYERTURN)
            turn = Turn.REMOTEPLAYERTURN;
        else if (mode == Mode.LOCAL && turn == Turn.LOCALPLAYERONE)
            turn = Turn.LOCALPLAYERTWO;
        else if (mode == Mode.LOCAL && turn == Turn.LOCALPLAYERTWO)
            turn = Turn.LOCALPLAYERONE;
    }

    public void setIdentity(Identity identity) {
        me = identity;
    }

    public Identity getIdentity() {
        return me;
    }

    public int getLatestMoveY() {
        return latestMoveY;
    }

    public int getLatestMoveF() {
        return latestMoveF;
    }

    public void setEndGame() {
        turn = Turn.GAMEOVER;
    }

    public void setTurn(Turn turn) {
        this.turn = turn;
    }

    public Turn getTurn() {
        return turn;
    }

    public int getCurrentPlayer() {
        if (turn == Turn.REMOTEPLAYERTURN || turn == Turn.LOCALPLAYERTWO)
            return 1;
        else
            return 0;
    }

    public void restart() {
        if(mode == Mode.LOCAL)
            turn = Turn.LOCALPLAYERONE;
        else
            turn = Turn.LOCALPLAYERTURN;
        for(int i = 0; i < 4; i++) {
            for(int j = 0; j < 16; j++) {
                /*player[0].clearField(i, j);
                player[1].clearField(i, j);*/
                clearField(i, j);
            }
        }
    }

    public void setPlayersNames(String player1, String player2) {
        player[0].setName(player1);
        player[1].setName(player2);
    }

    public void setMode(Mode mode) {
        this.mode = mode;
    }

    public Mode getMode() {
        return mode;
    }

    public boolean makeMove(int playerNr, int y, int f) {
        /*if(!player[1 - playerNr].checkField(y, f) && player[playerNr].makeMove(y, f)) {
            latestMoveY = y;
            latestMoveF = f;
            return true;
        }*/
        if(board[y][f] == 0 && player[playerNr].makeMove(y, f)) {
            latestMoveY = y;
            latestMoveF = f;
            return true;
        }
        return false;
    }

    private void clearField (int y, int f) {
        board[y][f] = 0;
    }

    public class Player implements Serializable {
        //private boolean[][] board;
        private String name;
        private int token;

        Player(String name, int token) {
            this.name = name;
            this.token = token;
            board = new int[4][16];
        }

        public int checkField(int y, int f) {
            return board[y][f];
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public boolean checkWin() {
            for (int i = 0; i < 4; i++) {
                for (int j = 0; j < 4; j++)
                    if ((board[i][4 * j] == token && board[i][4 * j + 1] == token && board[i][4 * j + 2] == token && board[i][4 * j + 3] == token) ||
                            (board[i][j] == token && board[i][4 + j] == token && board[i][8 + j] ==token && board[i][12 + j] == token) ||
                            (board[0][4 * j] == token && board[1][4 * j + 1] == token && board[2][4 * j + 2] == token && board[3][4 * j + 3] == token) ||
                            (board[0][j] == token && board[1][4 + j] == token && board[2][8 + j] == token && board[3][12 + j] == token) ||
                            (board[3][4 * j] == token && board[2][4 * j + 1] == token && board[1][4 * j + 2] == token && board[0][4 * j + 3] == token) ||
                            (board[3][j] == token && board[2][4 + j] == token && board[1][8 + j] == token && board[0][12 + j] == token)) { //check every horizontal option
                        return true;
                    }
            }
            for (int i = 0; i < 16; i++) {
                if ((board[0][i] == token && board [1][i] == token && board[2][i] == token && board[3][i] == token)) { //check vertical
                    return true;
                }
            }
            for (int i = 0;i < 4; i++) {
                if ((board[i][0] == token && board[i][5] == token && board[i][10] == token && board[i][15] == token) ||
                        (board[i][3] == token && board[i][6] == token && board[i][9] == token && board[i][12] == token)) { //check cross on every level
                    return true;
                }
            }
            if ((board[0][0] == token && board[1][5] == token && board[2][10] == token && board[3][15] == token) ||
                    (board[0][3] == token && board[1][6] == token && board[2][9] == token && board[3][12] == token)) {
                return true;
            }
            if ((board[3][0] == token && board[2][5] == token && board[1][10] == token && board[0][15] == token) ||
                    (board[3][3] == token && board[2][6] == token && board[1][9] == token && board[0][12] == token)) {
                return true;
            }
            return false;
        }

        boolean makeMove (int y, int f) {
            if(board[y][f] == 0) {
                board[y][f] = token;
                return true;
            }
            return false;
        }


    }
}
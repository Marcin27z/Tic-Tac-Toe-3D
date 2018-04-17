package main.java;

import java.io.Serializable;

class Model implements Serializable {
    private int turn = 1;
    private boolean mode = false;
    final static boolean LOCAL = false;
    final static boolean ONLINE = true;
    final static boolean SERVER = false;
    final static boolean CLIENT = true;
    final private int localPlayerOne = 1;
    final private int localPlayerTwo = 2;
    final private int localPlayerTurn = 3;
    final private int remotePlayerTurn = 4;
    private boolean me;
    private int latestMoveY, latestMoveF;
    Player[] player;

    Model() {
        player = new Player[2];
        player[0] = new Player("PlayerO");
        player[1] = new Player("PlayerX");
    }

    void changeTurn() {
        /*switch (turn) {
            case 0:
                if (!mode)
                    turn = 1;
                else
                    turn = 2;
                break;
            case 1:
                turn = 0;
                break;
            case 2:
                turn = 0;
                break;
        }*/
        if (mode && turn == remotePlayerTurn)
            turn = localPlayerTurn;
        else if (mode && turn == localPlayerTurn)
            turn = remotePlayerTurn;
        else if (!mode && turn == localPlayerOne)
            turn = localPlayerTwo;
        else if (!mode && turn == localPlayerTwo)
            turn = localPlayerOne;
    }

    void setIdentity(boolean identity) {
        me = identity;
    }

    boolean getIdentity() {
        return me;
    }

    int getLatestMoveY() {
        return latestMoveY;
    }

    int getLatestMoveF() {
        return latestMoveF;
    }

    void setEndGame() {
        turn = 5;
    }

    void setTurn(int turn) {
        this.turn = turn;
    }

    int getTurn() {
        return turn;
    }

    int getCurrentPlayer() {
        /*if(turn == 0)
            return 0;
        else
            return 1;*/
        if (turn == remotePlayerTurn || turn == localPlayerTwo)
            return 1;
        else return 0;
    }

    void restart() {
        if(!mode)
            turn = 1;
        else
            turn = 3;
        for(int i = 0; i < 4; i++) {
            for(int j = 0; j < 16; j++) {
                player[0].clearField(i, j);
                player[1].clearField(i, j);
            }
        }
    }

    void setPlayersNames(String player1, String player2) {
        player[0].setName(player1);
        player[1].setName(player2);
    }

    void setMode(boolean mode) {
        this.mode = mode;
    }

    boolean getMode() {
        return mode;
    }

    boolean makeMove(int playerNr, int y, int f) {
        if(!player[1 - playerNr].checkField(y, f) && player[playerNr].makeMove(y, f)) {
            latestMoveY = y;
            latestMoveF = f;
            return true;
        }
        return false;
    }
}

class Player implements Serializable {
    private boolean[][] board;
    private String name;

    Player(String name) {
        this.name = name;
        board = new boolean[4][16];
    }

    public boolean checkField(int y, int f) {
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
                if ((board[i][4 * j] && board[i][4 * j + 1] && board[i][4 * j + 2] && board[i][4 * j + 3]) ||
                        (board[i][j] && board[i][4 + j] && board[i][8 + j] && board[i][12 + j]) ||
                        (board[0][4 * j] && board[1][4 * j + 1] && board[2][4 * j + 2] && board[3][4 * j + 3]) ||
                        (board[0][j] && board[1][4 + j] && board[2][8 + j] && board[3][12 + j]) ||
                        (board[3][4 * j] && board[2][4 * j + 1] && board[1][4 * j + 2] && board[0][4 * j + 3]) ||
                        (board[3][j] && board[2][4 + j] && board[1][8 + j] && board[0][12 + j])) { //check every horizontal option
                    return true;
                }
        }
        for (int i = 0; i < 16; i++) {
            if ((board[0][i] && board [1][i] && board[2][i] && board[3][i])) { //check vertical
                return true;
            }
        }
        for (int i = 0;i < 4; i++) {
            if ((board[i][0] && board[i][5] && board[i][10] && board[i][15]) ||
                    (board[i][3] && board[i][6] && board[i][9] && board[i][12])) { //check cross on every level
                return true;
            }
        }
        if ((board[0][0] && board[1][5] && board[2][10] && board[3][15]) ||
                (board[0][3] && board[1][6] && board[2][9] && board[3][12])) {
            return true;
        }
        if ((board[3][0] && board[2][5] && board[1][10] && board[0][15]) ||
                (board[3][3] && board[2][6] && board[1][9] && board[0][12])) {
            return true;
        }
        return false;
    }

    public boolean makeMove (int y, int f) {
        if(!board[y][f]) {
            board[y][f] = true;
            return true;
        }
        return false;
    }

    public void clearField (int y, int f) {
        board[y][f] = false;
    }
}
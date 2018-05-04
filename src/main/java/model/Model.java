package main.java.model;

import java.io.Serializable;


public class Model implements Serializable {

    public enum Mode {
        LOCAL, ONLINE
    }
    public enum Turn {
        LOCAL_PLAYER_ONE, LOCAL_PLAYER_TWO, LOCAL_PLAYER_TURN, REMOTE_PLAYER_TURN, GAME_OVER
    }
    public enum Identity {
        SERVER, CLIENT, NONE
    }
    private Turn turn = Turn.LOCAL_PLAYER_ONE;
    private Mode mode = Mode.LOCAL;
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
        if (mode == Mode.ONLINE && turn == Turn.REMOTE_PLAYER_TURN)
            turn = Turn.LOCAL_PLAYER_TURN;
        else if (mode == Mode.ONLINE && turn == Turn.LOCAL_PLAYER_TURN)
            turn = Turn.REMOTE_PLAYER_TURN;
        else if (mode == Mode.LOCAL && turn == Turn.LOCAL_PLAYER_ONE)
            turn = Turn.LOCAL_PLAYER_TWO;
        else if (mode == Mode.LOCAL && turn == Turn.LOCAL_PLAYER_TWO)
            turn = Turn.LOCAL_PLAYER_ONE;
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
        turn = Turn.GAME_OVER;
    }

    public void setTurn(Turn turn) {
        this.turn = turn;
    }

    public Turn getTurn() {
        return turn;
    }

    public int getCurrentPlayer() {
        if (turn == Turn.REMOTE_PLAYER_TURN || turn == Turn.LOCAL_PLAYER_TWO)
            return 1;
        else
            return 0;
    }

    public void restart() {
        if(mode == Mode.LOCAL)
            turn = Turn.LOCAL_PLAYER_ONE;
        else
            turn = Turn.LOCAL_PLAYER_TURN;
        for(int i = 0; i < 4; i++) {
            for(int j = 0; j < 16; j++) {
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
        private String name;
        private int token;

        Player(String name, int token) {
            this.name = name;
            this.token = token;
            board = new int[4][16];
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
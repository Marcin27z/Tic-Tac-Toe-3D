/*
package main.java.model;


import java.io.Serializable;

public class Player implements Serializable {
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
}*/

package com.example.tictactoe;

import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private final Button[] buttons = new Button[9];
    private final char[] board = {' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '};
    private final Handler handler = new Handler(Looper.getMainLooper());
    private TextView statusText;
    private boolean gameActive = true;
    private int turnToken = 0;

    private static final int GLASS = Color.argb(210, 8, 13, 28);
    private static final int CELL_IDLE = Color.argb(232, 246, 251, 255);
    private static final int CELL_EDGE = Color.argb(140, 166, 238, 255);
    private static final int INK = Color.rgb(10, 18, 32);
    private static final int ICE = Color.rgb(248, 251, 255);
    private static final int CYAN = Color.rgb(63, 226, 255);
    private static final int VIOLET = Color.rgb(182, 112, 255);
    private static final int ROSE = Color.rgb(255, 91, 153);
    private static final int GOLD = Color.rgb(255, 218, 116);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        statusText = findViewById(R.id.statusText);
        stylePanel(statusText, Color.argb(142, 3, 8, 22),
                Color.argb(160, 166, 238, 255), 27);
        stylePanel(findViewById(R.id.boardPanel), GLASS,
                Color.argb(118, 248, 251, 255), 28);

        for (int i = 0; i < 9; i++) {
            int resID = getResources().getIdentifier("btn" + i, "id", getPackageName());
            buttons[i] = findViewById(resID);
            final int index = i;
            styleCell(buttons[i]);
            buttons[i].setOnClickListener(v -> onCellClicked(index));
        }

        statusText.setText("Your move");
    }

    private void styleCell(Button cell) {
        cell.setTextColor(INK);
        cell.setTextSize(34);
        cell.setGravity(Gravity.CENTER);
        cell.setAllCaps(false);
        cell.setTypeface(Typeface.DEFAULT_BOLD);
        cell.setMinHeight(0);
        cell.setMinWidth(0);
        cell.setPadding(0, 0, 0, 0);
        cell.setAlpha(0.94f);
        cell.setBackground(cellBackground(CELL_IDLE, CELL_EDGE, 20));
        cell.setStateListAnimator(null);
    }

    private void stylePanel(View view, int fill, int stroke, int radius) {
        if (view != null) view.setBackground(cellBackground(fill, stroke, radius));
    }

    private GradientDrawable cellBackground(int fill, int stroke, int radius) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(fill);
        drawable.setCornerRadius(radius);
        drawable.setStroke(2, stroke);
        return drawable;
    }

    private void onCellClicked(int index) {
        if (board[index] != ' ' || !gameActive) return;

        makeMove(index, 'X');
        if (checkEndGame()) return;

        gameActive = false;
        int token = ++turnToken;
        statusText.setText("Bot is thinking");
        statusText.setTextColor(GOLD);

        handler.postDelayed(() -> {
            if (token != turnToken || gameActive) return;

            int bestMove = findBestMove();
            if (bestMove != -1) makeMove(bestMove, 'O');

            gameActive = true;
            checkEndGame();
        }, 360L);
    }

    private void makeMove(int index, char player) {
        board[index] = player;
        buttons[index].setText(String.valueOf(player));
        buttons[index].setTextColor(player == 'X' ? CYAN : ROSE);
        buttons[index].setBackground(cellBackground(
                player == 'X' ? Color.argb(236, 6, 42, 70) : Color.argb(236, 62, 14, 58),
                player == 'X' ? CYAN : VIOLET, 20));
        buttons[index].setScaleX(0.82f);
        buttons[index].setScaleY(0.82f);
        buttons[index].animate().scaleX(1f).scaleY(1f).setDuration(150).start();
    }

    private boolean checkEndGame() {
        int score = evaluateBoard(board);
        if (score == 10) {
            finishRound("Bot wins", ROSE);
            return true;
        } else if (score == -10) {
            finishRound("You win", CYAN);
            return true;
        } else if (!isMovesLeft(board)) {
            finishRound("No moves left", GOLD);
            return true;
        }
        statusText.setText("Your move");
        statusText.setTextColor(ICE);
        return false;
    }

    private void finishRound(String title, int accent) {
        gameActive = false;
        statusText.setText(title);
        statusText.setTextColor(accent);
        scheduleRestart();
    }

    private void scheduleRestart() {
        int token = ++turnToken;
        handler.postDelayed(() -> {
            if (token == turnToken) resetGame();
        }, 1300L);
    }

    private void resetGame() {
        turnToken++;
        for (int i = 0; i < 9; i++) {
            board[i] = ' ';
            buttons[i].setText("");
            buttons[i].setTextColor(INK);
            buttons[i].setAlpha(0.94f);
            buttons[i].setScaleX(1f);
            buttons[i].setScaleY(1f);
            buttons[i].setBackground(cellBackground(CELL_IDLE, CELL_EDGE, 20));
        }
        gameActive = true;
        statusText.setText("Your move");
        statusText.setTextColor(ICE);
    }

    private int findBestMove() {
        int bestVal = Integer.MIN_VALUE;
        int bestMove = -1;
        for (int i = 0; i < 9; i++) {
            if (board[i] == ' ') {
                board[i] = 'O';
                int moveVal = minimax(board, 0, false);
                board[i] = ' ';
                if (moveVal > bestVal) {
                    bestMove = i;
                    bestVal = moveVal;
                }
            }
        }
        return bestMove;
    }

    private int minimax(char[] b, int depth, boolean isMax) {
        int score = evaluateBoard(b);
        if (score == 10) return score - depth;
        if (score == -10) return score + depth;
        if (!isMovesLeft(b)) return 0;

        if (isMax) {
            int best = Integer.MIN_VALUE;
            for (int i = 0; i < 9; i++) if (b[i] == ' ') {
                b[i] = 'O';
                best = Math.max(best, minimax(b, depth + 1, false));
                b[i] = ' ';
            }
            return best;
        }

        int best = Integer.MAX_VALUE;
        for (int i = 0; i < 9; i++) if (b[i] == ' ') {
            b[i] = 'X';
            best = Math.min(best, minimax(b, depth + 1, true));
            b[i] = ' ';
        }
        return best;
    }

    private boolean isMovesLeft(char[] b) {
        for (char c : b) if (c == ' ') return true;
        return false;
    }

    private int evaluateBoard(char[] b) {
        int[][] winLines = {
                {0, 1, 2}, {3, 4, 5}, {6, 7, 8},
                {0, 3, 6}, {1, 4, 7}, {2, 5, 8},
                {0, 4, 8}, {2, 4, 6}
        };
        for (int[] line : winLines) {
            if (b[line[0]] != ' ' && b[line[0]] == b[line[1]] && b[line[1]] == b[line[2]]) {
                return b[line[0]] == 'O' ? 10 : -10;
            }
        }
        return 0;
    }
}

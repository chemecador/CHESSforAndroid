package com.example.chessforandroid;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;

public class DosJugadoresActivity extends AppCompatActivity {
    private GridLayout oGameBoard;
    private LinearLayout oGameBoardShell;
    private ArrayList<ImageButton> casillas;
    private char[][] tablero;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dos_jugadores);
        casillas = new ArrayList<>();
        tablero = new char[8][8];
        this.oGameBoardShell = (LinearLayout) this.findViewById(R.id.shellGameBoard);
        this.oGameBoard = (GridLayout) this.findViewById(R.id.gridGameBoard);
        this.oGameBoard.getViewTreeObserver().addOnGlobalLayoutListener(this.SquareIfy());
    }

    ViewTreeObserver.OnGlobalLayoutListener SquareIfy() {
        return new ViewTreeObserver.OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {
                int width = DosJugadoresActivity.this.oGameBoardShell.getMeasuredWidth();
                int height = DosJugadoresActivity.this.oGameBoardShell.getMeasuredHeight();
                int cols = DosJugadoresActivity.this.oGameBoard.getColumnCount();
                int rows = DosJugadoresActivity.this.oGameBoard.getRowCount();
                int tileCount = cols * rows;

                double sizeA = (width / cols);
                double sizeB = (height / rows);

                double smallestSize = Math.min(sizeA, sizeB);
                int smallestSizeInt = (int) Math.floor(smallestSize);
                boolean cambiar = false;
                for (int x = 0; x <= tileCount - 1; x++) {
                    try {
                        if (x % 8 == 0) {
                            cambiar = !cambiar;
                        }
                        ImageButton b = new ImageButton(DosJugadoresActivity.this);
                        if ((x % 2 == 0 && cambiar) || x % 2 != 0 && !cambiar) {
                            b.setBackgroundColor(Color.BLACK);
                        } else {
                            b.setBackgroundColor(Color.WHITE);
                        }
                        b.setImageResource(R.drawable.bpeon);
                        b.setPadding(0, 0, 0, 0);

                        GridLayout.LayoutParams lp = new GridLayout.LayoutParams();
                        lp.width = smallestSizeInt;
                        lp.height = smallestSizeInt;
                        lp.leftMargin = 0;
                        lp.rightMargin = 0;
                        lp.topMargin = 0;
                        lp.bottomMargin = 0;
                        b.setLayoutParams(lp);
                        casillas.add(b);

                        oGameBoard.addView(b);
                        oGameBoard.getLayoutParams().width = smallestSizeInt * cols;
                        oGameBoard.getLayoutParams().height = smallestSizeInt * rows;
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }

                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                    DosJugadoresActivity.this.oGameBoard.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                } else {
                    DosJugadoresActivity.this.oGameBoard.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
            }
        };
    }
    //pasarlo a char[][] ??
    private void crearTablero(){
    }
}
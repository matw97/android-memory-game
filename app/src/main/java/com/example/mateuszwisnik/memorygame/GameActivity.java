package com.example.mateuszwisnik.memorygame;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayout;
import android.view.View;
import android.widget.Toast;


import java.util.*;

public class GameActivity extends AppCompatActivity implements View.OnClickListener {

    private int numberOfCards;

    private MemoryButton[] memoryButtons;
    private List<Integer> buttonLocations;

    private MemoryButton selectedButton;
    private MemoryButton secondSelectedButton;

    private boolean isBusy = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        GridLayout gridLayout = findViewById(R.id.gridLayout);

        int columns = gridLayout.getColumnCount();
        int rows = gridLayout.getRowCount();

        numberOfCards = columns * rows;

        memoryButtons = new MemoryButton[numberOfCards];

        List<Integer> buttonGraphics = new LinkedList<>();

        buttonLocations = new LinkedList<>();

        buttonGraphics.add(R.drawable.button_1);
        buttonGraphics.add(R.drawable.button_2);
        buttonGraphics.add(R.drawable.button_3);
        buttonGraphics.add(R.drawable.button_4);
        buttonGraphics.add(R.drawable.button_5);
        buttonGraphics.add(R.drawable.button_6);
        buttonGraphics.add(R.drawable.button_7);
        buttonGraphics.add(R.drawable.button_8);

        shuffleButtons();

        for(int r = 0; r < rows; r++) {
            for(int c = 0; c < columns; c++) {
                MemoryButton memoryButton = new MemoryButton(this, r, c, buttonGraphics.get(buttonLocations.get(r * rows + c)));
                memoryButton.setId(View.generateViewId());
                memoryButton.setOnClickListener(this);
                memoryButtons[r * columns + c] = memoryButton;
                gridLayout.addView(memoryButton);
            }
        }
    }

    private void shuffleButtons() {

        for(int i = 0; i<numberOfCards; i++) {
            buttonLocations.add(i % numberOfCards / 2);
        }

        Collections.shuffle(buttonLocations);
    }

    @Override
    public void onClick(View v) {
        if(isBusy) {
            return;
        }

        MemoryButton memoryButton = (MemoryButton) v;

        if(memoryButton.isMatched()) {
            return;
        }

        if(selectedButton == null) {
            selectedButton = memoryButton;
            selectedButton.flip();
            return;
        }

        if(selectedButton.getId() == memoryButton.getId()) {
            return;
        }

        if(selectedButton.getImageId() == memoryButton.getImageId()) {
            memoryButton.flip();

            memoryButton.setMatched(true);
            selectedButton.setMatched(true);

            selectedButton.setEnabled(false);
            memoryButton.setEnabled(false);

            selectedButton = null;

            if(shouldGameEnd()) {
                Toast.makeText(this, "Congrats! You have won!", Toast.LENGTH_SHORT).show();
                finish();
                startActivity(getIntent());
            }

        } else {
            secondSelectedButton = memoryButton;
            secondSelectedButton.flip();
            isBusy = true;

            final Handler handler = new Handler();

            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    secondSelectedButton.flip();
                    selectedButton.flip();
                    secondSelectedButton = null;
                    selectedButton = null;
                    isBusy = false;
                }
            }, 1000);
        }

    }

    private boolean shouldGameEnd() {
        List<Boolean> booleans = new LinkedList<>();
        for(MemoryButton memoryButton : memoryButtons) {
            booleans.add(memoryButton.isMatched());
        }
        return areAllTrue(booleans);
    }

    private boolean areAllTrue(List<Boolean> booleans) {
        for(boolean b : booleans) if(!b) return false;
        return true;
    }
}

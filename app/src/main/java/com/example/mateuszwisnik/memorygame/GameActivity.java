package com.example.mateuszwisnik.memorygame;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayout;
import android.view.View;
import android.widget.Toast;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class GameActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private int numberOfCards;

    private MemoryButton[] memoryButtons;
    private List<Integer> buttonLocations;
    List<BitmapDrawable> buttonGraphics = new LinkedList<>();

    private MemoryButton selectedButton;
    private MemoryButton secondSelectedButton;

    private int numberOfPhotosTaken = 0;

    private boolean isBusy = false;
    private static int number;

    GridLayout gridLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        gridLayout = findViewById(R.id.gridLayout);

        Intent intent = getIntent();
        gridLayout.setColumnCount(intent.getIntExtra(MainActivity.EXTRA_MESSAGE_COLUMNS, 0));
        gridLayout.setRowCount(intent.getIntExtra(MainActivity.EXTRA_MESSAGE_ROWS, 0));
        number = gridLayout.getColumnCount();
        takePicture();
    }

    private void gameLogic() {

        int columns = gridLayout.getColumnCount();
        int rows = gridLayout.getRowCount();

        numberOfCards = columns * rows;

        memoryButtons = new MemoryButton[numberOfCards];

        buttonLocations = new LinkedList<>();

        shuffleButtons();

        for(int r = 0; r < rows; r++) {
            for(int c = 0; c < columns; c++) {
                MemoryButton memoryButton = new MemoryButton(this, r, c);
                memoryButton.setId(View.generateViewId());
                memoryButton.setOnClickListener(this);
                memoryButtons[r * columns + c] = memoryButton;
                gridLayout.addView(memoryButton);
            }
        }

        shufflePicturesForButtons();
    }

    private void shufflePicturesForButtons() {

        Collections.shuffle(buttonGraphics);

        for (int i = 0; i < numberOfCards; i++) {
            memoryButtons[i].setFront(buttonGraphics.get(i));
        }
    }

    private void takePicture() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            numberOfPhotosTaken++;
            Bundle extras = data.getExtras();
            final Bitmap photo = (Bitmap) Objects.requireNonNull(extras).get("data");

            insertPhotoToArray(photo);
        } else {
            takePicture();
        }
    }

    private void insertPhotoToArray(Bitmap photo) {
        number--;
        buttonGraphics.add(new BitmapDrawable(getResources(), photo));
        buttonGraphics.add(new BitmapDrawable(getResources(), photo));

        if (number > 0) {
            takePicture();
        } else {
            gameLogic();
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

        if(selectedButton.getImage().getBitmap().equals(memoryButton.getImage().getBitmap())) {
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

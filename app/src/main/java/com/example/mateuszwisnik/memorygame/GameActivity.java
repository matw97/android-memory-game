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
    private int numberOfPhotosToBeTaken;
    private int numberOfCards;
    private int columns;
    private int rows;
    private boolean isBusy = false;
    private Card selectedCard;
    private Card secondSelectedCard;
    private Card[] cards;
    List<BitmapDrawable> images = new LinkedList<>();
    private GridLayout gridLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        gridLayout = findViewById(R.id.gridLayout);

        initVariables();

        takePicture();
    }

    private void initVariables() {
        Intent intent = getIntent();
        columns = intent.getIntExtra(MainActivity.EXTRA_MESSAGE_COLUMNS, 0);
        rows = intent.getIntExtra(MainActivity.EXTRA_MESSAGE_ROWS, 0);
        gridLayout.setColumnCount(columns);
        gridLayout.setRowCount(rows);
        numberOfPhotosToBeTaken = columns * rows / 2;
        numberOfCards = columns * rows;
        cards = new Card[numberOfCards];
    }

    private void dynamicallyCreateCards() {
        for(int r = 0; r < rows; r++) {
            for(int c = 0; c < columns; c++) {
                Card card = new Card(this, r, c);
                card.setId(View.generateViewId());
                card.setOnClickListener(this);
                cards[r * columns + c] = card;
                gridLayout.addView(card);
            }
        }
    }

    private void shuffleImages() {
        Collections.shuffle(images);

        for (int i = 0; i < numberOfCards; i++) {
            cards[i].setFront(images.get(i));
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
            Bundle extras = data.getExtras();
            final Bitmap photo = (Bitmap) Objects.requireNonNull(extras).get("data");
            addImageToContainer(photo);
        } else {
            takePicture();
        }
    }

    private void addImageToContainer(Bitmap photo) {
        numberOfPhotosToBeTaken--;
        images.add(new BitmapDrawable(getResources(), photo));
        images.add(new BitmapDrawable(getResources(), photo));

        if (numberOfPhotosToBeTaken > 0) {
            takePicture();
        } else {
            dynamicallyCreateCards();
            shuffleImages();
        }
    }

    @Override
    public void onClick(View v) {
        if(isBusy) {
            return;
        }

        Card card = (Card) v;

        if(card.isMatched()) {
            return;
        }

        if(selectedCard == null) {
            selectedCard = card;
            selectedCard.flip();
            return;
        }

        if(selectedCard.getId() == card.getId()) {
            return;
        }

        if(selectedCard.getImage().getBitmap().equals(card.getImage().getBitmap())) {
            pairFound(card);
            if(shouldGameEnd()) {
                endGame();
            }
        } else {
            pairNotFound(card);
        }
    }

    private void pairFound(Card card) {
        card.flip();

        card.setMatched();
        selectedCard.setMatched();

        selectedCard.setEnabled(false);
        card.setEnabled(false);

        selectedCard = null;
    }

    private void pairNotFound(Card card) {
        secondSelectedCard = card;
        secondSelectedCard.flip();
        isBusy = true;

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                secondSelectedCard.flip();
                selectedCard.flip();
                secondSelectedCard = null;
                selectedCard = null;
                isBusy = false;
            }
        }, 1000);
    }

    private void endGame() {
        Toast.makeText(this, "Congrats! You have won!", Toast.LENGTH_SHORT).show();
        finish();
        startActivity(getIntent());
    }

    private boolean shouldGameEnd() {
        List<Boolean> booleans = new LinkedList<>();
        for(Card card : cards) {
            booleans.add(card.isMatched());
        }
        return areAllTrue(booleans);
    }

    private boolean areAllTrue(List<Boolean> booleans) {
        for(boolean b : booleans) if(!b) return false;
        return true;
    }
}

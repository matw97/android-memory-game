package com.example.mateuszwisnik.memorygame;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.widget.GridLayout;

class MemoryButton extends android.support.v7.widget.AppCompatButton {

    private final int row;
    private final int col;

    private boolean isFlipped = false;
    private boolean isMatched = false;

    private BitmapDrawable front;
    private final Drawable back;

    MemoryButton(Context context, int row, int col) {
        super(context);
        this.row = row;
        this.col = col;
        this.back = context.getDrawable(R.drawable.button_question_mark);

        setBackground(back);

        GridLayout.LayoutParams layoutParams = new GridLayout.LayoutParams(GridLayout.spec(row), GridLayout.spec(col));
        layoutParams.width = (int) getResources().getDisplayMetrics().density * 70;
        layoutParams.height = (int) getResources().getDisplayMetrics().density * 70;
        setLayoutParams(layoutParams);
    }

    boolean isMatched() {
        return isMatched;
    }

    void setMatched(boolean matched) {
        isMatched = matched;
    }

    BitmapDrawable getImage() {
        return front;
    }

    public void setFront(BitmapDrawable image) {
        front = image;
    }

    void flip() {
        if(isMatched) {
            return;
        }
        if(isFlipped) {
            setBackground(back);
            isFlipped = false;
        } else {
            setBackground(front);
            isFlipped = true;
        }
    }

}

package com.example.mateuszwisnik.memorygame;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.widget.GridLayout;

class MemoryButton extends android.support.v7.widget.AppCompatButton {

    private final int row;
    private final int col;
    private final int imageId;

    private boolean isFlipped = false;
    private boolean isMatched = false;

    private final Drawable front;
    private final Drawable back;

    MemoryButton(Context context, int row, int col, int imageId) {
        super(context);
        this.row = row;
        this.col = col;
        this.imageId = imageId;
        this.front = context.getDrawable(imageId);
        this.back = context.getDrawable(R.drawable.button_question_mark);

        setBackground(back);

        GridLayout.LayoutParams layoutParams = new GridLayout.LayoutParams(GridLayout.spec(row), GridLayout.spec(col));
        layoutParams.width = (int) getResources().getDisplayMetrics().density * 50;
        layoutParams.height = (int) getResources().getDisplayMetrics().density * 50;
        setLayoutParams(layoutParams);
    }

    boolean isMatched() {
        return isMatched;
    }

    void setMatched(boolean matched) {
        isMatched = matched;
    }

    int getImageId() {
        return imageId;
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

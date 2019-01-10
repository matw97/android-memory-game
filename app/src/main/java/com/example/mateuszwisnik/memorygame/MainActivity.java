package com.example.mateuszwisnik.memorygame;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    public static final String EXTRA_MESSAGE_COLUMNS = "MainActivity.EXTRA_MESSAGE_COLUMNS";
    public static final String EXTRA_MESSAGE_ROWS = "MainActivity.EXTRA_MESSAGE_ROWS";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button buttonPlay = findViewById(R.id.buttonPlay);
        Button buttonPlay2x2 = findViewById(R.id.buttonPlay2x2);

        buttonPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendIntent(4,4);
            }
        });

        buttonPlay2x2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendIntent(2,2);
            }
        });
    }

    private void sendIntent(int columns, int rows){
        Intent intent = new Intent(MainActivity.this,GameActivity.class);

        intent.putExtra(EXTRA_MESSAGE_COLUMNS,columns);
        intent.putExtra(EXTRA_MESSAGE_ROWS,rows);

        startActivity(intent);
    }
}

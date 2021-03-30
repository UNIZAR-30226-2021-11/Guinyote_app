package manyosoft.guinyote.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import manyosoft.guinyote.R;

public class UserProfile extends AppCompatActivity {

    private static final int ACTIVITY_HISTORY = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        FloatingActionButton back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        Button history = (Button) findViewById(R.id.button_historial);
        history.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        act_history();
                    }
                }
        );

    }

    private void act_history(){
        Intent i = new Intent(this, HistoryActivity.class);
        startActivityForResult(i,ACTIVITY_HISTORY);
    }
}
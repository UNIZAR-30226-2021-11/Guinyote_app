package manyosoft.guinyote.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import manyosoft.guinyote.R;

public class LoginActivity extends AppCompatActivity {
    private static final int ACTIVITY_PROFILE = 0;
    private static final int ACTIVITY_CREAR_CUENTA = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        FloatingActionButton back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        Button acceder = (Button) findViewById(R.id.iniciarSesion_acceder);
        acceder.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        act_profile();
                    }
                }
        );
        Button crearCuenta = (Button) findViewById(R.id.iniciarSesion_crear_cuenta);
        crearCuenta.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        act_registro();
                    }
                }
        );

    }

    private void act_profile(){
        Intent i = new Intent(this, UserProfile.class);
        startActivityForResult(i,ACTIVITY_PROFILE);
    }

    private void act_registro(){
        Intent i = new Intent(this, Registro.class);
        startActivityForResult(i,ACTIVITY_CREAR_CUENTA);
    }

}
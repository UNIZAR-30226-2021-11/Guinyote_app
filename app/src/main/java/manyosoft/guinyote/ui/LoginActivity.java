package manyosoft.guinyote.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.concurrent.ExecutionException;

import manyosoft.guinyote.R;
import manyosoft.guinyote.util.GuinyoteClienteJWT;
import manyosoft.guinyote.util.Usuario;

public class LoginActivity extends AppCompatActivity {
    private static final int ACTIVITY_PROFILE = 0;
    private static final int ACTIVITY_CREAR_CUENTA = 1;

    private EditText userName;
    private EditText password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        userName = findViewById(R.id.iniciarSesion_usuario);
        password = findViewById(R.id.iniciarSesion_password);

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
                        try {
                            act_profile();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
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

    private void act_profile() throws ExecutionException, InterruptedException {
        GuinyoteClienteJWT guinyoteClienteJWT = GuinyoteClienteJWT.getInstance();
        guinyoteClienteJWT.loginUsuario(this,userName.getText().toString(),password.getText().toString());
        //if() {//acierto en el inicio de sesión
            Intent i = new Intent(this, UserProfile.class);
            i.putExtra("userName",userName.getText().toString());
            startActivityForResult(i, ACTIVITY_PROFILE);
        /*}
        else{//error en el inicio de sesión
            CharSequence text = "ERROR EN EL INICIO DE SESION";
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(this, text, duration);
            toast.show();
        }
        guinyoteClienteJWT.getUsuario(this,userName.getText().toString());*/
    }

    private void act_registro(){
        Intent i = new Intent(this, Registro.class);
        startActivityForResult(i,ACTIVITY_CREAR_CUENTA);
    }

}
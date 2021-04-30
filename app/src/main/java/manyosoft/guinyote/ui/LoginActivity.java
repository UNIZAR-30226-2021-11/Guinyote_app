package manyosoft.guinyote.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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

    private EditText userName;
    private EditText password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        userName = findViewById(R.id.iniciarSesion_usuario);
        password = findViewById(R.id.iniciarSesion_password);

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
        GuinyoteClienteJWT guinyoteClienteJWT = GuinyoteClienteJWT.getInstance();
        boolean error;
        try{
            error = guinyoteClienteJWT.loginUsuario(this, userName.getText().toString(), password.getText().toString());
        } catch (Exception e) {
            Log.d("Login de usuario", e.getMessage());
            error = true;
        }
        if(!error) {//acierto en el inicio de sesión
            try {

                Usuario user = guinyoteClienteJWT.getUsuario(this,userName.getText().toString());
                new Usuario(user);
            }catch(Exception e){
                Log.d("GET USUARIO",e.getMessage());
            }
            finish();
            Intent i = new Intent(this, MainActivity.class);
            startActivity(i);

        }
        else{//error en el inicio de sesión
            CharSequence text = "ERROR EN EL INICIO DE SESION";
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(this, text, duration);
            toast.show();
        }
    }

    private void act_registro(){
        Intent i = new Intent(this, Registro.class);
        startActivity(i);
    }

}
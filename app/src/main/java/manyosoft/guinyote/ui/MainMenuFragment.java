package manyosoft.guinyote.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import manyosoft.guinyote.R;
import manyosoft.guinyote.util.GuinyoteClienteJWT;
import manyosoft.guinyote.util.Usuario;

public class MainMenuFragment extends Fragment {

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.main_menu, container, false);

    }



    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.findViewById(R.id.jugar_online_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(MainMenuFragment.this)
                        .navigate(R.id.action_FirstFragment_to_SecondFragment);
            }
        });


        view.findViewById(R.id.jugar_solo_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Context context = getContext();
                Intent juegoIntent = new Intent(context, JuegoActivity.class);
                juegoIntent.putExtra("idPartida",1L);
                juegoIntent.putExtra("idPlayer",1L);
                juegoIntent.putExtra("idPair",1L);
                juegoIntent.putExtra("create",true);
                startActivity(juegoIntent);
            }
        });


        view.findViewById(R.id.perfil_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Context context = getContext();
                Usuario user = Usuario.getInstance();
                if(user == null) {// no se ha logeado
                    Intent i = new Intent(context, LoginActivity.class);
                    startActivity(i);
                }
                else{//ya se ha logeado
                    Intent i = new Intent(context, UserProfile.class);
                    startActivity(i);
                }
            }
        });
    }
}
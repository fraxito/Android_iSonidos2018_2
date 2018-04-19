package com.example.xp.isonidos2;

import android.content.ClipData;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.VideoView;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;

import static android.support.v4.content.FileProvider.getUriForFile;

public class MainActivity extends AppCompatActivity {




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        LinearLayout principal =  (LinearLayout) findViewById(R.id.botones);

        int numeroLinea = 0;
        LinearLayout auxiliar = creaLineaBotones(numeroLinea);
        principal.addView(auxiliar);

        Field[] listaCanciones = R.raw.class.getFields();
        int columnas = 5;
        for (int i=0; i< listaCanciones.length; i++) {
            //creamos un botón por código y lo añadimos a la pantalla principal
            Button b = creaBoton(i, listaCanciones);
            //añadimos el botón al layout
            auxiliar.addView(b);
            if ( i % columnas == columnas-1){
                auxiliar = creaLineaBotones(i);
                principal.addView(auxiliar);
            }
        }


    }


    public void sonidoCopiar(View view){
        Button b = (Button) findViewById(view.getId());
        String nombre = b.getText().toString();


//        File rutaSonido = new File(getFilesDir(), "raw");
//        rutaSonido.mkdirs();
//        File file = new File(rutaSonido, nombre+".mp3");

        File file=new File(this.getFilesDir(),"raw/" + nombre+".mp3");

//        String url = "content://com.example.xp.isonidos2/raw/"+ nombre+".mp3";
//        File file = new File(url);

        Uri uri = FileProvider.getUriForFile(this,"com.example.xp.isonidos2", file);
        Toast.makeText(this, uri.toString(), Toast.LENGTH_LONG).show();
        Log.i("nombre", getContentResolver().getType(uri));
        //***



        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        shareIntent.setDataAndType(
                uri,
                "audio/mpeg");
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
        startActivity(Intent.createChooser(shareIntent, "Share Sound File"));

//        shareIntent.setDataAndType(uri, type);
//        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
//        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//        final Intent chooser = Intent.createChooser(shareIntent, "<titulo>");
//        this.startActivity(chooser);





//
//        this.grantUriPermission("com.whatsapp", uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
//
//        Intent share = new Intent(Intent.ACTION_SEND);
//        share.setType("audio/*");
//        share.putExtra(Intent.EXTRA_STREAM, uri);
//        startActivity(Intent.createChooser(share, "Share Sound File"));



    }




    public void sonido(View view){
        //Log.i("etiqueta: ", findViewById(view.getId()).getTag().toString());
        Button b = (Button) findViewById(view.getId());
        String nombre = b.getText().toString();
        if (nombre.substring(0,2).contains("v_")) {
            VideoView videoview = (VideoView) findViewById(R.id.videoView);
            Uri uri = Uri.parse("android.resource://"+getPackageName()+"/"+view.getTag());
            videoview.setVideoURI(uri);
            videoview.start();
        } else {
            MediaPlayer m = new MediaPlayer();
            m = MediaPlayer.create(this, (int) findViewById(view.getId()).getTag());
            m.start();
            m.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    mediaPlayer.stop();
                    if (mediaPlayer != null) {
                        mediaPlayer.release();
                    }
                }
            });
        }
    }

    private LinearLayout creaLineaBotones(int numeroLinea){
        LinearLayout.LayoutParams parametros = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT
                ,LinearLayout.LayoutParams.WRAP_CONTENT);
        parametros.weight = 1;
        LinearLayout linea = new LinearLayout(this);

        linea.setOrientation(LinearLayout.HORIZONTAL);
        linea.setLayoutParams(parametros);
        linea.setId(numeroLinea);
        return linea;
    }

    private Button creaBoton(int i, Field[] _listaCanciones){
        LinearLayout.LayoutParams parametrosBotones = new LinearLayout.LayoutParams(0,LinearLayout.LayoutParams.MATCH_PARENT);
        parametrosBotones.weight = 1;
        parametrosBotones.setMargins(5, 5, 5, 5);
        parametrosBotones.gravity = Gravity.CENTER_HORIZONTAL;
        Button b = new Button(this);
        b.setLayoutParams(parametrosBotones);
        b.setText(_listaCanciones[i].getName());
        b.setTextColor(Color.WHITE);
        b.setBackgroundColor(Color.BLUE);
        b.setAllCaps(false); //todas las letras del botón en minúscula
        int id = this.getResources().getIdentifier(_listaCanciones[i].getName(), "raw", this.getPackageName());
        b.setTag(id);

        b.setId(i+50);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sonido(view);
            }
        });

        b.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                sonidoCopiar(v);
                return true;
            }
        });
        return b;
    }
}

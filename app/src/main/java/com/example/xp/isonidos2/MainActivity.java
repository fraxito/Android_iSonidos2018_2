package com.example.xp.isonidos2;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
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


        File rutaSonido = new File(getFilesDir(), "raw");
        rutaSonido.mkdirs();
        File file = new File(rutaSonido, nombre+".mp3");

        Uri uri = FileProvider.getUriForFile(this,"com.example.xp.isonidos2", file);

        Toast.makeText(this, file.toString(), Toast.LENGTH_LONG).show();
        //***











//        Intent intent = ShareCompat.IntentBuilder.from(this)
//                .setStream(uri) // uri from FileProvider
//                .setType("audio/*")
//                .getIntent()
//                .setAction(Intent.ACTION_SEND) //Change if needed
//                .setDataAndType(uri, "audio/*")
//                .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//
//        startActivity(intent);



//        Intent share = new Intent(Intent.ACTION_SEND);
//        share.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
//        share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//        share.setType("audio/mpeg");
//        share.putExtra(Intent.EXTRA_STREAM, uri);
//        share.putExtra(Intent.EXTRA_SUBJECT, uri.toString());
//        startActivity(Intent.createChooser(share, "Share Sound File"));

//        Intent mShareIntent = new Intent();
//        mShareIntent.setAction(Intent.ACTION_SEND);
//        mShareIntent.setType("audio/mpeg");
//        // Assuming it may go via eMail:
//        mShareIntent.putExtra(Intent.EXTRA_SUBJECT, "Here is a PDF from PdfSend");
//        // Attach the PDf as a Uri, since Android can't take it as bytes yet.
//        mShareIntent.putExtra(Intent.EXTRA_STREAM, uri);
//        startActivity(mShareIntent);


    }

    public Uri getRawUri(String filename) {
        return Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + File.pathSeparator + File.separator + getPackageName() + "/raw/" + filename);
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

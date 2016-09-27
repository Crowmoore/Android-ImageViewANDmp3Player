package fi.jamk.h3090.imageviewexercise;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import static android.R.id.list;
import static android.provider.ContactsContract.CommonDataKinds.Website.URL;

public class MusicActivity extends AppCompatActivity {

    private ListView listView;
    private String mediaPath;
    private List<String> songs = new ArrayList<>();
    private MediaPlayer player = new MediaPlayer();
    private LoadSongsTask task;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music);

        listView = (ListView) findViewById(R.id.listView);
        mediaPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC).getAbsolutePath();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
           @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
               try {
                   player.reset();
                   player.setDataSource(songs.get(position));
                   player.prepare();
                   player.start();
               } catch (IOException e) {
                   Toast.makeText(getBaseContext(), "Cannot start audio playback!", Toast.LENGTH_SHORT).show();
               }
           }
        });
        task = new LoadSongsTask();
        task.execute();
    }

    public void onChangeActivityClick(View view) {
        startActivity(new Intent(MusicActivity.this, MainActivity.class));
    }

    @Override
    public void onStop() {
        super.onStop();
        if(player.isPlaying()) {
            player.reset();
        }
    }

    private class LoadSongsTask extends AsyncTask<Void,String,Void> {
        private List<String> loadedSongs = new ArrayList<>();

        protected void onPreExecute() {
            Toast.makeText(getApplicationContext(), "Loading..", Toast.LENGTH_SHORT).show();
        }

        protected Void doInBackground(Void... url) {
            updateSongListRecursive(new File(mediaPath));
            return null;
        }

        protected void updateSongListRecursive(File path) {
            Log.d("tag", path.toString());
            if (path.isDirectory()) {
                for(int i = 0; i < path.listFiles().length; i++) {
                    File file = path.listFiles()[i];
                    updateSongListRecursive(file);
                }
            } else {
                String name = path.getAbsolutePath();
                publishProgress(name);
                if(name.endsWith(".mp3")) {
                    loadedSongs.add(name);
                }
            }
        }

        protected void onPostExecute(Void args) {
            ArrayAdapter<String> songList = new ArrayAdapter<>(MusicActivity.this,
                                                android.R.layout.simple_list_item_1,
                                                loadedSongs);
            listView.setAdapter(songList);
            songs = loadedSongs;

            Toast.makeText(getApplicationContext(), "Songs " + songs.size(), Toast.LENGTH_LONG).show();
        }
    }
}

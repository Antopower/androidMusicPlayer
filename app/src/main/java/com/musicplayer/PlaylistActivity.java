//Anthony Martel - Étienne Boisjoli
//Version 1.2
package com.musicplayer;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
 
public class PlaylistActivity extends Activity {
    // Songs list
    public ArrayList<HashMap<String, String>> songsList = new ArrayList<HashMap<String, String>>();
    ListView lv;
    SimpleAdapter adapter;
    EditText search;
    MediaStore ms = new MediaStore();
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.playlist);
       lv = (ListView)findViewById(R.id.list);
       search = (EditText)findViewById(R.id.etSearchBar);
       
       //Fonction qui crée la liste de chansons
       
        ArrayList<HashMap<String, String>> songsListData = new ArrayList<HashMap<String, String>>();
        songPlayList plm = new songPlayList();
        songsList = plm.getPlayList();
 
        for (int i = 0; i < songsList.size(); i++) {
            HashMap<String, String> song = songsList.get(i);
            songsListData.add(song);
        }
        adapter = new SimpleAdapter(this, songsListData,
                R.layout.playlist_item, new String[] { "songTitle" }, new int[] {
                        R.id.songTitle });
        lv.setAdapter(adapter);

        //Fonction pour filtrer la liste(Recherche)
        search.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                // When user changed the Text
            	adapter.getFilter().filter(cs);
            }
            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                    int arg3) { }
            @Override
            public void afterTextChanged(Editable arg0) {}
        });
   
        //Fonction onClick pour choisir la chanson. Cela retourne les informations pour savoir quel chahnson jouer.
        lv.setOnItemClickListener(new OnItemClickListener() {
 
        	@Override
            @SuppressWarnings("unchecked")
            public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
				HashMap<String, String> map = (HashMap<String, String>) lv.getItemAtPosition(position);
        		String numeroToune = map.get("index");
        		int toune = Integer.parseInt(numeroToune);
                Intent in = new Intent(getApplicationContext(), MainActivity.class);
                in.putExtra("songIndex", toune);
                setResult(100, in);
                finish();
            }
        });
    }
}
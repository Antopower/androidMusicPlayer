//Anthony Martel - �tienne Boisjoli
//Version 1.2
package com.musicplayer;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.HashMap;

import android.os.Environment;
import android.util.Log;

public class songPlayList {
	final String MEDIA_PATH = new String(Environment.getExternalStorageDirectory().getPath() + "/music/");
    private ArrayList<HashMap<String, String>> songsList = new ArrayList<HashMap<String, String>>();
    int numeroToune=0;
 

	public songPlayList(){
		 
    }
	//Fonction qui cherche les fichier .MP3 dans le dossier MEDIA_PATH. Elle les ajoute dans un arrayList
	public ArrayList<HashMap<String, String>> getPlayList(){
        File home = new File(MEDIA_PATH);
        try{
        	@SuppressWarnings("unused")
        	//Ce Try-catch est la pour confirmer qu'il y a un dossier MEDIA_PATH. Si non retourne un arrayList avec 1 valeur disant NOMUSIC.
			int bob =home.listFiles(new FileExtensionFilter()).length;
        }catch (Exception e) {
        	HashMap<String, String> song = new HashMap<String, String>();
            song.put("songTitle", "NOMUSIC");
            song.put("songPath", "NOMUSIC");
            song.put("index", "NOMUSIC");
            songsList.add(song);
        	return songsList;
		}
        Log.d("TAG", "Message");
        if (home.listFiles(new FileExtensionFilter()).length > 0) {
        	for (File file : home.listFiles(new FileExtensionFilter())) {
                HashMap<String, String> song = new HashMap<String, String>();
                song.put("songTitle", file.getName().substring(0, (file.getName().length() - 4)));
                song.put("songPath", file.getPath());
                song.put("index", String.valueOf(numeroToune));
                // Adding each song to SongList
                songsList.add(song);
                numeroToune++;
            }
        }
        // return songs list array
        return songsList;
    }
	class FileExtensionFilter implements FilenameFilter {
        public boolean accept(File dir, String name) {
            return (name.endsWith(".mp3") || name.endsWith(".MP3"));
        }
    }
	
}

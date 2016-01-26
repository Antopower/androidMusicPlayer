//Anthony Martel - �tienne Boisjoli
//Version 2.3
package com.musicplayer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements OnClickListener,SeekBar.OnSeekBarChangeListener{
	//Variables Globales
	DisplayMetrics displaymetrics = new DisplayMetrics();
	float mLastX=0, mLastY=0, mLastZ=0, deltaX=0,deltaY=0,deltaZ=0;
	final float NOISE = (float) 2.0;
	SensorManager mSensorManager2;
	SensorEventListener mSensorListener2;
	MediaPlayer mp = new MediaPlayer();
	long milliseconds, progress,lastUpdate=0;
	boolean isPlaying = false, isRepeat = false, isShuffle = false, inSeeking = false, aleaShake = false, shakeTiming = false;
	int currentSongIndex=0, progressLive = 0, oldX, ctrTiming = 0;
	Handler mHandler = new Handler();
	TextView txtCurrentTime,txtDuration,txtSongTitle;
	SeekBar progressBar;
	songTime songTime= new songTime();
	songPlayList songPlayList;
	ImageButton btnPlay,btnPlaylist,btnNext,btnPrevious,btnRepeat,btnShuffle;
	ArrayList<HashMap<String, String>> songsList = new ArrayList<HashMap<String, String>>();
	String sdcard = Environment.getExternalStorageDirectory().getAbsolutePath(), progressLiveString;
	Random shuffle = new Random();
	ImageView androidRobot;
	LinearLayout myLayoutPhoto;
	ActivityManager  manager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		//Initialisation
		btnPlay = (ImageButton) findViewById(R.id.btnPlay);
		btnNext = (ImageButton) findViewById(R.id.btnNext);
		btnPrevious = (ImageButton) findViewById(R.id.btnPrevious);
		btnRepeat = (ImageButton) findViewById(R.id.btnRepeat);
		btnPlaylist = (ImageButton) findViewById(R.id.btnPlaylist);
		btnShuffle = (ImageButton) findViewById(R.id.btnShuffle);
		txtDuration = (TextView)findViewById(R.id.txtDuration);
		txtSongTitle = (TextView)findViewById(R.id.txtSongTitle);
		txtCurrentTime = (TextView)findViewById(R.id.txtCurrentTime);
		progressBar = (SeekBar)findViewById(R.id.sbSongProgress12);
		androidRobot = (ImageView)findViewById(R.id.imgAndroidPlayer);
		myLayoutPhoto = (LinearLayout)findViewById(R.id.player_middle_bg);
		
	    //btnPause.setImageDrawable(Drawable.createFromPath(sdcard+"/MusicPlayer/pause.png"));
		
		//Cr�eation de la liste des chansons
		songPlayList songPlayList = new songPlayList();
		songsList = songPlayList.getPlayList();
		//V�rification, si il y a un dossier music ou non
		if(songsList.get(0).get("songPath") == "NOMUSIC")
		{
			txtSongTitle.setText("Cr�ez le dossier \"Music\" sur la m�moire principal du t�l�phone et ajoutez-si de la musique!");
			Toast.makeText(this,String.format(sdcard),
			Toast.LENGTH_LONG).show();
		}else{
			//Si oui, on fait les onClick et on ouvre la liste des chansons
			mSensorManager2 = (SensorManager)getSystemService(SENSOR_SERVICE);
			manager = (ActivityManager)MainActivity.this.getSystemService(Context.ACTIVITY_SERVICE);
			getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
			btnPlay.setOnClickListener(this);
			btnRepeat.setOnClickListener(this);
			btnPlaylist.setOnClickListener(this);
			btnNext.setOnClickListener(this);
			btnPrevious.setOnClickListener(this);
			btnShuffle.setOnClickListener(this);
			progressBar.setOnSeekBarChangeListener(this);
			myLayoutPhoto.setOnTouchListener(
	        		new RelativeLayout.OnTouchListener() {
	        			public boolean onTouch(View v, MotionEvent m) {
	        				handleTouch(m);     				
	        			    return true;
	        			}
	        		}
	        );
			//Listener pour la fonction de Shake
			mSensorListener2 = new SensorEventListener() {
		   	    @Override
		   	    public void onAccuracyChanged(Sensor arg0, int arg1) {
		   	    }
		   	    @Override
		   	    public void onSensorChanged(SensorEvent event2) {
					Sensor sensor = event2.sensor;   
		   	        if (sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
		   	        	if(aleaShake == true && isPlaying == true){ 
		   	        		deltaX=0;deltaY=0;deltaZ=0;
			   	        	float x = event2.values[0];
			   	    		float y = event2.values[1];
			   	    		float z = event2.values[2];
			   	    		deltaX = Math.abs(mLastX - x);
			   				deltaY = Math.abs(mLastY - y);
			   				deltaZ = Math.abs(mLastZ - z);
			   				if (deltaX < NOISE) deltaX = (float)0.0;
			   				if (deltaY < NOISE) deltaY = (float)0.0;
			   				if (deltaZ < NOISE) deltaZ = (float)0.0;
			   				mLastX = x;
			   				mLastY = y;
			   				mLastZ = z;
			   				deltaX = Math.round(deltaX);
			   				deltaY = Math.round(deltaY);
			   				deltaZ = Math.round(deltaZ);
			   				if(shakeTiming == true){
				   		        if(deltaX >12 || deltaY>12 || deltaZ>12){
				   		        	shakeTiming = false;
				   		        	ctrTiming = 0;
									currentSongIndex = shuffle.nextInt(songsList.size());
									mp.reset();
									playMusic(currentSongIndex);
				   		        }
			   		        }
		   				
		   	        	}
		   	        }	
				}    
		   	};
		   	mSensorManager2.registerListener(mSensorListener2, mSensorManager2.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_GAME);

		   	
			btnPlaylist.performClick();
		}		
	}
	
	
	

	//"Thread" pour actualiser le temps et la bar de progres
	final Runnable mUpdateTimeTask = new Runnable() {
		public void run() {
			//Si une chanson joue:
			if(isPlaying==true){
				long timeCurrent = mp.getCurrentPosition();
				long timeDuration = mp.getDuration();
				
				//Transforme les millisecondes en String de temps en format: m:ss et l'affiche.
				long progressMilli =(timeDuration*progressLive)/100;
				progressLiveString = songTime.time(progressMilli);
				String Duration = songTime.time(timeDuration);
				String Current = songTime.time(timeCurrent);
				progress = songTime.progressBarPercentage(timeDuration, timeCurrent);
				txtDuration.setText(Duration);
				
				//Timer pour r�activer le shake apres 3 sec.(6 fois 500 millisecondes) 
				if(ctrTiming == 2){
					shakeTiming = true;
					ctrTiming = 0;
				}
				ctrTiming = ctrTiming + 1;
				
				btnNext.setOnClickListener(MainActivity.this);
				btnPrevious.setOnClickListener(MainActivity.this);
	
				if(inSeeking == false){txtCurrentTime.setText(Current);progressBar.setProgress((int)progress);}
				else{txtCurrentTime.setText(progressLiveString);}
				
				//Quoi faire pour la fin de la chanson.
				if(timeCurrent > timeDuration-500){
					if(isRepeat == true){
						mp.reset();
						playMusic(currentSongIndex);
					}else if(isShuffle == true){
						currentSongIndex = shuffle.nextInt(songsList.size());
						mp.reset();
						playMusic(currentSongIndex);
					}else{
						btnNext.performClick();
					}
				}
				mHandler.postDelayed(this, 500);
			}
			//Si on veut bouger la barre quand la  musique est sur pause
			else if(isPlaying == false && inSeeking == true){
				long timeDuration = mp.getDuration();
				long progressMilli =(timeDuration*progressLive)/100;
				progressLiveString = songTime.time(progressMilli);
				txtCurrentTime.setText(progressLiveString);
				mHandler.postDelayed(this, 500);}
			//Sinon, on arrete le "Thread"
			else{mHandler.removeCallbacks(mUpdateTimeTask);}
		}
		
		
	};
	
	
	
	
	
	//Tous les boutons!
	public void onClick(View v) {
		switch (v.getId()) {
		//Bouton Next
		case R.id.btnNext:
			btnNext.setOnClickListener(null);
			currentSongIndex++;
			if(isShuffle == true){
				currentSongIndex = shuffle.nextInt(songsList.size());
			}else if(currentSongIndex == songsList.size()){
				currentSongIndex = 0;
			}	
			mp.reset();
			mHandler.removeCallbacks(mUpdateTimeTask);
			playMusic(currentSongIndex);			
			break;
			//Bouton Previous
		case R.id.btnPrevious:
			btnPrevious.setOnClickListener(null);
			currentSongIndex--;
			if(isShuffle == true){
				currentSongIndex = shuffle.nextInt(songsList.size());
			}else if(currentSongIndex < 0){
				currentSongIndex = songsList.size()-1;
			}	
			mp.reset();
			mHandler.removeCallbacks(mUpdateTimeTask);
			playMusic(currentSongIndex);
			break;
			//Bouton R�p�ter
		case R.id.btnRepeat:
				if(isRepeat == true){
					isRepeat = false;
					btnRepeat.setImageResource(R.drawable.repeat);
				}
				else{
					isRepeat = true;
					btnRepeat.setImageResource(R.drawable.repeat_pressed);
				}
			break;
			//Bouton Al�atoire
		case R.id.btnShuffle:
			if(isShuffle == true){
				isShuffle = false;
				btnShuffle.setImageResource(R.drawable.shuffle);
			}
			else{
				isShuffle = true;
				btnShuffle.setImageResource(R.drawable.shuffle_pressed);
			}
			break;
			//Bouton Play
		case R.id.btnPlay:
			if(mp.isPlaying()){
                if(mp!=null){
                	//PAUSE
                	isPlaying = false;
                    mp.pause();
                    // Changing button image to play button
                    btnPlay.setImageResource(R.drawable.btn_play);
                    
                }
            }else{
                // Resume song
                if(mp!=null){
                	isPlaying = true;
                    mp.start();
                    // Changing button image to pause button
                    btnPlay.setImageResource(R.drawable.btn_pause);
                    mHandler.postDelayed(mUpdateTimeTask, 500);
                }
            }
			break;
			//Bouton Playlist
		case R.id.btnPlaylist:
			Intent i = new Intent(getApplicationContext(), com.musicplayer.PlaylistActivity.class);
	        startActivityForResult(i, 100);
	        mHandler.postDelayed(mUpdateTimeTask, 500);
		break;
		}
	}
	//Resultat de l'activit� de la liste de choix de chansons
	protected void onActivityResult(int requestCode,
            int resultCode, Intent data) {
			super.onActivityResult(requestCode, resultCode, data);
				if(resultCode == 100){
					currentSongIndex = data.getExtras().getInt("songIndex");
					mp.reset();
					playMusic(currentSongIndex);
				}

	}
	
	
	
	
	
	//Fonction pour faire jouer la musique
	public void playMusic(int songIndex){
		
		try {
			isPlaying = true;
			btnPlay.setImageResource(R.drawable.btn_pause);
			mp.setDataSource(songsList.get(songIndex).get("songPath"));
			txtSongTitle.setText(songsList.get(songIndex).get("songTitle"));
			mp.prepare();
			mp.start();
			mHandler.removeCallbacks(mUpdateTimeTask);
			mHandler.postDelayed(mUpdateTimeTask, 500);

		}catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
	
	//Fonction pour mettre le bouton comme le bouton Home, pour eviter le "reset" de l'application
	@Override
    public void onBackPressed() {
        Intent backtoHome = new Intent(Intent.ACTION_MAIN);
        backtoHome.addCategory(Intent.CATEGORY_HOME);
        backtoHome.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(backtoHome);
    }
	
	
	
	//Fonction pour la bar de progr�s
	@Override
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
		progressBar.setMax(100);
		progressLive = progress;
	}

	//Fonction pour la bar de progr�s
	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		inSeeking = true;
		mHandler.postDelayed(mUpdateTimeTask, 1);
	}
	//Fonction pour la bar de progr�s
	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		mHandler.removeCallbacks(mUpdateTimeTask);
		
		int totalDuration = mp.getDuration();
        int currentDuration = (totalDuration*seekBar.getProgress())/100;
        if(currentDuration >= totalDuration-50)
        	currentDuration = totalDuration;
        mp.seekTo(currentDuration);
        mHandler.postDelayed(mUpdateTimeTask, 1);
        inSeeking = false;
        if(isPlaying == false){mHandler.removeCallbacks(mUpdateTimeTask);}
		
	}
	
	
	
	//Cr�ation du menu lorsque l'on clique sur le bouton menu du t�l�phone.
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
		return true;
	}
    public boolean onOptionsItemSelected(MenuItem item) {
        //On regarde quel item a �t� cliqu� gr�ce � son id et on d�clenche une action
        switch (item.getItemId()) {
           case R.id.action_settings:
        	   shakeTiming = true;
        	   String activer = "Cliquez pour activer l'al�aShake!";
        	   String desactiver = "Cliquez pour d�sactiver l'al�aShake!";
        	   if(aleaShake == false){
        		   aleaShake = true;
        		   item.setTitle(desactiver);
        		   Toast.makeText(MainActivity.this, "Fonction Al�aShake activ�!", Toast.LENGTH_SHORT).show();
        		   
        	   }else{
        		   aleaShake = false;
        		   item.setTitle(activer);
        		   Toast.makeText(MainActivity.this, "Fonction Al�aShake d�sactiv�! :(", Toast.LENGTH_SHORT).show();
        	   }
              return true;
              
           case R.id.action_exit:
        	   
        	   mp.stop();
        	   mp.reset();
        	   System.exit(0);
        	   //android.os.Process.killProcess(android.os.Process.myPid());
        	   return true;
        }
    return false;}
    
    
    
	//Fonction pour faire changer les chanchons avec le touch
	void handleTouch(MotionEvent m)
	    {  	
    		int x = (int) m.getX(0);  		
    		int action = m.getActionMasked();
    		
    		switch (action)
    		{
    			case MotionEvent.ACTION_DOWN:
    				oldX = (int) m.getX(0);
    				break;
    			case MotionEvent.ACTION_UP:
    				int width = displaymetrics.widthPixels;
    				width = width/2;
    				if (x > oldX+width)
    	            {
    					btnPrevious.performClick();
    	            }
    				else if (x < oldX-width)
    	            {
    					btnNext.performClick();
    	            }
    				break;	
    		}        		
	    } 
}

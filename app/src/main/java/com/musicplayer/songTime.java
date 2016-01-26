//Anthony Martel - Étienne Boisjoli
//Version 1.2
package com.musicplayer;

public class songTime {
	
	public songTime(){
		 
    }
	//Fonction qui transforme les millisecondes en texte formaté pour afficher le temps
	public String time(long time){
		String secondsString,timeString;
		
		int minutes = (int) (time % (1000*60*60)) / (1000*60);
        int seconds = (int) ((time % (1000*60*60)) % (1000*60) / 1000);

        if(seconds < 10){
        	secondsString = "0" + seconds;
        }else{
        	secondsString = "" + seconds;
        }

        timeString = minutes + ":" + secondsString;

        return timeString;
	}
	//Fonction pour transformer le temps vers le pourcentage pour la bar de progrès
	public long progressBarPercentage(long totalDuration, long currentDuration){
	       long percentage;

	        percentage = (100*currentDuration)/totalDuration;
	        return percentage;
	}
}

package me.syes.thehungergames.utils;

public class TimeUtils {

	public String getTime(double totalSeconds) {
		int hours = (int) (totalSeconds/3600);
		int minutes = (int) (totalSeconds/60 - hours*60);
		int seconds = (int) (totalSeconds - minutes*60 - hours*3600);
		if(hours > 1)
			return hours + " hours";
		else if(hours == 1)
			return hours + " hour";
		else if(minutes > 1)
			return minutes + " minutes";
		else if(minutes == 1)
			return minutes + " minute";
		else if(seconds > 1)
			return seconds + " seconds";
		else if(seconds == 1)
			return seconds + " second";
		return "ERROR";
	}

	public String getFullTime(double totalSeconds) {
		int hours = (int) (totalSeconds/3600);
		int minutes = (int) (totalSeconds/60 - hours*60);
		int seconds = (int) (totalSeconds - minutes*60 - hours*3600);
		String time = "";
		if(hours > 1)
			time = time + "" + hours + "h ";
		else if(hours == 1)
			time = time + "" + hours + "h ";
		if(minutes > 1)
			time = time + "" + minutes + "m ";
		else if(minutes == 1)
			time = time + "" + minutes + "m ";
		else if(minutes == 0 && hours > 0)
			time = time + "" + "0m";
		if(seconds > 1)
			time = time + "" + seconds + "s";
		else if(seconds == 1)
			time = time + "" + seconds + "s";
		else if(seconds == 0 && minutes > 0)
			time = time + "" + "0s";
		return time;
	}
	
}

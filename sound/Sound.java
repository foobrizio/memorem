package sound;

import java.io.*;

import sun.audio.*;

public class Sound {
	
	private static AudioPlayer player=AudioPlayer.player;
	private static AudioStream stream;
	
	public static int playRemove(){
		
		try{
			InputStream test=new FileInputStream("files//sounds//paper.wav");
			stream= new AudioStream(test);
			player.start(stream);
			return 0;
		}catch(IOException e){
			e.printStackTrace();
			System.out.println("Eccezione ioioio");
		}
		return -1;
	}
	
	public static int ding(){
		
		try{
			InputStream test=new FileInputStream("files//sounds//ding.wav");
			stream= new AudioStream(test);
			player.start(stream);
			return 0;
		}catch(IOException e){
			e.printStackTrace();
			System.out.println("Eccezione ioioio");
		}
		return -1;
	}
	
	public static int yeah(){
		
		try{
			InputStream test=new FileInputStream("files//sounds//yeah.wav");
			stream= new AudioStream(test);
			player.start(stream);
			return 0;
		}catch(IOException e){
			e.printStackTrace();
			System.out.println("Eccezione ioioio");
		}
		return -1;
	}
	
	public static int playLogin(){
		
		try{
			InputStream test=new FileInputStream("files//sounds//login.wav");
			stream= new AudioStream(test);
			player.start(stream);
			return 0;
		}catch(IOException e){
			e.printStackTrace();
			System.out.println("Eccezione ioioio");
		}
		return -1;
	}
	
	public static int playLogout(){
		
		try{
			InputStream test=new FileInputStream("files//sounds//logout.wav");
			stream= new AudioStream(test);
			player.start(stream);
			return 0;
		}catch(IOException e){
			e.printStackTrace();
			System.out.println("Eccezione ioioio");
		}
		return -1;
	}
	
	public static int playExpired(){
		
		try{
			InputStream test=new FileInputStream("files//sounds//ring.wav");
			stream= new AudioStream(test);
			player.start(stream);
			return 0;
		}catch(IOException e){
			e.printStackTrace();
			System.out.println("Eccezione ioioio");
		}
		return -1;
	}
	
	public static int playAdvice(){
		
		try{
			InputStream test=new FileInputStream("files//sounds//old_alarm.wav");
			stream= new AudioStream(test);
			player.start(stream);
			return 0;
		}catch(IOException e){
			e.printStackTrace();
			System.out.println("Eccezione ioioio");
		}
		return -1;
	}
	
	
	public static void main(String[] args){
		
		
		
		Sound.playExpired();
		//Sound.playRemove();
	}

}

package main;

import java.awt.EventQueue;
import graphic.MemoremGUI;

public class MemoRem {
	
	public static void main(String[] args){
		
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					new MemoremGUI();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

}

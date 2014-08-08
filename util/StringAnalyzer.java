package util;

import java.util.StringTokenizer;

public class StringAnalyzer {
	
	public static boolean verificaStringa(String string){
		
		StringTokenizer st=new StringTokenizer(" "+string+" ","'\\?()\"",false);
		if(st.countTokens()>1){
			return false;
		}
		else return true;
	}

}

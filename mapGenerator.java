package MapSearch;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

public class mapGenerator{
	String[] arrval = new String[10];
	
	public mapGenerator(){}
	
	public void genMap(){
		try{
			FileWriter writ = new FileWriter("Input.txt", false);
			BufferedWriter buffwrit = new BufferedWriter(writ);
			
			for (int i = 0; i < 10; i++){
				Random x = new Random();
				Random y = new Random();
				int val1 = x.nextInt(160);
				int val2 = y.nextInt(120);
				String temp = val1 + "," + val2;
				arrval[i] = temp;
				buffwrit.write(arrval[i]);
				buffwrit.newLine();
			}
			
			buffwrit.close();
			writ.close();
		}catch (IOException e){
			e.printStackTrace();
		}
	}
}
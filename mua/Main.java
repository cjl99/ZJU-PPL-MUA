package mua;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.*;

public class Main {
    public static void main(String argv[]) {
        InputStream is = null;
		try {
			is = new FileInputStream("in");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
        Scanner in=new Scanner(is);
        // Scanner in = new Scanner(System.in);
        Operation op = new Operation(true);  
        String lines = "";
        while(in.hasNext()) {
        	lines += in.nextLine() + " ";
		}
        lines = PerfectLines(lines);
        op.run(lines);
    }

    // 为了去掉连续函数
	private static String PerfectLines(String lines) {
		String newlines = "";
		lines = lines.replaceAll("\n", " ");
		lines = lines.replaceAll("\t", " ");
		lines = lines.trim();
		boolean flag = false;
		
        for (int i=0; i<lines.length(); ++i) {
            char t = lines.charAt(i);
			if (t==' ' && flag==false) {
                flag = true;
                newlines += String.valueOf(t);
            } else if (t==' ' && flag==true) {
                continue;
            } else {
                newlines += String.valueOf(t);
                flag = false;
            }
        }
		return newlines;
	}

}
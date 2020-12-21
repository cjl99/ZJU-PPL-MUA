package mua;
import java.util.HashMap;
import java.util.Set;

public class Function {
	HashMap<String, Value> local;
    String [] params = {};
    String funcjobs;
    Operation op;

    public Function(String func) {
        funcjobs = "";
        local = new HashMap<String, Value> ();
        
        int leftcnt=0, rightcnt=0;
        int sIndex=0, eIndex = 0;
        for(int i=0; i<func.length(); ++i) {
        	char chr = func.charAt(i);
        	if (chr=='[') {
        		if (leftcnt==0) sIndex = i+1;
        		leftcnt++;
        	} 
        	else if (chr==']') {
        		rightcnt++;
        		if (leftcnt==rightcnt) {
        			eIndex = i;
        			break;
        		} 
        	}
        }

        String pstr = func.substring(sIndex, eIndex);
        if (pstr.length()>0)
        	params = pstr.split(" ");
        
        leftcnt=0; rightcnt=0;
        for(int i=eIndex+1; i<func.length(); ++i) {
        	char chr = func.charAt(i);
        	if (chr=='[') {
        		if (leftcnt==0) sIndex = i+1;
        		leftcnt++;
        	} 
        	else if (chr==']') {
        		rightcnt++;
        		if (leftcnt==rightcnt) {
        			eIndex = i;
        			break;
        		}
        	}
        }
        
        funcjobs = func.substring(sIndex, eIndex);
    }

    public int GetParamSize() {
        return  params.length;
    }

    public void BuildLocal(int index, Value v){ 
        local.put(params[index], v);
    }

    public Value runfunc() {
        Variable varlocal = new Variable(local);
        op = new Operation(varlocal, false);
        return op.run(funcjobs);
    }
    
}
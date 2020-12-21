package mua;

public class Operation {
    public static Variable global = new Variable();
    public Variable local;
    public boolean isGlobal;
    
    public int index;
    public String[] strs = {};
    public String CurrentLine;   	// ??? really need???
    
    public Operation(boolean isglobal) {
    	index = 0;
    	local = new Variable();
        isGlobal = isglobal;
    }
    public Operation(Variable vlocal, boolean isglobal) {
    	index = 0;
    	local = vlocal;
        isGlobal = isglobal;
    }

    public Value run(String line) {
    	line = line.trim();
    	this.CurrentLine = line;
        index = 0;
    	strs = line.split(" ");

        Value temp = new Value();
        while(index < strs.length) {
            temp = GetOneValue();
        }

        return temp;
    }

    public Value DealWithOperator(String op) {
    	
        Value v = new Value();

        if(op.charAt(0)==':') {
            String key = op.substring(1);
            // GetValueFromKey(key).PrintValue();
            v = GetValueFromKey(key); 
            // else {
            //    System.out.println("Operator ':' don't contain the key");
            // }
        }
        else if (op.charAt(0)=='(') {
            String inorder = GetInorder();
            inorder = ReplaceForInorder(inorder);
            InorderOperation inop = new InorderOperation(inorder);
            double n = inop.runInorder();
            v = new Value(n);
        }
        else if(op.equals("print")) {
            v = GetOneValue();
            v.PrintValue();
        }
        else if (op.equals("thing")) {
            String name = GetOneValue().GetString();
            v = GetValueFromKey(name);
        } 
        else if (op.equals("make")) {
            String name = GetOneValue().GetString();
            v = GetOneValue();
            if (isGlobal) 
            	global.addItem(name, v);
            else 
            	local.addItem(name, v);
            // System.out.print("Make-Name: " + name);
            // System.out.print("Make-Value: "); v.PrintValue();
        } 
        else if (op.equals("erase")) {
            String name = GetOneValue().GetString(); 
            v = GetValueFromKey(name);
            global.removeItem(name);   
        } 
        else if (op.equals("read")) {
            v = GetOneValue();
        } 
        else if (isALUop(op)) {
            Value t1 = GetOneValue();
            Value t2 = GetOneValue();
            double n1 = t1.GetNumber();
            double n2 = t2.GetNumber();
            double n = 0;
            
            switch (op) {
            case "add": n = n1+n2; break;
            case "sub": n = n1-n2; break;
            case "mul": n = n1*n2; break;
            case "div": n = n1/n2; break;
            case "mod": n = n1%n2; break;
            }
            v = new Value(n);
        } 
        else if (isCompareop(op)) {
            Value t1 = GetOneValue();
            Value t2 = GetOneValue();
            boolean b = false;
            int tmp = 0;
            if (t1.isnum() && t2.isnum()) {
                double n1 = t1.GetNumber();
                double n2 = t2.GetNumber();
                tmp = (int)(n1-n2);
            }  
            else if (t1.isword() && t2.isword()) {
                String s1 = t1.GetWord();
                String s2 = t2.GetWord();
                tmp = s1.compareTo(s2);
            }
            switch (op) {
                case "gt": b = (tmp>0); break;
                case "lt": b = (tmp<0); break;
                case "eq": b = (tmp==0); break;
            }      
            v = new Value(b);
        } 
        else if (op.equals("not")) {
            v = GetOneValue();
            boolean b = v.GetBool();
            b = !b;        	
            v = new Value(b);
        }
        else if (op.equals("and")) {
        	Value t1 = GetOneValue();
            Value t2 = GetOneValue();
            boolean b = t1.GetBool() && t2.GetBool();
            v = new Value(b);
         }
        else if (op.equals("or")) {
        	Value t1 = GetOneValue();
            Value t2 = GetOneValue();
            boolean b = t1.GetBool() || t2.GetBool();
            v = new Value(b); 
        }
        else if (op.equals("isname")) {
            boolean b = false;
            String name = GetOneValue().GetString();
            if (isContainKey(name))
                b = true;
            v = new Value(b);
        }
        else if (isJudgeop(op)) {
            Value t = GetOneValue();
            boolean b = false;
            switch (op) {
                case "isword":      b = t.isword(); break;
                case "isnumber":    b = t.isnum();  break;
                case "isbool":      b = t.isbool(); break;
                case "islist":      b = t.islist(); break;
                case "isempty":     b = t.GetString().length()==0;  break;
            }
            v = new Value(b);
        }
        else if (op.equals("run")) {
            v = GetOneValue();
            Operation tmpop = new Operation(this.local, false);
            String rline = v.GetList();
            v = tmpop.run(rline);
        }
        else if (op.equals("if")) {
            Value t1 = GetOneValue();
            Value t2 = GetOneValue();
            Value t3 = GetOneValue();
            Operation tmpop = new Operation(this.local, false);
            if (t1.GetBool()) {  
                v = tmpop.run(t2.GetList());
            } else {
                v = tmpop.run(t3.GetList());
            }
        }
        else if (op.equals("return")) {
            v = GetOneValue();
        }
        else if (op.equals("export")) {
            v = GetOneValue();
            String name = v.GetString(); 
            // System.out.println(name);
            if(global.isContain(name)) {	// replace
            	//local.getValue(name).PrintValue();
            	global.replaceItem(name, global.getValue(name), local.getValue(name));
            	
            } 
            else {	// add
            	global.addItem(name, local.getValue(name));
            }
            // System.out.println("EXPORT: " + name);    
        }
        else if (isFunction(op)) {
            Value t1 = GetValueFromKey(op);
            String func = t1.GetList();   
            Function f = new Function(func);
            int psize = f.GetParamSize();  
            for(int s=0; s<psize; ++s) {
                f.BuildLocal(s, GetOneValue());
            }
            // System.out.println("Function-Name: " + op);
            // System.out.println("Function-Body: " + func);
            // System.out.println("Func-Psize: " + psize);
            v = f.runfunc();    
        }

        return v;
    }

    private String GetInorder() {
    	String inorder = "";
    	int leftcnt=0, rightcnt=0;
    	int nextIndex = 0;
    	boolean breakflag = false;
    	for(int i=this.index-1; i<strs.length; ++i) {
    		String str = strs[i];
    		for(int j=0; j<str.length(); ++j) {
    			char chr = str.charAt(j);
    			if(chr=='(') {
					leftcnt++;
    			}
    			else if (chr==')') {
    				rightcnt++;
    				if (leftcnt==rightcnt) {
    					breakflag = true;
    					break;
    				}
    			}
    		}
    		if (breakflag==true) {
    			nextIndex = i+1;
    			break;
    		}
    	}
    	
    	for (int i=index-1; i<nextIndex; ++i) {
    		if (i==(nextIndex-1)) {
    			inorder += strs[i];
    		} 
    		else {
    			inorder += strs[i] + " ";
    		}
    	}
    	
    	index = nextIndex;
    	
		return inorder;
	}
    
	private boolean isContainKey(String key) {
        if (local.isContain(key))
            return true;
    	else if (global.isContain(key)) 
            return true;
		return false;
	}
        
	public Value GetOneValue() {
        Value v = new Value();
        String tmp = strs[index++];
        if (tmp.length()==0) return v;
        // tmp is an Operator
        if (isOperator(tmp)) {
            v = DealWithOperator(tmp);
        } 
        // tmp is an Value
        else {
            v.fv = tmp;
            
            if(v.isnum()) {
                v = new Value(Double.parseDouble(tmp));
            }
            else if (v.isbool()) {
                v = new Value(Boolean.parseBoolean(tmp));
            }
            else if (v.islist()) {
                index--;
                int cnt1=0, cnt2=0;
                boolean neq_flag = true;
                String l = "", s = "";
                while(neq_flag==true) {
                	s = strs[index];
                    l += s + " ";
                    for(int i=0; i<s.length(); ++i) {
                        char chr = s.charAt(i);
                        if (chr=='[') {
                            cnt1++;
                        } else if (chr==']') {
                            cnt2++;
                        }
                        if (cnt1!=0 && cnt1==cnt2) {
                            neq_flag = false;
                            break;
                        }
                    }
                    index++;
                }
                l = l.substring(0, l.length()-1);
                // System.out.println("LIST: " + l);
                v = new Value(l);
            }
            else if (tmp.charAt(0)=='"') {
                v = new Value(tmp.substring(1));
            }
            else if (v.isword()) {
                v = new Value(tmp);
            }
         // System.out.println("Get One Value: "+tmp);
        }
        
        return v;
    }

//    public String GetOneName(boolean hasQ) {
//       String n = strs[index++];
//       if (hasQ==true) {
//           n = n.substring(1);
//       }
//        // if (n.charAt(0) == '"') {
//        //     n = n.substring(1);
//        // } 
//        // else {
//        //     System.out.println("The local's name is invalid!")
//        // }
//        return n;
//    }


    public boolean isOperator(String op) {
        if (op.equals(" ") || op.length()==0) return false;
        if (op.equals("print") || op.equals("make") || op.equals("thing") 
            || op.equals("q")
            || op.charAt(0)==':'
            || op.charAt(0) == '('  // operator ( 
            || op.equals("read")
            || isALUop(op) || isLogicop(op) || isCompareop(op) || isJudgeop(op)
            || op.equals("isname")
            || op.equals("erase")
            || op.equals("run")
            || op.equals("if")
            || isFunction(op)
            || op.equals("return")
            || op.equals("export")
        ) {
            return true;
        }
        return false;
    }
    public boolean isFunction(String op) {
        if (isContainKey(op)) {
            Value tmp = GetValueFromKey(op);
            if (tmp.islist()) {
                return true;
            }
        }
        return false;
    }

    public boolean isJudgeop(String op) {
        if (op.equals("isnumber") || op.equals("isword") || op.equals("islist") 
            || op.equals("isbool") || op.equals("isempty")) {
            return true;
        }
        return false;
    }

    public boolean isALUop(String op) {
        if (op.equals("add") || op.equals("sub") || op.equals("mul") || op.equals("mod") || op.equals("div") )
            return true;
        return false;
    }

    public boolean isLogicop(String op) {
        if (op.equals("and") || op.equals("or") || op.equals("not")) {
            return true;
        }
        return false;
    }

    public boolean isCompareop(String op) {
        if (op.equals("gt") || op.equals("lt") || op.equals("eq")) {
            return true;
        }
        return false;
    }

    private Value GetValueFromKey(String key) {
    	if (local.isContain(key)) 
    		return local.getValue(key);
    	else if (global.isContain(key)) 
            return global.getValue(key);
        
        return null;    	
    }
    

    public String ReplaceForInorder(String Expression) {
        // first replace all ':'
        for (int i=0; i<Expression.length(); ++i) {
            if (Expression.charAt(i)==':') { 
                //find next operator
                int j = i + 1;
                int namebegin = j;
                for (; j<Expression.length(); ++j) {
                    char tmp = Expression.charAt(j);
                    if(tmp==' ' || tmp=='*' || tmp=='+' || tmp=='-' || tmp=='/' || tmp=='%' || tmp==')') {
                        break;
                    } 
                }
                int nameend = j;
                Value t = GetValueFromKey(Expression.substring(namebegin, nameend));
                // t should be Value_number here
                // ReplaceName
                Expression = Expression.substring(0, namebegin-1) + t.fv + Expression.substring(nameend);
                i = j-1;
            }
        }
        // System.out.println(Expression);
        return Expression;
    }

}

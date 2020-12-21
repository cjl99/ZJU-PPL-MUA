package mua;

import java.util.Stack;

public class InorderOperation {
    private Stack<Double> numStack;
    private Stack<Character> opStack;
    private String Express;

    public InorderOperation(String str) {
        Express = str;
        numStack = new Stack<Double>();
        opStack = new Stack<Character>();        
    }

    public double runInorder() {
        boolean negFlag = true;
        boolean opFlag = false;
        // input Express
        for (int i=0; i<Express.length(); ++i) {
            char tmp = Express.charAt(i);
            if (tmp==' ') {
                // Ignore
            } else if (isNumCharacter(tmp)) { // is Number
                // get a double
                int j=i+1;
                for (; j<Express.length(); j++) {
                    if (!isNumCharacter(Express.charAt(j))) 
                        break;
                }
                String number = Express.substring(i, j);
                Double n = Double.parseDouble(number);
                i = j-1;
                if (negFlag==false) {
                    numStack.push(-1*n);
                } else {
                    numStack.push(n);
                }
                opFlag = false;
                negFlag = true;
            } else if (isOpCharacter(tmp)) {
                if (tmp=='+' || tmp=='-') {
                    if (opFlag==true) {
                        negFlag = tmp=='-' ? !negFlag : negFlag;
                    } 
                    else {
                        while (!opStack.isEmpty() && opStack.peek()!='(') {
                                operate();
                            }
                            opStack.push(tmp);
                        opFlag = true;
                    }
                } else if (tmp=='*' || tmp=='/' || tmp=='%') {
                    while (!opStack.isEmpty() && opStack.peek()!='(' &&
                            opStack.peek()!='+' && opStack.peek()!='-') {
                        operate();
                    }
                    opStack.push(tmp); 
                    opFlag = true;                   
                }
            } else if (tmp=='(') {
                opStack.push(tmp);
            } else if (tmp==')') {
                while (!opStack.isEmpty() && opStack.peek()!='(') {
                    operate();
                }
                opStack.pop();                
            } else {
                // need to do PreOrder Operation
                // find next ) and record its index
                int cnt = 1;
                int j=i+1;
                for (; j<Express.length(); ++j) {
                    char t = Express.charAt(j);
                    if (t=='(') cnt++;
                    else if (t==')' || t=='*') {
                        cnt--;
                        if (cnt==0) break; 
                    }
                }
                Value v = new Value();
                Operation op = new Operation(false);
                // System.out.println("INORDER: " + Express.substring(i, j));
                v = op.run(Express.substring(i, j));
                Double n = v.GetNumber();
                numStack.push(n);   
                i = j-1;             
            }
        }

        // no ( )
        while (!opStack.isEmpty()) {
            operate();
        }


        return numStack.peek();
    }


    private boolean isNumCharacter(char a) {
        return ((a>='0' && a<='9') || a=='.');
    }

    private boolean isOpCharacter(char a) {
        return (a=='+' || a=='-' || a=='*' || a=='/' || a=='%');
    }

    private void operate() {
        double num2 = numStack.pop();
        double num1 = numStack.pop();
        char op = opStack.pop();
        double res = 0;
                   
        switch (op) {
            case '+': res = num1+num2; break;
            case '-': res = num1-num2; break;
            case '*': res = num1*num2; break;
            case '/': res = num1/num2; break;
            case '%': res = num1%num2; break;
        }
        numStack.push(res);
        // System.out.println(numStack.peek());
    }


}
package mua;

public class Value {
    public String fv;
	private double number;
	private boolean bool;
	private String list;
	private String word;
	// public boolean isNumber;
	// print according to type
	public Value() {
		fv = "";
		number = 0;
		bool = false;
		list = "";
		word = "";
	}

	public Value(double num) {
		this.number = num;
		this.fv = String.valueOf(num);
	}

	public Value(boolean b) {
		this.bool = b;
		this.fv = String.valueOf(b);
	}

	public Value (String l) {
		this.list = l;
		this.word = l;
		this.fv = l;
	}

	public boolean isnum() {
        int k = 0;
        // not a strict way to determine a number ****** may be improved later.
		for(k=0; k<fv.length(); ++k) {
			if((fv.charAt(k)>='0' && fv.charAt(k)<='9') || fv.charAt(k)=='-'  || fv.charAt(k)=='.') {
				continue;
			} else {
				break;
			}
		}
		if(k==fv.length()) return true;
		return false;
	}

	public boolean isbool() {
		if (fv.equals("true") || fv.equals("false")) {
			return true;
		}
		return false;
	}

	public boolean isword() {
		if (this.isbool() || this.isnum() || this.islist()) {
			return false;
		}
		return true;
	}

	public boolean islist() {
		
        if (this.fv.charAt(0)=='[') {
            return true;
        }
        return false;
	}

	public double GetNumber() {
		if (this.isnum()) {
			return this.number;
		} else {
			System.out.println("The Type of Value is not Number!");
		}
		return -1;
	}

	public String GetWord() {
		if (this.isword()) {
			return this.word;
		} else {
			System.out.println("The Type of Value is not Word!");
		}
		return "";
	}

	public String GetList() {
		if (this.islist()) {
			
			// System.out.println("GetList: " + this.list.substring(1, this.list.length()-2));
			return this.list.substring(1, this.list.length()-1);
		} else {
			System.out.println("The Type of Value is not List!");
		}
		return "";
	}

	public boolean GetBool() {
		if (this.isbool()) {
			return this.bool;
		} else {
			System.out.println("The Type of Value is not Bool!");
		}
		return false;
	}

	public void PrintValue() {
		System.out.println(fv);
	}

	// to string value
	public String GetString() {
		return fv;	
	}

}

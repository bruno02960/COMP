package yal2jvm.ast;

public class ErrorCounter {
	int noErrors = 0;
	
	public ErrorCounter() {
		
	}
	
	public boolean errorControl() {
		noErrors++;
		
		if(noErrors == 10)
			return true;
		
		return false;
	}

	public int getNoErrors() {
		return noErrors;
	}
}

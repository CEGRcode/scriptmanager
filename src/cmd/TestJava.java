public class TestJava{

	public static void main( String args[] ){
		System.out.println("TestJava...");
		
		boolean r1 = true;
        boolean r2 = false;
        boolean mid = false;
        boolean comb = false;
        int STRAND = -1;
        
        
        if(r1) { STRAND = 0; }
		else if(r2) { STRAND = 1; }
		else if(mid) { STRAND = 2; }
		else if(comb) { STRAND = 3; }
		
		if(!r2){
			System.out.println("blah");
		}		
	}

}

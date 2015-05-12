package hunkann;

class DatabaseException extends Exception{
	public  DatabaseException(int transactionID,int queryID,String message){
		super(String.format("(t%d, q%d) %s",transactionID,queryID,message));
	}
}

public class TestStringFormat {
//	public static void main(String[] args) {
//		try{
//			throw new DatabaseException(3,7,"WriteFailed");
//		}catch( Exception e){
//			System.out.println(e);
//		}
//	}
	public static void main(String[] args) {
		System.out.println(String.format("%d\t%s\t%s\t%s", 1,"hello","world","fuckyou"));
	}
}

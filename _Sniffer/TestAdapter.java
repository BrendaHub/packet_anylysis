package hunkann;

interface Inter{
	public abstract void func1();
	public abstract void func2();
}

class InterAdapter implements Inter{
	@Override
	public void func1() {
		// TODO Auto-generated method stub
	}
	@Override
	public void func2() {
		// TODO Auto-generated method stub
	}
}

class UseAdapter extends InterAdapter{
	public void func1(){
		System.out.println("hello world");
	}
}

public class TestAdapter {
	public static void main(String[] args) {
		UseAdapter instance = new UseAdapter();
		instance.func1();
	}
}

package TetrisState;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Tetris implements Runnable{
	
	public boolean running = false;
	
	public Tetris(){
		Thread thread = new Thread(this);
		thread.start();
	}
	
	public static void main(String[] args) {
		List<Tetris> tetrisList = new ArrayList<Tetris>();
		
		System.out.println("main");
		
		Date start = new Date();
		
		for(int i=0; i<5; i++){
			tetrisList.add(new Tetris());
		}
		
		for(Tetris tetris : tetrisList){
			while(tetris.running){
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		
		Date end = new Date();
		
		long difference = end.getTime() - start.getTime();
		
		System.out.println ("This whole process took: " + difference/1000 + " seconds.");
	}
	
	@Override
	public void run(){
		this.running = true;
		
		System.out.println("Thread " + Thread.currentThread().getId() );
		
		try{
			Thread.sleep(5000);
		}catch(InterruptedException e){
			Thread.currentThread().interrupt();
		}
		this.running = false;
	}
}

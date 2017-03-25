package TetrisState;
import java.util.Arrays;
import java.util.concurrent.Semaphore;

public class MultiThread extends Thread {
	
	public boolean running = false;
	
	PlayerSkeleton p ;
	ParticleSwarm ps;

	int maxRowCleared, maxTurn, maxIndex, foundIter;
	double maxHeuristic[];
	
	int maxRowClearedPerRound;
    int maxTurnPerRound;
    int maxIndexPerRound;
    
    int i;
    int iter;
    double numOfHeuristic, inertia, localAcceleration, globalAcceleration, maxVelocity;
	
    
    
	MultiThread(int i, PlayerSkeleton p, ParticleSwarm ps, int maxRowCleared, int maxTurn, int maxIndex, int foundIter, 
			double maxHeuristic[], int maxRowClearedPerRound, int maxTurnPerRound, int maxIndexPerRound, int iter,
			int numOfHeuristic, double inertia, double localAcceleration, double globalAcceleration, double maxVelocity){
		
		this.i = i;
		this.p = p;
		this.ps = ps;
		this.maxRowCleared = maxRowCleared;
		this.maxTurn = maxTurn;
		this.maxIndex = maxIndex;
		this.foundIter = foundIter;
		this.maxHeuristic = maxHeuristic;
		this.maxRowClearedPerRound = maxRowClearedPerRound;
		this.maxTurnPerRound = maxTurnPerRound;
		this.maxIndexPerRound = maxIndexPerRound;
		this.iter = iter;
		this.numOfHeuristic = numOfHeuristic;
		this.inertia = inertia;
		this.localAcceleration = localAcceleration;
		this.globalAcceleration = globalAcceleration;
		this.maxVelocity = maxVelocity;
		
		System.out.println("Setting Particle Id: " + i);
	}
	
	static Semaphore semaphore = new Semaphore(1);

	public void run(){
	
		System.out.println("Particle Id: " + i);
		
		//MultiThread Here
	    boolean changed = false;
	    
        //TFrame t = new TFrame(s);
	    double[] weights = ps.getHeuristics(i);

	    TetrisState.State s = new TetrisState.State();
	    
		while(!s.hasLost()) {
			s.makeMove(p.pickMove(s,s.legalMoves(), weights));
		}
		//System.out.println("You have completed "+s.getRowsCleared()+" rows.");
		//
		//semaphore HERE
		System.out.println("Particle" + i + " : acquiring lock...");
		System.out.println("Particle" + i + " : available Semaphore permits now: " + semaphore.availablePermits());
		ps.setReward(i, new double[]{s.getRowsCleared(), s.getTurnNumber()});
		/*
		try {
			System.out.println("Particle" + i + " : acquiring lock...");
			System.out.println("Particle" + i + " : available Semaphore permits now: " + semaphore.availablePermits());
			
			semaphore.acquire();
			System.out.println("Particle" + i + " : got the permit!");
			try {
				System.out.println("Particle" + i + " : is setting reward" + ", available Semaphore permits : " + semaphore.availablePermits());
				ps.setReward(i, new double[]{s.getRowsCleared(), s.getTurnNumber()});
			}finally {
				System.out.println("Particle" + i + " : releasing lock...");
				semaphore.release();
				System.out.println("Particle" + i  + " : available Semaphore permits now: " + semaphore.availablePermits());
			}
			
		}catch (InterruptedException e) {
			e.printStackTrace();
		}
		*/
		            		
		if (maxRowCleared < s.getRowsCleared()) {
		    changed = true;
		    maxRowCleared = s.getRowsCleared();
		    maxTurn = s.getTurnNumber();
		    maxIndex = i;
		    foundIter = iter;
		    maxHeuristic = Arrays.copyOf(ps.getHeuristics(i), ps.getHeuristics(i).length);
		}
		
		if (maxRowClearedPerRound < s.getRowsCleared()) {                        
		    maxRowClearedPerRound = s.getRowsCleared();
            maxTurnPerRound = s.getTurnNumber();
            maxIndexPerRound = i;
        }
		
		ps.moveAllParticles(inertia, localAcceleration, globalAcceleration, maxVelocity);
		
		
		if (changed) {
		    System.out.println("Changed Heuristic of max:");
            for (int k = 0; k < numOfHeuristic; k++) {
                System.out.print(ps.getHeuristics(maxIndex)[k] + ", ");
            }
            System.out.println();
		}
		
		
	
	}
}

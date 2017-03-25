package TetrisState;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;



public class PlayerSkeleton {
    
    private final static double maxIter = 1;//1000;
    private final static double maxRoundPerIter = 2;//20;
    
    private final static int numOfHeuristic = 4;
    private final static int numOfParticle = 100;
    private final static int numOfReward = 2;
    
    private final static double inertia = 0.9;
    private final static double localAcceleration = 2;
    private final static double globalAcceleration = 2;
    private final static double range = 20;
    private final static double maxVelocity = range / 100;
    

    
	//implement this function to have a working system
	public int pickMove(State s, int[][] legalMoves, double[] weights) {
        double hole = weights[0];
        double edge = weights[1];
        double well  = weights[2];
        double landingHeight = weights[3];
        
	    int move = 0;
	    double fn = -Double.MAX_VALUE;
	    ProjectedState nextState;
		for (int i = 0; i < legalMoves.length; i++) {
		    nextState = new ProjectedState(s);
		    nextState.makeMove(i);
		    
		    double curFn = hole * nextState.getNumberOfHoles()
		                    + edge * nextState.getTouchingEdges()
		                    + well * nextState.getWells()
		                    + landingHeight * nextState.getLandingHeight();
		    
		    if (fn < curFn) {
		        fn = curFn;
		        move = i;
		    }
		}
		return move;
	}
	
	public static void main(String[] args) {
		PlayerSkeleton p = new PlayerSkeleton();
		ParticleSwarm ps = new ParticleSwarm(numOfParticle, numOfHeuristic, numOfReward, range);
		
		int maxRowCleared = 0, maxTurn = 0, maxIndex = 0, foundIter = 0;
		double maxHeuristic[] = new double[numOfHeuristic];
		
		for (int iter = 0; iter < maxIter; iter++) {
    		for (int round = 0; round < maxRoundPerIter; round++) {
    		    int maxRowClearedPerRound = 0;
                int maxTurnPerRound = 0;
                int maxIndexPerRound = 0;
                
                //MultiThread Here
                //semaphore for global best 
                //ParticleSwarm.java setReward() line 119 
                List<MultiThread> threadList = new ArrayList<MultiThread>();
                
        		for (int i = 0; i < numOfParticle; i++) {
        			System.out.println("PlayerSkeleton.java, num of particle: " + i);
        			
        			MultiThread multiThread = new MultiThread(i, p, ps, maxRowCleared, maxTurn, maxIndex, foundIter, maxHeuristic, 
        					maxRowClearedPerRound, maxTurnPerRound, maxIndexPerRound, maxIndexPerRound, iter,
        					inertia, localAcceleration, globalAcceleration, maxVelocity);
        					
        			//MultiThread multiThread = new MultiThread(i);
        			multiThread.start();
        			threadList.add(multiThread);
        		}
        		
        		for(MultiThread thread : threadList){
        			try {
						thread.join();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
        		}
        		
        		System.out.println();
        		System.out.println("######################################################################");
        		System.out.println("END OF MULTITHREADING");
        		System.out.println("######################################################################");
        		
        		System.out.println();
        		System.out.println("Max of Iteration " + iter + ", Round " + round + "-----");
        		System.out.println("Local Max: "+ ps.getReward(maxIndexPerRound)[0]);
        		System.out.println("Max Turn: "+ maxTurnPerRound + " turns.");
        		System.out.println("Max Row Cleared: "+ maxRowClearedPerRound +" rows.");
        		System.out.println("Found By Particle "+ maxIndexPerRound);
        		/*
        		try {
        			Thread.sleep(100);
        		}catch (InterruptedException e) {
        			e.printStackTrace();
        		}
        		*/
        	
       		}
    		
    		System.out.println();
    		System.out.println("Max of Iteration " + iter + "---------------------");
    		System.out.println("Max Turn: "+ maxTurn + " turns.");
            System.out.println("Max Row Cleared: "+ maxRowCleared +" rows.");
            System.out.println("Found By Particle "+ maxIndex + " in iter " + foundIter);
            for (int i = 0; i < numOfHeuristic; i++) {
                System.out.println(maxHeuristic[i]);
            }
		}
		System.out.println();
		System.out.println("Max found---------------------------------------------");
        System.out.println("Max Turn: "+ maxTurn + " turns.");
        System.out.println("Max Row Cleared: "+ maxRowCleared +" rows.");
        System.out.println("Found By Particle "+ maxIndex + " in iter " + foundIter);
		for (int i = 0; i < numOfHeuristic; i++) {
		    System.out.println(maxHeuristic[i]);
		}
	
	}
	
}



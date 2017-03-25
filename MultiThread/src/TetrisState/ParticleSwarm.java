package TetrisState;
import java.util.Arrays;
import java.util.Random;

public class ParticleSwarm {
    Random random = new Random();
    
    public class Fitness implements Comparable<Fitness>{
        private double fitnesses[];
        
        public Fitness(int numOfReward) {
            fitnesses = new double[numOfReward];
        }
        
        public Fitness(double rewards[]) {
            fitnesses = rewards;
        }
        
        public void setFitness(double newFitness[]) {
            fitnesses = newFitness;
        }
        
        public double[] getFitness() {
            return fitnesses;
        }

        @Override
        public int compareTo(Fitness fitnessObj) {
            for (int i = 0; i < fitnesses.length; i++) {
                if (fitnesses[i] > fitnessObj.getFitness()[i]) {
                    return 1;
                } else if (fitnesses[i] < fitnessObj.getFitness()[i]) {
                    return -1;
                }
            }
            return 0;
        }
    }
    
    public class Particle {  
        private double position[];
        private double velocity[];
        private double localBest[];
        private Fitness localBestFitness;
        
        public Particle(int numOfHeuristic, int numOfReward, double range) {            
            position = new double[numOfHeuristic];
            /*
            for (int i = 0; i < position.length; i++) {
                position[i] = random.nextDouble() * 20 -10;
            }
            */
            position[0] = -Math.random() * (range / 2);
            position[1] = Math.random() * (range / 2);
            position[2] = -Math.random() * (range / 2);
            position[3] = -Math.random() * (range / 2);
            
            velocity = new double[numOfHeuristic];
            localBest = Arrays.copyOf(position, position.length);
            localBestFitness = new Fitness(numOfReward);
        }
        
        public void setPosition(double[] weights) {
            position = weights;
        }
        
        public void setVelocity(double[] changes) {
            velocity = changes;
        }
        
        public void setLocalBest(Fitness newFitness) {
            localBest = Arrays.copyOf(position, position.length);
            localBestFitness = newFitness;
        }
        
        public void move() {
            for (int i = 0; i < position.length; i++) {
                position[i] += velocity[i];
            }
        }
        
        public double[] getPosition() {
            return position;
        }
        
        public double[] getVelocity() {
            return velocity;
        }
        
        public double[] getLocalBest() {
            return localBest;
        }
        
        public Fitness getLocalBestFitness() {
            return localBestFitness;
        }
    }
    
    int dimension;
    
    private double globalBest[];
    private Fitness globalBestFitness;
    private Particle particles[];
    
    public ParticleSwarm(int numOfParticle, int numOfHeuristic, int numOfReward, double range) {
        dimension = numOfHeuristic;
        
        particles = new Particle[numOfParticle];
        for (int i = 0; i < particles.length; i++) {
            particles[i] = new Particle(numOfHeuristic, numOfReward, range);
        }
        globalBest = new double[numOfHeuristic];
        globalBestFitness = new Fitness(numOfReward);
    }
    
    public double[] getHeuristics(int particleIndex) {
        return particles[particleIndex].getPosition();
    }
    
    public void setReward(int particleIndex, double[] newReward) {
        Fitness newFitness = new Fitness(newReward);
        if (newFitness.compareTo(globalBestFitness) > 0) {
            globalBestFitness = newFitness;
            globalBest = Arrays.copyOf(particles[particleIndex].getPosition(), particles[particleIndex].getPosition().length);
            
            System.out.println();
            System.out.println("new Global Max: ");
            System.out.println("Heuristic: ");
            for (int i = 0; i < globalBest.length; i++) {
                System.out.print(globalBest[i] + ", ");
            }
            System.out.println();
            System.out.println("Fitness: ");
            for (int i = 0; i < globalBestFitness.getFitness().length; i++) {
                System.out.print(globalBestFitness.getFitness()[i] + ", ");
            }
            System.out.println();
            System.out.println("Found By Particle: " + particleIndex);
        }
        
        if (newFitness.compareTo(particles[particleIndex].getLocalBestFitness()) > 0) {
            particles[particleIndex].setLocalBest(newFitness);
        }
    }
    
    public void moveAllParticles(double inertia, double localAcceleration, double globalAccleration, double maxVelocity) {
        for (int i = 0; i < particles.length; i++) {
            double probOflocalBest = random.nextDouble();
            double probOfGlobalBest = random.nextDouble();

            double newVelocity[] = new double[dimension];
            for (int j = 0; j < dimension; j++) {
                //System.out.println("Old Velocity: " + particles[i].getVelocity()[j]);
                newVelocity[j] = inertia * particles[i].getVelocity()[j] +
                                    probOflocalBest * localAcceleration *
                                    (particles[i].getLocalBest()[j] - particles[i].getPosition()[j]) +
                                    probOfGlobalBest * globalAccleration *
                                    (globalBest[j] - particles[i].getPosition()[j]);
                
                if (newVelocity[j] > maxVelocity) {
                    newVelocity[j] = maxVelocity;
                } else if (newVelocity[j] < -maxVelocity) {
                    newVelocity[j] = -maxVelocity;
                }
                //System.out.println("New Velocity: " + newVelocity[j]);
                //System.out.println();
                }
            
            particles[i].setVelocity(newVelocity);
            particles[i].move();
        }
    }
    
    public double[] getReward(int particleIndex) {
        return particles[particleIndex].getLocalBestFitness().getFitness();
    }
}

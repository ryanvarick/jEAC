/*
Copyright 2006 by Sean Luke
Licensed under the Academic Free License version 3.0
See the file "LICENSE" for more information
*/


package edu.indiana.cs.ga.snakeEvolver;
import ec.*;
import ec.simple.*;
import ec.vector.*;

public class SnakeEvolver2 extends Problem implements SimpleProblemForm
{
	private static int count = 0;
	
	
    public void evaluate(final EvolutionState state, final Individual ind, final int threadnum)
    {
    	
    	System.out.println("Hello! " + ++count);
    	
    	
        if(ind.evaluated) return;

        if(!(ind instanceof BitVectorIndividual))
            state.output.fatal("Whoa!  It's not a BitVectorIndividual!!!",null);
        
        int sum=0;
        BitVectorIndividual ind2 = (BitVectorIndividual)ind;
        
        for(int x=0; x<ind2.genome.length; x++)
            sum += (ind2.genome[x] ? 1 : 0);
        
        if (!(ind2.fitness instanceof SimpleFitness))
            state.output.fatal("Whoa!  It's not a SimpleFitness!!!",null);
        ((SimpleFitness)ind2.fitness).setFitness(state,
                                                 /// ...the fitness...
                                                 (float)(((double)sum)/ind2.genome.length),
                                                 ///... is the individual ideal?  Indicate here...
                                                 sum == ind2.genome.length);
        ind2.evaluated = true;
    }
    
    
    
    
    
    
    
    
    
    
    
    public void describe(final Individual ind, final EvolutionState state, final int threadnum, final int log, final int verbosity)
    {
    }
    
}
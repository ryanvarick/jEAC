/*
 * This file is part of jEAC (http://jeac.sf.net/).
 * 
 * Copyright (C) 2006.  All rights reserved.
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation; either version 2 of
 * the License, or (at your option) any later version.
 * 
 */

package edu.indiana.cs.ga.snakeEvolver;

import java.awt.*;
import javax.swing.*;

import org.jgap.*;
import org.jgap.impl.*;

import edu.indiana.cs.eac.hardware.*;
import edu.indiana.cs.ga.*;

/**
 * Details go here.
 * 
 * @author   Ryan R. Varick
 * @since    1.2.0
 * 
 */
public class SnakeEvolver extends EacEvolverFitnessFunction
{
	// old stuff
	public static int num_chromosomes = 0;
	public static int total_num_chromosomes = 0;
	private USBuEACDriver driver;
	private Object[] off, inputs, sinks, llas, llaFxs;
	private static final int MAX_LIFESPAN_SECONDS = 60;
	private static final int MAX_LLAS = 8;
	private double MAX_FITNESS = 100000;
	
	/* fitness function configuration */
	private static final int MAX_TIME_MS = 50000;
	
	/* evolver configuration */
	private static final boolean USE_EAC     = false;
	private static final int POPULATION_SIZE = 10;
	private static final int NUM_GENERATIONS = 3;
	private int chromosomeLength = 25;
	
	/* appearance */
	private static final boolean LOAD_NATIVE_LAF = false;
	private static final String  WINDOW_TITLE    = "Evaluating candidate...";
	
	/* game variables (do not alter) */
	private Game   game;
	private JFrame gameWindow;
	private JLabel foodEaten            = new JLabel(); 
	private JLabel timeLeft             = new JLabel();
	private JLabel foodLocation         = new JLabel();
	private JLabel snakeLocation        = new JLabel();
	private JLabel absoluteFoodDistance = new JLabel();
	private JLabel relativeFoodDistance = new JLabel();
	private JLabel fitness              = new JLabel();
	private JLabel snakeDirection       = new JLabel();
	

	
	/* -------------------------[ Generic class methods ]------------------------- */

	/**
	 * Returns a new <code>SnakeEvolver</code> instance.
	 * 
	 * @author   Ryan R. Varick
	 * @since    1.2.0
	 * 
	 */
	public SnakeEvolver() throws Exception
	{
		/*
		 * This class might be used as part of a larger GUI someday, so we
		 * want to take care not to stomp on the UI settings.
		 * 
		 */
		if(LOAD_NATIVE_LAF)
		{
			try 
			{
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	}
	

	/**
	 * Tries to evolve snakes. :-)
	 * 
	 * @param args         Command line arguments are ignored.
	 * @throws Exception   Exceptions are passed to the console, untouched.
	 *
	 * @author             Ryan R. Varick
	 * @since              1.2.0
	 * 
	 */
	public static void main(String[] args) throws Exception
	{
//		SnakeEvolver se = new SnakeEvolver();
//		se.init();
		
		
		/*
 		 * -------------------------[ Fitness function ]-------------------------
 		 * 
		 * Configure the fitness function.
		 * 
		 * The fitness function assesses the viability of individaul EAC
		 * configurations.  Whereas evolution is a generic process, the fitness
		 * function is specific to the problem at hand.  This class extends the
		 * basic EacEvolverFitnessFunction class, which provides a number of
		 * convenience methods; however, the details of evaluation must be 
		 * written from scratch.  This should happen in the evaluate() method.
		 * 
		 * We only want to tweak the parameters of the fitness function here.
		 * 
		 */
//		SnakeEvolver fitnessFunction  = new SnakeEvolver();
//		fitnessFunction.setMaxRunningTimeMs(MAX_TIME_MS);
		
		
		
		/*
  		 * -------------------------[ Evolution ]-------------------------
  		 * 
		 * Configure the evolver.
		 * 
		 * The evolver oversees evolution.  While the fitness function must
		 * be customized to each problem, the mechanics of evolution are much
		 * more general.  As a result, there are a number of parameters that
		 * can be adjusted to fine-tune the process.  This functionality is
		 * provided by the EacEvolver class, which tries to abstract both JGAP
		 * and jEAC functionality under a single API.
		 * 
		 * In general, the code should be sufficient as written.  If, for some
		 * reason, we need to modify the code, then it would probably be best to
		 * create a separate class that extends EacEvolver and override the code
		 * there.  We should only tweak evolutionary parameters here.
		 * 
		 */
//		EacEvolver evolver = new EacEvolver();
//		evolver.setPopulationSize(POPULATION_SIZE);
//		evolver.setNumGenerations(NUM_GENERATIONS);

		// specify whether the evolver should use a live EAC or not
//		evolver.setUseLiveEac(USE_EAC);
		
		// add the fitness function that we defined above
//		evolver.setFitnessFunction(fitnessFunction);
		
		
		
		/*
  		 * -------------------------[ Chromosome ]-------------------------
  		 * 
		 * Configure the gene layout.
		 * 
		 * The gene layout specifies the encoding of the EAC configuration,
		 * referred as the "sample configuration."  I prefer the term "genome,"
		 * but anyway.  The sample chromosome codifies the representation of 
		 * the EAC configuration (the genotype).
		 * 
		 * Configurations can be represented a number of different ways.  It 
		 * is not clear which method is optimal yet.  For this example, we will
		 * allocate one gene to each connection on the EAC.  Each gene will 
		 * code for either OFF, SOURCE, SINK, or LLA_IN.
		 * 
		 * TODO: More about the specifics of this.
		 * 
		 */

		/*
		 * One side-effect of JGAP's flexibility is that it is freaking 
		 * complicated.  To instantiate new genes, a valid configuration must
		 * be passed to the constructor (to allow for custom random number
		 * generators, etc).  Before we specify the gene layout, we need to
		 * grab the configuration.  Le sigh. :-/  
		 * 
		 */
//		Configuration configuration = evolver.getConfiguration();
//
//		Gene[] geneLayout = new Gene[evolver.getChromosomeLength()];
//		for(int i = 0; i < geneLayout.length; i++)
//		{
//			// The gene and each of its pieces
//			CompositeGene gene  = new CompositeGene(configuration);
//			IntegerGene type    = new IntegerGene(configuration, 0, 2);
//			IntegerGene payload = new IntegerGene(configuration, 1, 27); // ignored for all but LLA_IN
//
//			gene.addGene(type);
//			gene.addGene(payload);
//			
//			IntegerGene gene = new IntegerGene(configuration, 1, 27);
//			
//			geneLayout[i] = gene;
//		}
//		evolver.setGeneLayout(geneLayout);
		
		
		
		
		/*
  		 * -------------------------[ Run the evolver! ]-------------------------
  		 * 
		 * EacEvolver already provides everything we need to manage evolution,
		 * but we can override that if necessary.  The stock evolver might throw
		 * exceptions.  We'll pass those to the console.
		 * 
		 */
//		evolver.run();
	}
	
	
	
	
	
	
	
	/* -------------------------[ Game methods ]------------------------- */
	
	// API-mapped
	public void cleanup()
	{
		game.stop();
		gameWindow.dispose();
	}
	
	// API-mapped
	public void init()
	{	
		// set up game defaults
		game = new Game();
		
		game.setManualControlEnabled(true);
		game.setUseMomentum(false);
		
		game.setGrowSnake(false);
		game.setIgnoreSelfCollisions(true);
		game.setIgnoreWallCollisions(true);
		
		// allocate generation information panel
		JPanel generalPanel = new JPanel(new GridLayout(2, 2));
		generalPanel.setBorder(BorderFactory.createTitledBorder("General information"));
		generalPanel.add(new JLabel(" Food eaten:"));
		generalPanel.add(foodEaten);
		generalPanel.add(new JLabel(" Time remaining:"));
		generalPanel.add(timeLeft);
		
		// allocate world information panel
		JPanel worldPanel = new JPanel(new GridLayout(5, 2));
		worldPanel.setBorder(BorderFactory.createTitledBorder("World information"));
		worldPanel.add(new JLabel(" Snake (x,y):"));
		worldPanel.add(snakeLocation);
		worldPanel.add(new JLabel(" Food (x,y):"));
		worldPanel.add(foodLocation);
		worldPanel.add(new JLabel(" Absolute dt:"));
		worldPanel.add(absoluteFoodDistance);
		worldPanel.add(new JLabel(" Absolute direction:"));
		worldPanel.add(snakeDirection);
		worldPanel.add(new JLabel(" Relative direction (dx,dy):"));
		worldPanel.add(relativeFoodDistance);

		// allocate snake information panel
		JPanel snakePanel = new JPanel(new GridLayout(3, 2));
		snakePanel.setBorder(BorderFactory.createTitledBorder("Snake information"));
		snakePanel.add(new JLabel(" Input vector:"));
		snakePanel.add(new JLabel());
		snakePanel.add(new JLabel(" Output vector:"));
		snakePanel.add(new JLabel());
		snakePanel.add(new JLabel(" Fitness score:"));
		snakePanel.add(fitness);

		// finalize the window
		// TODO: Register the game frame with the MDI manager
		gameWindow = new JFrame();
		gameWindow.setLayout(new BoxLayout(gameWindow.getContentPane(), BoxLayout.Y_AXIS));
		gameWindow.add(game);
		gameWindow.add(generalPanel);
		gameWindow.add(worldPanel);
		gameWindow.add(snakePanel);
		gameWindow.setResizable(false);
		gameWindow.setTitle(WINDOW_TITLE);
		gameWindow.pack();
		gameWindow.setVisible(true);
		
		// launch
		game.start();
		startTimer();
	}
	
	// API-mapped
	public double evaluate(IChromosome candidate)
	{
		init();
		
		System.out.println(candidate.toString());
		
		// run the game and evaluate
		while(!game.isOver() && !isTimeExpired())
		{
			Point f = game.getFoodLocation();
			Point s = game.getSnakeLocation();
			Point d = game.getRelativeDistance(s, f);
			double distance = s.distance(f);

			// update the GUI
			foodEaten.setText("" + game.getScore());
			timeLeft.setText("" + getTimeRemaining() / 1000 + "s");
			foodLocation.setText ("(" + f.x + "," + f.y + ")");
			snakeLocation.setText("(" + s.x + "," + s.y + ")");
			absoluteFoodDistance.setText("" + (int)distance);
			snakeDirection.setText(game.getSnakeDirectionName());
			relativeFoodDistance.setText("(" + d.x + "," + d.y + ")");
		}
		
		cleanup();
		return Math.random() * 1000;
	}
	

	
	
	

	
	//
//					//
//					// ----- HACK -----
//					//
//					// apparently, % isn't really modulo in Java, and it doesn't
//					// work with negative numbers, so instead we have to manually
//					// check our bounds (see: http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4639626)
//					//
//					new_direction += change;
//					if(new_direction < 0) new_direction = 3;
//					if(new_direction > 3) new_direction = 0;
//					//
//					// ----- END HACK -----
//					//
//					
////					new_direction = (new_direction + change) % 4;
//					new_direction++;     // 0-based -> 1-based (for game)
	//
	public int computeFitness(int foodEaten, int elapsedSeconds)
	{
		int fitness   = 0;
		int timeBonus = 0;

		// [1] reward for food eaten
		if(foodEaten == 0)          // no food eaten, not very good
		{
			fitness  += 10;
			timeBonus = 1;
		}
		else if(foodEaten == 1)     // one food eaten, likely by chance
		{
			fitness  += 100;  
			timeBonus = 10;
		}
		else                        // multiple food eaten, good job
		{
			fitness  += foodEaten * 1000;
			timeBonus = 100;
		}

		// [2] reward for survival time, depending on food eaten 
		fitness += elapsedSeconds * timeBonus;
		
		// [3] reward an extra bonus for longevity, provided food was eaten
		if(foodEaten > 1 && elapsedSeconds >= MAX_LIFESPAN_SECONDS)
		{
			fitness += MAX_FITNESS / 4;
		}
		
		return fitness;
	}
	
	
	
	
	/* --------[ Drivers ]--------- */

	public void sendToEac(IChromosome candidate)
	{
		int num_llas = Math.min(llas.length, MAX_LLAS); // don't send to many LLAs
		for(int i = 0; i < num_llas; i++)
		{
			int coords[] = getCoords(((Integer)llas[i]).intValue());
			driver.addLLA(coords[0], coords[1], 0, 0, (Integer)llaFxs[i] - 1, 1);
			
//			int[] coords = getCoords((Integer)llas[0]);
//			int[] values = driver.reportLLA(driver.getLLAIndex(coords[0], coords[1]));

			driver.enableLLA(driver.getLLAIndex(coords[0],coords[1]));
		}
		for(int i = 0; i < inputs.length; i++)
		{
			int coords[] = getCoords(((Integer)inputs[i]).intValue());
			driver.setCurrent(coords[0], coords[1], (int)(Math.random() * 200));
//			driver.changeNode(JEACNode.SOURCE,
		}
		for(int i = 0; i < sinks.length; i++)
		{
			int coords[] = getCoords(((Integer)sinks[i]).intValue());	
			driver.setCurrent(coords[0], coords[1], (int)(Math.random() * 200) * -1);
		}
	}

	public void clearEac(IChromosome candidate)
	{
		// now reset the EAC
		driver.resetAllLLAs();
		for(int i = 0; i < inputs.length; i++)
		{
			int[] coords = this.getCoords(((Integer)inputs[i]).intValue());
			driver.setCurrent(coords[0], coords[1], 0);
		}
		for(int i = 0; i < sinks.length; i++)
		{
			int[] coords = this.getCoords(((Integer)sinks[i]).intValue());
			driver.setCurrent(coords[0], coords[1], 0);
		}
	}
	
	
	
	/* -------------------------[ Get/set methods ]------------------------- */
	
	/**
	 * 
	 * @param index
	 * @return
	 */
	public int[] getCoords(int index)
	{
		int rows = driver.getNumRows();
		int cols = driver.getNumCols();
		
		int row = 1 + (index / cols);
		int col = 1 + (index % cols);
		
		int[] r = {row, col};
		
		return r;
	}
	
}

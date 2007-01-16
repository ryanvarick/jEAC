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

package edu.indiana.cs.ga;

import edu.indiana.cs.eac.driver.*;
import edu.indiana.cs.ga.snakeEvolver.*;

import org.jgap.*;
import org.jgap.event.*;
import org.jgap.impl.*;

/**
 * Provides basic functionality for EAC-based genetic algorithms.
 * 
 * <p>This class tries to provide a unified starting point for 
 * EAC-based evolution.  It tries to bring together the existing
 * jEAC driver model and JGAP.
 * 
 * <p>More...
 * 
 * Evolving things consists of two major tasks:
 *  - Configuration - the <i>Evolver</i>.
 *  - Fitness function
 *  
 * The configuration itself is in turn a multiphase process:
 * 
 * 1. Create a configuration object
 * 2. Create a population (collection of chromosomes)
 * 3. Create a sample chromosome (sample to draw off of)
 * 4. Create the evolution function.
 * 
 * The fitness function is the process of evaluating an individual
 * candidate chromosome.  It involves overriding the evaluate() method
 * of the FitnessFunction.
 * 
 * @author   Ryan R. Varick
 * @since    1.2.0
 *
 */
public class EacEvolver
{
	/* defaults (do not alter directly; use the API instead) */
	private boolean useLiveEac = false;
	private boolean resetEacOnConnect   = true;
	private boolean toggleLEDsOnConnect = true;
	
	private int mutationRate      = 10;
	private int numElites         = 5;
	private int tournamentMatches = 3;
	private int populationSize    = 50;
	private int maxGenerations    = 10;
	private int chromosomeLength  = 3;
	
	/* variables */
	private Configuration configuration;
	private FitnessFunction fitnessFunction;
	private EventManager eventManager;
	
	private HAL driver;

	private Gene[] sample1;
//	private Chromosome sC;

	private Genotype population;

	
	
	/* -------------------------[ Generic class methods ]------------------------- */
	
	/**
	 * Returns a new <code>EacEvolver</code> instance.
	 * 
	 * @author   Ryan R. Varick
	 * @since    1.2.0
	 * 
	 */
	public EacEvolver() throws Exception
	{
		/*
		 * The configuration is required by many aspects of JGAP, even though
		 * it might not be done yet.  Hence we instantiate our "working copy"
		 * now.  It will be fully initialized when init() is called.
		 *  
		 */
		configuration = new Configuration();
		configuration.setName("EacEvolver Base Configuration");
	}


	
	/* -------------------------[ Evolver methods ]------------------------- */

	/**
	 * Configures the <code>Evolver</code>.
	 * 
	 * <p>The Configuration object stores the settings that are used to guide
	 * the evolutionary process.  There is only one Configuration instance
	 * per JVM; the settings are global.  JGAP is a fairly powerful genetic
	 * algorithm package, so there are quite a few options.  I have tried
	 * to organize things in a way that makes sense of EAC-based evolution.
	 *
	 * <p>The configuration consists of three components:
	 * 
	 * <ol>
	 *   <li>A fitness function, used to evaluate individual configurations.</li>
	 *   <li>A number of settings, which determine how evolution operates.</li>
	 *   <li>A sample chromosome, which specifies how configurations are encoded.</li>
	 * </ol>
	 *  
	 * <p>The fitness function should be a derived subclass of
	 * <code>EacEvolverFitnessFunction</code>, and is generally specified in the 
	 * <code>main()</code> method of the specific <code>EacEvolver</code> subclass.  
	 * It is simply added to the Configuration.
	 * 
	 * <p>The settings determine how evolution proceeds (e.g., elitism,
	 * mutation, tournament selection, etc).  Individual settings are
	 * commented inline.
	 * 
	 * <p>The sample chromosome specifies the way that the EAC is represented
	 * genetically.  [More...]
	 * 
	 * TODO: More information about the sample chromosome.
	 * 
	 * @author Ryan R. Varick
	 * @since 1.2.0
	 * 
	 */
	private void configure() throws Exception
	{
		/*
		 * Specify the fitness function.
		 * 
		 * The fitness function should be specified by the EacEvolver subclass,
		 * usually in main().  It determines the fate of all those who would be king!
		 *  
		 */
		configuration.setFitnessFunction(fitnessFunction);
		
		/*
		 * Specify the fitness evaluator (not usually important).
		 * 
		 * The fitness evaluator *does not* evaluate fitness!  It simply 
		 * decides how the fitness score is treated.  By default, higher fitness
		 * scores are better.  Sometimes, lower scores are better.  If that's
		 * the case, here's where you change how things are interperted.
		 *  
		 */
		configuration.setFitnessEvaluator(new DefaultFitnessEvaluator());

		/*
		 * Specify the event manager (not usually important).
		 * 
		 * The event manager processes evolution events.  It doesn't seem to be
		 * very well supported by JGAP right now.  I use a named manager because
		 * it might be useful to "hook" the evolutionary process, someday.  
		 *
		 */
		eventManager = new EventManager();
		eventManager.addEventListener(GeneticEvent.GENOTYPE_EVOLVED_EVENT, 
									  new AnonymousGeneticEventListener());
		configuration.setEventManager(eventManager);
			
		/*
		 * Specify chromosome reuse (not usually important).
		 * 
		 * This is an optimization that re-uses old chromosomes. It probably
		 * does not matter for an I/O-bound application like EAC evolution.
		 * 
		 */
		configuration.setChromosomePool(new ChromosomePool());
			
		/*
		 * Specify the random number generator.
		 * 
		 * The random number generator is used by mutation, etc., and to 
		 * initialize the first population.  
		 * 
		 * TODO: Audit random generator needs.
		 * TODO: Write a random generator that biases OFF connection types.
		 * 
		 */
		configuration.setRandomGenerator(new StockRandomGenerator());
				
		/*
		 * Configure population behavior.
		 * 
		 * This is a bit strange because, by default, JGAP prefers dynamic
		 * population sizes.  For digital-based simulations, this probably
		 * doesn't matter, since the process happens so quickly.  But for EAC-
		 * based evolution, evaluating each candidate in hardware takes a
		 * while, and fluctuations in population size are much more apparent.
		 * 
		 * These settings specify a fixed population size.  
		 * 
		 * TODO: Determine how JGAP "fills up" the population.
		 * 
		 */
		configuration.setPopulationSize(this.populationSize);
		configuration.setMinimumPopSizePercent(100);
		configuration.setKeepPopulationSizeConstant(true);
		

		
		/*
		 * -------------------------[ Note ]-------------------------
		 * 
		 * Up to this point, we have been defining the technical details
		 * that JGAP needs to manage the evolutionary process.  We are now
		 * ready to specify the *parameters* (mutation, elitism, tournament
		 * selection, etc) that will be used during evolution.  JGAP divides
		 * these parameters into two sets:
		 * 
		 *  - Natural selectors - elitism, tournament selection, etc.
		 *  - Genetic operators - mutation, crossover, etc.
		 *  
		 * Selectors choose different individuals to advance to the next
		 * population while operators act on the resultant population.  At 
		 * least one of each is required.  In general, selectors are applied
		 * in the order they are specified, followed by operators.  It is
		 * possible, however, to specify selectors that are applied after the
		 * operators, if this is necessary.  JGAP is cool (and complicated) 
		 * like that. :-)
		 * 
		 */


		
		/*
		 * Add elitism - save the top-N individuals.
		 * 
		 * JGAP provides two ways to save fit individuals.  The first method,
		 * setPreserveFittestIndividual(true) is easy to apply; however, as the 
		 * name implies, it will only save the most fit individual.  To save more
		 * than one fit individual, we have to use a custom selector.  JGAP 
		 * expects this value to be a percentage of the population.  I'm used to
		 * specifying a fixed number of elites.  The following lines provide 
		 * support for both methods.
		 * 
		 * TODO: Verify that the elites *are not* mutated.
		 * 
		 */
		double elitePercent = (double)this.numElites / this.populationSize;
		BestChromosomesSelector es = new BestChromosomesSelector(configuration, elitePercent);
		configuration.addNaturalSelector(es, true);
		
		/*
		 * Add tournament selection - choose the fittest N individuals after 
		 * they compete in M matches.
		 * 
		 * Tournament selection is a way to select reasonably (but not
		 * necessarily elite) individuals for breeding.  The fittest individual
		 * after M matches is used for breeding.  
		 * 
		 * TODO: Verify that tournament selection is used to seed breeding.
		 * 
		 */
//		TournamentSelector ts = new TournamentSelector(configuration, this.tournamentMatches, .90d);
//		configuration.addNaturalSelector(ts, true);

		/*
		 * Add crossover.
		 * 
		 * 
		 */
//		configuration.addGeneticOperator(new CrossoverOperator(configuration));
		
		/*
		 * Add mutation.
		 * 
		 */
		configuration.addGeneticOperator(new MutationOperator(configuration, this.mutationRate));
		
		
		
		
		
		
		


		/*
		 * Specify the sample chromosome.
		 * 
		 * TODO: Make sure this is working properly.
		 * 
		 */
		configuration.setSampleChromosome(new Chromosome(configuration, sample1));
		
		
		
		/*
		 * -------------------------[ Note ]-------------------------
		 * 
		 * We're ready to go now!  The rest of this method applies our
		 * settings and gets things ready for action.  You shouldn't need to
		 * change anything beyond this point.
		 * 
		 */
		


		// generate the initial population
		population = Genotype.randomInitialGenotype(configuration);

		// initialize the driver
//		initDriver();
//		
//		// ===[ DEBUG ]===
//		System.out.println(configuration.toString());
//		
//		/* alternate population initialization */
//		population_alt = new Population(configuration, this.populationSize);
//		initPopulation();
//		genotype = new Genotype(configuration, population);
	}
	

	

	
	/**
	 * 
	 */
//	private void initDriver()
//	{
////		// connect to the first available uEAC
////		if(useLiveEac) // skip all the hardware calls when we're not in live mode
////		{
////			String[] d = USBDriver.getDeviceList();
////			driver = new USBDriver(d[0]);
////			USBDriver.setDebug(false);
////			driver.connect();
////			driver.reset();
//////			driver.toggleLEDs();
////		}
//	}
	
	public void run() throws Exception
	{
		configure();
		
		for(int i = 1; i <= getNumGenerations(); i++)
		{
			System.out.println("---------------[ Evolving generation " + i + " ]----------------");
			System.out.println(" ");
			
			/*
			 * Evaluate the current generation.
			 * 
			 * By requesting the fittest individual, we are requesting that
			 * the current generation is evaluated.  The evaulation routine will,
			 * in turn, invoke the fitness function's evaluate() method.  
			 * 
			 */
//			List best = population.getFittestChromosomes(3);
			Genotype population = getPopulation();
			IChromosome fittestChromosome = population.getFittestChromosome();
			
			System.out.println("---------------");
			System.out.println(" ");
			System.out.println("Best fitness so far: " + fittestChromosome.getFitnessValue());
			System.out.println("\n");
			
			// check for suitable fitness here
			
			/* 
			 * Evolve the next generation.
			 * 
			 * After we have analyzed the current generation, we are ready to
			 * apply the genetic operators specified by the Configuration object.
			 * This will produce a new generation, and the circle of life
			 * continues. :-)
			 * 
			 */
			population.evolve();

			/* ---[ DEBUGGING ]--- */
			SnakeEvolver.num_chromosomes = 0;
		}
		
		
		cleanup();
	}
	
	
	/**
	 * Safely prepares the Evolver for disposal by disconnecting
	 * drivers, closing threads, etc.
	 * 
	 * @author Ryan R. Varick
	 * @since 1.2.0
	 *
	 */
	private void cleanup()
	{
		if(useLiveEac) { driver.disconnect(); }
	}



	/* -------------------------[ Get/set methods ]------------------------- */
	

	// FIXME: this should work properly :-)
	public int getChromosomeLength()
	{
		return chromosomeLength;
	}
	
	public int getNumGenerations() { return this.maxGenerations; }
	public void setNumGenerations(int maxGenerations) { this.maxGenerations = maxGenerations; }

	public Configuration getConfiguration() { return this.configuration; }
	public Genotype getPopulation() { return this.population; }
	

	/**
	 * Toggles the hardware mode.  When enabled, the Evolver uses a live EAC.
	 * When disabled, no EAC is used.
	 * 
	 * @param flag - set to true to enable live hardware; false otherwise
	 * 
	 * @author Ryan R. Varick
	 * @since 1.2.0
	 * 
	 */
	public void setUseLiveEac(boolean flag) 
	{
		this.useLiveEac = flag;
	}
	
	public void setFitnessFunction(FitnessFunction f) 
	{ 
		this.fitnessFunction = f;
	}
	
	public void setPopulationSize(int size)
	{
		this.populationSize = size;
	}
	
	public void setGeneLayout(Gene[] s)
	{
		this.sample1 = s;
		
	}
	
//	public void setResetEacOnConnect(boolean flag) { this.resetEacOnConnect = flag; }
//	public void setToggleLEDsOnConnect(boolean flag) { this.toggleLEDsOnConnect = flag; }
	
	
	
	
	
	
	
	/* =========================[ Listeners, etc. ]========================= */
	
	/**
	 * Listens for genetic events.
	 * 
	 * <p>JGAP seems to provide a way of hooking genetic events (mutation,
	 * selection, etc) as they occur.  This feature does not seem to be very
	 * well developed right now though.  For now, this class just kind of sits
	 * here and does nothing.  It might be useful in the future.
	 * 
	 * @author   Ryan R. Varick
	 * @since    1.2.0
	 *
	 */
	private class AnonymousGeneticEventListener implements GeneticEventListener
	{
		public void geneticEventFired(GeneticEvent e)
		{
//			System.out.println("Hooked me a good one, boss: " + e.getEventName());
		}
	}

}

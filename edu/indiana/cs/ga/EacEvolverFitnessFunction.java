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

import org.jgap.*;

/**
 * Details go here.
 * 
 * <p>More... an abstract class that provides base functionality.
 * 
 * This class cannot be directly instantiated.
 * 
 * @author   Ryan R. Varick
 * @since    1.2.0
 *
 */
public abstract class EacEvolverFitnessFunction extends FitnessFunction
{
	/** Minimum valid fitness score. */ 
	public static final int MIN_FITNESS = 1;
	
	/* defaults (do not alter directly, use the API instead) */
	private long maxRunningTimeMs = 60000;
	
	/* variables (do not alter) */
	long startTime;


	
	/* =========================[ Abstract methods ]========================= */

	/**
	 * Setup.
	 *
	 */
	protected abstract void init();
	
	/**
	 * Cleanup.
	 *
	 */
	protected abstract void cleanup();
	
	/**
	 * Called by JGAP, evaluates.
	 */
	protected abstract double evaluate(IChromosome candidate);


	
	/* -------------------------[ Alternate abstract structure ]------------------------- */

	/*
	 * TODO: Think about FitnessFunction method structure.
	 * 
	 * Alternative class structure, if we want to enforce code
	 * segementation.  I'm on the fence about this right now.
	 * 
	 * init() and cleanup() could be empty by default, instead of
	 * abstract, reducing the amount of work required by the end
	 * programmer.  Does this offer any benefit?  What about scoping
	 * rules?
	 * 
	 */

//	
//	protected void init()
//	{
//		/* Do nothing, unless overridden. */
//	}
//	
//	protected void cleanup()
//	{
//		/* Do nothing, unless overridden. */
//	}
//	
//	protected double evaluate(IChromosome candidate)
//	{
//		init();
//		double score = evaluate2(candidate);
//		cleanup();
//		return score;
//	}
//	
//	protected abstract double evaluate2(IChromosome candidate);
//	
	
	
	
	
	
	
	/* =========================[ Utility methods ]========================= */
	


	/* -------------------------[ Timer utility methods ]------------------------- */
	
	/**
	 * Starts the timer.
	 * 
	 * <p>The timer provides functionality that makes timing-calculations
	 * easier for the fitness function author.  The timer can be used to
	 * run the evaluator for a fixed interval of time, or perform other time-
	 * sensitive computations.  
	 * 
	 * @author   Ryan R. Varick
	 * @since    1.2.0
	 *
	 */
	protected void startTimer()
	{
		startTime = System.currentTimeMillis();
	}
	
	/**
	 * Returns the status of the timer.
	 * 
	 * <p>The timer does not enforce any kind of time limit.  It merely
	 * provides timing functionality for the <code>evaluate()</code> method.

	 * @return   <code>true</code> if time has expired for this evaluation.
	 * 
	 * @author   Ryan R. Varick
	 * @since    1.2.0
	 * 
	 */
	protected boolean isTimeExpired()
	{
		return (getTimeRemaining() <= 0);
	}

	/**
	 * Returns the time remaining, in milliseconds.
	 * 
	 * @return   The time remaining, in milliseconds.
	 * 
	 * @author   Ryan R. Varick
	 * @since    1.2.0
	 * 
	 */
	protected long getTimeRemaining()
	{
		return maxRunningTimeMs - (System.currentTimeMillis() - startTime);
	}
	
	/**
	 * Sets the maximum time the evaluator should run, per instance.
	 * 
	 * <p>The timer does not enforce any kind of time limit.  It merely
	 * provides timing functionality for the <code>evaluate()</code> method.
	 * 
	 * @param time    The maximum time to run, in milliseconds.
	 * 
	 * @author        Ryan R. Varick
	 * @since         1.2.0
	 * 
	 */
	protected void setMaxRunningTimeMs(long time)
	{
		maxRunningTimeMs = time;
	}
	
}

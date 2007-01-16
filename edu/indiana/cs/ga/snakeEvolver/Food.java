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

/**
 * An abstract representation of food.
 * 
 * <p>Food is represented simply as a Java <code>Point</code>.  Some care 
 * went the design of the placement method to assure that it would 
 * work with arbitrary world sizes.
 * 
 * @author   Ryan R. Varick
 * @since    1.2.0
 *
 */
public class Food
{
	/* variables (do not alter) */
	private Point location;
	
	

	/* -------------------------[ Generic class methods ]------------------------- */

	/** 
	 * Returns a new Food instance. 
	 *
	 * @author   Ryan R. Varick
	 * @since    1.2.0
	 * 
	 */
	public Food()
	{
		// choose the first point
		this.location = new Point();
		this.placeNew();
	}


	
	/* -------------------------[ Food methods] ------------------------- */
	
	/**
	 * Places a new piece of food somewhere in the world.
	 * 
	 * <p>The location of the food should be checked for validity.
	 *
	 * @author   Ryan R. Varick
	 * @since    1.2.0
	 * 
	 */
	protected void placeNew()
	{
		/*
		 * For the purposes of the game, we have to choose a position that
		 * corresponds to the upper-right corner of the snake's head, because
		 * that is the only point that is checked for food.  If we aren't
		 * careful to line up food placement on the block boundaries, the snake
		 * will not be able to eat.  Poor snake.
		 * 
		 */
		int x = (int)(Math.random() * 100) % (Game.WORLD_SIZE_X / Game.BLOCK_SIZE_X + 1) * Game.BLOCK_SIZE_X;
		int y = (int)(Math.random() * 100) % (Game.WORLD_SIZE_Y / Game.BLOCK_SIZE_Y + 1) * Game.BLOCK_SIZE_Y;
	
		location.setLocation(x, y);

	}
	
	
	
	/* -------------------------[ Get/set methods ]------------------------- */

	/**
	 * Returns the location of the current unit of food.
	 * 
	 * @return   Location of the food.
	 * 
	 * @author   Ryan R. Varick
	 * @since    1.2.0
	 * 
	 */
	protected Point getLocation() 
	{ 
		return location;
	}

}

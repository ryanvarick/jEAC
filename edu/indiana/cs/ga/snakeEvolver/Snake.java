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
import java.util.*;

/**
 * An abstract representation of a snake.  
 * 
 * <p>There is not really anything interesting about this class.  The 
 * snake is implemented as a <code>LinkedList</code> and the collision 
 * logic is somewhat complicated; but otherwise, things are fairly 
 * straightforward.  
 * 
 * @author   Ryan R. Varick
 * @since    1.2.0
 *
 */
public class Snake
{
	/* defaults (do not alter directly; use the API instead) */
	private boolean growOnEat            = true;
	private boolean ignoreSelfCollisions = false;
	private boolean ignoreWallCollisions = false;
	
	/* snake information (do not alter) */
	private int direction;
	private boolean isAlive;
	private LinkedList<Point> body;
	private Point newHeadLocation = new Point(-1, -1);
	private Point oldTailLocation = new Point(-1, -1);


	
	/* -------------------------[ Generic class methods ]------------------------- */

	/**
	 * Returns a new Snake instance.
	 * 
	 * @author   Ryan R. Varick
	 * @since    1.2.0
	 * 
	 */
	public Snake(int length)
	{
		body = new LinkedList<Point>();

		// add default sections, centered in the world
		for(int i = 0; i < length; i++)
		{
			int x = (int)Math.floor((Game.WORLD_SIZE_X / Game.BLOCK_SIZE_X) / 2) * Game.BLOCK_SIZE_X;
			int y = (int)Math.floor((Game.WORLD_SIZE_Y / Game.BLOCK_SIZE_Y) / 2) * Game.BLOCK_SIZE_Y;
			
			body.add(new Point(x, y));
		}
		
		isAlive = true;
	}
	
	
	
	/* -------------------------[ Snake methods] ------------------------- */
	
	/**
	 * Increases the length of the snake by one unit.
	 * 
	 * <p>If the game is configured with dynamic growth disabled, this 
	 * method does nothing.
	 *
	 * @author   Ryan R. Varick
	 * @since    1.2.0
	 * 
	 */
	protected void grow() 
	{ 
		if(growOnEat) 
		{
			body.addFirst(body.getFirst());
		} 
	}

	/**
	 * Moves the snake in the given direction.
	 * 
	 * <p>First, the direction is translated to a relative offset. Then that
	 * offset is passed to moveTo() for evaluation.  If the move is successful,
	 * then the Snake is updated accordingly.  Various settings come into
	 * effect here, such as the collision settings.
	 * 
	 * @param direction   New direction to move (<code>Game</code> constant expected).
	 * 
	 * @author            Ryan R. Varick
	 * @since             1.2.0
	 * 
	 */
	protected void move(int direction)
	{
		boolean moveSuccess = true;
		
		switch(direction)
		{
		case Game.UP:
			moveSuccess = moveBy(0, -Game.BLOCK_SIZE_Y);
			break;
		case Game.DOWN:
			moveSuccess = moveBy(0, Game.BLOCK_SIZE_Y);
			break;
		case Game.LEFT:
			moveSuccess = moveBy(-Game.BLOCK_SIZE_X, 0);
			break;
		case Game.RIGHT:
			moveSuccess = moveBy(Game.BLOCK_SIZE_X, 0);
			break;
		default:
			// at game start, the move will be null, so we ignore it
			return;
		}
		
		// after a successful move, update the coordinates of the snake
		if(moveSuccess) 
		{ 
			// but only update when we're not stuck (ignore collisions)
			if(!(newHeadLocation.x == body.getFirst().x && newHeadLocation.y == body.getFirst().y))
			{
				setDirection(direction);
				oldTailLocation = body.getLast();
				
				body.addFirst(newHeadLocation);
				body.removeLast();
			}
		}
		
		// die after an unsucessful move
		else 
		{ 
			isAlive = false;
		}
	}
	
	/**
	 * Moves the snake by the given offset.
	 * 
	 * <p>This method tests that the new coordinate is valid.  The
	 * Snake data structure is not updated here, just the pointer.
	 * move() is responsible for updating the data structure.
	 * 
	 * @param offsetX   X-coordinate of the offset.
	 * @param offsetY   Y-coordinate of the offset.
	 * @return          <code>true</code> if the move was successful, or 
	 *                  <code>false</code> if the move resulted in the death 
	 *                  of the snake.
	 * 
	 * @author          Ryan R. Varick
	 * @since           1.2.0
	 * 
	 */
	private boolean moveBy(int offsetX, int offsetY)
	{
		// first, we compute the new location
		Point p = new Point(body.getFirst().x + offsetX,
							body.getFirst().y + offsetY);
		
		// then we check for wall-collisions and self-collisions
		boolean inBounds = isLocationInBounds(p);
		boolean isValid  = isLocationValid(p);
		
		/*
		 * - If there are no problems at all, we can go ahead and
		 *   update the location and record a successful move
		 *   
		 * - If we encounter a collision, we have to check for the
		 *   appropriate ignore flag before recording an unsucessful
		 *   move -- if the flag is set, we record success, but we do
		 *   not update the location
		 *   
		 * - If all else fails, call it a day and kill the snake :-)
		 * 
		 */
		if(inBounds && isValid)
		{
			newHeadLocation = p;
			return true;
		}
		else if((inBounds && (ignoreSelfCollisions && !isValid)) ||
			    (isValid  && (ignoreWallCollisions && !inBounds))) 
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	


	/* -------------------------[ Get/set methods ]------------------------- */
	
	/**
	 * Returns the status of the snake.
	 * 
	 * <p>The snake dies when it either collides with itself, or when 
	 * it collides with a wall, unless collisions are disabled.
	 * 
	 * @return   <code>true</code> if the snake is alive.
	 * 
	 * @author   Ryan R. Varick
	 * @since    1.2.0
	 * 
	 */
	protected boolean isAlive() 
	{ 
		return isAlive;
	}
	
	/**
	 * Tests if the specified coordinates fall within the confines
	 * of the world.  
	 * 
	 * @param p   Coordinates of the point to test.
	 * @return    <code>true</code> if the coordinate is valid.
	 * 
	 * @author    Ryan R. Varick
	 * @since     1.2.0
	 * 
	 */
	protected boolean isLocationInBounds(Point p)
	{
		if(p.x < 0 || p.x > Game.WORLD_SIZE_X ||
		   p.y < 0 || p.y > Game.WORLD_SIZE_Y)
		{ 
			return false;
		}
		else 
		{
			return true;
		}
	}

	/**
	 * Tests if the specified coordinates fall within the body of the snake.
	 * 
	 * <p>This method is useful for determining if a new food location 
	 * falls within the snake's body.
	 * 
	 * @param p   Coordinates of the point to test.
	 * @return    <code>true</code> if the coordinate is valid.
	 * 
	 * @author    Ryan R. Varick
	 * @since     1.2.0
	 * 
	 */
	protected boolean isLocationValid(Point p)
	{
		ListIterator<Point> list = body.listIterator();
		
		// if any part of the snake matches the test point, we fail
		while(list.hasNext())
		{
			Point test = list.next();
			if(p.x == test.x && p.y == test.y) 
			{ 
				return false;
			}
		}

		return true;
	}
	
	/**
	 * Returns an array of Points that indicate the body of the snake.
	 * 
	 * @return   Array of <code>Point</code>s.
	 * 
	 * @author   Ryan R. Varick
	 * @since    1.2.0
	 * 
	 */
	protected Point[] getBody() 
	{ 
		return body.toArray(new Point[0]);
	}
	
	/**
	 * Returns the snake's current direction.
	 * 
	 * @return   The snake's current direction.
	 * 
	 * @author   Ryan R. Varick
	 * @since    1.2.0
	 * 
	 */
	protected int getDirection() 
	{ 
		return direction;
	}
	
	/**
	 * Sets the direction of the snake.
	 * 
	 * @param direction   New direction (<code>Game</code> constant expected).
	 *  
	 * @author            Ryan R. Varick
	 * @since             1.2.0
	 * 
	 */
	protected void setDirection(int direction) 
	{ 
		this.direction = direction;
	}

	/** 
	 * Returns the location of the snake's head.
	 * 
	 * @return   Location of the snake's head.
	 * 
	 * @author   Ryan R. Varick
	 * @since    1.2.0
	 * 
	 */
	protected Point getLocation() 
	{ 
		return body.getFirst();
	}
	
	/**
	 * Returns the previous coordinates of the snake's tail.
	 *  
	 * @return   Previous location of the snake's tail.
	 * 
	 * @author   Ryan R. Varick
	 * @since    1.2.0
	 * 
	 */
	protected Point getLastTailLocation()
	{
		return oldTailLocation;
	}
	
	/**
	 * Toggles dynamic growth.
	 * 
	 * <p>When enabled, the length of the snake increases by one unit each 
	 * time a piece of food is eaten.  When disabled, the snake's length 
	 * remains constant.
	 * 
	 * @param flag   Set to <code>true</code> if the snake should grow dynamically.
	 * 
	 * @author       Ryan R. Varick
	 * @since        1.2.0
	 * 
	 */
	protected void setGrowOnEat(boolean flag) 
	{ 
		this.growOnEat = flag;
	}
	
	/**
	 * Toggles self collisions.
	 * 
	 * <p>When enabled, the snake will not die if it collides with another part
	 * of itself.  When disabled, collisions with itself result in the death 
	 * of the snake.
	 * 
	 * @param flag   Set to <code>true</code> to ignore collisions.
	 * 
	 * @author       Ryan R. Varick
	 * @since        1.2.0
	 * 
	 */
	protected void setIgnoreSelfCollisions(boolean flag) 
	{ 
		this.ignoreSelfCollisions = flag;
	}
	
	/**
	 * Toggles wall collisions.
	 * 
	 * <p>When enabled, the snake will not die if it collides with a wall.
	 * When disabled, collisions with the wall result in the death of the snake
	 * 
	 * @param flag   Set to <code>true</code> to ignore collisions.
	 * 
	 * @author       Ryan R. Varick
	 * @since        1.2.0
	 * 
	 */
	protected void setIgnoreWallCollisions(boolean flag) 
	{ 
		this.ignoreWallCollisions = flag;
	}
	
}

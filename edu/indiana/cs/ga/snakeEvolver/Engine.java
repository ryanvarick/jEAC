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

/**
 * Handles <code>Game</code> events and snake movement.
 * 
 * <p>This class could probably be merged into the <code>Game</code> class.
 * 
 * @author   Ryan R. Varick
 * @since    1.2.0
 *
 */
public class Engine
{
	/* defaults (do not alter directly; use the API instead) */
	private boolean useMomentum = true;
	
	/* variables (do not alter) */
	private final Snake snake;
	private final Food food;

	private int lastDirection;
	private int score;
	private boolean gameOver;
	
	
	
	/* -------------------------[ Generic class methods ]------------------------- */

	/**
	 * Returns a new <code>Engine</code> instance.
	 * 
	 * @author   Ryan R. Varick
	 * @since    1.2.0
	 * 
	 */
	public Engine(Snake snake, Food food)
	{
		this.food  = food;
		this.snake = snake;
		
		this.score    = 0;
		this.gameOver = false;
	}
	
	
	
	/* -------------------------[ Engine methods] ------------------------- */
	
	/**
	 * Moves the snake.
	 * 
	 * <p>The engine tries to move the snake in the given direction.  If the 
	 * move is valid, the game continues.  If the snake collides with itself, 
	 * or a wall, it dies and the game is over.  If the move is invalid (move 
	 * DOWN when travelling UP, for example), it is ignored. 
	 * 
	 * @param direction   New direction (<code>Game</code> constant expected).
	 * 
	 * @author            Ryan R. Varick
	 * @since             1.2.0
	 * 
	 */
	protected void move(int direction)
	{
		// Check that the game is not already over
		if(gameOver) { return; }

		// check that the key does not equal its anti-key (ie, UP when moving DOWN)
		if(!isDirectionValid(direction)) 
		{
			if(useMomentum) { direction = lastDirection; }
			else { direction = -1; }
		}
		
		// move
		snake.move(direction);
		
		// check that the snake lived
		gameOver = !snake.isAlive();
		
		// check for eaten food
		if(snake.getLocation().x == food.getLocation().x && 
		   snake.getLocation().y == food.getLocation().y) 
		{
			score += 1;
			snake.grow();
			
			while(!snake.isLocationValid(food.getLocation()))
			{
				food.placeNew();
			}
		}

		// save the direction
		lastDirection = snake.getDirection();
	}
	
	/**
	 * Returns <code>true</code> if snake's new direction is valid 
	 * for the current move.
	 * 
	 * <p>A valid direction is such that the key pressed is not equal to 
	 * the ``anti-key'' for the current direction.  The anti-key is the
	 * opposite of the current direction.  For example, trying to move UP
	 * when the snake is travelling DOWN would be invalid.  In this case,
	 * valid directions are RIGHT, LEFT, and DOWN (continue).
	 * 
	 * @param direction   Direction to test.
	 * @return            <code>true</code> if the direction is valid. 
	 *  
	 * @author            Ryan R. Varick
	 * @since             1.2.0
	 * 
	 */
	private boolean isDirectionValid(int direction)
	{
		switch(direction)
		{
		case Game.UP:
			if(lastDirection == Game.DOWN)  { return false; }
			break;
		case Game.DOWN:
			if(lastDirection == Game.UP)    { return false; }
			break;
		case Game.LEFT:
			if(lastDirection == Game.RIGHT) { return false; }
			break;
		case Game.RIGHT:
			if(lastDirection == Game.LEFT)  { return false; }
			break;
		}
		return true;
	}

	
	
	
	/* -------------------------[ Get/set methods ]------------------------- */
	
	/**
	 * Returns the status of the game.
	 * 
	 * @return   <code>true</code> if the game is over.
	 * 
	 * @author   Ryan R. Varick
	 * @since    1.2.0
	 * 
	 */
	protected boolean isGameOver() 
	{ 
		return gameOver;
	}
	
	/**
	 * Toggles the game's state.
	 * 
	 * <p>When set to true, the game is over; otherwise it is considered
	 * to be active.
	 * 
	 * @param flag   Set to <code>true</code> to mark the game as over.
	 * 
	 * @author       Ryan R. Varick
	 * @since        1.2.0
	 * 
	 */
	protected void setGameOver(boolean flag)
	{
		this.gameOver = flag;
	}

	/**
	 * Returns the score, which is the number of units of food 
	 * eaten so far.
	 * 
	 * @return   Current game score.
	 * 
	 * @author   Ryan R. Varick
	 * @since    1.2.0
	 * 
	 */
	protected int getScore() 
	{ 
		return score;
	}	
	
	/**
	 * Toggles momentum.
	 * 
	 * <p>When enabled, invalid anti-key directions will be registered
	 * as the current direction.  When disabled, invalid directions are
	 * ignored.
	 * 
	 * @param flag   Set to <code>true</code> to enable momentum. 
	 * 
	 * @author       Ryan R. Varick
	 * @since        1.2.0
	 * 
	 */
	protected void setUseMomentum(boolean flag) 
	{ 
		this.useMomentum = flag;
	}
	
}

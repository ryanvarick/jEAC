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
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;

/**
 * An implementation of the classic videogame ``Snake''.
 * 
 * <p>This class provides a standalone implementation of Snake.  It can be
 * played by itself via the keyboard, or integrated into a larger project (see
 * <code>SnakeEvolver</code>, for example).  Here are the files that are part
 * of this package:
 *
 * <ul>
 *   <li><code>Game</code>   - API, UI, timer thread, and keyboard processing</li>
 *   <li><code>Engine</code> - the world and event handling</li>
 *   <li><code>Food</code>   - food generation and placement</li>
 *   <li><code>Snake</code>  - the creature</li>
 * </ul>
 * 
 * <p>The <code>start()</code> and <code>stop()</code> methods are useful when
 * this game is used as a part of a simulation.  Other parameters include the
 * ability to ignore collisions (both with walls and the snake), grow the snake
 * dynamically, and move automatically at predetermined intervals (momentum).
 * 
 * <p><i>Note: I also tend to use this class as a place to test out different
 * "software engineering" techiques, so it sees a lot of refactoring. :-)</i>
 * 
 * @author   Ryan R. Varick
 * @since    1.2.0
 * 
 */
public class Game extends JPanel
{
	/** Width of the world. */
	public static final int WORLD_SIZE_X = 300;
	
	/** Height of the world. */
	public static final int WORLD_SIZE_Y = 300;
	
	/** Width of one unit of space within the world. */
	public static final int BLOCK_SIZE_X = 20;
	
	/** Height of one unit of space within the world. */
	public static final int BLOCK_SIZE_Y = 20;
	
	/** Initial size of the snake. */
	private static final int INITIAL_SNAKE_SIZE = 5;
	
	/* appearance */
	private static final int   BORDER_SIZE      = 1;
	private static final Color BORDER_COLOR     = Color.BLACK;
	private static final Color BACKGROUND_COLOR = Color.WHITE;
	private static final Color FOOD_COLOR       = Color.RED;
	private static final Color SNAKE_HEAD_COLOR = Color.GREEN.darker();
	private static final Color SNAKE_BODY_COLOR = Color.GREEN;
	
	/* defaults (do not alter directly; use the API instead) */
	private boolean useMomentum          = true; 
	private boolean manualControlEnabled = true;
	private int direction                = -1;
	private int gameSpeedMs              = 250;
	
	/* keyboard codes (do not alter) */
	public static final int LEFT  = 37;
	public static final int RIGHT = 39;
	public static final int UP    = 38;
	public static final int DOWN  = 40;

	/* game variables (do not alter) */
	private final GameThread gameThread;

	private final Engine engine;
	private final Food food;
	private final Snake snake;
	
	
	
	/* -------------------------[ Generic class methods ]------------------------- */
		
	/**
	 * Returns a new <code>Game</code> instance.
	 *  
	 * @author   Ryan R. Varick
	 * @since    1.2.0
	 *  
	 */
	public Game() 
	{
		// allocate game components
		this.snake  = new Snake(INITIAL_SNAKE_SIZE);
		this.food   = new Food();
		this.engine = new Engine(snake, food);

		// allocate the thread
		gameThread = new GameThread();
		
		// allocate the keyboard listener, which may not be used
		this.addKeyListener(new AnonymousKeyListener());

		// add a border and compute the size
		this.setBackground(BACKGROUND_COLOR);
		this.setBorder(new LineBorder(BORDER_COLOR, BORDER_SIZE));
		this.setPreferredSize(new Dimension(WORLD_SIZE_X + BLOCK_SIZE_X, WORLD_SIZE_Y + BLOCK_SIZE_Y));
	}
	
	/**
	 * Standalone version of the game.
	 * 
	 * @param args   Command line arguments are ignored.
	 * 
	 * @author       Ryan R. Varick
	 * @since        1.2.0
	 * 
	 */
	public static void main(String[] args)
	{
		Game game = new Game();
		
		game.setManualControlEnabled(true);
		game.setUseMomentum(true);
		game.setSpeed(150);
		
		game.setGrowSnake(true);
		game.setIgnoreSelfCollisions(false);
		game.setIgnoreWallCollisions(false);
		
		// set up a window to contain the game
		JFrame window = new JFrame();
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setResizable(false);
		window.setTitle("Snake!");
		window.add(game);
		window.pack();
		window.setVisible(true);
		
		// off we go!
		game.start();
		
		// spin lock
		while(!game.isOver()) { }
		
		// game over
		JOptionPane.showMessageDialog(game, "Game over!\n\nFinal score: " + game.getScore());
		System.exit(0);
	}
	
	/**
	 * Updates the UI.
	 * 
	 * <p>This method is called automagically by <code>paint()</code>
	 * and <code>repaint()</code>.
	 * 
	 * @param g    Default graphics object, provided automagically.
	 *
	 * @author     Ryan R. Varick
	 * @since      1.2.0
	 * 
	 */
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		
		Graphics2D world = (Graphics2D)g;
		
		/*
		 * Ugh.  I really don't understand the Java rendering model all that
		 * well.  The idea is to clear the old snake and redraw it in its new
		 * location.  I couldn't figure out how to do this The Java Way, so
		 * instead I am manually clearing the viewport.
		 * 
		 * FIXME: Figure out how to properly update the drawing area.
		 * 
		 */
//		world.clearRect(0, 0, this.getWidth(), this.getHeight());
		
		// perhaps this is a *slight* improvement
		world.clearRect
			(
					snake.getLastTailLocation().x, 
					snake.getLastTailLocation().y, 
					BLOCK_SIZE_X, 
					BLOCK_SIZE_Y
			);
		
		
		// draw the food, arbitrarily smaller than the block size
		world.setColor(FOOD_COLOR);
		world.fillOval
			(
				food.getLocation().x + 2, 
				food.getLocation().y + 2, 
				BLOCK_SIZE_X - 2, 
				BLOCK_SIZE_Y - 2
			);

		// draw the snake
		Point[] p = snake.getBody();
		
		world.setColor(SNAKE_BODY_COLOR);
		for (int i = 1; i < p.length; i++)
		{
			world.fillRect(p[i].x, p[i].y, BLOCK_SIZE_X, BLOCK_SIZE_Y);
		}
		
		world.setColor(SNAKE_HEAD_COLOR);
		world.fillRect(p[0].x, p[0].y, BLOCK_SIZE_X, BLOCK_SIZE_Y);
	}
	
	
	
	/* -------------------------[ Game methods ]------------------------- */
	
	/**
	 * Starts the game.
	 *
	 * @author   Ryan R. Varick
	 * @since    1.2.0
	 *
	 */
	public void start()
	{
		/*
		 * Keyboard events are sent to the component in focus.  By default,
		 * that will be the frame that contains this JPanel.  To regain focus,
		 * and thus process keyboard events, we have to request it.
		 * 
		 */
		this.requestFocusInWindow();
		
		engine.setGameOver(false);
		gameThread.start();
	}
	
	/**
	 * Stops the game.
	 *
	 * @author Ryan R. Varick
	 * @since 1.2.0
	 *
	 */
	public void stop() 
	{ 
		engine.setGameOver(true);
	}

	/**
	 * Updates the game by moving the snake in the appropriate direction.
	 *
	 * @author   Ryan R. Varick
	 * @since    1.2.0
	 *
	 */
	private void move()
	{
		engine.move(direction);
		
		if(!isOver())
		{ 
			repaint();
		}
	}
	

	
	/* -------------------------[ Get/set methods ]------------------------- */
	
	/**
	 * Determines if manual control is currently enabled.
	 *  
	 * @return   <code>true</code> if manual control is enabled.
	 * 
	 * @author   Ryan R. Varick
	 * @since    1.2.0
	 * 
	 */
	public boolean isManualControlEnabled()
	{
		return manualControlEnabled;
	}

	/**
	 * Returns the status of the game.
	 * 
	 * @return    <code>true</code> if the game is over.
	 * 
	 * @author    Ryan R. Varick
	 * @since     1.2.0
	 *
	 */
	public boolean isOver() 
	{ 
		return engine.isGameOver();
	}
	
	/**
	 * Returns the location of the current piece of food.
	 * 
	 * @return   The location of the food.
	 * 
	 * @author   Ryan R. Varick
	 * @since    1.2.0
	 * 
	 */
	public Point getFoodLocation()
	{
		return food.getLocation();
	}
	
	/**
	 * Returns the relative distance between two points, given the current
	 * direction of the snake.
	 * 
	 * <p>This method is useful to determine the distance between the sanke and 
	 * the food.  The distance is computed from point p1 to point p2.  The order
	 * of the arguments is important.
	 * 
	 * @param p1   Location of the first point.
	 * @param p2   Location of the second point.
	 * @return     Relative distance between the two points.
	 * 
	 * @author     Ryan R. Varick
	 * @since      1.2.0
	 * 
	 */
	public Point getRelativeDistance(Point p1, Point p2)
	{
		/*
		 * We want to compute the distance relative to the snake's current
		 * direction.  That means we have to translate the absolute direction
		 * Here is our table:
		 * 
		 * UP:     ( x, y) (viewport native coordinates)
		 * DOWN:   (-x,-y)
		 * LEFT:   ( y, x)
		 * RIGHT:  (-y,-x)
		 * 
		 */

		// compute the raw distance
		Point d = new Point(0, 0); 
		int dx  = p1.x - p2.x;
		int dy  = p1.y - p2.y;
		
		// translate the distance
		switch(getSnakeDirection())
		{
		case(Game.UP):
			d = new Point( dx,  dy);
			break;
		case(Game.DOWN):
			d = new Point(-dx, -dy);
			break;
		case(Game.LEFT):
			d = new Point( dy,  dx);
			break;
		case(Game.RIGHT):
			d = new Point(-dy, -dx);
			break;
		}
		
		return d;
	}

	/**
	 * Returns the printable name of the snake's direction.
	 * 
	 * <p>This method strikes me as kind of a hack.  There is probably a better
	 * way of doing this.  Normally, we could use string constants instead of 
	 * int's for the direction; however, the values are already mapped to 
	 * keyboard scan codes.  Thus we have this intermediate method to translate
	 * the identifier into a printable string.
	 * 
	 * @return   Printable version of the direction.
	 * 
	 * @author   Ryan R. Varick
	 * @since    1.2.0
	 * 
	 */
	public String getSnakeDirectionName()
	{
		int direction = getSnakeDirection();
		String d      = "";
		
		switch(direction)
		{
		case Game.UP:
			d = "UP";
			break;
		case Game.DOWN:
			d = "DOWN";
			break;
		case Game.LEFT:
			d = "LEFT";
			break;
		case Game.RIGHT:
			d = "RIGHT";
			break;
		}
		
		return d;
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
	public int getSnakeDirection()
	{
		return snake.getDirection();
	}
	
	/**
	 * Returns the location of the snake's head.
	 * 
	 * @return   The location of the snake's head.
	 * 
	 * @author   Ryan R. Varick
	 * @since    1.2.0
	 * 
	 */
	public Point getSnakeLocation()
	{
		return snake.getLocation();
	}
	
	/**
	 * Toggles dynamic snake growth.
	 * 
	 * <p>When enabled, the snake will grow after each unit of food is eaten.
	 * When disabled, the length of the snake remains constant.
	 * 
	 * @param flag   Set to <code>true</code> to enable dynamic growth.
	 * 
	 * @author       Ryan R. Varick
	 * @since        1.2.0
	 *
	 */
	public void setGrowSnake(boolean flag) 
	{ 
		snake.setGrowOnEat(flag);
	}
	
	/**
	 * Toggles self collisions.
	 * 
	 * <p>When enabled, the snake will not die if it collides with another 
	 * part of itself.  When disabled, collisions with itself result in 
	 * the death of the snake.
	 * 
	 * @param flag   Set to <code>true</code> to ignore collisions.
	 * 
	 * @author       Ryan R. Varick
	 * @since        1.2.0
	 * 
	 */
	public void setIgnoreSelfCollisions(boolean flag) 
	{ 
		snake.setIgnoreSelfCollisions(flag);
	}
	
	/**
	 * Toggles wall collisions.
	 * 
	 * <p>When enabled, the snake will not die if it collides with a wall.
	 * When disabled, collisions with the wall result in the death of the snake.
	 * 
	 * @param flag   Set to <code>true</code> to ignore collisions.
	 * 
	 * @author       Ryan R. Varick
	 * @since        1.2.0
	 * 
	 */
	public void setIgnoreWallCollisions(boolean flag) 
	{ 
		snake.setIgnoreWallCollisions(flag);
	}

	/**
	 * Toggles keyboard control.
	 * 
	 * <p>When enabled, a person can control the snake manually, using 
	 * the keyboard.  When disabled, the snake must be controlled programmatically.
	 * 
	 * @param flag   Set to <code>true</code> to enable keyboard control. 
	 * 
	 * @author       Ryan R. Varick
	 * @since        1.2.0
	 *
	 */
	public void setManualControlEnabled(boolean flag) 
	{ 
		this.manualControlEnabled = flag;
	}
	
	/** 
	 * Sets the snake's new direction.
	 * 
	 * <p>If the game is set to automove, the direction will be used at 
	 * the next move.  Otherwise, the direction will be applied instantly.
	 * 
	 * @param direction	   Desired direction (use <code>Game</code> constants).
	 * 
	 * @author             Ryan R. Varick
	 * @since              1.2.0
	 * 
	 */
	public void setNewDirection(int direction)
	{ 
		this.direction = direction;
		if(!useMomentum) { move(); }
	}

	/**
	 * Returns the current score, which is the number of units of food eaten
	 * by the snake so far.
	 * 
	 * @return    Current game score.
	 * 
	 * @author    Ryan R. Varick
	 * @since     1.2.0
	 *
	 */
	public int getScore() 
	{ 
		return engine.getScore();
	}
	
	/**
	 * Sets the game speed.
	 * 
	 * <p>The speed controls how frequently the update thread is called.
	 * 
	 * @param speed   Desired game speed, in milliseconds.
	 * 
	 * @author        Ryan R. Varick
	 * @since         1.2.0
	 * 
	 */
	public void setSpeed(int speed) 
	{ 
		this.gameSpeedMs = speed;
	}
	
	/**
	 * Toggles momentum.
	 * 
	 * <p>When enabled, the game will automatically move the snake at a 
	 * predetermined interval.  When disabled, the snake will not move unless
	 * <code>move()</code> is called explicitly.
	 * 
	 * @param flag   Set to <code>true</code> to enable momentum.
	 * 
	 * @author       Ryan R. Varick
	 * @since        1.2.0
	 * 
	 */
	public void setUseMomentum(boolean flag) 
	{ 
		this.useMomentum = flag;
		engine.setUseMomentum(flag);
	}
	

	
	
	
	
	
	/* =========================[ Listeners, etc. ]========================= */
	
	/**
	 * Updates the game periodically.
	 * 
	 * @author   Ryan R. Varick
	 * @since    1.2.0
	 *
	 */
	private class GameThread extends Thread
	{
		public void run()
		{
			while(true)
			{
				// only update if momentum is enabled
				if(useMomentum) 
				{ 
					move();
				}
				
				// wait for the next move
				try { Thread.sleep(gameSpeedMs); }
				catch(InterruptedException e) { e.printStackTrace(); }
			}
		}
	}
	
	/**
	 * Listens for keyboard input; provides manual control over the game.
	 * 
	 * @author   Ryan R. Varick
	 * @since    1.2.0
	 * 
	 */
	private class AnonymousKeyListener implements KeyListener 
	{
		public void keyPressed (KeyEvent e)
		{
			// only process keyboard events if manual control is enabled
			if(!isManualControlEnabled())
			{
				return;
			}
			else if(e.getKeyCode() == UP   || e.getKeyCode() == DOWN ||
					e.getKeyCode() == LEFT || e.getKeyCode() == RIGHT)
			{
				setNewDirection(e.getKeyCode());
			}
		}
		public void keyReleased(KeyEvent e) { /* Do nothing */ }
		public void keyTyped   (KeyEvent e) { /* Do nothing */ }
	}
	
}

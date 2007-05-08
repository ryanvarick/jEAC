/*
 * This file is part of jEAC (http://jeac.sf.net/).
 * 
 * Copyright (C) 2007.  All rights reserved.
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation; either version 2 of
 * the License, or (at your option) any later version.
 * 
 */

package edu.indiana.cs.eac;



/**
 * Abstract definition of a component manager.
 * 
 * <p>A <i>Manager</i> represents some defined element of the application.  It
 * encapsulates and consolidates the code necessary for that functionality in
 * a single class.  Managers are intended to split complex components into
 * its simpler, constituent pieces.
 * 
 * <p>Further, managers are designed to <i>hide</i> complexity.  The inner
 * mechanics of a manager component are revealed only through <code>get()</code>
 * methods--the underlying code is hidden.  Unlike extended objects, the super
 * types should not be know.
 * 
 * <p>init() should be called first.  A manager should not be expected to work
 * properly if init() is not called.
 * 
 *  TODO: Rewrite this summary.
 * 
 * @author   Ryan R. Varick
 * @since    2.0.0
 *
 */
public interface Manager
{
	/**
	 * Initializes the manager.
	 * 
	 * <p>Initialization tasks that cannot go in the constructor (ie, circular
	 * references to a parent mananger) should go here.  This method is expected
	 * to be called before any other requests are made.
	 * 
	 * @author   Ryan R. Varick
	 * @since    2.0.0
	 *
	 */
	public void init();
	
	/**
	 * Signals a state change has occured elsewhere in the application.
	 * 
	 * <p>This method <b>does not</b> force a manager to update; rather, it is
	 * intended to indicate that an event has occured elsewhere that may or may
	 * not be of interest here.  Managers should provide their own "force
	 * update" method if such behavior is necessary.
	 * 
	 * @author   Ryan R. Varick
	 * @since    2.0.0
	 *
	 */
	public void update();
}

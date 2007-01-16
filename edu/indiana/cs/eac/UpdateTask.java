/**
 * UpdateTask.java - Thread manager for UI updates.
 * 
 * Copyright (C) 2006.  All rights reserved.
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation; either version 2 of
 * the License, or (at your option) any later version.
 * 
 */

package edu.indiana.cs.eac;

import java.util.*;
import java.io.IOException;
import edu.indiana.cs.eac.*;

import edu.indiana.cs.eac.driver.HAL;
import edu.indiana.cs.eac.gradient.Gradient;
import edu.indiana.cs.eac.gradient.Gradient2D;
import edu.indiana.cs.eac.gradient.Gradient3D;

public class UpdateTask extends TimerTask
{
	private HAL driver;
	private double[][] gradient;
	private Gradient gradient2DPanel, gradient3DPanel;
	private LLAInspectorFrame inspectorFrame;
	
	/**
	 * Constructor - Sets up a new timed thread.
	 * 
	 * @param driver - active EAC driver to talk to.
	 * @param Gradient2D panel - UI component to update
	 * @param Gradient3D panel - UI component to update
	 * @param LLAInspectorFrame frame - UI component to update
	 * 
	 */
	public UpdateTask(HAL driver, Gradient2D panel2D, Gradient3D panel3D, LLAInspectorFrame inspectorframe)
	{
		this.driver          = driver;		
		this.gradient3DPanel = panel3D;
		this.gradient2DPanel = panel2D;
		this.inspectorFrame  = inspectorframe;
	}
	
	/**
	 * Update the UI with new machine data.
	 * 
	 */
	public void run()
	{
		try
		{
			// only poll LLAs if the inspector is open 
			if(inspectorFrame != null)
			{
				String[] llaInValues  = driver.getAllLLAInputValues();
				String[] llaOutValues = driver.getAllLLAOutputValues();
		    		
		    	inspectorFrame.refreshDisplay(llaInValues, llaOutValues);
			}

			// cache the new voltage gradient
			gradient = driver.getVoltageGradient();
				
			// try to update the individual panels
			if(gradient3DPanel != null)
			{
				gradient3DPanel.removeAllPlots();
				gradient3DPanel.setPlotSource(gradient);
				gradient3DPanel.plot();
			}
			if(gradient2DPanel != null)
			{
				gradient2DPanel.removeAllPlots();
				gradient2DPanel.setPlotSource(gradient);
				gradient2DPanel.plot();
			}			
		}
		catch (IOException e)
		{
			System.err.println("Could not get voltage gradient.");
		}
		catch (StringIndexOutOfBoundsException e)
		{
			/* Do nothing, keep the thread running */
		}
		catch (NumberFormatException e)
		{
			/* Do nothing, keep the thread running */
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}

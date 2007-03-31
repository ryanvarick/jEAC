package edu.indiana.cs.eac;

import java.util.*;

public final class ManagerRegistry
{
	private static ManagerRegistry registry = new ManagerRegistry();
	private static HashMap map = new HashMap();
	
	private ManagerRegistry()
	{
		// thwart direct instantiation
	}
	
	/**
	 * 
	 * @param classname
	 * @return
	 * 
	 * @author   Ryan R. Varick
	 * @since    2.0.0
	 * 
	 */
	public static synchronized Object getInstance(String classname)
	{
		Object singleton = map.get(classname);

		if(singleton != null)
		{
			return singleton;
		}
		
		try
		{
			singleton = Class.forName(classname).newInstance();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		map.put(classname, singleton);
		return singleton;
	}
	
	public static ManagerRegistry getRegistry()
	{
		return registry;
	}

}

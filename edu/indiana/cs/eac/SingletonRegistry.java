package edu.indiana.cs.eac;

import java.util.*;

public final class SingletonRegistry
{
	private static SingletonRegistry registry = new SingletonRegistry();
	private static HashMap map = new HashMap();
	
	private SingletonRegistry()
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
	
	public static SingletonRegistry getRegistry()
	{
		return registry;
	}

}

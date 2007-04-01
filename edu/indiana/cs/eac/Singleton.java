package edu.indiana.cs.eac;

import java.util.HashMap;

/**
 * 
 * @author Varick
 *
 */
public abstract class Singleton
{
	public Singleton() {} 
	
	private static HashMap map = new HashMap();
	
	/**
	 * Returns the single instance of the class.
	 * 
	 * <p>The idea is to prevent multiple instances of jEAC from running, in
	 * the same JVM anyway.  There shouldn't be a need for simultaneous instances.
	 * 
	 * @return   Class instance.
	 * 
	 * @author   Ryan R. Varick
	 * @since    2.0.0
	 * 
	 */
	public static final synchronized Object getInstance(Class classname)
	{
		System.out.println("In Singleton, trying to return " + classname.getName());
		
		Object singleton = map.get(classname);

		if(singleton != null)
		{
			return singleton;
		}
		
		try
		{
			singleton = Class.forName(classname.getName()).newInstance();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		map.put(classname, singleton);
		return singleton;
	}
	
	
	/**
	 * Prevents attempts to create multiple instances via cloning.
	 * 
	 * @throws   Don't copy that floppy!
	 * 
	 * @author   Ryan R. Varick
	 * @since    2.0.0
	 * 
	 */
	public final Object clone() throws CloneNotSupportedException
	{
		throw new CloneNotSupportedException();
	}

}

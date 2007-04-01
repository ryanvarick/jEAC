/**
 * 
 */
package edu.indiana.cs.testing.ga;

import org.jgap.RandomGenerator;

/**
 * @author Ryan R. Varick
 * @since 2.0
 *
 */
public class BiasedIntegerGenerator implements RandomGenerator
{
	private static boolean isType = true;
	private static int counter = 0;
	
	/**
	 * 
	 *
	 */
	public BiasedIntegerGenerator()
	{
	}
	
	
	

	/* (non-Javadoc)
	 * @see org.jgap.RandomGenerator#nextInt()
	 */
	public int nextInt()
	{
		System.err.println("In nextInt() with no args!");
//		counter++;
//		// this is only called during gene TYPE init
//		System.out.print(counter + ": Choosing TYPE: ");
//		
//		// 75% bias to OFF
//		if(Math.random() < .75)
//		{
//			System.out.println("OFF");
//			return 0;
//		}
//		
//		// evenly bias inputs and outputs otherwise
//		else if(Math.random() < .5)
//		{
//			System.out.println("SOURCE");
//			return 1;
//		}
//		else
//		{
//			System.out.println("LLA");
//			return 2;
//		}
		return 0;
	}

	/* (non-Javadoc)
	 * @see org.jgap.RandomGenerator#nextInt(int)
	 */
	public int nextInt(int max)
	{
//		System.err.println("In nextInt() with args: " + max);
//		counter++;
//		
//		int random = (int)(Math.random() * max);
//		System.out.println(counter + ": Choosing VALUE: " + random);
//		return random;
		return (int)(Math.random() * max);
	}

	/* (non-Javadoc)
	 * @see org.jgap.RandomGenerator#nextLong()
	 */
	public long nextLong() {
		System.err.println("Invalid datatype (long).");
		return 0;
	}

	/* (non-Javadoc)
	 * @see org.jgap.RandomGenerator#nextDouble()
	 */
	public double nextDouble()
	{
		counter++;
		double random = -1;
		
		if(isType)
		{
//			// this is only called during gene TYPE init
//			System.out.print(counter + ": Choosing TYPE: ");
			
			// 75% bias to OFF
			if(Math.random() < .85)
			{
//				System.out.println("OFF");
				random = 0.0;
			}
			
			// evenly bias inputs and outputs otherwise
			else if(Math.random() < .5)
			{
//				System.out.println("SOURCE");
				random = 0.49;  // = 1
			}
			else
			{
//				System.out.println("LLA");
				random = 1.0;  // = 2
			}			
		}
		else
		{
			random = Math.random();
//			System.out.println(counter + ": Choosing VALUE: " + random);
		}
	
		// toggle... such a horrible kludge, I hope these methods aren't asynch
		isType = !isType;
		return random;
	}

	/* (non-Javadoc)
	 * @see org.jgap.RandomGenerator#nextFloat()
	 */
	public float nextFloat() {
		System.err.println("Invalid datatype (float).");
		return 0;
	}

	/* (non-Javadoc)
	 * @see org.jgap.RandomGenerator#nextBoolean()
	 */
	public boolean nextBoolean() {
		System.err.println("Invalid datatype (boolean).");
		return false;
	}

}

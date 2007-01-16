package edu.indiana.cs.ga;

import org.jgap.Gene;

public class DefaultChromosome
{
	
	public DefaultChromosome()
	{
		// Finally, we need to tell JGAP what our chromosome looks like.  We do 
		// this by first creating a sample Chromosome and then adding it to the
		// Configuration.
		//
		// In JGAP, a chromosome (one individual) is made up of genes.  The value
		// of a gene is referred to as an allele.  The mechanisms that govern 
		// genes are defined in the Gene interface, which is implemented by 
		// BaseGene, then extended to perform various different tasks.  Look at
		// the JGAP API for more information about gene types.
		//
		// We use CompositeGenes for the EAC.  Each gene in an EAC configuration
		// must simultaneously encode three variables:  its position, its 
		// connection type, and its payload.
		//
		// The position is handled implicity by the gene's position in the 
		// Chromosome.  The type and payload, on the other hand, are encoded by
		// the gene itself.  To represent these, we define a CompositeGene that is
		// made up of two IntegerGenes.  The IntegerGene class is nice because it
		// allows us to define useful upper and lower bounds on the values.
//		Gene[] sampleGenes = new Gene[driver.getNumCols() * driver.getNumRows()];
//		Gene[] sampleGenes = new Gene[evolver.getChromosomeLength()];
//		for(int i = 0; i < sampleGenes.length; i++)
//		{
//			// The gene and each of its pieces
//			CompositeGene gene  = new CompositeGene(configuration); // why do these take a configuration?
//			IntegerGene type    = new IntegerGene(configuration, 0, 2);
//			IntegerGene payload = new IntegerGene(configuration, 1, 27); // ignored for all but LLA_IN
//
//			gene.addGene(type);
//			gene.addGene(payload);
			
//			IntegerGene gene = new IntegerGene(configuration, 1, 27);
//			
//			sampleGenes[i] = gene;
//		}
	}

}

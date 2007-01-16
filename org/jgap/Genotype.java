/*
 * This file is part of JGAP.
 *
 * JGAP offers a dual license model containing the LGPL as well as the MPL.
 *
 * For licencing information please see the file license.txt included with JGAP
 * or have a look at the top of class org.jgap.Chromosome which representatively
 * includes the JGAP license policy applicable for any file delivered with JGAP.
 */
package org.jgap;

import java.io.*;
import java.util.*;
import org.jgap.event.*;

/**
 * Genotypes are fixed-length populations of chromosomes. As an instance of
 * a Genotype is evolved, all of its Chromosomes are also evolved. A Genotype
 * may be constructed normally via constructor, or the static
 * randomInitialGenotype() method can be used to generate a Genotype with a
 * randomized Chromosome population.
 * <p>
 * Please note that among all created Genotype instances there may only be one
 * configuration (singleton!), used by all Genotype instances
 *
 * @author Neil Rotstan
 * @author Klaus Meffert
 * @since 1.0
 */
public class Genotype
    implements Serializable {
  /** String containing the CVS revision. Read out via reflection!*/
  private final static String CVS_REVISION = "$Revision: 1.83 $";

  /**
   * The current active Configuration instance.
   * @since 1.0
   */
  transient private static Configuration m_activeConfiguration;

  /**
   * The array of Chromosomes that makeup the Genotype's population.
   * @since 2.0
   */
  private Population m_population;

  /**
   * Constructs a new Genotype instance with the given array of Chromosomes and
   * the given active Configuration instance. Note that the Configuration object
   * must be in a valid state when this method is invoked, or a
   * InvalidConfigurationException will be thrown.
   *
   * @param a_activeConfiguration the current active Configuration object
   * @param a_initialChromosomes the Chromosome population to be managed by
   * this Genotype instance
   * @throws InvalidConfigurationException if the given Configuration object is
   * in an invalid state
   *
   * @author Neil Rotstan
   * @author Klaus Meffert
   * @since 1.0
   * @deprecated use Genotype(Configuration, Population) instead
   */
  public Genotype(Configuration a_activeConfiguration,
                  IChromosome[] a_initialChromosomes)
      throws InvalidConfigurationException {
    this(a_activeConfiguration,
         new Population(a_activeConfiguration, a_initialChromosomes));
  }

  /**
   * Constructs a new Genotype instance with the given array of
   * Chromosomes and the given active Configuration instance. Note
   * that the Configuration object must be in a valid state
   * when this method is invoked, or a InvalidconfigurationException
   * will be thrown.
   *
   * @param a_activeConfiguration the current active Configuration object
   * @param a_population the Chromosome population to be managed by this
   * Genotype instance
   * @throws InvalidConfigurationException
   *
   * @author Neil Rotstan
   * @author Klaus Meffert
   * @since 2.0
   */
  public Genotype(Configuration a_activeConfiguration, Population a_population)
      throws InvalidConfigurationException {
    // Sanity checks: Make sure neither the Configuration, the array
    // of Chromosomes, nor any of the Genes inside the array are null.
    // ---------------------------------------------------------------
    if (a_activeConfiguration == null) {
      throw new IllegalArgumentException(
          "The Configuration instance may not be null.");
    }
    if (a_population == null) {
      throw new IllegalArgumentException(
          "The Population may not be null.");
    }
    for (int i = 0; i < a_population.size(); i++) {
      if (a_population.getChromosome(i) == null) {
        throw new IllegalArgumentException(
            "The Chromosome instance at index " + i + " of the array of " +
            "Chromosomes is null. No Chromosomes instance in this array " +
            "may be null.");
      }
    }
    m_population = a_population;
    setConfiguration(a_activeConfiguration);
    // Lock the settings of the configuration object so that it cannot
    // be altered.
    // ---------------------------------------------------------------
    getConfiguration().lockSettings();
  }

  /**
   * Sets the active configuration object on this Genotype and its
   * member Chromosomes. This method should be invoked immediately following
   * deserialization of this Genotype. If an active Configuration has already
   * been set on this Genotype, then this method will do nothing.
   *
   * @param a_activeConfiguration the current active Configuration object that
   * is to be referenced internally by this Genotype and its member Chromosome
   * instances
   *
   * @throws InvalidConfigurationException if the Configuration object is null
   * or cannot be locked because it is in an invalid or incomplete state
   *
   * @author Neil Rotstan
   * @author Klaus Meffert
   * @since 1.0
   * @deprecated don't use any more, see
   * GenotypeTest.testSetActiveConfiguration_0 for an example of the problem
   */
  public void setActiveConfiguration(Configuration a_activeConfiguration)
      throws InvalidConfigurationException {
    // Only assign the given Configuration object if we don't already
    // have one.
    // --------------------------------------------------------------
    if (m_activeConfiguration == null) {
      if (a_activeConfiguration == null) {
        throw new InvalidConfigurationException(
            "The given Configuration object may not be null.");
      }
      else {
        // Make sure the Configuration object is locked and cannot be
        // changed.
        // ----------------------------------------------------------
        a_activeConfiguration.lockSettings();
        m_activeConfiguration = a_activeConfiguration;
      }
    }
  }

  /**
   * Retrieves the array of Chromosomes that make up the population of this
   * Genotype instance.
   *
   * @return the population of Chromosomes
   *
   * @author Neil Rotstan
   * @author Klaus Meffert
   * @since 1.0
   * @deprecated uses getPopulation() instead
   */
  public synchronized IChromosome[] getChromosomes() {
    Iterator it = getPopulation().iterator();
    IChromosome[] result = new Chromosome[getPopulation().size()];
    int i = 0;
    while (it.hasNext()) {
      result[i++] = (IChromosome) it.next();
    }
    return result;
  }

  /**
   * @return the current population of chromosomes
   *
   * @author Klaus Meffert
   * @since 2.1 (?)
   */
  public Population getPopulation() {
    return m_population;
  }

  /**
   * Retrieves the Chromosome in the population with the highest fitness
   * value.
   *
   * @return the Chromosome with the highest fitness value, or null if there
   * are no chromosomes in this Genotype
   *
   * @author Neil Rotstan
   * @author Klaus Meffert
   * @since 1.0
   */
  public synchronized IChromosome getFittestChromosome() {
    return getPopulation().determineFittestChromosome();
  }

  /**
   * Retrieves the Chromosome in the population with the highest fitness
   * value within the given indices.
   *
   * @return the Chromosome with the highest fitness value within the given
   * indices, or null if there are no chromosomes in this Genotype
   *
   * @author Klaus Meffert
   * @since 3.0
   */
  public synchronized IChromosome getFittestChromosome(int a_startIndex,
      int a_endIndex) {
    return getPopulation().determineFittestChromosome(a_startIndex, a_endIndex);
  }

  /**
   * Retrieves the top n Chromsomes in the population (the ones with the best
   * fitness values).
   *
   * @param a_numberOfChromosomes the number of chromosomes desired
   * @return the list of Chromosomes with the highest fitness values, or null
   * if there are no chromosomes in this Genotype
   *
   * @author Charles Kevin Hill
   * @since 2.4
   */
  public synchronized List getFittestChromosomes(int a_numberOfChromosomes) {
    return getPopulation().determineFittestChromosomes(a_numberOfChromosomes);
  }

  /**
   * Evolves the population of Chromosomes within this Genotype. This will
   * execute all of the genetic operators added to the present active
   * configuration and then invoke the natural selector to choose which
   * chromosomes will be included in the next generation population. Note
   * that the population size not always remains constant (dependent on the
   * NaturalSelectors used!).<p>
   * To consecutively call this method, use evolve(int)!!!
   *
   * @author Neil Rotstan
   * @author Klaus Meffert
   * @since 1.0
   */
  public synchronized void evolve() {
    if (getConfiguration() == null) {
      throw new IllegalStateException(
          "The active Configuration object must be set on this"
          + " Genotype prior to evolution.");
    }
    // Adjust population size to configured size (if wanted).
    // Theoretically, this should be done at the end of this method.
    // But for optimization issues it is not. If it is the last call to
    // evolve() then the resulting population possibly contains more
    // chromosomes than the wanted number. But this is no bad thing as
    // more alternatives mean better chances having a fit candidate.
    // If it is not the last call to evolve() then the next call will
    // ensure the correct population size by calling keepPopSizeConstant.
    // ------------------------------------------------------------------
    if (m_activeConfiguration.isKeepPopulationSizeConstant()) {
      keepPopSizeConstant(getPopulation(),
                          getConfiguration().getPopulationSize());
    }
    // Apply certain NaturalSelectors before GeneticOperators will be applied.
    // -----------------------------------------------------------------------
    applyNaturalSelectors(true);
    // Execute all of the Genetic Operators.
    // -------------------------------------
    applyGeneticOperators();
    // Reset fitness value of genetically operated chromosomes.
    // Normally, this should not be necessary as the Chromosome
    // class initializes each newly created chromosome with
    // FitnessFunction.NO_FITNESS_VALUE. But who knows which
    // Chromosome implementation is used...
    // --------------------------------------------------------
    int originalPopSize = getConfiguration().getPopulationSize();
    int currentPopSize = getPopulation().size();
    for (int i = originalPopSize; i < currentPopSize; i++) {
      IChromosome chrom = getPopulation().getChromosome(i);
      chrom.setFitnessValueDirectly(FitnessFunction.NO_FITNESS_VALUE);
    }
    // Apply certain NaturalSelectors after GeneticOperators have been applied.
    // ------------------------------------------------------------------------
    applyNaturalSelectors(false);
    // If a bulk fitness function has been provided, call it.
    // ------------------------------------------------------
    BulkFitnessFunction bulkFunction =
        getConfiguration().getBulkFitnessFunction();
    if (bulkFunction != null) {
      bulkFunction.evaluate(getPopulation());
    }
    // Fill up population randomly if size dropped below specified percentage
    // of original size.
    // ----------------------------------------------------------------------
    if (m_activeConfiguration.getMinimumPopSizePercent() > 0) {
      int sizeWanted = getConfiguration().getPopulationSize();
      int popSize;
      int minSize = (int) Math.round(sizeWanted *
                                     (double) getConfiguration().
                                     getMinimumPopSizePercent()
                                     / 100);
      popSize = getPopulation().size();
      if (popSize < minSize) {
        IChromosome newChrom;
        try {
          while (getPopulation().size() < minSize) {
            newChrom = Chromosome.randomInitialChromosome(getConfiguration());
            getPopulation().addChromosome(newChrom);
          }
        }
        catch (InvalidConfigurationException invex) {
          throw new IllegalStateException(invex.getMessage());
        }
      }
    }
    if (getConfiguration().isPreserveFittestIndividual()) {
      IChromosome fittest = getFittestChromosome(0,
                                                 getConfiguration().
                                                 getPopulationSize() - 1);
      if (m_activeConfiguration.isKeepPopulationSizeConstant()) {
        keepPopSizeConstant(getPopulation(),
                            getConfiguration().getPopulationSize());
      }
      // Determine the fittest chromosome in the population.
      // ---------------------------------------------------
      if (!getPopulation().contains(fittest)) {
        // Re-add fittest chromosome to current population.
        // ------------------------------------------------
        getPopulation().addChromosome(fittest);
      }
    }
    // Increase number of generation.
    // ------------------------------
    getConfiguration().incrementGenerationNr();
    // Fire an event to indicate we've performed an evolution.
    // -------------------------------------------------------
    getConfiguration().getEventManager().fireGeneticEvent(
        new GeneticEvent(GeneticEvent.GENOTYPE_EVOLVED_EVENT, this));
  }

  /**
   * Evolves this Genotype the specified number of times. This is
   * equivalent to invoking the standard evolve() method the given number
   * of times in a row.
   *
   * @param a_numberOfEvolutions the number of times to evolve this Genotype
   * before returning
   *
   * @author Klaus Meffert
   * @since 1.1
   */
  public void evolve(int a_numberOfEvolutions) {
    for (int i = 0; i < a_numberOfEvolutions; i++) {
      evolve();
    }
    if (m_activeConfiguration.isKeepPopulationSizeConstant()) {
      keepPopSizeConstant(getPopulation(),
                          m_activeConfiguration.getPopulationSize());
    }
  }

  /**
   * Return a string representation of this Genotype instance,
   * useful for display purposes.
   *
   * @return string representation of this Genotype instance
   *
   * @author Neil Rotstan
   * @since 1.0
   */
  public String toString() {
    StringBuffer buffer = new StringBuffer();
    for (int i = 0; i < getPopulation().size(); i++) {
      buffer.append(getPopulation().getChromosome(i).toString());
      buffer.append(" [");
      buffer.append(getPopulation().getChromosome(i).getFitnessValueDirectly());
      buffer.append(']');
      buffer.append('\n');
    }
    return buffer.toString();
  }

  /**
   * Convenience method that returns a newly constructed Genotype
   * instance configured according to the given Configuration instance.
   * The population of Chromosomes will be created according to the setup of
   * the sample Chromosome in the Configuration object, but the gene values
   * (alleles) will be set to random legal values.
   *
   * @param a_activeConfiguration the current active Configuration object
   * @return a newly constructed Genotype instance
   *
   * @throws IllegalArgumentException if the given Configuration object is null
   * @throws InvalidConfigurationException if the given Configuration
   * instance is not in a valid state
   *
   * @author Neil Rotstan
   * @author Klaus Meffert
   * @since 2.3
   */
  public static Genotype randomInitialGenotype(Configuration
                                               a_activeConfiguration)
      throws InvalidConfigurationException {
    if (a_activeConfiguration == null) {
      throw new IllegalArgumentException(
          "The Configuration instance may not be null.");
    }
    a_activeConfiguration.lockSettings();
    // Create an array of chromosomes equal to the desired size in the
    // active Configuration and then populate that array with Chromosome
    // instances constructed according to the setup in the sample
    // Chromosome, but with random gene values (alleles). The Chromosome
    // class randomInitialChromosome() method will take care of that for
    // us.
    // ------------------------------------------------------------------
    int populationSize = a_activeConfiguration.getPopulationSize();
    IChromosome sampleChrom = a_activeConfiguration.getSampleChromosome();
    IInitializer chromIniter = a_activeConfiguration.getJGAPFactory().
        getInitializerFor(sampleChrom, sampleChrom.getClass());
    if (chromIniter == null) {
      throw new InvalidConfigurationException("No initializer found for class "
                                              + sampleChrom.getClass());
    }
    Population pop = new Population(a_activeConfiguration, populationSize);
    // Do randomized initialization.
    // -----------------------------
    try {
      for (int i = 0; i < populationSize; i++) {
        pop.addChromosome( (IChromosome) chromIniter.perform(sampleChrom,
            sampleChrom.getClass(), null));
      }
    }
    catch (Exception ex) {
      throw new IllegalStateException(ex.getMessage());
    }
    return new Genotype(a_activeConfiguration, pop);
  }

  /**
   * Compares this Genotype against the specified object. The result is true
   * if the argument is an instance of the Genotype class, has exactly the
   * same number of chromosomes as the given Genotype, and, for each
   * Chromosome in this Genotype, there is an equal chromosome in the
   * given Genotype. The chromosomes do not need to appear in the same order
   * within the populations.
   *
   * @param a_other the object to compare against
   * @return true if the objects are the same, false otherwise
   *
   * @author Neil Rotstan
   * @author Klaus Meffert
   * @since 1.0
   */
  public boolean equals(Object a_other) {
    try {
      // First, if the other Genotype is null, then they're not equal.
      // -------------------------------------------------------------
      if (a_other == null) {
        return false;
      }
      Genotype otherGenotype = (Genotype) a_other;
      // First, make sure the other Genotype has the same number of
      // chromosomes as this one.
      // ----------------------------------------------------------
      if (getPopulation().size() != otherGenotype.getPopulation().size()) {
        return false;
      }
      // Next, prepare to compare the chromosomes of the other Genotype
      // against the chromosomes of this Genotype. To make this a lot
      // simpler, we first sort the chromosomes in both this Genotype
      // and the one we're comparing against. This won't affect the
      // genetic algorithm (it doesn't care about the order), but makes
      // it much easier to perform the comparison here.
      // --------------------------------------------------------------
      Collections.sort(getPopulation().getChromosomes());
      Collections.sort(otherGenotype.getPopulation().getChromosomes());
      for (int i = 0; i < getPopulation().size(); i++) {
        if (! (getPopulation().getChromosome(i).equals(
            otherGenotype.getPopulation().getChromosome(i)))) {
          return false;
        }
      }
      return true;
    }
    catch (ClassCastException e) {
      return false;
    }
  }

  /**
   * Applies all NaturalSelectors registered with the Configuration.
   * @param a_processBeforeGeneticOperators true apply NaturalSelectors
   * applicable before GeneticOperators, false: apply the ones applicable
   * after GeneticOperators
   *
   * @author Klaus Meffert
   * @since 2.0
   */
  protected void applyNaturalSelectors(
      boolean a_processBeforeGeneticOperators) {
      /**@todo optionally use working pool*/
    try {
      // Process all natural selectors applicable before executing the
      // genetic operators (reproduction, crossing over, mutation...).
      // -------------------------------------------------------------
      int selectorSize = m_activeConfiguration.getNaturalSelectorsSize(
          a_processBeforeGeneticOperators);
      if (selectorSize > 0) {
        int m_population_size = m_activeConfiguration.getPopulationSize();
        int m_single_selection_size;
        Population m_new_population;
        m_new_population = new Population(m_activeConfiguration,
                                          m_population_size);
        NaturalSelector selector;
        // Repopulate the population of chromosomes with those selected
        // by the natural selector. Iterate over all natural selectors.
        // ------------------------------------------------------------
        for (int i = 0; i < selectorSize; i++) {
          selector = m_activeConfiguration.getNaturalSelector(
              a_processBeforeGeneticOperators, i);
          if (i == selectorSize - 1 && i > 0) {
            // Ensure the last NaturalSelector adds the remaining Chromosomes.
            // ---------------------------------------------------------------
            m_single_selection_size = m_population_size - getPopulation().size();
          }
          else {
            m_single_selection_size = m_population_size / selectorSize;
          }
          // Do selection of Chromosomes.
          // ----------------------------
          selector.select(m_single_selection_size, getPopulation(),
                          m_new_population);
          // Clean up the natural selector.
          // ------------------------------
          selector.empty();
        }
        setPopulation(new Population(getConfiguration()));
        getPopulation().addChromosomes(m_new_population);
      }
    }
    catch (InvalidConfigurationException iex) {
      // This exception should never be reached
      throw new IllegalStateException(iex.getMessage());
    }
  }

  /**
   * Applies all GeneticOperators registered with the Configuration.
   *
   * @author Klaus Meffert
   * @since 3.0
   */
  public void applyGeneticOperators() {
    List geneticOperators = getConfiguration().getGeneticOperators();
    Iterator operatorIterator = geneticOperators.iterator();
    while (operatorIterator.hasNext()) {
      GeneticOperator operator = (GeneticOperator) operatorIterator.next();
      applyGeneticOperator(operator, getPopulation(),
                           getPopulation().getChromosomes());

//      List workingPool = new Vector();
//      applyGeneticOperator(operator, getPopulation(),
//                           workingPool);
    }
  }

  /**
   * @return the configuration to use with the Genetic Algorithm
   *
   * @author Klaus Meffert
   * @since 2.0 (?)
   */
  public static Configuration getConfiguration() {
    return m_activeConfiguration;
  }

  /**
   * Sets the configuration to use with the Genetic Algorithm.
   * @param a_configuration the configuration to use
   *
   * @author Klaus Meffert
   * @since 2.0 (?)
   */
  public static void setConfiguration(Configuration a_configuration) {
    m_activeConfiguration = a_configuration;
  }

  /***
   * Hashcode function for the genotype, tries to create a unique hashcode for
   * the chromosomes within the population. The logic for the hashcode is
   *
   * Step  Result
   * ----  ------
   *    1  31*0      + hashcode_0 = y(1)
   *    2  31*y(1)   + hashcode_1 = y(2)
   *    3  31*y(2)   + hashcode_2 = y(3)
   *    n  31*y(n-1) + hashcode_n-1 = y(n)
   *
   * Each hashcode is a number and the binary equivalent is computed and
   * returned.
   * @return the computed hashcode
   *
   * @author vamsi
   * @since 2.1
   */
  public int hashCode() {
    int i, size = getPopulation().size();
    IChromosome s;
    int twopower = 1;
    // For empty genotype we want a special value different from other hashcode
    // implementations.
    int localHashCode = -573;
    for (i = 0; i < size; i++, twopower = 2 * twopower) {
      s = getPopulation().getChromosome(i);
      localHashCode = 31 * localHashCode + s.hashCode();
    }
    return localHashCode;
  }

  protected void setPopulation(Population a_pop) {
    m_population = a_pop;
  }

  /**
   * Overwritable method that calls a GeneticOperator to operate on a given
   * population and asks him to store the result in the list of chromosomes.
   * Override this method if you want to ensure that a_chromosomes is not
   * part of a_population resp. if you want to use a different list.
   *
   * @param a_operator the GeneticOperator to call
   * @param a_population the population to use
   * @param a_chromosomes the list of Chromosome objects to return
   *
   * @author Klaus Meffert
   * @since 2.4
   */
  protected void applyGeneticOperator(GeneticOperator a_operator,
                                      Population a_population,
                                      List a_chromosomes) {
    a_operator.operate(a_population, a_chromosomes);
  }

  /**
   * Cares that the population size does not exceed the given maximum size.
   * @param a_pop the population to keep constant in size
   * @param a_maxSize the maximum size allowed for the population
   *
   * @author Klaus Meffert
   * @since 2.5
   */
  protected void keepPopSizeConstant(Population a_pop, int a_maxSize) {
    int popSize = a_pop.size();
    // See request  1213752.
    // ---------------------
    while (popSize > a_maxSize) {
      // Remove a chromosome.
      // --------------------
      a_pop.removeChromosome(0);
      popSize--;
    }
  }
}

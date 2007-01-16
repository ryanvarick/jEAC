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

/**
 * An implementation of a fitness evaluator interpreting the fitness as delta
 * value.
 *
 * @author Klaus Meffert
 * @since 2.0
 */
public class DeltaFitnessEvaluator
    implements FitnessEvaluator {
  /** String containing the CVS revision. Read out via reflection!*/
  private final static String CVS_REVISION = "$Revision: 1.7 $";

  /**
   * Compares the first given fitness value with the second and returns true
   * if the first one is smaller than the second one. Otherwise returns false
   * @param a_fitness_value1 first fitness value
   * @param a_fitness_value2 second fitness value
   * @return true: first fitness value smaller than second
   *
   * @since 2.0 (until 1.1: input types int)
   */
  public boolean isFitter(final double a_fitness_value1,
                          final double a_fitness_value2) {
    if (a_fitness_value1 < 0) {
     return false;
    }
   if (a_fitness_value2 < 0) {
     return true;
   }
    return a_fitness_value1 < a_fitness_value2;
  }

  public boolean isFitter(IChromosome a_chrom1, IChromosome a_chrom2) {
    return isFitter(a_chrom1.getFitnessValue(), a_chrom2.getFitnessValue());
  }
}

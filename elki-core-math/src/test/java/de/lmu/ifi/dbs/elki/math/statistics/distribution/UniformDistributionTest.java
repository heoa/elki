/*
 * This file is part of ELKI:
 * Environment for Developing KDD-Applications Supported by Index-Structures
 *
 * Copyright (C) 2017
 * ELKI Development Team
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package de.lmu.ifi.dbs.elki.math.statistics.distribution;

import org.junit.Test;

/**
 * Unit test for the Uniform distribution in ELKI.
 *
 * @author Erich Schubert
 * @since 0.7.1
 */
public class UniformDistributionTest extends AbstractDistributionTest {
  @Test
  public void testPDF() {
    load("unif.ascii.gz");
    checkPDF(new UniformDistribution(0., 1.), "pdf_gnur_0_1", 1e-15);
    checkPDF(new UniformDistribution(-1., 2.), "pdf_gnur_M1_2", 1e-15);
    checkPDF(new UniformDistribution(0., 1.), "pdf_scipy_0_1", 1e-15);
    checkPDF(new UniformDistribution(-1., 2.), "pdf_scipy_M1_2", 1e-15);
  }

  @Test
  public void testLogPDF() {
    load("unif.ascii.gz");
    checkLogPDF(new UniformDistribution(0., 1.), "logpdf_gnur_0_1", 1e-15);
    checkLogPDF(new UniformDistribution(-1., 2.), "logpdf_gnur_M1_2", 1e-15);
    checkLogPDF(new UniformDistribution(0., 1.), "logpdf_scipy_0_1", 1e-15);
    checkLogPDF(new UniformDistribution(-1., 2.), "logpdf_scipy_M1_2", 1e-15);
  }

  @Test
  public void testCDF() {
    load("unif.ascii.gz");
    checkCDF(new UniformDistribution(0., 1.), "cdf_gnur_0_1", 1e-15);
    checkCDF(new UniformDistribution(-1., 2.), "cdf_gnur_M1_2", 1e-15);
    checkCDF(new UniformDistribution(0., 1.), "cdf_scipy_0_1", 1e-15);
    checkCDF(new UniformDistribution(-1., 2.), "cdf_scipy_M1_2", 1e-15);
  }

  @Test
  public void testQuantile() {
    load("unif.ascii.gz");
    checkQuantile(new UniformDistribution(0., 1.), "quant_gnur_0_1", 1e-15);
    checkQuantile(new UniformDistribution(-1., 2.), "quant_gnur_M1_2", 1e-15);
    checkQuantile(new UniformDistribution(0., 1.), "quant_scipy_0_1", 1e-15);
    checkQuantile(new UniformDistribution(-1., 2.), "quant_scipy_M1_2", 1e-15);
  }
}
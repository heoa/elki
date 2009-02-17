package de.lmu.ifi.dbs.elki.algorithm.statistics;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import de.lmu.ifi.dbs.elki.algorithm.DistanceBasedAlgorithm;
import de.lmu.ifi.dbs.elki.algorithm.clustering.ByLabelClustering;
import de.lmu.ifi.dbs.elki.data.DoubleVector;
import de.lmu.ifi.dbs.elki.data.RealVector;
import de.lmu.ifi.dbs.elki.data.cluster.Cluster;
import de.lmu.ifi.dbs.elki.data.model.Model;
import de.lmu.ifi.dbs.elki.database.Database;
import de.lmu.ifi.dbs.elki.distance.DoubleDistance;
import de.lmu.ifi.dbs.elki.distance.distancefunction.DistanceFunction;
import de.lmu.ifi.dbs.elki.math.MeanVariance;
import de.lmu.ifi.dbs.elki.result.CollectionResult;
import de.lmu.ifi.dbs.elki.utilities.Description;
import de.lmu.ifi.dbs.elki.utilities.Progress;
import de.lmu.ifi.dbs.elki.utilities.QueryResult;
import de.lmu.ifi.dbs.elki.utilities.Util;
import de.lmu.ifi.dbs.elki.utilities.pairs.ComparablePair;

/**
 * Evaluate a distance function with respect to kNN queries.
 * For each point, the neighbors are sorted by distance, then the ROC AUC is computed.
 * A score of 1 means that the distance function provides a perfect ordering of relevant
 * neighbors first, then irrelevant neighbors. A value of 0.5 can be obtained by random sorting.
 * A value of 0 means the distance function is inverted, i.e. a similarity.
 * 
 * TODO: Make number of bins configurable
 * TODO: Allow fixed binning range, configurable 
 * TODO: Add sampling
 * 
 * @author Erich Schubert
 */
public class EvaluateRankingQuality<V extends RealVector<V,?>> extends DistanceBasedAlgorithm<V,DoubleDistance,CollectionResult<DoubleVector>> {
  private CollectionResult<DoubleVector> result;
  
  /**
   * Empty constructor. Nothing to do.
   */
  public EvaluateRankingQuality() {
    super();
  }

  /**
   * Run the algorithm. 
   */
  @Override
  protected CollectionResult<DoubleVector> runInTime(Database<V> database) throws IllegalStateException {
    DistanceFunction<V,DoubleDistance> distFunc = getDistanceFunction();
    distFunc.setDatabase(database, isVerbose(), isTime());

    // local copy, not entirely necessary. I just like control, guaranteed sequences
    // and stable+efficient array index -> id lookups.
    ArrayList<Integer> ids = new ArrayList<Integer>(database.getIDs());
    int size = ids.size();

    if(isVerbose()) {
      verbose("Preprocessing clusters...");
    }
    // Cluster by labels
    ByLabelClustering<V> split = new ByLabelClustering<V>();
    Set<Cluster<Model>> splitted = split.run(database).getAllClusters();

    // Compute cluster averages
    HashMap<Cluster<?>, V> averages = new HashMap<Cluster<?>, V>(splitted.size());
    for (Cluster<?> clus : splitted) {
      V cent = Util.centroid(database, clus.getIDs());
      averages.put(clus, cent);
    }
    
    int bins = 100;
    MeanVariance[] vals = new MeanVariance[bins];
    for(int i = 0; i < bins; i++) { vals[i] = new MeanVariance(); }

    if(isVerbose()) {
      verbose("Processing points...");
    }
    Progress rocloop = new Progress("ROC computation loop ...", size);
    int rocproc = 0;

    // sort neighbors
    for (Cluster<?> clus : splitted) {
      ArrayList<ComparablePair<Double, Integer>> cmem = new ArrayList<ComparablePair<Double,Integer>>(clus.size());
      V av = averages.get(clus);
      for (Integer i1 : clus.getIDs()) {
        Double d = distFunc.distance(database.get(i1), av).getValue();
        cmem.add(new ComparablePair<Double,Integer>(d, i1));
      }
      Collections.sort(cmem);

      for (int ind = 0; ind < cmem.size(); ind++) {
        Integer i1 = cmem.get(ind).getSecond();
        List<QueryResult<DoubleDistance>> knn = database.kNNQueryForID(i1, size, distFunc);
        double result = computeROCAUC(size, clus, knn);

        //results.add(new ComparablePair<Double, Double>((double) ind / clus.size(), result));
        int binnr = (bins * ind) / clus.size();
        vals[binnr].addData(result);
        
        if(isVerbose()) {
          rocproc++;
          rocloop.setProcessed(rocproc);
          progress(rocloop);
        }
      }
    }
    if(isVerbose()) {
      verbose("");
    }
    //Collections.sort(results);
    
    Collection<DoubleVector> res = new ArrayList<DoubleVector>(size);
    for (int i = 0; i < bins; i++) {
      DoubleVector row = new DoubleVector(new double[] {(double) i / bins, vals[i].getCount(), vals[i].getMean(), vals[i].getVariance()});
      res.add(row);
    }
    result = new CollectionResult<DoubleVector>(res);
    return result;
  }

  /**
   * Compute a ROC curves Area-under-curve.
   * 
   * @param size
   * @param clus
   * @param nei
   * @return area under curve
   */
  private double computeROCAUC(int size, Cluster<?> clus, List<QueryResult<DoubleDistance>> nei) {
    int postot = clus.size();
    int negtot = size - postot;
    int poscur = 0;
    int negcur = 0;
    double lastpos = 0.0;
    double lastfalse = 0.0;
    double result = 0.0;
    Collection<Integer> ids = clus.getIDs();
    for (QueryResult<DoubleDistance> p : nei) {
      if (ids.contains(p.getID())) {
        poscur += 1;
      } else {
        negcur += 1;
      }
      double posrate = ((double) poscur) / postot;
      double negrate = ((double) negcur) / negtot;
      result += (negrate - lastfalse) * lastpos;
      lastfalse = negrate;
      lastpos = posrate;
    }
    return result;
  }

  /**
   * Describe the algorithm and it's use.
   */
  public Description getDescription() {
    return new Description("EvaluateRankingQuality","EvaluateRankingQuality",
        "Evaluates the effectiveness of a distance function via the obtained rankings.","");
  }

  /**
   * Return a result object
   */
  public CollectionResult<DoubleVector> getResult() {
    return result;
  }
}

package de.lmu.ifi.dbs.elki.algorithm.clustering;

import java.util.List;

import de.lmu.ifi.dbs.elki.algorithm.AbstractAlgorithm;
import de.lmu.ifi.dbs.elki.data.Clustering;
import de.lmu.ifi.dbs.elki.data.DatabaseObject;
import de.lmu.ifi.dbs.elki.data.DatabaseObjectGroup;
import de.lmu.ifi.dbs.elki.data.DatabaseObjectGroupCollection;
import de.lmu.ifi.dbs.elki.data.cluster.Cluster;
import de.lmu.ifi.dbs.elki.data.model.ClusterModel;
import de.lmu.ifi.dbs.elki.data.model.Model;
import de.lmu.ifi.dbs.elki.database.Database;
import de.lmu.ifi.dbs.elki.utilities.Description;

/**
 * Trivial pseudo-clustering that just considers all points to be noise.
 * 
 * Useful for evaluation and testing.
 * 
 * @author Erich Schubert
 *
 * @param <O>
 */
public class TrivialAllNoise<O extends DatabaseObject> extends AbstractAlgorithm<O,Clustering<Model>> implements ClusteringAlgorithm<Clustering<Model>,O> {
  /**
   * Holds the result of the algorithm.
   */
  private Clustering<Model> result;

  /**
   * Return clustering result
   */
  public Clustering<Model> getResult() {
    return result;
  }

  /**
   * Obtain a description of the algorithm
   */
  public Description getDescription() {
    return new Description("TrivialAllNoise", "Trivial all-noise clustering",
        "Returns a 'trivial' clustering which just considers all points as noise points.", "");
  }

  /**
   * Run the actual clustering algorithm.
   * 
   * @param database The database to process
   */
  @Override
  protected Clustering<Model> runInTime(Database<O> database) throws IllegalStateException {
    result = new Clustering<Model>();
    DatabaseObjectGroup group = new DatabaseObjectGroupCollection<List<Integer>>(database.getIDs());
    Cluster<Model> c = new Cluster<Model>(group, true, ClusterModel.CLUSTER);
    result.addCluster(c);
    return result;
  }
}

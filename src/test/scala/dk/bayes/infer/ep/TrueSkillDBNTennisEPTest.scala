package dk.bayes.infer.ep

import dk.bayes.infer.ep.util.TennisFactorGraphDBN
import org.junit._
import Assert._
import TennisFactorGraphDBN._
import Assert._
import com.typesafe.scalalogging.slf4j.Logger
import org.slf4j.LoggerFactory
import dk.bayes.model.factor.GaussianFactor

/**
 * This is a test for a skill update with TrueSkill rating system in a two-person game, like Tennis, over 3 time slices including 3 players and 6 games.
 *
 * Bayesian inference is performed with Expectation Propagation algorithm.
 *
 * @author Daniel Korzekwa
 */
class TrueSkillDBNTennisEPTest {

  private def progress(currIter: Int) = println("EP iteration: " + currIter)

  @Test def no_result_set {

    val tennisFactorGraph = createTennisFactorGraph()
    val ep = GenericEP(tennisFactorGraph)

    assertEquals(1,ep.calibrate(10, progress))

    val outcomeMarginal_t2 = ep.marginal(match1v3Time2VarId)
    assertEquals(0.5, outcomeMarginal_t2.getValue((match1v3Time2VarId, 0)), 0.00001)
    assertEquals(0.5, outcomeMarginal_t2.getValue((match1v3Time2VarId, 1)), 0.00001)

    val skill1_t0_marginal = ep.marginal(player1Time0VarId).asInstanceOf[GaussianFactor]
    assertEquals(4, skill1_t0_marginal.m, 0.0001)
    assertEquals(81, skill1_t0_marginal.v, 0.0001)

    val skill1_t1_marginal = ep.marginal(player1Time1VarId).asInstanceOf[GaussianFactor]
    assertEquals(4, skill1_t1_marginal.m, 0.0001)
    assertEquals(98.3611, skill1_t1_marginal.v, 0.0001)

    val skill1_t2_marginal = ep.marginal(player1Time2VarId).asInstanceOf[GaussianFactor]
    assertEquals(4, skill1_t2_marginal.m, 0.0001)
    assertEquals(115.7222, skill1_t2_marginal.v, 0.0001)

  }

  @Test def two_results_are_known {

    val tennisFactorGraph = createTennisFactorGraph()
    val ep = GenericEP(tennisFactorGraph)
    ep.setEvidence(match1v2Time0VarId, 0)
    ep.setEvidence(match2v3Time1VarId, 0)

    assertEquals(7,ep.calibrate(50, progress))

    val outcomeMarginal_t1 = ep.marginal(match1v2Time1VarId)
    assertEquals(0.6590, outcomeMarginal_t1.getValue((match1v2Time1VarId, 0)), 0.0001)
    assertEquals(0.3409, outcomeMarginal_t1.getValue((match1v2Time1VarId, 1)), 0.0001)

    val outcomeMarginal_t2 = ep.marginal(match1v2Time2VarId)
    assertEquals(0.6449, outcomeMarginal_t2.getValue((match1v2Time2VarId, 0)), 0.0001)
    assertEquals(0.3550, outcomeMarginal_t2.getValue((match1v2Time2VarId, 1)), 0.0001)

    val skill1_t0_marginal = ep.marginal(player1Time0VarId).asInstanceOf[GaussianFactor]
    assertEquals(10.2143, skill1_t0_marginal.m, 0.0001)
    assertEquals(54.6463, skill1_t0_marginal.v, 0.0001)

    val skill1_t1_marginal = ep.marginal(player1Time1VarId).asInstanceOf[GaussianFactor]
    assertEquals(10.2143, skill1_t1_marginal.m, 0.0001)
    assertEquals(72.0075, skill1_t1_marginal.v, 0.0001)

    val skill1_t2_marginal = ep.marginal(player1Time2VarId).asInstanceOf[GaussianFactor]
    assertEquals(10.2143, skill1_t2_marginal.m, 0.0001)
    assertEquals(89.3686, skill1_t2_marginal.v, 0.0001)

    val skill2_t0_marginal = ep.marginal(player2Time0VarId).asInstanceOf[GaussianFactor]
    assertEquals(3.7426, skill2_t0_marginal.m, 0.0001)
    assertEquals(44.8959, skill2_t0_marginal.v, 0.0001)

    val skill2_t1_marginal = ep.marginal(player2Time1VarId).asInstanceOf[GaussianFactor]
    assertEquals(5.0194, skill2_t1_marginal.m, 0.0001)
    assertEquals(53.8643, skill2_t1_marginal.v, 0.0001)

    val skill2_t2_marginal = ep.marginal(player2Time2VarId).asInstanceOf[GaussianFactor]
    assertEquals(5.0194, skill2_t2_marginal.m, 0.0001)
    assertEquals(71.2254, skill2_t2_marginal.v, 0.0001)
  }

}
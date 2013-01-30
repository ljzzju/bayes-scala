package dk.bayes.gaussian.localisation1d

import org.junit._
import Assert._
import dk.bayes.gaussian.CanonicalGaussian
import dk.bayes.gaussian.Linear._
import dk.bayes.gaussian.Gaussian
import dk.bayes.gaussian.LinearGaussian

class StaticLocalisationCanonicalGaussianTest {

  val priorProb = Gaussian(mu = 3, sigma = 1.5)
  val emissionProb = LinearGaussian(a = 1, b = 0, sigma = 0.9)

  @Test def single_observation {

    val locationId = 1
    val observationId = 2

    val location = CanonicalGaussian(locationId, priorProb.mu, priorProb.sigma)
    val observation = CanonicalGaussian(Array(locationId, observationId), emissionProb.b, emissionProb.sigma, Matrix(emissionProb.a))

    val locationPosterior = (location * observation).withEvidence(observationId, 0.6)

    assertEquals(1.5, locationPosterior.getMu.at(0), 0.001)
    assertEquals(0.5625, locationPosterior.getSigma.at(0), 0.001)

  }

  @Test def two_observations {

    val locationId = 1
    val observation1Id = 2
    val observation2Id = 3

    val location = CanonicalGaussian(locationId, priorProb.mu, priorProb.sigma)

    val observation1 = CanonicalGaussian(Array(locationId, observation1Id), emissionProb.b, emissionProb.sigma, Matrix(emissionProb.a))
    val observation2 = CanonicalGaussian(Array(locationId, observation2Id), emissionProb.b, emissionProb.sigma, Matrix(emissionProb.a))

    val jointProb = location * observation1 * observation2
    val locationPosterior = jointProb.withEvidence(observation1Id, 0.6).withEvidence(observation2Id, 0.62)

    //Alternative approach - applying evidence in a serial order
    //val locationPosterior = (location * observation1).withEvidence(observation1Id, 0.6) * observation2.withEvidence(observation2Id, 0.62)

    assertEquals(1.161, locationPosterior.getMu.at(0), 0.001)
    assertEquals(0.346, locationPosterior.getSigma.at(0), 0.001)

  }

  @Test def multiple_100K_observations {

    val locationId = 1
    val observationId = 2

    val location = CanonicalGaussian(locationId, priorProb.mu, priorProb.sigma)
    val observation = CanonicalGaussian(Array(locationId, observationId), emissionProb.b, emissionProb.sigma, Matrix(emissionProb.a))

    val lastLocation = (1 to 100).foldLeft(location) { (currLocation, i) =>
      (currLocation * observation).withEvidence(observationId, 0.6)
    }

    assertEquals(0.614, lastLocation.getMu.at(0), 0.001)
    assertEquals(0.008, lastLocation.getSigma.at(0), 0.001)
  }
}
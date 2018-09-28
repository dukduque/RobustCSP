package RobCSP.InstanceGenerator;

import umontreal.iro.lecuyer.probdist.GammaDist;
import umontreal.iro.lecuyer.rng.MRG32k3a;
import umontreal.iro.lecuyer.rng.RandomStream;

public class RandomTests {
	public static void main(String[] args) {
		RandomStream random_mean  = new MRG32k3a();
		

		double alpha = 2; 
		double lambda = 0.10;
		System.out.println(GammaDist.getMean(alpha, lambda));
		double mean = 0;
		for (int i = 0; i < 10000; i++) {

			mean +=GammaDist.inverseF(alpha,  lambda , 100,  random_mean.nextDouble());
		}
		
		System.out.println(mean/10000);
		random_mean.resetStartStream();
		mean = 0;
		for (int i = 0; i < 10000; i++) {

			mean +=GammaDist.inverseF(alpha,  lambda , 100,  random_mean.nextDouble());
		}
		System.out.println(mean/10000);
	}
}

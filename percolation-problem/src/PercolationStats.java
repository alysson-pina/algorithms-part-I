/****************************************************************************
 *  Author: Alysson Ferreira
 *  Date: 7-28-2014
 *  Compilation:  javac PercolationVisualizer.java
 *  Execution:    java PercolationVisualizer input.txt
 *  Dependencies: Percolation.java StdDraw.java In.java
 *
 *  This program takes two command-line arguments N and T, performs T independent computational experiments on an N-by-N grid.
 *  Then it prints to standard output the mean, standard deviation, and the 95% confidence interval for the percolation threshold.
 *
 * It relies on the following assumptions: 
 *- T >= 1, N >= 1.
 *- T == 1, standard deviation is not applicable.
 *- T < 10, T is not big enough to print a reliable confidence interval for the percolation threshold.
 ****************************************************************************/

public class PercolationStats {
	private int T, N;
	
	private static double[] fractionSites;
	private static double mean;
	private static double stddev;
	private static double confidenceLo;
	private static double confidenceHi;
	
	 // perform T independent computational experiments on an N-by-N grid
	public PercolationStats(int Nn, int Tt) throws IllegalArgumentException {
		if (Nn <= 0 || Tt <= 0)
			throw new IllegalArgumentException();
		
		T = Tt;
		N = Nn;
		
		//instantiate class properties
		fractionSites = new double[T];
		
		//execute T times the percolation NxN grid problem 
		for (int i = 0; i < T; i++){
			Percolation perc = new Percolation(N);
			fractionSites[i] = solvePercolationProblem(perc, N);
		}
	}
	
	// sample mean of percolation threshold
	public double mean() {
		double sum = 0;
		
		for (int i = 0; i < T; i++){
			sum += fractionSites[i];
		}
		
		return sum / T;
	}
	
	// sample standard deviation of percolation threshold
	public double stddev() {
		double sum = 0;
		
		// handle pathological case
		if (T == 1)
			return Double.NaN;
		
		//calculate standard deviation
		for (int i = 0; i < T; i++){
			sum += Math.pow(fractionSites[i] - mean, 2);
		}
		
		return sum /(T - 1);
	}
	
	// returns lower bound of the 95% confidence interval
	public double confidenceLo() {
		double result = 0;
		
		// handle pathological case where T is not sufficient large
		if (T < 10)
			return Double.NaN;
		
		//calculate lower bound
		result = mean -((1.96 * Math.sqrt(stddev)) / Math.sqrt(T));
		
		return result;
	}
	
	// returns upper bound of the 95% confidence interval
	public double confidenceHi() {
		double result = 0;
		
		// handle pathological case where T is not sufficient large
		if (T < 10)
			return Double.NaN;
		
		//calculate lower bound
		result = mean +((1.96 * Math.sqrt(stddev)) / Math.sqrt(T));
		
		return result;
	}
   
	// test client, described below
	public static void main(String[] args){
		int N = Integer.parseInt(args[0]), 
			T = Integer.parseInt(args[1]);
		
		PercolationStats percStatus = new PercolationStats(N, T);
		
		//calculate mean, standard deviation and 95% confidence interval
		mean = percStatus.mean();
		stddev = percStatus.stddev();
		confidenceLo = percStatus.confidenceLo();
		confidenceHi = percStatus.confidenceHi();
		
		//print values on standard output
		System.out.println("mean                    = " + mean);
		System.out.println("mean %                  = " + mean * 100 / (N * N));
		System.out.println("stddev                  = " + stddev);
		System.out.println("95% confidence interval = " + confidenceLo + ", " + confidenceHi);
	}
	
	//keeps opening new sites on the grid until system percolates
	private double solvePercolationProblem(Percolation perc, int N){
		int i, j, 
			totalFractions = 0; // counter of total number of opened sites.
		
		while(!perc.percolates() && totalFractions <= N * N){
			i = (int)(StdRandom.uniform() * N) + 1;
            j = (int)(StdRandom.uniform() * N) + 1;
            perc.open(i, j);
            
            //System.out.println( "i,j = " + i + "," + j );
            
            totalFractions ++;
		}
			
		return totalFractions / N;
	}
}
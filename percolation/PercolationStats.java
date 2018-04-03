import java.awt.Point;
import java.util.List;
import java.util.Random;

import javax.swing.JOptionPane;

/**
 * Compute statistics on Percolation afte performing T independent experiments on an N-by-N grid.
 * Compute 95% confidence interval for the percolation threshold, and  mean and std. deviation
 * Compute and print timings
 * 
 * @author Kevin Wayne
 * @author Jeff Forbes
 * @author Josh Hug
 */

public class PercolationStats {
	public static int RANDOM_SEED = 1234;
	public static Random ourRandom = new Random(RANDOM_SEED);
	private IPercolate myPerc;
	private double[] threshold;
	private int times;
	
	// TODO Add methods as described in assignment writeup
	// perform T independent experiments on an N-by-N grid
	public PercolationStats(int N, int T){
		if(N <= 0 || T<=0)
			throw new IllegalArgumentException(N+" or "+T+" is illegal argument!");
		threshold = new double[T];
		times = T;
		for(int t=0; t<T; t++){
			double count = 0.0;
			//myPerc = new PercolationDFS(N);
			myPerc = new PercolationUF(N);
			PercolationVisualizer pv = new PercolationVisualizer(N, myPerc);
			List<Point> sites = pv.getShuffledCells(ourRandom);
			for(Point cell: sites){
				myPerc.open(cell.y, cell.x);
				count++;
				if (myPerc.percolates()){
					threshold[t] = count/(N*N);
					break;
				}
			}	
		}
	}  
	
	// sample mean of percolation threshold
	public double mean(){
		double total= 0.0;
		for(int i = 0; i < times; i++){
			total+=threshold[i];
		}
		return total/times;
	}      
	
	// sample standard deviation of percolation threshold
	public double stddev(){
		double mean = mean();
		double total =0.0; 
		for(int i = 0; i < times; i++){
			total += (threshold[i]-mean)*(threshold[i]-mean);
		}
		return Math.sqrt(total/(times-1));
	}             
	
	// low  endpoint of 95% confidence interval
	public double confidenceLow(){
		double mean = mean();
		double sd = stddev();
		return mean - (1.96*sd/Math.sqrt(times)); 
	}          
	
	// high endpoint of 95% confidence interval
	public double confidenceHigh(){
		double mean = mean();
		double sd = stddev();
		return mean + (1.96*sd/Math.sqrt(times)); 
	}   
	
	// print out values for testing &  analysis
	public static void main(String[] args){
		double start = System.currentTimeMillis();
		PercolationStats stats = new PercolationStats(20, 10);
		double end = System.currentTimeMillis();
		double time = (end - start) / 1000;
		System.out.println("It took "+ time);
		System.out.println(stats.mean());
		System.out.println(stats.stddev());
		System.out.println(stats.confidenceLow());
		System.out.println(stats.confidenceHigh());
	} 
}

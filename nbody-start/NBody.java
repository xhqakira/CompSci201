import java.util.*;
import java.io.*;

public class NBody {
	
	public static void main(String[] args){
		double T = 157788000.0;
		//double T = 1000000.0;
		//double T = 2000000.0;
		double dt = 25000.0;
		//double dt = 1000000;
		String pfile = "data/planets.txt";
		if (args.length > 2) {
			T = Double.parseDouble(args[0]);
			dt = Double.parseDouble(args[1]);
			pfile = args[2];
		}	
		//Planet[] planets = null;
		//double radius = 0.0;
		
		Planet[] planets = readPlanets(pfile);
		double radius = readRadius(pfile);
		
		StdDraw.setScale(-radius, radius);
		StdDraw.picture(0, 0,"./images/starfield.jpg");
		StdAudio.play("./audio/2001.mid");
		for(int i=0; i<planets.length; i++){
			planets[i].draw();
		}
		double time =0;
		
		for(time=0; time<T;){
			double[] xForces = new double[planets.length];
			double[] yForces = new double[planets.length];
			for(int i=0; i<planets.length; i++){
				xForces[i] = planets[i].calcNetForceExertedByX(planets);
				yForces[i] = planets[i].calcNetForceExertedByY(planets);
			}
			
			for(int i=0; i<planets.length; i++){
				planets[i].update(dt, xForces[i], yForces[i]);
				planets[i].draw();
			}
			
			
			StdDraw.setScale(-radius, radius);
			StdDraw.picture(0, 0,"./images/starfield.jpg");
			for(int i=0; i<planets.length; i++){
				planets[i].draw();
			}
			StdDraw.show(10);
			time+=dt;	
			
		}	
	
		System.out.printf("%d\n", planets.length);
		System.out.printf("%.2e\n", radius);
		for (int i = 0; i < planets.length; i++) {
		    System.out.printf("%11.4e %11.4e %11.4e %11.4e %11.4e %12s\n",
		   		              planets[i].myXPos, planets[i].myYPos, 
		                      planets[i].myXVel, planets[i].myYVel, 
		                      planets[i].myMass, planets[i].myFileName);	
		}
	}
	
	public static double readRadius(String filePath){
		File f = new File(filePath);
		Scanner scan =null;
		try {
			scan = new Scanner(f);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		int n = scan.nextInt();
		double radius = scan.nextDouble();
		return radius;
	}
	
	public static Planet[] readPlanets(String filePath){
		File f = new File(filePath);
		Scanner scan =null;
		try {
			scan = new Scanner(f);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		int n = scan.nextInt();
		double radius = scan.nextDouble();
		Planet[] allPlanets = new Planet[n];
		for(int i = 0;i < n; i++){
			double XPos = scan.nextDouble();
			double YPos = scan.nextDouble();
			double XVel = scan.nextDouble();
			double YVel = scan.nextDouble();
			double Mass = scan.nextDouble();
			String FileName = scan.next();
			allPlanets[i] = new Planet(XPos,YPos,XVel,YVel,Mass,FileName);
			//allPlanets[i] = p;
		}
		return allPlanets;
	}
}

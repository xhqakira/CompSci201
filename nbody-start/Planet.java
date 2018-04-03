import java.math.*;

public class Planet {
	double myXPos;
	double myYPos;
	double myXVel;
	double myYVel;
	double myMass;
	String myFileName;
	
	
	public Planet(double xP, double yP, double xV,
            double yV, double m, String img){
		myXPos = xP;
		myYPos = yP;
		myXVel = xV;
		myYVel = yV;
		myMass = m;
		myFileName = img;
	}
	
	/**
	 * Creates a new planet that is a copy of p
	 * @param p
	 */
	public Planet(Planet p){
		myXPos = p.myXPos;
		myYPos = p.myYPos;
		myXVel = p.myXVel;
		myYVel = p.myYVel;
		myMass = p.myMass;
		myFileName = p.myFileName;
	}
	
	
	public double calcDistance(Planet p){
		double dx = myXPos - p.myXPos;
		double dy = myYPos - p.myYPos;
		double r_squared= dx*dx + dy*dy;
		return Math.sqrt(r_squared);
	}
	
	
	public double calcForceExertedBy(Planet p){
		double G = 6.67*Math.pow(10, -11);
		double r = this.calcDistance(p);
		double Force = G*myMass*p.myMass/(r*r);
		return Force;
	}
	
	
	public double calcForceExertedByX(Planet p){
		double Force = this.calcForceExertedBy(p);
		double dx = p.myXPos - myXPos; 
		double r = this.calcDistance(p); 
		double Fx = Force*dx/r;
		return Fx;
	}
	public double calcForceExertedByY(Planet p){
		double Force = this.calcForceExertedBy(p);
		double dy = p.myYPos - myYPos; 
		double r = this.calcDistance(p); 
		double Fy = Force*dy/r;
		return Fy;
	}
	
	
	public double calcNetForceExertedByX(Planet[] allPlanets){
        double sumx = 0; 
		for(Planet p:allPlanets){
			if(! p.equals(this)){
				sumx+=this.calcForceExertedByX(p);
			}
		}
		return sumx;
	}
	
	public double calcNetForceExertedByY(Planet[] allPlanets){
        double sumy = 0; 
		for(Planet p:allPlanets){
			if(! p.equals(this)){
				sumy+=this.calcForceExertedByY(p);
			}
		}
		return sumy;
	}
	
	
	public void update(double seconds, double xforce, double yforce){
		double ax = xforce/myMass;
		double ay = yforce/myMass;
		double vnewx = myXVel+seconds*ax;
		double vnewy = myYVel+seconds*ay;
		double pnewx = myXPos+seconds*vnewx;
		double pnewy = myYPos+seconds*vnewy;
		myXPos = pnewx;
		myYPos = pnewy;
		myXVel = vnewx;
		myYVel = vnewy;
	}
	
	
	public void draw(){
		StdDraw.picture(myXPos, myYPos, "./images/"+myFileName);
	}
}

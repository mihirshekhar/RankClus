package com.cde.helper;
/**
 * This class defines pair datatype which is used when ID and distance are to be used and are used to sort according to distance
 * @author mihir
 *
 */
public class Pair {
	int Id;
	double dist;
	
	public Pair(int Id,double dist)
	{
		this. Id = Id;
		this.dist = dist;
		
	}
	
	public double getDist() {
		return dist;
	}
	public int getId() {
		return Id;
	}
	public void setDist(double dist) {
		this.dist = dist;
	}
	public void setId(int id) {
		Id = id;
	}

}

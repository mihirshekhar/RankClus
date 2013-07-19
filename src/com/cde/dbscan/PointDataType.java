package com.cde.dbscan;

import java.io.ObjectInputStream.GetField;
import java.util.Vector;
/**
 * This class is used by classes like DBSCANRank to keep all the values associated with a point
 * @author mihir
 *
 */
public class PointDataType {

	private int id;
	private double cRank;
	private double nRank;
	private double breakRank;
	private int noOfNearestNeighbourNeededToBreakCluster;
	private int noOfNearestNeighbourNeededForNoiseAndCluster;
	private Vector<Integer>nearestNeighbour;
	private int clusterNum;
	private double [] dimVal;
	private boolean nRankFlag ;
	private int noOfNoisePoints;
	//Set values of dimension for each dimension of point

	public PointDataType (int id,double [] dimVal,int clusterNum)
	{
		this.id = id;
		this.dimVal = dimVal;
		this.clusterNum = clusterNum;
		cRank = 0;
		nRank = 0;
		breakRank =0.0;
		nearestNeighbour = null;
		noOfNearestNeighbourNeededToBreakCluster=0;
		nRankFlag =false;
		noOfNearestNeighbourNeededForNoiseAndCluster=0;
		noOfNoisePoints = 0;
	}

	public int getNoOfNoisePoints() {
		return noOfNoisePoints;
	}
	public void setNoOfNoisePoints(int noOfNoisePoints) {
		this.noOfNoisePoints = noOfNoisePoints;
	}
	public boolean getnRankFlag(){
		return nRankFlag;
	}
	
	public void setnRankFlag(boolean nRankFlag){
		this.nRankFlag = nRankFlag;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public double getcRank() {
		return cRank;
	}
	public void setcRank(double cRank) {
		this.cRank = cRank;
	}
	public double getnRank() {
		return nRank;
	}
	public void setnRank(double nRank) {
		this.nRank = nRank;
	}
	public double getBreakRank() {
		return breakRank;
	}
	public void setBreakRank(double breakRank) {
		this.breakRank = breakRank;
	}
	public Vector<Integer> getNearestNeighbour() {
		return nearestNeighbour;
	}
	public void setNearestNeighbour(Vector<Integer> nearestNeighbour) {
		this.nearestNeighbour = nearestNeighbour;
	}
	public int getClusterNum() {
		return clusterNum;
	}
	public void setClusterNum(int clusterNum) {
		this.clusterNum = clusterNum;
	}

	public void setDimVal(double[] dimVal) {
		this.dimVal = dimVal;
	}
	public double[] getDimVal() {
		return dimVal;
	}
  public int getNoOfNearestNeighbourNeededToBreakCluster() {
	return noOfNearestNeighbourNeededToBreakCluster;
}
  public void setNoOfNearestNeighbourNeededToBreakCluster(
		int noOfNearestNeighbourNeededToBreakCluster) {
	this.noOfNearestNeighbourNeededToBreakCluster = noOfNearestNeighbourNeededToBreakCluster;
}
  public int getNoOfNearestNeighbourNeededForNoiseAndCluster() {
	return noOfNearestNeighbourNeededForNoiseAndCluster;
}
  public void setNoOfNearestNeighbourNeededForNoiseAndCluster(
		int noOfNearestNeighbourNeededForNoiseAndCluster) {
	this.noOfNearestNeighbourNeededForNoiseAndCluster = noOfNearestNeighbourNeededForNoiseAndCluster;
}
	public static double EuclideanDistance (PointDataType pdt1,PointDataType pdt2)
	{
		double dist =0.0 ;
		double [] dim1 = pdt1.getDimVal();
		double [] dim2 = pdt2.getDimVal();
		for(int i=0;i<dim1.length;i++)
		{
          dist = dist+((dim1[i]-dim2[i])*(dim1[i]-dim2[i]));
		}
        
		dist = Math.sqrt(dist);
		return dist;


	}

}

package com.cde.kRNN;

import java.io.ObjectInputStream.GetField;
import java.util.Vector;

import com.cde.dbscan.PointDataType;

public class PointkRNN {
	
	private int id;
	private int pttype;//+1 for core -1 for outlier and 0 for boundary
	private int clusterNum;
	private int clustersize;
	private double [] dim;
	private double core_rank;
	private double agglor_rank;
	private double boundary_rank;
	//private Vector<Integer>nearestNeighbour_corepoint;
	private Vector<Integer>nearestNeighbour;
	private Vector<Integer>kRNN;
	public PointkRNN (int id,double [] dim,int clusterNo)
	{
		this.id = id;
		this.dim = dim;
		this.clusterNum = clusterNo;
		this.pttype = 1000;
		this.agglor_rank=0;
		this.boundary_rank =0 ;
		this.core_rank=0;
		this.kRNN = new Vector<Integer>();
		this.nearestNeighbour = new Vector<Integer>();
		//this.nearestNeighbour = null;
		 ;
		//this.nearestNeighbour_corepoint = null;
				}
    public void setkRNN(Vector<Integer> kRNN) {
		this.kRNN = kRNN;
	}
    public void appendkRNN(int kRNN) {
    	
    	this.kRNN.add(kRNN);
		
	}
    public Vector<Integer> getNearestNeighbour() {
		return nearestNeighbour;
	}
    public void setNearestNeighbour(Vector<Integer> nearestNeighbour) {
		this.nearestNeighbour = nearestNeighbour;
	}
    public Vector<Integer> getkRNN() {
		return kRNN;
	}
	public void setAgglor_rank(double agglor_rank) {
		this.agglor_rank = agglor_rank;
	}
	public void setBoundary_rank(double boundary_rank) {
		this.boundary_rank = boundary_rank;
	}
	public void setClusterNum(int clusterNum) {
		this.clusterNum = clusterNum;
	}
	public void setClustersize(int clustersize) {
		this.clustersize = clustersize;
	}
	public void setCore_rank(double core_rank) {
		this.core_rank = core_rank;
	}
	public void setDim(double[] dim) {
		this.dim = dim;
	}
	public void setId(int id) {
		this.id = id;
	}
	public void setPttype(int pttype) {
		this.pttype = pttype;
	}
	public double getAgglor_rank() {
		return agglor_rank;
	}
	public double getBoundary_rank() {
		return boundary_rank;
	}
   public int getClusterNum() {
	return clusterNum;
}
	public double getCore_rank() {
		return core_rank;
	}
	public int getClustersize() {
		return clustersize;
	}
	public double[] getDim() {
		return dim;
	}
	public int getId() {
		return id;
	}
	public int getPttype() {
		return pttype;
	}
	
	public static double EuclideanDistance (PointkRNN pdt1,PointkRNN pdt2)
	{
		double dist =0.0 ;
		double [] dim1 = pdt1.getDim();
		double [] dim2 = pdt2.getDim();
		for(int i=0;i<dim1.length;i++)
		{
          dist = dist+((dim1[i]-dim2[i])*(dim1[i]-dim2[i]));
		}
        
		dist = Math.sqrt(dist);
		return dist;


	}
}

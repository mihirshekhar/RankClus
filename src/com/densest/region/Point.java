package com.densest.region;

import java.util.Vector;

import com.cde.dbscan.PointDataType;


public class Point {
	private int id;
	private Vector<Integer>nearestNeighbour;
	private double [] dimVal;
	private int degree_nn;
	private Vector<Integer>rnn;
	private int ClusterNo;
	private int degree_rnn;
	
	public Point(int id,double [] dimVal,int ClusterNo) {
		
		this.id = id;
		this.dimVal = dimVal;
		this.ClusterNo = ClusterNo;
		this.nearestNeighbour = new Vector<Integer>();
		nearestNeighbour = null;
		this.rnn = new Vector<Integer>();
		rnn = null;
		this.degree_rnn =0;
		this.degree_nn=0;
		
	}
	
	public void setClusterNo(int clusterNo) {
		ClusterNo = clusterNo;
	}
	public void setDimVal(double[] dimVal) {
		this.dimVal = dimVal;
	}
	public void setId(int id) {
		this.id = id;
	}
	public void setNearestNeighbour(Vector<Integer> nearestNeighbour) {
		this.nearestNeighbour = nearestNeighbour;
	}
	
	public void setDegree_nn(int degree_nn) {
		this.degree_nn = degree_nn;
	}
	public void setDegree_rnn(int degree_rnn) {
		this.degree_rnn = degree_rnn;
	}
	public void setRnn(Vector<Integer> rnn) {
		this.rnn = rnn;
	}
	public int getClusterNo() {
		return ClusterNo;
	}
	public int getDegree_nn() {
		return degree_nn;
	}
	public int getDegree_rnn() {
		return degree_rnn;
	}
	public double[] getDimVal() {
		return dimVal;
	}
	public int getId() {
		return id;
	}
	public Vector<Integer> getNearestNeighbour() {
		return nearestNeighbour;
	}
	public Vector<Integer> getRnn() {
		return rnn;
	}
	public static double EuclideanDistance (Point pdt1,Point pdt2)
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

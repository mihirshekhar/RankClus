package com.cde.kRNN;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import org.jgrapht.GraphPath;
import org.jgrapht.alg.DijkstraShortestPath;
import org.jgrapht.alg.EulerianCircuit;
import org.jgrapht.alg.FloydWarshallShortestPaths;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;
import org.jgrapht.graph.SimpleWeightedGraph;

import com.cde.dbscan.PointDataType;
import com.cde.helper.Pair;
import com.cde.helper.ReadDataSet;

public class CalculateCentralityRank {
	
	private String PathOfInputDataset;
	private int NoOfDimension ;
	private String CSVFileDelimiter ;
	private String PathToElki  ;
	private String PathToClusterOutput;
	private String ReportName;
	private int MinPts = 4;
	private double Epsilon = 5.7;
	private int IncrementVal;
	int cc_count = 0;

	private Vector<PointDataType>arr_id_dimval;
	private Vector<Vector<Integer>>arr_clus_id_dimVal;
	public CalculateCentralityRank (String PathOfInputDataset,int NoOfDimension,String PathToElki,
			String PathToClusterOutput,String OutputReportName,int MinPts,double Epsilon) throws IOException
	{
		this.PathOfInputDataset = PathOfInputDataset;
		this.NoOfDimension = NoOfDimension;
		this.CSVFileDelimiter = "\\s+";
		this.PathToElki = PathToElki;
		this.PathToClusterOutput = PathToClusterOutput;
		this.MinPts = MinPts;
		this.Epsilon = Epsilon;
		this.IncrementVal = 1;
		ReportName = OutputReportName;
		//graph = new SimpleDirectedWeightedGraph<Integer, DefaultWeightedEdge>(DefaultWeightedEdge.class);
		
		ReadDataSet rds = new ReadDataSet(PathOfInputDataset, NoOfDimension, PathToElki, PathToClusterOutput, MinPts, Epsilon);
		arr_id_dimval = rds.ReadDataSetAfterClustering();
		SetNearestNeighbour();
		arr_clus_id_dimVal = new Vector<Vector<Integer>>();
		ArrangeOuputVectorInCluster();
		System.out.println("Ids congregated to Cluster");
		String outputPath = PathToClusterOutput+"/ReportCentrality_"+ReportName+".csv";
		PrintCentralityRank(outputPath);
	}
	
	/**
	 * Calls FindCentralityRankForEachCluster.Then outputs results of each point in a file
	 * @throws IOException 
	 */
	public void PrintCentralityRank(String outputPath) throws IOException
	{
		FindCentralityRankForEachCluster();
		System.out.println("Printing Report");
		FileWriter stream = new FileWriter(outputPath, false);
		BufferedWriter bo = new BufferedWriter(stream);
		bo.write("dim1");
		for(int i=1;i<NoOfDimension;i++)
		{
			bo.write(",dim"+i);
		}
		bo.write(",Rank,logRank,ClusterNo,ClusterSize,noOfNearestNeighbour");
		bo.newLine();
		for(int i=0;i<arr_id_dimval.size();i++)
		{
			if(arr_id_dimval.get(i).getClusterNum()>-1)
			{
				double [] dim = arr_id_dimval.get(i).getDimVal();
				bo.write(dim[0]+"");
				for(int j=1;j<NoOfDimension;j++)
				{
					bo.write(","+dim[j]+"");

				}
				double cRank = arr_id_dimval.get(i).getcRank();
	            double logRank = Math.log10(cRank+1);
				int nn = arr_id_dimval.get(i).getNearestNeighbour().size();
				int clusterNo = arr_id_dimval.get(i).getClusterNum();
				int clusterSize = clusterSize = arr_clus_id_dimVal.get(clusterNo).size();
				bo.write(","+cRank+","+logRank+","+clusterNo+","+clusterSize+","+nn);
				bo.newLine();
			}
		}
		bo.close();
		stream.close();
		
	}
	
	/**
	 * Calls FloydMarshallAlgorithm iteratively on each cluster to get Centrality Rank
	 */
	public void FindCentralityRankForEachCluster()
	{
		for(int i=0;i<arr_clus_id_dimVal.size();i++)
		{
			Vector<Integer>cluster = new Vector<Integer>();
			cluster = arr_clus_id_dimVal.get(i);
			
			System.out.println(cluster.size());
			FloydMarshallAlgorithm(cluster);
			
		}
	}
	/**
	 * Creates a nn graph for a cluster ,runs  FloydMarshallAlgorithm on it and then returns shortest path.
	 * Also finds rank of each point
	 * @param cluster
	 */
	public void FloydMarshallAlgorithm (Vector<Integer>cluster)
	{
		SimpleDirectedWeightedGraph<Integer, DefaultWeightedEdge>graph = GenerateEdgeGraphForCluster(cluster);
		FloydWarshallShortestPaths<Integer, DefaultWeightedEdge> f = new FloydWarshallShortestPaths<Integer, DefaultWeightedEdge>(graph);
		++cc_count;
		
		System.out.println("Started for Cluster "+cc_count);
		System.out.println("Of size : "+cluster.size());
		for(int i=0;i<cluster.size();i++)
		{
		
			List<GraphPath<Integer, DefaultWeightedEdge>> s = f.getShortestPaths(i+1);
			for(int k=0;k<s.size();k++)
			{
		    	List<DefaultWeightedEdge> p = s.get(k).getEdgeList();
			
			    System.out.println(p);
			 if(p.size()>1)
			  {
				for(int j=0 ;j<p.size()-1;j++)
				{
					String temp = p.get(j).toString();
					System.out.println(temp);
					temp = temp.replaceAll("[()\\[\\]]", "");
					//System.out.println(temp);
					String edge_lastvertex =  temp.substring(((temp).indexOf(":")+1) );
					int edge = Integer.parseInt(edge_lastvertex.trim());
					System.out.println(edge);
					double rank = arr_id_dimval.get(edge-1).getcRank();
					rank = rank+IncrementVal;
					arr_id_dimval.get(edge-1).setcRank(rank);
				}
			}
			}
			
			
		}
		System.out.println("Ended for Cluster "+cc_count);
		
		
	}
	/**
	 * This function generates graph from nn points of an Id from cluster 
	 * @param cluster Integer Vector containing points in a cluster
	 * @return graph return a graph with edge between nearest neighbours (to reduce memory)
	 * with weight equal to distance
	 */
	public SimpleDirectedWeightedGraph<Integer, DefaultWeightedEdge> GenerateEdgeGraphForCluster (Vector<Integer>cluster)
	{
		SimpleDirectedWeightedGraph<Integer, DefaultWeightedEdge>graph = new SimpleDirectedWeightedGraph<Integer, DefaultWeightedEdge>(DefaultWeightedEdge.class);
		HashMap<Integer,Integer>map = new HashMap<Integer, Integer>();
		
		for(int i=0;i<cluster.size();i++)
		{
			graph.addVertex(cluster.get(i));
			map.put(cluster.get(i), 1);
		}
		for(int i=0;i<cluster.size();i++)
		{
			int id = cluster.get(i);
			Vector<Integer>nn = arr_id_dimval.get(id-1).getNearestNeighbour();
			for(int j=0;j<nn.size();j++)
			{
				PointDataType pdt1     = arr_id_dimval.get(id-1);
				PointDataType pdt2     = arr_id_dimval.get((nn.get(j)-1));
				if(map.containsKey(pdt2.getId()))
				{
				DefaultWeightedEdge e  = graph.addEdge(id,nn.get(j));
				double dist = PointDataType.EuclideanDistance(pdt1, pdt2);
				graph.setEdgeWeight(e, dist);
				}
			}	
		}
		return graph;
	}
	/**
	 * Arranges the arr_id_dimval in clusters and returns it as vector of vector.
	 */
	private void ArrangeOuputVectorInCluster()
	{

		File folder = new File(PathToClusterOutput+"/ClusterOutput");
		File files[] = folder.listFiles();
		int noOfCluster =files.length-3;
		for(int i=0;i<noOfCluster;i++)
		{
			Vector<Integer> temp = new Vector<Integer>();
			temp.add(1);
			arr_clus_id_dimVal.add(temp);

		}
		for(int i=0;i<arr_id_dimval.size();i++)
		{

			PointDataType pdt = arr_id_dimval.get(i);
			if(pdt.getClusterNum()>=0)
			{
				Vector<Integer>temp = arr_clus_id_dimVal.get(pdt.getClusterNum());
				if(temp.get(0)==1)
				{
					temp.remove(0);
					temp.add(pdt.getId());
				}
				else
				{
					temp.add(pdt.getId());  
				}

				arr_clus_id_dimVal.remove(pdt.getClusterNum());
				arr_clus_id_dimVal.add(pdt.getClusterNum(), temp);
			}
		}		
	}
	private void SetNearestNeighbour() {
		System.out.println("SetNearestNeighbour");
		for (int i=0;i<arr_id_dimval.size();i++)
		{
			Vector<Pair>vcpr = new Vector<Pair>();
			for (int j=0;j<arr_id_dimval.size();j++)
			{
				if(i==j)
					continue;
				else
				{
					double dist = PointDataType.EuclideanDistance(arr_id_dimval.get(i),arr_id_dimval.get(j));
					if(dist<=Epsilon)
					{
						Pair pr = new Pair(arr_id_dimval.get(j).getId(),dist);
						vcpr.add(pr);
					}
				}
			}
			if(vcpr.size()>0)
			{
				vcpr = Sort(vcpr);
				Vector<Integer>vci = new Vector<Integer>();
				for(int k=0;k<vcpr.size();k++)
				{
					vci.add(vcpr.get(k).getId());
				}
				arr_id_dimval.get(i).setNearestNeighbour(vci);
				vcpr = null;
				vci = null;
			}

		}

	}
	private Vector<Pair> Sort(Vector<Pair> vcpr) {
		//System.out.println("Sort ");
		Collections.sort(vcpr,new Comparator<Pair>()
				{

			@Override
			public int compare(Pair p1,
					Pair p2) {
				return Double.valueOf(p1.getDist()).compareTo(Double.valueOf(p2.getDist()));

			}


				});

		return vcpr;

	}
	

}

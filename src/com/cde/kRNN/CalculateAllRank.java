package com.cde.kRNN;

import java.io.File;
import java.io.IOException;
import java.util.Vector;

import com.cde.dbscan.PointDataType;
import com.cde.helper.ReadDataSet;

public class CalculateAllRank {

	private String PathOfInputDataset;
	private int NoOfDimension ;
	private String CSVFileDelimiter ;
	private String PathToElki  ;
	private String PathToClusterOutput;
	private String ReportName;
	private int MinPts ;
	private double Epsilon;
	private int k;

	private Vector<PointkRNN>arr_id_dimval;
	private Vector<Vector<Integer>>arr_clus_id_dimVal;

	public CalculateAllRank(String PathOfInputDataset,int NoOfDimension ,
			String CSVFileDelimiter,String PathToElki,String PathToClusterOutput
			,String OutputReportName,int MinPts,double Epsilon,int kRNN) throws IOException
			{

		this.PathOfInputDataset = PathOfInputDataset;
		this.NoOfDimension = NoOfDimension;
		this.CSVFileDelimiter = CSVFileDelimiter;
		this.PathToElki =PathToElki;
		this.PathToClusterOutput =PathToClusterOutput;
		this.ReportName = OutputReportName;
		this.MinPts = MinPts;
		this.Epsilon = Epsilon;
		this.k = kRNN;

		CalculatekRNN ckrnn = new CalculatekRNN(PathOfInputDataset, NoOfDimension, CSVFileDelimiter, PathToElki, PathToClusterOutput, OutputReportName, MinPts, Epsilon, kRNN);
		arr_id_dimval = ckrnn.ReturnkRNNPoints();
		ArrangeOuputVectorInCluster();

			}



	/**
	 * Calculates core rank of a point 
	 * An Core point will be defined as a point that has atleast k kRNN points.
	 * Core Rank is defined as no of core points in epsilon distance of P
	 */
	public void CalculateCoreAndBreakRank ()
	{
		for(int i=0;i<arr_id_dimval.size();i++)
		{
			Vector<Integer>nn = arr_id_dimval.get(i).getNearestNeighbour();
			double core_rank = 0;
			double break_rank = 0;
			for(int j=0;j<nn.size();j++)
			{
				int pttype = arr_id_dimval.get(nn.get(j)-1).getPttype();
				if(pttype==1)
				{
					++core_rank;
				}
				else if(pttype==0)
				{
					++break_rank;
				}

			}
			arr_id_dimval.get(i).setCore_rank(core_rank);
			arr_id_dimval.get(i).setBoundary_rank(break_rank);

		}

	}
	
	
	/**
	 * Groups all the points in a cluster and form a vector of cluster in O(n)time 
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

			PointkRNN pdt = arr_id_dimval.get(i);
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




}

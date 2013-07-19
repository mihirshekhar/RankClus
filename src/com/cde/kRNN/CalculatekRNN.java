package com.cde.kRNN;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.Vector;

import com.cde.dbscan.PointDataType;
import com.cde.helper.Pair;
import com.cde.helper.ReadDataSet;

public class CalculatekRNN {

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

	public CalculatekRNN (String PathOfInputDataset,int NoOfDimension ,
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

		ReadDataSet rds = new ReadDataSet(PathOfInputDataset, NoOfDimension, PathToElki, PathToClusterOutput, MinPts, Epsilon);
		arr_id_dimval = rds.ReadDataSetAfterClustering("");
		SetNearestNeighbour();
		arr_clus_id_dimVal = new Vector<Vector<Integer>>();
		ArrangeOuputVectorInCluster();
			}
	
	  public Vector<PointkRNN>ReturnkRNNPoints()
	  {
		  GetkRNNForDataset();
		  return arr_id_dimval;
	  }
	/**
	 * Going through all points,increment kRNN count calculating k nearest neighbours for each point
	 * But we have clustering already done,so we can assume conditions for kRNN already done.
	 * We just need to find out which one is core point and which one is boundary point.
	 * 
	 * Will need to prune how to derive value of k automatically.
	 * We can use RECORD to find out k easily
	 */
      private void GetkRNNForDataset()
      {
    	 for(int i=0;i<arr_clus_id_dimVal.size();i++) 
    	 {
    		 Vector<Integer>temp_Clus = arr_clus_id_dimVal.get(i);
    		 
    		  for(int j=0;j<temp_Clus.size();j++)
    		  {
    			  int temp_id = temp_Clus.get(j);
    			  kRNN(temp_id,temp_Clus);
    			  
    		  }
    	 }
    	 int []count = new int [3];//used to get an statistics on number of points in dtaset
    	for(int i=0;i<arr_id_dimval.size();i++)
    	{
    		if(arr_id_dimval.get(i).getkRNN().size()>=k && arr_id_dimval.get(i).getClusterNum()>-1)
    		{
    			arr_id_dimval.get(i).setPttype(1);count[0]++;
    		}
    		else if (arr_id_dimval.get(i).getkRNN().size()<k && arr_id_dimval.get(i).getClusterNum()>-1)
    		{
    			arr_id_dimval.get(i).setPttype(0);count[1]++;

    		}
    		else
    		{
    			arr_id_dimval.get(i).setPttype(-1);count[2]++;

    		}
    	}
    	//5532   3617    851 statistics for k =5 in DS3
    	//System.out.println(count[0]+"   "+count[1]+"    "+count[2]);
      }
	
	/**
	 * Gets a point and cluster vector, and finds K nearest neighbours of it.
	 * Updates corressponding k nearnest neighbours to it 
	 * @param point
	 */
	private void kRNN(int point,Vector<Integer>temp_Clus)
	{
		PointkRNN perm = arr_id_dimval.get(point-1);
		arr_id_dimval.get(point-1).setClustersize(temp_Clus.size());
		
		Vector<Pair>vcpr = new Vector<Pair>();
		for(int i=0;i<temp_Clus.size();i++)
		{
			if(point == temp_Clus.get(i))
			continue;
			else
			{	
			PointkRNN tmp = arr_id_dimval.get(temp_Clus.get(i)-1);
			double dist = PointkRNN.EuclideanDistance(perm, tmp);
			Pair pr = new Pair(temp_Clus.get(i), dist);
			vcpr.add(pr);
			}
		}
		vcpr = Sort(vcpr);
		for(int i=0;i<k && i<vcpr.size();i++)
		{
			int id = vcpr.get(i).getId();
			
            PointkRNN pts = arr_id_dimval.get(id-1);
            Vector<Integer>nn = pts.getkRNN();
            nn.add(point);
            System.out.println(nn.size());
            arr_id_dimval.get(id-1).setkRNN(nn);
			
		}


	}
	private Vector<Pair> Sort(Vector<Pair> vcpr) {
		///System.out.println("Sort ");
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
	/**
	 * Sets nearest neighbour of al the points
	 */
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
						double dist = PointkRNN.EuclideanDistance(arr_id_dimval.get(i),arr_id_dimval.get(j));
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
					
					vcpr = null;
					vci = null;
				}

			}

		}
	

}

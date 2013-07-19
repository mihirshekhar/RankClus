package com.densest.region;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.Vector;

import com.cde.dbscan.PointDataType;
import com.cde.helper.Pair;
import com.cde.helper.ReadDataSet;
/**
 * 
 * @author mihirshekhar
 * Finds densest region according to NN.Exports the graph in gexf format.
 */
public class FindDensestRegionUsingNN {
	
	private String PathOfInputDataset;
	private int    NoOfDimension ;
	private String CSVFileDelimiter ;
	private String PathToElki  ;
	private String PathToClusterOutput;
	private String ReportName;
	private int    MinPts ;
	private double Epsilon ;
	private int threshold;
	
	private Vector<Point>arr_id_dimval;
	
	public FindDensestRegionUsingNN(String PathOfInputDataset,int NoOfDimension,String CSVFileDelimiter,String PathToElki,String PathToClusterOutput,String ReportName,int MinPts,double Epsilon,int threshold) throws IOException {
		
		this.PathOfInputDataset = PathOfInputDataset;
		this.NoOfDimension =NoOfDimension;
		this.CSVFileDelimiter = CSVFileDelimiter;
		this.PathToElki = PathToElki;
		this.PathToClusterOutput =  PathToClusterOutput;
		this.ReportName = ReportName;
	    this.MinPts = MinPts;
	    this.Epsilon= Epsilon;
	    if(threshold<2)
	    {
	    	this.threshold = MinPts;
	    }
	    else
	    {
	    	this.threshold = threshold;
	    }
	    ReadDataSet rds = new ReadDataSet(PathOfInputDataset, NoOfDimension, PathToElki, PathToClusterOutput, MinPts, Epsilon);
		arr_id_dimval = rds.ReadDataSetAfterClustering(5);
		SetNearestNeighbour();
		
	}
	  public void ExportGraphInArffFormat () throws IOException
	  {
		  String filString = PathToClusterOutput+"/Density_"+ReportName+".gexf";
		  FileWriter fw = new FileWriter(filString);
		  BufferedWriter bw = new BufferedWriter(fw);
		  bw.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		  bw.newLine();
		  bw.write("<gexf xmlns=\"http://www.gephi.org/gexf\" xmlns:viz=\"http://www.gephi.org/gexf/viz\" version = \"1.3\">");
		  bw.newLine();
		  bw.write("<graph type=\"static\">");
		  bw.newLine();
		  bw.write("<nodes>");
		  bw.newLine();
		  int node =-1;
		  /*<node id="0">
		  <viz:position x="-113.203964" y="30.300962" z="0.0"/>
		  </node>*/
		  for(int i=0;i<arr_id_dimval.size();i++)
		  {
			  ++node;
			  String str1 = "<node id=\""+arr_id_dimval.get(i).getId()+"\">";
			  double [] dim=arr_id_dimval.get(node).getDimVal();
			  String str2="";
			  if(dim.length==1)
			  {
				   str2 = "<viz:position x=\""+dim[0]+"\" y=\""+0.0+"\" z=\""+0.0+"\"/>";
			  }
			   else if(dim.length==2)
			  {
			   str2 = "<viz:position x=\""+dim[0]+"\" y=\""+dim[1]+"\" z=\""+0.0+"\"/>";
			  }
			  else if(dim.length==3)
			  {
				  str2= "<viz:position x=\""+dim[0]+"\" y=\""+dim[1]+"\" z=\""+dim[2]+"\"/>";  
			  }
			  else
			  {
				  System.out.println("Can handle only upto three dimensiuons ");
			  }
			  String str4 = "<viz:size value=\"0.01\"/>";
			  String str3 = "</node>";
			  bw.write(str1);
			  bw.newLine();
			  bw.write(str2);
			  bw.newLine();
			  bw.write(str4);
			  bw.newLine();
			  bw.write(str3);
			  bw.newLine();
		  }
		  System.out.println(node);
		     bw.write("</nodes>");
		     bw.newLine();bw.newLine();bw.newLine();bw.newLine();
		     bw.write("<edges>");
		     bw.newLine();
		     int enode =-1;
		     for(int i=0;i<arr_id_dimval.size();i++)
		     {
		    	 Vector<Integer>nn = arr_id_dimval.get(i).getNearestNeighbour();
		    	 System.out.println(nn.size());
		    	  if(nn.size()>=threshold)
		    	  {
		    		for(int j=0;j<nn.size();j++)
		    		{
		    			//<edge id="0" source="0" target="1"/>

		    			++enode;
		    			String str1 = "<edge id=\""+enode+"\" source=\""+arr_id_dimval.get(i).getId()+"\" target=\""+nn.get(j)+"\"/>";
		    			bw.write(str1);
		    			bw.newLine();
		    			
		    		}
		    		  
		    		  
		    	  }
		    	 
		     }
		     bw.write("</edges>");
		     bw.newLine();
		     bw.write("</graph>");
		     bw.newLine();
		     bw.write("</gexf>");
		     
		     bw.close();
		     fw.close();
		  
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
					double dist = Point.EuclideanDistance(arr_id_dimval.get(i),arr_id_dimval.get(j));
					if(dist<=Epsilon)
					{
						Pair pr = new Pair(arr_id_dimval.get(j).getId(),dist);
						vcpr.add(pr);
					}
				}
			}
			
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
	/**
	 * Sorts nearest neighbour vector according to distance
	 * @param vcpr vector containing all nearest neighbours with distance
	 * @return
	 */
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
	

}

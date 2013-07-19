package com.cde.dbscan;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Vector;

import com.cde.helper.Pair;
import com.cde.helper.ReadDataSet;
/**
 * This class get called from main and usd to generate clustering rank for points using DBSCAn CLUSTERING ALGORITHM
 * @author mihir
 *
 */
public class DbscanRank {

	private String PathOfInputDataset;
	private int NoOfDimension ;
	private String CSVFileDelimiter ;
	private String PathToElki  ;
	private String PathToClusterOutput;
	private String ReportName;
	private int MinPts = 4;
	private double Epsilon = 5.7;


	private Vector<PointDataType>arr_id_dimval;
	private Vector<Vector<Integer>>arr_clus_id_dimVal;
	/**
	 * to invoke this class function just call constructor with following parameters
	 * @param PathOfInputDataset Path of CSV DAtafile
	 * @param NoOfDimension No Of Dimenesions in dataset file
	 * @param CSVFileDelimiter What delelimiter is in csvDAtafile currently only ","
	 * @param PathToElki Path of elki jar file
	 * @param PathToClusterOutput PAth of folder where REport will be generated,It is also needed for temp processing
	 * @param OutputReportName Name of Report file Ued to distinguish between different file names
	 * @param MinPts minpts required for clustering 
	 * @param Epsilon similar 
	 * @throws IOException
	 */
	public DbscanRank (String PathOfInputDataset,int NoOfDimension ,
			String CSVFileDelimiter,String PathToElki,String PathToClusterOutput
			,String OutputReportName,int MinPts,double Epsilon) throws IOException
			{
		this.PathOfInputDataset = PathOfInputDataset;
		this.NoOfDimension = NoOfDimension;
		this.CSVFileDelimiter = CSVFileDelimiter;
		this.PathToElki =PathToElki;
		this.PathToClusterOutput =PathToClusterOutput;

		this.ReportName = OutputReportName;
		this.MinPts = MinPts;
		this.Epsilon = Epsilon;

		ReadDataSet rds = new ReadDataSet(PathOfInputDataset, NoOfDimension, PathToElki, PathToClusterOutput, MinPts, Epsilon);
		arr_id_dimval = rds.ReadDataSetAfterClustering();
		System.out.println("No of Ttl points "+ arr_id_dimval.size());
		SetNearestNeighbour();
		System.out.println("Nearest Neighbour Read");
		arr_clus_id_dimVal = new Vector<Vector<Integer>>();
		ArrangeOuputVectorInCluster();
		System.out.println("Ids congregated to Cluster");
		CalculateAndOutputRank();
		System.out.println("Calculating Ranks completed");
		String outputPath = PathToClusterOutput+"/Report_"+ReportName+".csv";
		PrintDBScanClusteringReport(outputPath);
		System.out.println("Completed");
			}
	/**
	 * Outputs ranks in a CSV file
	 * @param outputPath
	 * @throws IOException
	 */
	private void PrintDBScanClusteringReport(String outputPath) throws IOException {
		System.out.println("Printing Report");
		FileWriter stream = new FileWriter(outputPath, false);
		BufferedWriter bo = new BufferedWriter(stream);
		bo.write("dim1");
		for(int i=1;i<NoOfDimension;i++)
		{
			bo.write(",dim"+i);
		}
		bo.write(",Cluster Rank,Break Rank,NoOfNNRequiredNNForClusterBreak,"
				+ "Noise Rank,NoOfNNRequiredForNoiseBreak,AbsoluteClusterRank,AbsoluteNoiseRank,"
				+ "AbsoluteRank,AbsoluteRankNormalised,ClusterNO,ClusterSize,NoOfNearestNeighbour,NoOfNoisePointsUsedToCalculateNoiseRank");
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
				double breakRank =arr_id_dimval.get(i).getBreakRank();
				int nnc = arr_id_dimval.get(i).getNoOfNearestNeighbourNeededToBreakCluster();
				double nRank =  arr_id_dimval.get(i).getnRank();
				int nnr = arr_id_dimval.get(i).getNoOfNearestNeighbourNeededForNoiseAndCluster();
				int clusterNo = arr_id_dimval.get(i).getClusterNum();
				int clusterSize = clusterSize = arr_clus_id_dimVal.get(clusterNo).size();;
				double absoluteClusterRank = (cRank*breakRank)/(nnc+1);
				int noOfNoisePoints = arr_id_dimval.get(i).getNoOfNoisePoints();
				double absoluteNoiseRank = (nRank)*noOfNoisePoints/(nnr+1);
				double Absolute_Rank = (absoluteClusterRank)*absoluteNoiseRank;
				double normalisedRank = Absolute_Rank/(clusterSize);
				int nno = arr_id_dimval.get(i).getNearestNeighbour().size();
				bo.write(","+cRank+","+breakRank+","+nnc+","+ nRank+","+nnr+","+absoluteClusterRank+","+absoluteNoiseRank+","+
						Absolute_Rank+","+normalisedRank+","+clusterNo+","+clusterSize+","+nno+","+noOfNoisePoints);
				bo.newLine();
			}
		}
		bo.close();
		stream.close();

	}
	/**
	 * Used by CalculateAndOutputRank(); to remove one point and cluster .It uses WriteInputFile(id, temp_Clus,nn, excluded,path);
		WriteScript(path,scriptPath,tempOutFolder);
		RunScript(scriptPath);  to generate statistics
	 * @param id
	 * @param temp_Clus
	 * @param nn
	 * @param excluded
	 * @return
	 * @throws IOException
	 */
	private double[] RemovePointAndCluster(int id, Vector<Integer> temp_Clus,
			Vector<Integer> nn, HashMap<Integer,Integer>excluded) throws IOException {

		String path = PathToClusterOutput+"/tempDataSet";
		String scriptPath = PathToClusterOutput+"/tempScript";
		String tempOutFolder = PathToClusterOutput+"/temp";
		WriteInputFile(id, temp_Clus,nn, excluded,path);
		WriteScript(path,scriptPath,tempOutFolder);
		RunScript(scriptPath);
		return CalculateStatistics(tempOutFolder,id,excluded,nn);

	}
	/**
	 * Calculates vital statistics from each clustering like cRAnk,nRAnk,breakRAnk.It has automatic mechanism such that it 
	 * automatically updates point with respective ranks when required
	 * @param tempOutFolder
	 * @param id
	 * @param excluded
	 * @param nn
	 * @return
	 * @throws IOException
	 */
	private double[] CalculateStatistics(String tempOutFolder,int id,HashMap<Integer,Integer>excluded,Vector<Integer> nn) throws IOException {


		double cRank =0.0;
		double nRank =0.0;
		double breakRank = 0.0;
		//int [] count={1,1,1,1,1};
		int count = 1;
		int tmpCount=1;
		int ncount=0;
		File folder = new File(tempOutFolder);
		File files[] = folder.listFiles();
		if(files.length>4)
		{
			cRank = (double)(files.length-4)/(double)excluded.size();
			System.out.println("Setting CRank = "+cRank);
			arr_id_dimval.get(id-1).setNoOfNearestNeighbourNeededToBreakCluster(excluded.size()-1);
			double crank_old = arr_id_dimval.get(id-1).getcRank();
			double cRankNew =crank_old+cRank;
			arr_id_dimval.get(id-1).setcRank(cRankNew);
			for(int i=0;i<excluded.size()-1;i++)
			{
				int nn_Id = nn.get(i);
				crank_old = arr_id_dimval.get(nn_Id-1).getcRank();
				cRankNew =crank_old+cRank;
				arr_id_dimval.get(nn_Id-1).setcRank(cRankNew);
			}
			//read all files in output folder
			for(int i=0;i<files.length;i++)
			{

				String fileName = files[i].getName();
				//take files as cluster_3
				if(fileName.contains("_"))
				{
					String arr[] = fileName.split("_");
					arr[1]= arr[1].replace(".txt", "");
					FileReader fr = new FileReader(files[i].getAbsolutePath());
					BufferedReader br = new BufferedReader(fr);
					String line;
					while((line = br.readLine())!=null)
					{
						if(line.contains("ID="))
						{
							++tmpCount;
						}
					}
					count=count*tmpCount;
					tmpCount =1;
					br.close();
					fr.close();
				}
			}

			breakRank = Math.sqrt(count);
			System.out.println("Break Rank");
			arr_id_dimval.get(id-1).setBreakRank(breakRank);
		}
		if(arr_id_dimval.get(id-1).getnRankFlag()==false)
		{
			File f = new File(tempOutFolder+"/noise.txt");
			if(f.exists())
			{
				FileReader	fr = new FileReader(tempOutFolder+"/noise.txt");
				BufferedReader br = new BufferedReader(fr);
				String line;
				while((line = br.readLine())!=null)
				{
					if(line.contains("ID="))
					{
						++ncount;
					}
				}

				if(ncount>0)
				{
					nRank = (double)(ncount+1)/(double)excluded.size();
					arr_id_dimval.get(id-1).setnRankFlag(true);
					arr_id_dimval.get(id-1).setNoOfNearestNeighbourNeededForNoiseAndCluster(excluded.size()-1);
					double nrank_old = arr_id_dimval.get(id-1).getnRank();
					double nRankNew =nrank_old+nRank;
					arr_id_dimval.get(id-1).setnRank(nRankNew);
					arr_id_dimval.get(id-1).setNoOfNoisePoints(ncount);
					for(int i=0;i<excluded.size()-1;i++)
					{
						int nn_Id = nn.get(i);
						nrank_old = arr_id_dimval.get(nn_Id-1).getnRank();
						nRankNew =nrank_old+nRank;
						arr_id_dimval.get(nn_Id-1).setnRank(nRankNew);
					}

				}
				br.close();
				fr.close();
			}
		}





		double [] arr = {cRank,breakRank,nRank};
		return arr;
	}

	/**
	 * Runs DBSCAN Clusterting temp script generated
	 * @param PathToScriptFile
	 */
	private void RunScript(String PathToScriptFile) {

		String srpt = "sh "+PathToScriptFile;
		try
		{            
			Runtime rt = Runtime.getRuntime();
			Process proc = rt.exec(srpt);
			int exitVal = proc.waitFor();
			//System.out.println("Process exitValue: " + exitVal);
		} catch (Throwable t)
		{
			t.printStackTrace();
		}


	}
	/**
	 * Writes script for dbscan clustering through elki
	 * @param Path
	 * @param scriptPath
	 * @param tempOutFolder
	 * @throws IOException
	 */
	private void WriteScript(String Path,String scriptPath, String tempOutFolder) throws IOException {

		FileWriter stream = new FileWriter(scriptPath, false);
		BufferedWriter bo = new BufferedWriter(stream);
		bo.write("#!/bin/bash");
		bo.newLine();
		bo.write("rm -rf "+tempOutFolder);
		bo.newLine();
		bo.write("mkdir "+tempOutFolder);
		bo.newLine();
		bo.write("java -jar "+PathToElki+" KDDCLIApplication -algorithm clustering.DBSCAN -dbc.in "+ Path+ " -dbscan.epsilon "+ Epsilon+" -dbscan.minpts "+ MinPts+
				" -out "+tempOutFolder);
		System.out.println("Query : java -jar "+PathToElki+" KDDCLIApplication -algorithm clustering.DBSCAN -dbc.in "+ Path+ " -dbscan.epsilon "+ Epsilon+" -dbscan.minpts "+ MinPts+
				" -out "+tempOutFolder);

		bo.close();
		stream.close();

	}
	/**
	 * Writes the temp file needed to be executed i ELKI with some points removed
	 * @param id
	 * @param temp_Clus
	 * @param nn     
	 * @param excluded: points to exclude while clustering
	 * @param path
	 * @throws IOException
	 */
	private void WriteInputFile(int id, Vector<Integer> temp_Clus,
			Vector<Integer> nn, HashMap<Integer, Integer> excluded, String path) throws IOException {

		FileWriter fw = new FileWriter(path);
		BufferedWriter bw = new BufferedWriter(fw);
		int tmp=0;
		for(int i=0;i<temp_Clus.size();i++)
		{
			if(!excluded.containsKey(tmp=temp_Clus.get(i)))
			{
				double [] dim = arr_id_dimval.get(tmp-1).getDimVal();
				bw.write(""+dim[0]);
				for(int j=1;j<dim.length;j++)
				{
					bw.write(","+dim[j]);
				}
				bw.newLine();
			}
		}
		bw.close();
		fw.close();


	}
	/**
	 * iterates through all cluster,use Rank finding algo on every point
	 * @throws IOException
	 */
	private void CalculateAndOutputRank() throws IOException {

		int c=0;
		for(int i=0;i<arr_clus_id_dimVal.size();i++)
		{
			Vector<Integer>temp_Clus = arr_clus_id_dimVal.get(i);
			System.out.println("Processing Cluster No "+i +"of Size "+temp_Clus.size() );
			System.out.println("No Of Clusters left to process "+(arr_clus_id_dimVal.size()-i));


			for(int j=0;j<temp_Clus.size();j++)
			{

				System.err.println("Processing point no "+(++c) +"and cluster no Processing Cluster No "+i +"of Size "+temp_Clus.size());
				int id = temp_Clus.get(j);
				Vector <Integer>nn = arr_id_dimval.get(id-1).getNearestNeighbour();
				System.out.println("NoOf Nearest Neighbour "+arr_id_dimval.get(id-1).getNearestNeighbour().size());
				HashMap <Integer,Integer>excludeId = new HashMap<Integer, Integer>();
				excludeId.put(id, 1);

				double [] rank = RemovePointAndCluster(id,temp_Clus,nn,excludeId);
				int count = nn.size();
				while(count>0 && rank[0]==0 && (temp_Clus.size()-excludeId.size())>MinPts)
				{

					excludeId.put(nn.get(nn.size()-count), 1);
					rank = RemovePointAndCluster(id,temp_Clus,nn,excludeId);
					System.out.println("No Of nearest Neighbour Processed :"+(excludeId.size()-1));
					--count;
				}
				//				//add a loop for adding cRank and NRank to other nn points.This 
				//THis part taken care in statistics
				//				double cRank= arr_id_dimval.get(i-1).getcRank();
				//				cRank+=rank[0];
				//				double nRank = arr_id_dimval.get(i-1).getnRank();
				//				nRank+=rank[1];
				//				arr_id_dimval.get(i-1).setcRank(cRank);
				//				arr_id_dimval.get(i-1).setnRank(nRank);
				//				arr_id_dimval.get(i-1).setBreakRank(rank[2]);
				//				arr_id_dimval.get(i-1).setNoOfNearestNeighbourNeededToBreakCluster(nn.size()-count);

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


}

package com.cde.helper;

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

import com.cde.dbscan.PointDataType;
import com.cde.kRNN.PointkRNN;
import com.densest.region.Point;
/**
 * 
 * @author Mihir Shekhar,MS By Research(2012)
 * @Date 21/06/2013
 * @function This class contains various functions to read dataset 
 */
public class ReadDataSet {
	private String PathOfInputDataset;
	private int NoOfDimension;
	private String CSVFileDelimiter;
	private String PathToElki;
	private String PathToClusterOutput;
	private int MinPts;
	private double Epsilon;
	private String PathToScriptFile;

	/**
	 * Constructor 1 Will be used for ReadDatSetInCSV function
	 * @param PathOfInputDataset :Path to dataset file
	 * @param NoOfDimension      :Number of dimension in Dataset
	 * @param CSVFileDelimiter   :Delimiter to separate dimensions in Dataset File(File shouldnt contain headers)
	 */
	public ReadDataSet(String PathOfInputDataset,int NoOfDimension,String CSVFileDelimiter) {

		this.PathOfInputDataset = PathOfInputDataset;
		this.NoOfDimension = NoOfDimension;
		this .CSVFileDelimiter = CSVFileDelimiter;
	}
	/**
	 * Constructor 2 Will be used for ReadDataSetAfterClustering
	 * @param PathOfInputDataset
	 * @param NoOfDimension
	 * @param Query
	 * @param PathToElki
	 * @param PathToClusterOutput
	 * @param MinPts
	 * @param Epsilon
	 */

	public ReadDataSet(String PathOfInputDataset,int NoOfDimension,String PathToElki,
			String PathToClusterOutput,int MinPts,double Epsilon)
	{
		this.PathOfInputDataset = PathOfInputDataset;
		this.NoOfDimension = NoOfDimension;
		this.CSVFileDelimiter = "\\s+";
		this.PathToElki = PathToElki;
		//System.out.println(PathToElki);
		this.PathToClusterOutput = PathToClusterOutput;
		this.PathToScriptFile = PathToClusterOutput+"/script.sh";
		this.MinPts = MinPts;
		this.Epsilon = Epsilon;
	}
/**
 * This class is used to read points after clustering from Elki.So Clusters can be distinguishy read
 * @return
 * @throws IOException
 */
	public Vector<PointDataType>ReadDataSetAfterClustering() throws IOException
	{
		CreateScriptFile();//Creates script file which will run and create a script file to be run on elki for DBSCAn clustering 
		RunScriptFile();//Runs the script file created

		Vector<PointDataType>pts = new Vector<PointDataType>();//Reads from the cluster files so obtained to get 
		pts = ReadOutPutFromElki();
         //This function sorts all entries in Vector according to iod so that Vector.get(i-1)=ID
		pts = Sort(pts);//Sorts the Vector so obtained in ascending order of ID\

		return pts;


	}
	private Vector<PointDataType> Sort(Vector<PointDataType> pts) {

		Collections.sort(pts,new Comparator<PointDataType>()
				{

			@Override
			public int compare(PointDataType p1,
					PointDataType p2) {
				return Integer.valueOf(p1.getId()).compareTo(Integer.valueOf(p2.getId()));

			}


				});

		return pts;
	}
	private Vector<PointDataType> ReadOutPutFromElki() throws IOException {
		Vector<PointDataType>pts = new Vector<PointDataType>();
		File folder = new File(PathToClusterOutput+"/ClusterOutput");
		File files[] = folder.listFiles();
		if(files.length>4)
		{
			//read all files in output folder
			for(int i=0;i<files.length;i++)
			{

				String fileName = files[i].getName();
				//take files as cluster_3
				if(fileName.contains("_"))
				{
					String arr[] = fileName.split("_");
					arr[1]= arr[1].replace(".txt", "");
					int clusterNo = Integer.parseInt(arr[1]);
					FileReader fr = new FileReader(files[i].getAbsolutePath());
					BufferedReader br = new BufferedReader(fr);
					String line;
					while((line = br.readLine())!=null)
					{
						if(line.contains("ID="))
						{
							line = line.replace("ID=", "");
							String [] ID_dimVal = line.split(" ");
							int ID = Integer.parseInt(ID_dimVal[0]);
							double dim [] = new double [ID_dimVal.length-1];
							for(int i1=1;i1<ID_dimVal.length;i1++)
							{
								dim[i1-1] = Double.parseDouble(ID_dimVal[i1]);
							}
							PointDataType pdt = new PointDataType(ID, dim, clusterNo);
							pts.add(pdt);
						}
					}

					br.close();
					fr.close();
				}
				//take noise file
				else if(fileName.contains("noise"))
				{
					int clusterNo = -1;
					FileReader fr = new FileReader(files[i].getAbsolutePath());
					BufferedReader br = new BufferedReader(fr);
					String line;
					while((line = br.readLine())!=null)
					{
						if(line.contains("ID="))
						{
							line = line.replace("ID=", "");
							String [] ID_dimVal = line.split(" ");
							int ID = Integer.parseInt(ID_dimVal[0]);
							double dim [] = new double [ID_dimVal.length-1];
							for(int i1=1;i1<ID_dimVal.length;i1++)
							{
								dim[i1-1] = Double.parseDouble(ID_dimVal[i1]);
							}
							PointDataType pdt = new PointDataType(ID, dim, clusterNo);
							pts.add(pdt);
						}
					}
					br.close();
					fr.close();

				}
				else
				{

					//blank code for further addition of other code for main or cluster evaluation
				}

			}


		}
		else
		{
			File filecluster = new File(PathToClusterOutput+"/ClusterOutput/cluster.txt");

			if(filecluster.exists())
			{
				int clusterNo = 0;
				FileReader fr = new FileReader(PathToClusterOutput+"/ClusterOutput/cluster.txt");
				BufferedReader br = new BufferedReader(fr);
				String line;
				while((line = br.readLine())!=null)
				{
					if(line.contains("ID="))
					{
						line = line.replace("ID=", "");
						String [] ID_dimVal = line.split(" ");
						int ID = Integer.parseInt(ID_dimVal[0]);
						double dim [] = new double [ID_dimVal.length-1];
						for(int i1=1;i1<ID_dimVal.length;i1++)
						{
							dim[i1-1] = Double.parseDouble(ID_dimVal[i1]);
						}
						PointDataType pdt = new PointDataType(ID, dim, clusterNo);
						pts.add(pdt);
					}
				}

				br.close();
				fr.close();

			}
			else
			{
				System.out.println("no cluster points");
			}
			File filenoise = new File(PathToClusterOutput+"/ClusterOutput/noise.txt");

			if(filenoise.exists())
			{
				int clusterNo = -1;
				FileReader fr = new FileReader(PathToClusterOutput+"/ClusterOutput/noise.txt");
				BufferedReader br = new BufferedReader(fr);
				String line;
				while((line = br.readLine())!=null)
				{
					if(line.contains("ID="))
					{
						line = line.replace("ID=", "");
						String [] ID_dimVal = line.split(" ");
						int ID = Integer.parseInt(ID_dimVal[0]);
						double dim [] = new double [ID_dimVal.length-1];
						for(int i1=1;i1<ID_dimVal.length;i1++)
						{
							dim[i1-1] = Double.parseDouble(ID_dimVal[i1]);
						}
						PointDataType pdt = new PointDataType(ID, dim, clusterNo);
						pts.add(pdt);
					}
				}
				br.close();
				fr.close();

			}
		}


		return pts;
	}
	private void RunScriptFile() {
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
	private void CreateScriptFile() throws IOException {
		FileWriter stream = new FileWriter(PathToScriptFile, false);
		BufferedWriter bo = new BufferedWriter(stream);
		bo.write("#!/bin/bash");
		bo.newLine();
		bo.write("mkdir "+PathToClusterOutput+"/ClusterOutput");
		bo.newLine();
		bo.write("java -jar "+PathToElki+" KDDCLIApplication -algorithm clustering.DBSCAN -dbc.in "+ PathOfInputDataset+ " -dbscan.epsilon "+ Epsilon+" -dbscan.minpts "+ MinPts+
				" -out "+PathToClusterOutput+"/ClusterOutput");
		System.out.println("Query : java -jar "+PathToElki+" KDDCLIApplication -algorithm clustering.DBSCAN -dbc.in "+ PathOfInputDataset+ " -dbscan.epsilon "+ Epsilon+" -dbscan.minpts "+ MinPts+
				" -out "+PathToClusterOutput+"/ClusterOutput");

		bo.close();
		stream.close();

	}
	/**
	 * Description : Reads a dataset file and stores all the values in a hashmap and returns that hashmap
	 * Needs file in format dim1,dim2,...
	 * @return HashMap<Integer,double[]>
	 * @throws IOException
	 */

	public HashMap<Integer,double[]>ReadDatSetInCSV () throws IOException
	{
		HashMap<Integer,double[]>map = new HashMap<Integer, double[]>();

		int id = 0;
		FileReader fr     = new FileReader (PathOfInputDataset);
		BufferedReader  br    = new BufferedReader(fr);
		String line = "";
		while((line=br.readLine())!=null)
		{
			++id;
			double[] dim = new double[NoOfDimension];
			String [] arr = line.split(CSVFileDelimiter);
			for (int i =0;i<dim.length;i++)
			{
				dim[i] = Double.parseDouble(arr[i]);
			}

			map.put(id,dim);
		}
		return map;

	}

//------------------------------------------------------------------------------------------------------
	
	public Vector<PointkRNN>ReadDataSetAfterClustering(String m) throws IOException
	{
		CreateScriptFile();//Creates script file which will run and create a script file to be run on elki for DBSCAn clustering 
		RunScriptFile();//Runs the script file created
        
		Vector<PointkRNN>pts = new Vector<PointkRNN>();//Reads from the cluster files so obtained to get 
		pts = ReadOutPutFromElki(m);
         //This function sorts all entries in Vector according to iod so that Vector.get(i-1)=ID
		pts = Sort(pts,m);//Sorts the Vector so obtained in ascending order of ID\

		return pts;


	}
	private Vector<PointkRNN> Sort(Vector<PointkRNN> pts,String m) {

		Collections.sort(pts,new Comparator<PointkRNN>()
				{

			@Override
			public int compare(PointkRNN p1,
					PointkRNN p2) {
				return Integer.valueOf(p1.getId()).compareTo(Integer.valueOf(p2.getId()));

			}


				});

		return pts;
	}
	private Vector<PointkRNN> ReadOutPutFromElki(String m) throws IOException {
		Vector<PointkRNN>pts = new Vector<PointkRNN>();
		File folder = new File(PathToClusterOutput+"/ClusterOutput");
		File files[] = folder.listFiles();
		if(files.length>4)
		{
			//read all files in output folder
			for(int i=0;i<files.length;i++)
			{

				String fileName = files[i].getName();
				//take files as cluster_3
				if(fileName.contains("_"))
				{
					String arr[] = fileName.split("_");
					arr[1]= arr[1].replace(".txt", "");
					int clusterNo = Integer.parseInt(arr[1]);
					FileReader fr = new FileReader(files[i].getAbsolutePath());
					BufferedReader br = new BufferedReader(fr);
					String line;
					while((line = br.readLine())!=null)
					{
						if(line.contains("ID="))
						{
							line = line.replace("ID=", "");
							String [] ID_dimVal = line.split(" ");
							int ID = Integer.parseInt(ID_dimVal[0]);
							double dim [] = new double [ID_dimVal.length-1];
							for(int i1=1;i1<ID_dimVal.length;i1++)
							{
								dim[i1-1] = Double.parseDouble(ID_dimVal[i1]);
							}
							PointkRNN pdt = new PointkRNN(ID, dim, clusterNo);
							pts.add(pdt);
						}
					}

					br.close();
					fr.close();
				}
				//take noise file
				else if(fileName.contains("noise"))
				{
					int clusterNo = -1;
					FileReader fr = new FileReader(files[i].getAbsolutePath());
					BufferedReader br = new BufferedReader(fr);
					String line;
					while((line = br.readLine())!=null)
					{
						if(line.contains("ID="))
						{
							line = line.replace("ID=", "");
							String [] ID_dimVal = line.split(" ");
							int ID = Integer.parseInt(ID_dimVal[0]);
							double dim [] = new double [ID_dimVal.length-1];
							for(int i1=1;i1<ID_dimVal.length;i1++)
							{
								dim[i1-1] = Double.parseDouble(ID_dimVal[i1]);
							}
							PointkRNN pdt = new PointkRNN(ID, dim, clusterNo);
							pts.add(pdt);
						}
					}
					br.close();
					fr.close();

				}
				else
				{

					//blank code for further addition of other code for main or cluster evaluation
				}

			}


		}
		else
		{
			File filecluster = new File(PathToClusterOutput+"/ClusterOutput/cluster.txt");

			if(filecluster.exists())
			{
				int clusterNo = 0;
				FileReader fr = new FileReader(PathToClusterOutput+"/ClusterOutput/cluster.txt");
				BufferedReader br = new BufferedReader(fr);
				String line;
				while((line = br.readLine())!=null)
				{
					if(line.contains("ID="))
					{
						line = line.replace("ID=", "");
						String [] ID_dimVal = line.split(" ");
						int ID = Integer.parseInt(ID_dimVal[0]);
						double dim [] = new double [ID_dimVal.length-1];
						for(int i1=1;i1<ID_dimVal.length;i1++)
						{
							dim[i1-1] = Double.parseDouble(ID_dimVal[i1]);
						}
						PointkRNN pdt = new PointkRNN(ID, dim, clusterNo);
						pts.add(pdt);
					}
				}

				br.close();
				fr.close();

			}
			else
			{
				System.out.println("no cluster points");
			}
			File filenoise = new File(PathToClusterOutput+"/ClusterOutput/noise.txt");

			if(filenoise.exists())
			{
				int clusterNo = -1;
				FileReader fr = new FileReader(PathToClusterOutput+"/ClusterOutput/noise.txt");
				BufferedReader br = new BufferedReader(fr);
				String line;
				while((line = br.readLine())!=null)
				{
					if(line.contains("ID="))
					{
						line = line.replace("ID=", "");
						String [] ID_dimVal = line.split(" ");
						int ID = Integer.parseInt(ID_dimVal[0]);
						double dim [] = new double [ID_dimVal.length-1];
						for(int i1=1;i1<ID_dimVal.length;i1++)
						{
							dim[i1-1] = Double.parseDouble(ID_dimVal[i1]);
						}
						PointkRNN pdt = new PointkRNN(ID, dim, clusterNo);
						pts.add(pdt);
					}
				}
				br.close();
				fr.close();

			}
		}


		return pts;
	}
	//----------------------------------------------------------------------------------------------
	
	public Vector<Point>ReadDataSetAfterClustering(int m) throws IOException
	{
		CreateScriptFile();//Creates script file which will run and create a script file to be run on elki for DBSCAn clustering 
		RunScriptFile();//Runs the script file created
        
		Vector<Point>pts = new Vector<Point>();//Reads from the cluster files so obtained to get 
		pts = ReadOutPutFromElki(m);
         //This function sorts all entries in Vector according to iod so that Vector.get(i-1)=ID
		pts = Sort(pts,m);//Sorts the Vector so obtained in ascending order of ID\

		return pts;


	}
	private Vector<Point> Sort(Vector<Point> pts,int m) {

		Collections.sort(pts,new Comparator<Point>()
				{

			@Override
			public int compare(Point p1,
					Point p2) {
				return Integer.valueOf(p1.getId()).compareTo(Integer.valueOf(p2.getId()));

			}


				});

		return pts;
	}
	private Vector<Point> ReadOutPutFromElki(int m) throws IOException {
		Vector<Point>pts = new Vector<Point>();
		File folder = new File(PathToClusterOutput+"/ClusterOutput");
		File files[] = folder.listFiles();
		if(files.length>4)
		{
			//read all files in output folder
			for(int i=0;i<files.length;i++)
			{

				String fileName = files[i].getName();
				//take files as cluster_3
				if(fileName.contains("_"))
				{
					String arr[] = fileName.split("_");
					arr[1]= arr[1].replace(".txt", "");
					int clusterNo = Integer.parseInt(arr[1]);
					FileReader fr = new FileReader(files[i].getAbsolutePath());
					BufferedReader br = new BufferedReader(fr);
					String line;
					while((line = br.readLine())!=null)
					{
						if(line.contains("ID="))
						{
							line = line.replace("ID=", "");
							String [] ID_dimVal = line.split(" ");
							int ID = Integer.parseInt(ID_dimVal[0]);
							double dim [] = new double [ID_dimVal.length-1];
							for(int i1=1;i1<ID_dimVal.length;i1++)
							{
								dim[i1-1] = Double.parseDouble(ID_dimVal[i1]);
							}
							Point pdt = new Point(ID, dim, clusterNo);
							pts.add(pdt);
						}
					}

					br.close();
					fr.close();
				}
				//take noise file
				else if(fileName.contains("noise"))
				{
					int clusterNo = -1;
					FileReader fr = new FileReader(files[i].getAbsolutePath());
					BufferedReader br = new BufferedReader(fr);
					String line;
					while((line = br.readLine())!=null)
					{
						if(line.contains("ID="))
						{
							line = line.replace("ID=", "");
							String [] ID_dimVal = line.split(" ");
							int ID = Integer.parseInt(ID_dimVal[0]);
							double dim [] = new double [ID_dimVal.length-1];
							for(int i1=1;i1<ID_dimVal.length;i1++)
							{
								dim[i1-1] = Double.parseDouble(ID_dimVal[i1]);
							}
							Point pdt = new Point(ID, dim, clusterNo);
							pts.add(pdt);
						}
					}
					br.close();
					fr.close();

				}
				else
				{

					//blank code for further addition of other code for main or cluster evaluation
				}

			}


		}
		else
		{
			File filecluster = new File(PathToClusterOutput+"/ClusterOutput/cluster.txt");

			if(filecluster.exists())
			{
				int clusterNo = 0;
				FileReader fr = new FileReader(PathToClusterOutput+"/ClusterOutput/cluster.txt");
				BufferedReader br = new BufferedReader(fr);
				String line;
				while((line = br.readLine())!=null)
				{
					if(line.contains("ID="))
					{
						line = line.replace("ID=", "");
						String [] ID_dimVal = line.split(" ");
						int ID = Integer.parseInt(ID_dimVal[0]);
						double dim [] = new double [ID_dimVal.length-1];
						for(int i1=1;i1<ID_dimVal.length;i1++)
						{
							dim[i1-1] = Double.parseDouble(ID_dimVal[i1]);
						}
						Point pdt = new Point(ID, dim, clusterNo);
						pts.add(pdt);
					}
				}

				br.close();
				fr.close();

			}
			else
			{
				System.out.println("no cluster points");
			}
			File filenoise = new File(PathToClusterOutput+"/ClusterOutput/noise.txt");

			if(filenoise.exists())
			{
				int clusterNo = -1;
				FileReader fr = new FileReader(PathToClusterOutput+"/ClusterOutput/noise.txt");
				BufferedReader br = new BufferedReader(fr);
				String line;
				while((line = br.readLine())!=null)
				{
					if(line.contains("ID="))
					{
						line = line.replace("ID=", "");
						String [] ID_dimVal = line.split(" ");
						int ID = Integer.parseInt(ID_dimVal[0]);
						double dim [] = new double [ID_dimVal.length-1];
						for(int i1=1;i1<ID_dimVal.length;i1++)
						{
							dim[i1-1] = Double.parseDouble(ID_dimVal[i1]);
						}
						Point pdt = new Point(ID, dim, clusterNo);
						pts.add(pdt);
					}
				}
				br.close();
				fr.close();

			}
		}


		return pts;
	}


	
}

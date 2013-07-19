import java.beans.FeatureDescriptor;
import java.io.IOException;

import com.cde.dbscan.DbscanRank;
import com.cde.kRNN.CalculateCentralityRank;
import com.cde.kRNN.CalculatekRNN;
import com.densest.region.FindDensestRegionUsingNN;
import com.densest.region.FindDensestUsingkRNN;


public class Main {

	public static void main(String[] args) throws IOException {
		
		String PathOfInputDataset ="/home/mihirshekhar/Dataset/chameleonDS3.dat";
		int NoOfDimension =2;
		String  CSVFileDelimiter=",";
		String PathToElki ="/home/mihirshekhar/Common_Required_jar/elki.jar";
		String  PathToClusterOutput="/home/mihirshekhar/Research_Project/workspace/RankClus/output";
		String ReportName = "ChameleonDS3"; 
		int  MinPts=4;
		int k = 5;
		double Epsilon=5.7; 
		
		int threshold=10;
	/*	
		DbscanRank dbscnrnk = new DbscanRank(PathOfInputDataset, NoOfDimension, CSVFileDelimiter, PathToElki, PathToClusterOutput, OutputReportName, MinPts, Epsilon);
		System.out.println(PathToElki);

	    CalculateCentralityRank ccr = new CalculateCentralityRank(PathOfInputDataset, NoOfDimension, PathToElki, PathToClusterOutput,OutputReportName, MinPts, Epsilon);
		CalculatekRNN ckrnn = new CalculatekRNN(PathOfInputDataset, NoOfDimension, CSVFileDelimiter, PathToElki, PathToClusterOutput, OutputReportName, MinPts, Epsilon, 6);*/
		
		
		//Calculates the densest region on the dataset using NN.
		 FindDensestRegionUsingNN fdrunn = new FindDensestRegionUsingNN(
				 PathOfInputDataset,
				 NoOfDimension, 
				 CSVFileDelimiter,
				 PathToElki,
				 PathToClusterOutput, 
				 ReportName, 
				 MinPts, 
				 Epsilon, 
				 threshold);//degree of node to be used to create edges.
		 fdrunn.ExportGraphInArffFormat();
		
	/*	FindDensestUsingkRNN fdukr = new FindDensestUsingkRNN(
				PathOfInputDataset,
				NoOfDimension, 
				CSVFileDelimiter, 
				PathToElki,
				PathToClusterOutput,
				ReportName,
				MinPts,
				Epsilon,
				threshold,
				k);
		fdukr.ExportGraphInArffFormat();*/

}
}

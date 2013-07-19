#!/bin/bash
mkdir /home/mihirshekhar/Research_Project/workspace/RankClus/output/ClusterOutput
java -jar /home/mihirshekhar/Common_Required_jar/elki.jar KDDCLIApplication -algorithm clustering.DBSCAN -dbc.in /home/mihirshekhar/Dataset/chameleonDS3.dat -dbscan.epsilon 5.7 -dbscan.minpts 4 -out /home/mihirshekhar/Research_Project/workspace/RankClus/output/ClusterOutput
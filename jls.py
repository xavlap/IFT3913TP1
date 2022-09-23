import sys
import os
import csv

f = open(os.getcwd() + "/myCSV.csv","w")
writer = csv.writer(f)

for root, dirs, files in os.walk(sys.argv[1]): 
    for name in files:
        writer.writerow([os.path.join(root, name),root.replace("/","."), name])

f.close()
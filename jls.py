import os
import csv

f = open(os.getcwd() + "/myCSV.csv","w")
writer = csv.writer(f)


input = input("please enter a directory : ")

for root, dirs, files in os.walk(input): 
    for name in files:
        writer.writerow([os.path.join(root, name),root.replace("/","."), name])

f.close()
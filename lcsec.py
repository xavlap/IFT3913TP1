from asyncio import subprocess
import csv
from dataclasses import replace
from operator import contains
import sys
import os


# os.system("jls.py {}".format(sys.argv[1]))

os.system("python3 jls.py {}".format(sys.argv[1]))

f = open(os.getcwd() + "/myCSV.csv","r")
rows = list(csv.reader(f))

cpts = []

# un tableau de compteur qui peut compter le CSEC pour chaque fichier java
for row in rows:
    cpts.append(0)

# un tableau contenant le contenue de chaque fichier java
f.seek(0)
files = []
for row in rows:
    file = open(os.getcwd() +"/"+ row[0],"r")
    files.append(file.read())
    file.close()

f.seek(0)
i=0
for file in files:
    if contains(file, rows[i][2].replace(".java","")) :
        cpts[i] += 1
    i+=1

f.close()

f = open(os.getcwd() + "/myCSV.csv","w")
writer = csv.writer(f)

i=0
for row in rows:
    row.append(cpts[i])
    i+=1

writer.writerows(rows)

f.close()





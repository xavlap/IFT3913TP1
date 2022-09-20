import csv
import os.path
import sys


def dir_is_dir(dir_path):

    if os.path.isdir(dir_path):
        jls(dir_path)
    else:
        print("The path provided does not lead to a directory")


def jls(dirPath):
    file_to_write = open("/home/louis/Documents/Ecole/Session6/IFT3913/IFT3913TP1",'w')
    writer = csv.writer(file_to_write)
    writer.writerow('row')
    file_to_write.close()


dir_is_dir(sys.argv[1])
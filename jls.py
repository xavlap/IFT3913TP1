import sys
import os
import csv


def jls(file_path):
    f = open(os.getcwd() + "/myCSV.csv", "w")
    writer = csv.writer(f)

    for root, dirs, files in os.walk(file_path):
        for name in files:
            class_name = name.replace(".java", "")
            file_location = root.replace(sys.argv[1], ".")
            package_location = root.replace(sys.argv[1] + "/", "").replace("/", ".")
            writer.writerow([os.path.join(file_location, name), package_location, class_name])
    f.close()


jls(sys.argv[1])

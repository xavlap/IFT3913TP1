import sys
import os
import csv

# exemple de commande pour lancer de programme
# python3 jls.py ./ckjm/src/
def jls(file_path):
    f = open(os.getcwd() + "/jls.csv", "w")
    writer = csv.writer(f)

    for root, dirs, files in os.walk(file_path):
        for name in files:
            folder_location = str(os.path.join(root, name)).replace(sys.argv[1],"./")
            package_name = str(root).replace(sys.argv[1],"").replace("/", ".")
            file_name = str(name).replace(".java", "")
            writer.writerow([folder_location, package_name, file_name])
    f.close()

    return os.getcwd() + "/jls.csv"


if len(sys.argv) > 0:
    jls(sys.argv[1])

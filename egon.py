import csv
import math
import os
import subprocess
import sys

# Mettre le même path que pour les méthodes jls et lcsec
# Exemple de commande pour créer le csv résultant
# python3 egon.py ./ckjm/src/ 30

def egon(path_to_dir, threshold: int):
    l = open("./lcsec.csv", "r")

    e = open("./egon.csv", "w")
    writer = csv.writer(e)

    # initialiser des lists pour faciliter le calcul des seuils
    results = []
    nvloc_results = []
    lcsec_results = []


    for row in csv.reader(l):
        path = os.path.join(path_to_dir, str(row[0])).replace("./", "")
        process = subprocess.run(['python3', 'nvloc.py', str(path)], stdout=subprocess.PIPE, universal_newlines=True)
        nvloc = int(process.stdout)

        nvloc_results.append(nvloc)
        lcsec_results.append(int(row[3]))
        results.append([row[0], row[1], row[2], row[3], str(nvloc)])

        row.append(nvloc)
        writer.writerow(row)

    nvloc_results.sort(reverse=True)
    lcsec_results.sort(reverse=True)

    results_in_percentile = (int(threshold) / 100) * (len(nvloc_results))

    if results_in_percentile < 1:
        results_in_percentile = math.ceil(results_in_percentile)
    else:
        results_in_percentile = round(results_in_percentile)

    threshold_lcsec = []
    threshold_nvloc = []
    for i in range(0, results_in_percentile):
        threshold_lcsec.append(lcsec_results[i])
        threshold_nvloc.append(nvloc_results[i])

    for result in results:
        if (int(result[3]) in threshold_lcsec) and (int(result[4]) in threshold_nvloc):
            print(result[0] + "," + result[1] + "," + result[2] + "," + result[3] + "," + result[4])

    l.close()
    e.close()

    return


egon(sys.argv[1], sys.argv[2])

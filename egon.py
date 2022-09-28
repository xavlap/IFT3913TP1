import csv
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
        path = os.path.join(path_to_dir, str(row[0]).replace("./", ""))
        process = subprocess.run(['python3', 'nvloc.py', str(path)], stdout=subprocess.PIPE, universal_newlines=True)
        nvloc = int(process.stdout)

        nvloc_results.append(nvloc)
        lcsec_results.append(int(row[3]))
        results.append([row[0], row[1], row[2], row[3], str(nvloc)])

        row.append(nvloc)
        writer.writerow(row)

    nvloc_results.sort(reverse=True)
    lcsec_results.sort(reverse=True)

    for result in results:

        percentile_nvloc = 0
        percentile_lcsec = 0

        for i in range(len(nvloc_results)):
            if int(result[4]) == nvloc_results[i]:
                percentile_nvloc = int(round(((len(nvloc_results) - i) / len(nvloc_results)) * 100))

        for i in range(len(lcsec_results)):
            if int(result[3]) == lcsec_results[i]:
                percentile_lcsec = int(round(((len(lcsec_results) - i) / len(lcsec_results)) * 100))

        print(result[2] + " nvloc % = " + str(percentile_nvloc) + " and lcsec % = " + str(percentile_lcsec))

        if 100 - percentile_nvloc < int(threshold) and 100 - percentile_lcsec < int(threshold):
            print(result[0] + "," + result[1] + "," + result[2] + "," + result[3] + "," + result[4])

    l.close()
    e.close()

    return


egon(sys.argv[1], sys.argv[2])

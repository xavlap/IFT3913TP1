import csv
import ntpath
import os
import sys


# devrait être le même path qui a été donné pour jls.py

def lcsec(path_to_folder, csv_file):
    f = open(csv_file, "r")
    g = open(os.getcwd() + "/lcsec.csv", "w")
    writer = csv.writer(g)

    methods = []
    paths = []
    total = []
    writing_index = 0

    for row in csv.reader(f):
        methods.append(row[2])
        paths.append(os.path.join(path_to_folder, str(row[0]).replace("./", "")))

    for path in paths:

        method = os.path.basename(path).replace(".java", "")

        # Faire une liste qui comprend toutes les utilisations à trouver sauf elle-même
        # (IE on ne cherche pas MethodVisitor dans MethodVisitor)
        list_to_look = []
        for j in methods:
            if method != j:
                list_to_look.append(j)

        uses = compter_les_occurences_de_methode_dans_une_classe(path, list_to_look)

        # Faire une liste des files ou la methode rechercher pourrait
        # se trouver en excluant le fichier ou elle est déclaré
        paths_to_look = []
        for i in paths:
            if method != ntpath.basename(i).replace(".java", ""):
                paths_to_look.append(i)

        is_used = trouver_usage_autres_classes(str(method), paths_to_look)

        total.append(is_used + uses)

    f.seek(0, 0)
    for row in csv.reader(f):
        writer.writerow([row[0], row[1], row[2], total[writing_index]])
        writing_index += 1

    f.close()
    g.close()

    return


def trouver_usage_autres_classes(method_to_find, files):

    usages = 0

    for i in files:
        f = open(i, "r")
        file = f.read()

        if file.count(method_to_find) > 0:
            usages += 1
        f.close()

    return usages


def compter_les_occurences_de_methode_dans_une_classe(file, method_list):
    f = open(file, "r")
    usages = 0
    reader = f.read()

    for i in method_list:
        if reader.count(str(i)) > 0:
            usages += 1

    return usages


if len(sys.argv) > 1:
    lcsec(sys.argv[1], sys.argv[2])

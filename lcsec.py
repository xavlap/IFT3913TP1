import csv
import ntpath
import os
import sys
from operator import contains


# os.system("jls.py {}".format(sys.argv[1]))

# os.system("python3 jls.py {}".format(sys.argv[1]))

# open CSV FILE made in part and extract all the method name

def lcsec():
    f = open(sys.argv[1], "r")
    g = open(os.getcwd() + "/lcsec.csv", "w")
    writer = csv.writer(g)

    methods = []
    paths = []
    total = []
    writing_index = 0

    for row in csv.reader(f):
        methods.append(row[2])
        paths.append(row[0])

    for method in methods:
        class_name = method + ".java"
        package = "/home/louis/Documents/Ecole/Session6/IFT3913/ckjm/src/"

        # Faire une liste qui comprend toutes les utilisations à trouver sauf elle-même
        # (IE on ne cherche pas MethodVisitor dans MethodVisitor)
        list_to_look = []
        for j in methods:
            if method != j:
                list_to_look.append(j)

        uses = compter_les_occurances_de_methode_dans_une_classe(os.path.join(package, class_name),
                                                                 list_to_look)  # MethodVisitor utilise toute ces methods

        # Faire une liste des files ou la methode rechercher pourrait
        # se trouver en excluant le fichier ou elle est déclarer
        paths_to_look = []
        for path in paths:
            if method != ntpath.basename(path).replace(".java", ""):
                paths_to_look.append(path)

        is_used = trouver_usage_autres_classes(str(method),
                                               "/home/louis/Documents/Ecole/Session6/IFT3913/ckjm/src/")  # MethodVisitor est utilise par toute ces methodes

        total.append(is_used + uses)

    f.seek(0,0)
    for row in csv.reader(f):
        print(row)
        writer.writerow([row[0],row[1],row[2],total[writing_index]])
        writing_index+=1

    f.close()
    g.close()

    return


def trouver_usage_autres_classes(method_to_find, package_path):
    files = ["/home/louis/Documents/Ecole/Session6/IFT3913/ckjm/src/gr/spinellis/ckjm/MethodVisitor.java",
             "/home/louis/Documents/Ecole/Session6/IFT3913/ckjm/src/gr/spinellis/ckjm/CkjmOutputHandler.java",
             "/home/louis/Documents/Ecole/Session6/IFT3913/ckjm/src/gr/spinellis/ckjm/ClassMetrics.java",
             "/home/louis/Documents/Ecole/Session6/IFT3913/ckjm/src/gr/spinellis/ckjm/ClassMetricsContainer.java",
             "/home/louis/Documents/Ecole/Session6/IFT3913/ckjm/src/gr/spinellis/ckjm/ClassVisitor.java",
             "/home/louis/Documents/Ecole/Session6/IFT3913/ckjm/src/gr/spinellis/ckjm/MetricsFilter.java",
             "/home/louis/Documents/Ecole/Session6/IFT3913/ckjm/src/gr/spinellis/ckjm/PrintPlainResults.java",
             "/home/louis/Documents/Ecole/Session6/IFT3913/ckjm/src/gr/spinellis/ckjm/ant/CkjmTask.java",
             "/home/louis/Documents/Ecole/Session6/IFT3913/ckjm/src/gr/spinellis/ckjm/ant/PrintXmlResults.java"
             ]

    usages = 0

    for i in files:
        f = open(i, "r")
        file = f.read()

        if file.count(method_to_find) > 0:
            usages += 1
        f.close()

    # On soustrait 1 pour éviter de compter la classe dans laquelle elle est declarer
    return usages - 1


def compter_les_occurances_de_methode_dans_une_classe(file, method_list):
    f = open("/home/louis/Documents/Ecole/Session6/IFT3913/ckjm/src/gr/spinellis/ckjm/MethodVisitor.java", "r")
    usages = 0
    file = f.read()

    for i in method_list:
        if file.count(str(i)) > 0:
            usages += 1
    return usages


# /home/louis/Documents/Ecole/Session6/IFT3913/ckjm/src/gr/spinellis/ckjm/ClassVisitor.java

lcsec()

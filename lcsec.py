import csv
import os
import sys
from operator import contains


# os.system("jls.py {}".format(sys.argv[1]))

# os.system("python3 jls.py {}".format(sys.argv[1]))

# open CSV FILE made in part and extract all the method name

def lcsec():
    f = open("./myCSV.csv","r")
    #f = open(sys.argv[1], "r")
    method_list = []
    for row in csv.reader(f):
        method_list.append(row[2])

    for i in method_list:

        class_name = i + ".java"
        package = os.path.dirname(row[0])

        # Regarder dans toutes les classes sauf elle meme
        list_to_look = []
        for j in method_list:
            if i != j:
                list_to_look.append(j)

        uses = compter_les_occurances_de_methode_dans_une_classe(os.path.join(package, class_name), list_to_look)
        is_used = trouver_usage_autres_classes(os.path.join(package, class_name), list_to_look)

        total = is_used + uses

    return "done"


def trouver_usage_autres_classes(path_to_class, method_list):
    return 0


def compter_les_occurances_de_methode_dans_une_classe(file, method_list):
    f = open("/home/louis/Documents/Ecole/Session6/IFT3913/ckjm/src/gr/spinellis/ckjm/MethodVisitor.java", "r")
    usages = 0
    file = f.read()

    for i in method_list:
        if file.count(str(i)) > 0:
            usages += 1
            print(i + " is used")
        else:
            print(i + " is not used in MethodVisitor.java")

    print(usages)
    return usages

    # f = open(path_to_class,"r")
    # print(f)
    #
    # result = 0
    # return result


# /home/louis/Documents/Ecole/Session6/IFT3913/ckjm/src/gr/spinellis/ckjm/ClassVisitor.java

lcsec()

# def ajouter_les_deux_compteur_pour_total():

#
# f = open(os.getcwd() + "/myCSV.csv","r")
# rows = list(csv.reader(f))

# cpts = []
#
# # un tableau de compteur qui peut compter le CSEC pour chaque fichier java
# for row in rows:
#     cpts.append(0)
#
# # un tableau contenant le contenue de chaque fichier java
# f.seek(0)
# files = []
# for row in rows:
#     file = open(os.getcwd() +"/"+ row[0],"r")
#     files.append(file.read())
#     file.close()
#
# f.seek(0)
# i=0
# for file in files:
#     if contains(file, rows[i][2].replace(".java","")) :
#         cpts[i] += 1
#     i+=1
#
# f.close()
#
# f = open(os.getcwd() + "/myCSV.csv","w")
# writer = csv.writer(f)
#
# i=0
# for row in rows:
#     row.append(cpts[i])
#     i+=1
#
# writer.writerows(rows)
#
# f.close()

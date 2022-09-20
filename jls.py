import os

input = input("please enter a directory : ")

for root, dirs, files in os.walk(input,): 
    for name in files:
        print("{path}, {package}, {file}"
            .format(path = os.path.join(root, name),package = root.replace("/","."),file = name))
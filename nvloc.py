import os.path
import sys
from pathlib import Path


def file_is_a_file(filePath):

    if os.path.isfile(filePath):
        nvloc(filePath)
    else :
        print("The path provided \"",filePath ,"\" does not lead to a file")


def nvloc(filePath):

    file = open(filePath, 'r')
    Lines = file.readlines()
    totalLine = 0

    for line in Lines:
        if not line in ['\n', '\r\n']:
            totalLine += 1

    print(totalLine)
    file.close()

file_is_a_file(sys.argv[1])

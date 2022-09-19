import sys

def nvloc(filePath):

    file = open(filePath, 'r')
    Lines = file.readlines()
    totalLine = 0

    for line in Lines:
        if not line in ['\n', '\r\n']:
            totalLine += 1

    print(totalLine)

nvloc(sys.argv[1])
def nvloc(filePath):
    file = open(filePath,'r')
    Lines = file.readlines()
    totalLine = 0

    for line in Lines:
        if line in ['\n', '\r\n']:
            totalLine+=0
        else :
            totalLine+=1

    print('number of line in this file is : ' , totalLine)

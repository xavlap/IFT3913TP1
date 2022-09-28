Louis-André Brassard, Xavier Lapalme

Lien vers le repositoire: https://github.com/xavlap/IFT3913TP1.git

Pour exécuter le code il faut faire les trois ligne de commande suivante, dans l'ordre :
------
* python3 jls.py path
 
* python 3 lcsec.py path jls.csv /n

* python 3 egon.py path seuil

brief resumer du fonctionnement
------

* jls.py écrit ses résultats dans jls.csv.

* lcsec.py lit dans jls.csv, écrit ses résultats dans lcsec.csv et utilise directement nvloc.py.

* egon.py lit dans lcsec.csv et écrit ses résultats dans egon.csv.

#!/bin/bash
# Le shebang : dit à Linux d'utiliser Bash pour exécuter ce script

# === VARIABLES ===
NOM="Airbus Monitor"
PORT=8080
DOSSIER="C:/Users/user/Desktop"

echo "=== $NOM ==="
echo "Port : $PORT"
echo "Dossier scanné : $DOSSIER"

# === TEST DU SERVICE ===
# On appelle l'API et on récupère le code HTTP (200 = ok, autre = erreur)
CODE=$(curl -o /dev/null -w "%{http_code}" "http://localhost:$PORT/api/fichiers?dossier=$DOSSIER")

    #curl                # outil pour appeler une URL (comme un navigateur en ligne de commande)
    #-o /dev/null        # -o = output = où écrire la réponse
                         # /dev/null = on la jette, on s'en fiche du JSON
                         # sans -o, curl afficherait tout le JSON dans le terminal
    #-w "%{http_code}"   # -w = write-out = qu'est-ce qu'on veut afficher à la fin
                         # %{http_code} = une variable interne à curl
                         # 200 = OK, 404 = introuvable, 000 = service éteint

if [ "$CODE" == "200" ]; then
    echo "Service OK — HTTP $CODE"
else
    echo "ERREUR — HTTP $CODE"
fi

# === BOUCLE SUR LES FICHIERS ===
# On liste les fichiers du dossier courant et on affiche leur taille
echo ""
echo "--- Fichiers dans le répertoire courant ---"

for FICHIER in *; do
    # -f = est-ce un fichier (pas un dossier) ?
    if [ -f "$FICHIER" ]; then
        TAILLE=$(du -sh "$FICHIER" | cut -f1)
        echo "$FICHIER — $TAILLE"
    fi
done

    #du -sh "$FICHIER"      # du = disk usage = taille d'un fichier
                            # -s = summary = juste ce fichier (pas les sous-dossiers)
                            # -h = human readable = affiche "4.0K" au lieu de "4096"
                            # résultat : "4.0K    monitor.sh"
    #cut -f1                 # cut = découper le texte en colonnes
                            # -f1 = garder uniquement la colonne 1
                            # résultat : "4.0K"  (on enlève le nom du fichier)

echo "Script terminé."

#################################################################
#                                                               #      
#    pour lancer le script dans terminale git bash              #  
#    bash monitor.sh                                            # 
#                                                               # 
#################################################################
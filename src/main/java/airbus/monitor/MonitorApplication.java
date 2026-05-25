// ce fichier a ete generee automatiquement voir readme


package airbus.monitor;
// package = dossier logique qui organise les classes
// airbus.monitor = toutes les classes de ce dossier appartiennent au meme groupe

// imports = on dit a Java quelles classes externes on va utiliser
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// @SpringBootApplication = 3 annotations en 1 :
// 1. Scanner tout le package pour trouver @RestController, @Service, etc.
// 2. Activer la configuration automatique (Tomcat, Jackson pour le JSON...)
// 3. Enregistrer les composants Spring
@SpringBootApplication
public class MonitorApplication {

    // main() = point d'entree de l'application
    // Java cherche TOUJOURS cette methode exacte pour demarrer
    // public   = accessible depuis l'exterieur
    // static   = Java peut l'appeler sans creer un objet MonitorApplication
    // void     = ne retourne rien
    // String[] args = arguments passes en ligne de commande (optionnels)
    public static void main(String[] args) {

        // SpringApplication.run() demarre tout :
        // - lance le serveur Tomcat sur le port 8080
        // - charge MonitorController et tous les @RestController trouves
        // - rend l'appli accessible sur http://localhost:8080
        // - configure Jackson pour serialiser les objets en JSON automatiquement
        // MonitorApplication.class = dit a Spring quel fichier est le point de depart
        SpringApplication.run(MonitorApplication.class, args);
    }
}
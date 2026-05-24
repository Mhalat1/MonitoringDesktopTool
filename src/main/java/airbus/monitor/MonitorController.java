package airbus.monitor;

import org.springframework.web.bind.annotation.*;
import java.io.File;
import java.util.*;

// ============================================================
// 1. INTERFACE
// Un contrat : toute classe qui l'implémente DOIT avoir analyser()
// ============================================================
interface Analysable {
    String analyser();
}

// ============================================================
// 2. CLASSE DE BASE
// Un moule pour créer des objets Fichier
// "implements Analysable" = elle respecte le contrat
// ============================================================
class Fichier implements Analysable {

    // Attributs privés = on ne peut pas y accéder directement depuis l'extérieur
    private String nom;
    private long tailleKo;
    private String type;

    // Constructeur = appelé quand on fait new Fichier(...)
    public Fichier(String nom, long tailleKo, String type) {
        this.nom = nom;
        this.tailleKo = tailleKo;
        this.type = type;
    }

    // Getters = seule façon de lire les attributs privés
    public String getNom() { return nom; }
    public long getTailleKo() { return tailleKo; }
    public String getType() { return type; }

    // @Override = on implémente la méthode imposée par l'interface
    @Override
    public String analyser() {
        return "Fichier: " + nom + " | Taille: " + tailleKo + " Ko | Type: " + type;
    }

    @Override
    public String toString() {
        return analyser();
    }
}

// ============================================================
// 3. HERITAGE
// FichierLog hérite de tout ce que Fichier a
// et ajoute son propre attribut : niveau (INFO/WARNING/ERROR)
// ============================================================
class FichierLog extends Fichier {

    private String niveau;

    public FichierLog(String nom, long tailleKo, String niveau) {
        super(nom, tailleKo, "log"); // appelle le constructeur de Fichier
        this.niveau = niveau;
    }

    public String getNiveau() { return niveau; }

    // On redéfinit analyser() pour ajouter le niveau
    // super.analyser() = version de la classe parente
    @Override
    public String analyser() {
        return "[" + niveau + "] " + super.analyser();
    }
}

// ============================================================
// 4. COLLECTIONS + 5. EXCEPTIONS
// List = liste ordonnée d'objets
// Map = paires clé/valeur
// throws Exception = cette méthode peut lancer une erreur
// ============================================================
class Moniteur {

    // List<Fichier> = liste qui accepte Fichier ET FichierLog (héritage)
    private List<Fichier> fichiers = new ArrayList<>();

    // Map<String, Integer> = "dossier" -> 12, "fichier" -> 30
    private Map<String, Integer> stats = new HashMap<>();

    public void scanner(String cheminDossier) throws Exception {

        File dossier = new File(cheminDossier);

        // EXCEPTION : on lance une erreur si le dossier n'existe pas
        if (!dossier.exists()) {
            throw new Exception("Dossier introuvable : " + cheminDossier);
        }

        if (!dossier.isDirectory()) {
            throw new Exception("Ce n'est pas un dossier : " + cheminDossier);
        }

        fichiers.clear();
        stats.clear();

        for (File f : dossier.listFiles()) {
            Fichier fichier;

            // POLYMORPHISME : FichierLog ou Fichier selon l'extension
            if (f.getName().endsWith(".log")) {
                fichier = new FichierLog(f.getName(), f.length() / 1024, "INFO");
            } else {
                fichier = new Fichier(
                    f.getName(),
                    f.length() / 1024,
                    f.isDirectory() ? "dossier" : "fichier"
                );
            }

            fichiers.add(fichier);

            // getOrDefault = retourne la valeur ou 0 si la clé n'existe pas encore
            String type = fichier.getType();
            stats.put(type, stats.getOrDefault(type, 0) + 1);
        }
    }

    public List<Fichier> getFichiers() { return fichiers; }
    public Map<String, Integer> getStats() { return stats; }
}

// ============================================================
// API REST Spring Boot
// @RestController = cette classe gère des requêtes HTTP
// @RequestMapping = préfixe de toutes les routes
// ============================================================
@RestController
@RequestMapping("/api")
public class MonitorController {

    // ============================================================
    // ROUTE 1 : liste les fichiers en JSON
    // GET http://localhost:8080/api/fichiers
    // ============================================================
    @GetMapping("/fichiers")
    public List<Map<String, Object>> listerFichiers(
        @RequestParam(defaultValue = "C:/Users/user/Desktop") String dossier
    ) {
        List<Map<String, Object>> resultat = new ArrayList<>();
        File rep = new File(dossier);

        if (!rep.exists() || !rep.isDirectory()) {
            return resultat;
        }

        for (File f : rep.listFiles()) {
            Map<String, Object> info = new HashMap<>();
            info.put("nom", f.getName());
            info.put("taille_ko", f.length() / 1024);
            info.put("type", f.isDirectory() ? "dossier" : "fichier");
            resultat.add(info);
        }

        return resultat;
    }

    // ============================================================
    // ROUTE 2 : analyse avec les concepts OOP (Interface, Héritage, Collections)
    // GET http://localhost:8080/api/analyse
    // ============================================================
    @GetMapping("/analyse")
    public Map<String, Object> analyser(
        @RequestParam(defaultValue = "C:/Users/user/Desktop") String dossier
    ) {
        Map<String, Object> reponse = new HashMap<>();

        // TRY/CATCH = on essaie, si ça plante on renvoie l'erreur en JSON
        try {
            Moniteur moniteur = new Moniteur();
            moniteur.scanner(dossier);

            // On convertit les objets Fichier en Map pour le JSON
            List<Map<String, Object>> fichiersJson = new ArrayList<>();
            for (Fichier f : moniteur.getFichiers()) {
                Map<String, Object> item = new HashMap<>();
                item.put("analyse", f.analyser()); // appelle analyser() — polymorphisme
                item.put("nom", f.getNom());
                item.put("taille_ko", f.getTailleKo());
                item.put("type", f.getType());
                fichiersJson.add(item);
            }

            reponse.put("fichiers", fichiersJson);
            reponse.put("stats", moniteur.getStats());
            reponse.put("statut", "OK");

        } catch (Exception e) {
            // e.getMessage() = le message qu'on a mis dans throw new Exception(...)
            reponse.put("statut", "ERREUR");
            reponse.put("message", e.getMessage());
        }

        return reponse;
    }
}
# Déployer Monitor sur Kubernetes — commandes essentielles
# (Chapitre 6 du cours appliqué à ton projet)

# dans terminale powershell 

# etape 1 demmarrer service docker 

# PS C:\Windows\system32> net stop com.docker.service
# PS C:\Windows\system32> net start com.docker.service

# etape 2 demmarrer service minikube
# PS C:\Windows\system32> c:\minikube\minikube.exe start

# dans terminale git bash 

## 1. Déployer tout d'un coup
kubectl apply -f k8s/

## 2. Vérifier que tout tourne
kubectl get all -n airbus-monitor

## 3. Tester les endpoints de MonitorController en local
kubectl port-forward svc/monitor-service 8080:80 -n airbus-monitor
# Puis dans un autre terminal :
# curl "http://localhost:8080/api/fichiers?dossier=/opt/data"
# curl "http://localhost:8080/api/analyse?dossier=/opt/data"

## 4. Voir les logs de Spring Boot (MonitorApplication)
kubectl logs -l app=monitor -n airbus-monitor -f

## 5. Débugger un Pod qui plante (CrashLoopBackOff)
kubectl get pods -n airbus-monitor
kubectl logs <nom-du-pod> -n airbus-monitor
kubectl describe pod <nom-du-pod> -n airbus-monitor

## 6. Mettre à jour l'image (rolling update automatique)
kubectl set image deployment/monitor-deployment \
  monitor=airbus/monitor:2.0.0 -n airbus-monitor
kubectl rollout status deployment/monitor-deployment -n airbus-monitor

## 7. Annuler si ça se passe mal
kubectl rollout undo deployment/monitor-deployment -n airbus-monitor

## 8. Scaler à 5 réplicas
kubectl scale deployment monitor-deployment --replicas=5 -n airbus-monitor

## 9. Supprimer tout
kubectl delete -f k8s/

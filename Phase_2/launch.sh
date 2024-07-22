minikube start

eval $(minikube docker-env)

chmod 777 launch.sh

chmod 777 stop.sh
 
cd h2db/

docker build -t dev-h2db-service:v7 .

minikube kubectl -- create deployment dev-h2db-service --image=dev-h2db-service:v7

minikube kubectl -- expose deployment dev-h2db-service --type=LoadBalancer --port=9082

minikube kubectl -- apply -f hpa.yaml
 
 
cd ../userService/

docker build -t dev-user-service:v7 .

minikube kubectl -- create deployment dev-user-service --image=dev-user-service:v7

minikube kubectl -- expose deployment dev-user-service --type=LoadBalancer --port=8080

minikube kubectl -- apply -f hpa.yaml
 
 
cd ../bookingService/

docker build -t dev-booking-service:v7 .

minikube kubectl -- create deployment dev-booking-service --image=dev-booking-service:v7

minikube kubectl -- expose deployment dev-booking-service --type=LoadBalancer --port=8081

minikube kubectl -- apply -f hpa.yaml
 
 
cd ../walletService/

docker build -t dev-wallet-service:v7 .

minikube kubectl -- create deployment dev-wallet-service --image=dev-wallet-service:v7

minikube kubectl -- expose deployment dev-wallet-service --type=LoadBalancer --port=8082

minikube kubectl -- apply -f hpa.yaml 

cd ../
 
pkill -f port-forward

sleep 5s

gnome-terminal -- minikube kubectl -- port-forward service/dev-wallet-service 8082:8082 &

gnome-terminal -- minikube kubectl -- port-forward service/dev-booking-service 8081:8081 &

gnome-terminal -- minikube kubectl -- port-forward service/dev-user-service 8080:8080 &

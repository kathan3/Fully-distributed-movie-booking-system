package com.example;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.http.javadsl.Http;
import akka.http.javadsl.ServerBinding;
import akka.http.javadsl.server.Route;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.GroupRouter;
import akka.actor.typed.javadsl.Routers;
import akka.actor.typed.receptionist.Receptionist;
import akka.actor.typed.receptionist.ServiceKey;
import akka.actor.typed.ActorSystem;
import akka.cluster.sharding.typed.javadsl.ClusterSharding;

import akka.actor.typed.ActorRef;
import akka.actor.typed.ActorSystem;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.Behaviors;

import akka.cluster.sharding.typed.javadsl.Entity;
import akka.cluster.sharding.typed.javadsl.EntityTypeKey;
import akka.cluster.sharding.typed.javadsl.ClusterSharding;
import akka.cluster.sharding.typed.javadsl.EntityRef;

import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletionStage;

import com.typesafe.config.ConfigFactory;
import com.typesafe.config.Config;

public class QuickstartApp {
    public static ClusterSharding sharding;
    public static  Map<Integer,EntityRef<ShowActor.Command>> showActors =new HashMap<>();
    public static Integer replica =0;
    static void startHttpServer(Route route, ActorSystem<?> system) {
        CompletionStage<ServerBinding> futureBinding =
            Http.get(system).newServerAt("0.0.0.0", 8081).bind(route);
        
        futureBinding.whenComplete((binding, exception) -> {
            if (binding != null) {
                InetSocketAddress address = binding.localAddress();
                system.log().info("Server online at http://{}:{}/",
                    address.getHostString(),
                    address.getPort());
            } else {
                system.log().error("Failed to bind HTTP endpoint, terminating system", exception);
                system.terminate();
            }
        });
    }

    private static Behavior<Void> rootBehavior(int port) {

   
 

        return Behaviors.setup(context -> {

            ServiceKey<WorkerActor.Command> serviceKey = ServiceKey.create(WorkerActor.Command.class, "log-worker");
            GroupRouter<WorkerActor.Command> group = Routers.group(serviceKey);
            ActorRef<WorkerActor.Command> router = context.spawn(group, "worker-group");
            String[] shows = { "1,1,Youth in Revolt,50,40",
            "2,1,Leap Year,55,30",
            "3,1,Remember Me,60,55",
            "4,2,Fireproof,65,65",
            "5,2,Beginners,55,50",
            "6,3,Music and Lyrics,75,40",
            "7,3,The Back-up Plan,65,60",
            "8,4,WALL-E,45,55",
            "9,4,Water For Elephants,50,45",
            "10,5,What Happens in Vegas,65,65",
            "11,6,Tangled,55,40",
            "12,6,The Curious Case of Benjamin Button,65,50",
            "13,7,Rachel Getting Married,40,60",
            "14,7,New Year's Eve,35,45",
            "15,7,The Proposal,45,55",
            "16,8,The Time Traveler's Wife,75,65",
            "17,8,The Invention of Lying,50,40",
            "18,9,The Heartbreak Kid,60,50",
            "19,10,The Duchess,70,60",
            "20,10,Mamma Mia!,40,45" };
    Map<Integer, ShowActor.Show> showsMap = new HashMap<>();
    
    for (String show : shows) {
      String[] parts = show.split(",");
      int showId = Integer.parseInt(parts[0]);
      int theatreId = Integer.parseInt(parts[1]);
      String title = parts[2];
      int price = Integer.parseInt(parts[3]);
      int seatsAvailable = Integer.parseInt(parts[4]);
      showsMap.put(showId, new ShowActor.Show(showId, theatreId, title, price, seatsAvailable));
    }
            sharding = ClusterSharding.get(context.getSystem());
            sharding.init(
                Entity.of(ShowActor.TypeKey,
                    entityContext -> {
                        String entityId = entityContext.getEntityId();
                        ShowActor.Show show = showsMap.get(Integer.parseInt(entityId));
                        return ShowActor.create(Integer.parseInt(entityId), show.theatre_id(), show.title(), show.price(), show.seats_available());
                    }
                )
                );
            if(port == 8083){
        for (String line : shows) {
          String[] str = line.split(",");
          int show_id = Integer.parseInt(str[0]);
          int theatre_id = Integer.parseInt(str[1]);
          String title = str[2];
          int price = Integer.parseInt(str[3]);
          int seats_available = Integer.parseInt(str[4]);
          sharding= ClusterSharding.get(context.getSystem());
          
          //context.getLog().info("Before creating EntityRef for show_id {}", show_id);
            EntityRef<ShowActor.Command> ref2 = sharding.entityRefFor(ShowActor.TypeKey, Integer.toString(show_id));
            //context.getLog().info("After creating EntityRef for show_id {}", show_id);

showActors.put(show_id, ref2);
context.getLog().info("Ref for show 1:" + showActors.get(1));
          
        }
        //context.getLog().info("ShardRegion actors started on node with port {}", port);
                ActorRef<BookingRegistry.Command> bookingRegistryActor =
                    context.spawn(BookingRegistry.create(router,showActors), "UserRegistry");
                BookingRoutes booking = new BookingRoutes(context.getSystem(), bookingRegistryActor);
                startHttpServer(booking.bookingRoutes(), context.getSystem());
                
                for(int i = 0; i < 50; i++){
                    ActorRef<WorkerActor.Command> worker = context.spawn(WorkerActor.create(showActors), "worker" + replica*50+i);
                    context.getLog().info("Worker actor {} started", "worker" + replica*50+i);
                    context.getSystem().receptionist().tell(Receptionist.register(serviceKey, worker));
                    context.getLog().info("Worker actor {} told", "worker" + replica*50+i);
                }
            }

  

            replica++;
            return Behaviors.empty();
        });
    }

    private static void startup(int port) {
        Map<String, Object> overrides = new HashMap<>();
        overrides.put("akka.remote.artery.canonical.port", port);

        // See the cluster and cluster-sharding slides to understand
        // what all is required in application.conf.
        
        Config config = ConfigFactory.parseMap(overrides)
            .withFallback(ConfigFactory.load());

        // Create an Akka system
        ActorSystem.create(rootBehavior(port), "ClusterSystem", config);
    }

    public static void main(String[] args) throws Exception {
        Arrays.stream(args).map(Integer::parseInt).forEach(QuickstartApp::startup);
    }

}
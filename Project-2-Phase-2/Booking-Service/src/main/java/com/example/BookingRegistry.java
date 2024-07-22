package com.example;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import akka.http.javadsl.Http;
import java.util.*;

import akka.actor.typed.ActorRef;
import akka.actor.typed.ActorSystem;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.Behaviors;

import akka.cluster.sharding.typed.javadsl.Entity;
import akka.cluster.sharding.typed.javadsl.EntityTypeKey;
import akka.cluster.sharding.typed.javadsl.ClusterSharding;
import akka.cluster.sharding.typed.javadsl.EntityRef;

import java.io.Console;

import com.example.CborSerializable;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;





public class BookingRegistry extends AbstractBehavior<BookingRegistry.Command>  {
  public static Map<Integer,EntityRef<ShowActor.Command>> showActors =new HashMap<>();
  public static final List<Theatre> theatres=new ArrayList<>();
  public Integer count;
  sealed interface Command  extends CborSerializable{}


  public final record Theatre(Integer id, String name , String location) implements CborSerializable{}
  public final record User(Integer id,String name, String email)implements CborSerializable{}
  public final record Theatres(List<Theatre> theatres)implements CborSerializable{}
  public final record Booking(Integer id,Integer user_id,Integer show_id , Integer seats_booked) implements CborSerializable{}
  public final record Bookings(List<Booking> bookings)implements CborSerializable{}
  //public final record Show(Integer id, Integer theatre_id , String title,Integer price ,Integer seats_available){}


  public final record GetTheatre(ActorRef<Theatres> replyTo) implements Command{}
  public final record GetUserBookings(Integer user_id, ActorRef<Bookings> replyTo) implements  Command{}
  public final record GetShow(Integer id,ActorRef<ShowActor.Show> replyTo) implements Command{ }
  public final record ShowByTheatre(Integer theatre_id,ActorRef<WorkerActor.Showlist> replyTo) implements  Command{}

  public final record AddBooking(Booking booking,ActorRef<ShowActor.Booking> replyTo) implements  Command{}

  public final record DeleteAllUserBookings(Integer user_id, ActorRef<ShowActor.Response> replyTo) implements  Command{}
  public final record DeleteAllBookings(ActorRef<WorkerActor.DeleteResponse> replyTo) implements Command {}
  public final record DeleteUserBooking( Integer user_id,Integer show_id, ActorRef<ShowActor.Response> replyTo) implements Command{}

  public ActorRef<WorkerActor.Command> router;

  private BookingRegistry(ActorContext<Command> context, ActorRef<WorkerActor.Command> router,Map<Integer,EntityRef<ShowActor.Command>> showActors) {
    super(context);
    this.router = router;
   this.showActors = showActors;
   getContext().getLog().info("Greeting {} for {}");
   getContext().getLog().info(Integer.toString(showActors.size())); 
   count=0;
    String[] theatresList = { "1,Helen Hayes Theater,240 W 44th St.",
            "2,Cherry Lane Theatre,38 Commerce Street",
            "3,New World Stages,340 West 50th Street",
            "4,The Zipper Theater,100 E 17th St",
            "5,Queens Theatre,Meadows Corona Park",
            "6,The Public Theater,425 Lafayette St",
            "7,Manhattan Ensemble Theatre,55 Mercer St.",
            "8,Metropolitan Playhouse,220 E 4th St.",
            "9,Acorn Theater,410 West 42nd Street",
            "10,Apollo Theater,253 West 125th Street" };

    for (String line : theatresList) {
      String[] str = line.split(","); // use comma as separator
      int id = Integer.parseInt(str[0]);
      String name = str[1];
      String location = str[2];
      theatres.add(new Theatre(id, name, location));
    }
//     String[] shows = { "1,1,Youth in Revolt,50,40",
//             "2,1,Leap Year,55,30",
//             "3,1,Remember Me,60,55",
//             "4,2,Fireproof,65,65",
//             "5,2,Beginners,55,50",
//             "6,3,Music and Lyrics,75,40",
//             "7,3,The Back-up Plan,65,60",
//             "8,4,WALL-E,45,55",
//             "9,4,Water For Elephants,50,45",
//             "10,5,What Happens in Vegas,65,65",
//             "11,6,Tangled,55,40",
//             "12,6,The Curious Case of Benjamin Button,65,50",
//             "13,7,Rachel Getting Married,40,60",
//             "14,7,New Year's Eve,35,45",
//             "15,7,The Proposal,45,55",
//             "16,8,The Time Traveler's Wife,75,65",
//             "17,8,The Invention of Lying,50,40",
//             "18,9,The Heartbreak Kid,60,50",
//             "19,10,The Duchess,70,60",
//             "20,10,Mamma Mia!,40,45" };
//   Map<Integer, ShowActor.Show> showsMap = new HashMap<>();

//   for (String show : shows) {
//       String[] parts = show.split(",");
//       int showId = Integer.parseInt(parts[0]);
//       int theatreId = Integer.parseInt(parts[1]);
//       String title = parts[2];
//       int price = Integer.parseInt(parts[3]);
//       int seatsAvailable = Integer.parseInt(parts[4]);
//       showsMap.put(showId, new ShowActor.Show(showId, theatreId, title, price, seatsAvailable));
//   }
//   sharding.init(
//     Entity.of(ShowActor.TypeKey,
//         entityContext -> {
//             String entityId = entityContext.getEntityId();
//             ShowActor.Show show = showsMap.get(Integer.parseInt(entityId));
//             return ShowActor.create(Integer.parseInt(entityId), show.theatre_id(), show.title(), show.price(), show.seats_available());
//         }
//     )
// );
//     for (String line : shows) {
//       String[] str = line.split(",");
//       int show_id = Integer.parseInt(str[0]);
//       int theatre_id = Integer.parseInt(str[1]);
//       String title = str[2];
//       int price = Integer.parseInt(str[3]);
//       int seats_available = Integer.parseInt(str[4]);
//       sharding= ClusterSharding.get(context.getSystem());
//       this.sharding=sharding;
     
    
      
//       EntityRef<ShowActor.Command> ref2 = sharding.entityRefFor(ShowActor.TypeKey, Integer.toString(show_id));
//       //showActors.put(show_id, ref2);
      
//   }
      // showActors.put(show_id,
      //         context.spawn(ShowActor.create(show_id, theatre_id, title, price, seats_available), "Show" + show_id));
    }
  


  public static Behavior<Command> create(ActorRef<WorkerActor.Command> router,Map<Integer,EntityRef<ShowActor.Command>> showActors) {

    return Behaviors.setup(context->(new BookingRegistry(context, router,showActors)));
  }

  @Override
  public Receive<Command> createReceive() {
    return newReceiveBuilder()
        .onMessage(GetTheatre.class, this::onGetTheatre)
        .onMessage(GetShow.class, this::onGetShow)
        .onMessage(AddBooking.class,this::onAddBooking)
        .onMessage(DeleteUserBooking.class,this::onDeleteUserBooking)
        .onMessage(GetUserBookings.class,this::onGetUserBookings)
        .onMessage(ShowByTheatre.class,this::onShowByTheatre)
        .onMessage(DeleteAllUserBookings.class,this::onDeleteAllUserBookings)
        .onMessage(DeleteAllBookings.class,this::onDeleteAllBookings)
        .build();
  }

  private Behavior<Command> onGetTheatre(GetTheatre message) {
    message.replyTo().tell(new Theatres(Collections.unmodifiableList(new ArrayList<>(theatres))));
    return this;
  }
  private Behavior<Command> onGetShow(GetShow message) {
    if(message.id>20||message.id<1)
      message.replyTo.tell(new ShowActor.Show(-1,-1,null,-1,-1));
    else 
    {
      Integer show_id = message.id;
      getContext().getLog().info("Show details recv");
      getContext().getLog().info("Show details to "+ showActors);
      EntityRef<ShowActor.Command> ref = showActors.get(show_id);
      getContext().getLog().info("Show ref to "+ ref);
      ref.tell(new ShowActor.GetShow(message.replyTo));
    }
    return this;
  }

  private Behavior<Command> onAddBooking(AddBooking message) {
        final Http http = Http.get(getContext().getSystem());
        Integer user_id = message.booking.user_id;
        Integer show_id = message.booking.show_id;
        Integer seats_booked = message.booking.seats_booked;
        if(show_id>0 && show_id<21 && UserService.isUser(message.booking.user_id,http)){ 
          this.count = this.count + 1;
          ActorRef<ShowActor.Booking> replyTo = message.replyTo;
          EntityRef<ShowActor.Command> ref = showActors.get(show_id);
        
          ref.tell(new ShowActor.AddBooking(new ShowActor.Booking(count, user_id, show_id, seats_booked), replyTo));
        }
        else{
          message.replyTo.tell(new ShowActor.Booking(-1,-1,-1,-1));
        }
    return this;
  }
                           
  private Behavior<Command> onDeleteUserBooking(DeleteUserBooking message) {
    if(message.show_id>20 || message.show_id<1)
      message.replyTo.tell(new ShowActor.Response(false));
    else {
      EntityRef<ShowActor.Command> ref = showActors.get(message.show_id);
      ref.tell(new ShowActor.DeleteUserBooking(message.user_id, message.show_id, message.replyTo, null));
    }
    return this;
  }

  private Behavior<Command> onDeleteAllUserBookings(DeleteAllUserBookings message) {
    //ActorRef<WorkerActor.Command> workerActor = getContext().spawn(WorkerActor.create(), "Worker" + message.user_id);
    this.router.tell(new WorkerActor.DeleteAllUserBookings(message.replyTo, message.user_id));
    return this;
  }

  private Behavior<Command> onGetUserBookings(GetUserBookings message) {
    //ActorRef<WorkerActor.Command> workerActor = getContext().spawn(WorkerActor.create(), "Worker" + message.user_id);
    this.router.tell(new WorkerActor.GetUserBookings(message.replyTo, message.user_id));
    return this;
  }

  private Behavior<Command> onShowByTheatre(ShowByTheatre message) {
    if(message.theatre_id>10|| message.theatre_id<1)
        message.replyTo.tell(new WorkerActor.Showlist(null));
    else{
      //ActorRef<WorkerActor.Command> workerActorRef = getContext().spawn(WorkerActor.create(), "Worker" + message.theatre_id);
       System.out.println("Booking shard actor: "+showActors.get(1));
      this.router.tell(new WorkerActor.ShowByTheatre(message.theatre_id, message.replyTo));
    }
    return this;
  }

  private Behavior<Command> onDeleteAllBookings(DeleteAllBookings message) {
    //ActorRef<WorkerActor.Command> workerActor = getContext().spawn(WorkerActor.create(), "Worker");
    this.router.tell(new WorkerActor.DeleteAllBookings(message.replyTo));
    return this;
  }


}

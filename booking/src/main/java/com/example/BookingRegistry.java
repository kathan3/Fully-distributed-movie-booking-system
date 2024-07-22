package com.example;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.*;

// booking-registry-actor
public class BookingRegistry extends AbstractBehavior<BookingRegistry.Command> {

  public Integer count;
  public Integer change;

  // Stores show actors for each showId
  public static final Map<Integer, ActorRef<ShowRegistry.Command>> showActors = new HashMap<>();

  // Stores list of theatres
  public static final List<Theatres> allTheatres = new ArrayList<>();

  // actor protocol
  sealed interface Command {
  }

  public final static record GetTheatres(ActorRef<TheatresReply> replyTo) implements Command {
  }

  public final static record Theatres(Integer id, String name, String location) {
  }


  public final static record Booking(Integer id,Integer user_id,Integer show_id , Integer seats_booked){}
  public final static record AddBooking(Booking booking,ActorRef<ShowRegistry.Booking> replyTo) implements  Command{}

  public final record DeleteUserBooking( Integer user_id,Integer show_id, ActorRef<ShowRegistry.Response> replyTo) implements Command{}

  public final static record Deletewithid(Integer user_id,ActorRef<ShowRegistry.Response> replyTo) implements Command{}

  public final static record deleteAllBookings(ActorRef<ShowRegistry.Response> replyTo) implements Command{}


  public record TheatresReply(List<Theatres> theatres) {
  }

  private BookingRegistry(ActorContext<Command> context) {
    super(context);
    count=0;
    change=0;
    // Initialisation of theatres
    String[] theatres = { "1,Helen Hayes Theater,240 W 44th St.",
        "2,Cherry Lane Theatre,38 Commerce Street",
        "3,New World Stages,340 West 50th Street",
        "4,The Zipper Theater,100 E 17th St",
        "5,Queens Theatre,Meadows Corona Park",
        "6,The Public Theater,425 Lafayette St",
        "7,Manhattan Ensemble Theatre,55 Mercer St.",
        "8,Metropolitan Playhouse,220 E 4th St.",
        "9,Acorn Theater,410 West 42nd Street",
        "10,Apollo Theater,253 West 125th Street" };

    for (String line : theatres) {
      String[] str = line.split(","); // use comma as separator
      int id = Integer.parseInt(str[0]);
      String name = str[1];
      String location = str[2];
      allTheatres.add(new Theatres(id, name, location));
    }

    // Initialisation of shows - one actor per show
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

    for (String line : shows) {
      String[] str = line.split(",");
      int show_id = Integer.parseInt(str[0]);
      int theatre_id = Integer.parseInt(str[1]);
      String title = str[2];
      int price = Integer.parseInt(str[3]);
      int seats_available = Integer.parseInt(str[4]);

      showActors.put(show_id,
          context.spawn(ShowRegistry.create(show_id, theatre_id, title, price, seats_available), "Show" + show_id));
    }
  }

  public static Behavior<Command> create() {
    return Behaviors.setup(BookingRegistry::new);
  }

  @Override
  public Receive<Command> createReceive() {
    return newReceiveBuilder()
            .onMessage(GetTheatres.class, this::onGetTheatres)
            .onMessage(AddBooking.class,this::onAddBooking)
            .onMessage(DeleteUserBooking.class,this::onDeleteUserBooking)
            .onMessage(Deletewithid.class,this::onDeletewithid)
            .onMessage(deleteAllBookings.class,this::ondeleteAllBookings)
        .build();
  }


  private Behavior<Command> onGetTheatres(GetTheatres command) {
    command.replyTo().tell(new TheatresReply(allTheatres));
    return this;
  }
  private Behavior<Command> onAddBooking(AddBooking message) {
    this.count=this.count+1;
    Integer user_id=message.booking.user_id;
    Integer show_id=message.booking.show_id;
    Integer seats_booked=message.booking.seats_booked;
    ActorRef<ShowRegistry.Booking>replyTo=message.replyTo;
    if (show_id > 20 || show_id < 1) {
      replyTo.tell(new ShowRegistry.Booking(null, null, null, null));
    } else {
      showActors.get(show_id)
              .tell(new ShowRegistry.AddBooking(new ShowRegistry.Booking(count, user_id, show_id, seats_booked), replyTo));
    }
    return this;
//    showActors.get(show_id).tell(new ShowRegistry.AddBooking(new ShowRegistry.Booking(count,user_id,show_id,seats_booked),replyTo));
  }

  private Behavior<Command> onDeleteUserBooking(DeleteUserBooking message) {
    showActors.get(message.show_id).tell(new ShowRegistry.DeleteUserBooking(message.user_id, message.replyTo));
    return this;
  }

    private Behavior<Command> onDeletewithid(Deletewithid message) {
      ActorRef<requestProcess.Command> ref=getContext().spawn(requestProcess.create(showActors),"ref"+message.user_id);
      ref.tell(new requestProcess.Deletewithid(message.user_id,message.replyTo,ref));
      return this;
    }

  private Behavior<Command> ondeleteAllBookings(deleteAllBookings message) {
    ActorRef<requestProcess.Command> requestProcessref=getContext().spawn(requestProcess.create(showActors),"requestProcessref"+ change++);
    requestProcessref.tell(new requestProcess.deleteAllBookings(message.replyTo,requestProcessref));
    return this;
  }

}
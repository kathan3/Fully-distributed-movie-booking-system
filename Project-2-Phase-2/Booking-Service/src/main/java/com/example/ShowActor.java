package com.example;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import akka.http.javadsl.Http;

import akka.cluster.sharding.typed.javadsl.Entity;
import akka.cluster.sharding.typed.javadsl.EntityTypeKey;
import akka.cluster.sharding.typed.javadsl.ClusterSharding;
import akka.cluster.sharding.typed.javadsl.EntityRef;


import java.util.*;

import com.example.CborSerializable;

public class ShowActor extends AbstractBehavior<ShowActor.Command>  {

    public Integer id;
    public Integer theatre_id;
    String title;
    public Integer price;
    public Integer seats_available;
    public static Integer count=0;
    public final List<ShowActor.Booking> bookings=new ArrayList<>();
    public static final EntityTypeKey<ShowActor.Command> TypeKey =
			    EntityTypeKey.create(ShowActor.Command.class,"SimpleCounterEntity");
	
    // actor protocol
    sealed interface Command extends CborSerializable{}

    public final record Show(Integer id, Integer theatre_id , String title,Integer price ,Integer seats_available)implements CborSerializable{}
    public final record Booking(Integer id,Integer user_id,Integer show_id , Integer seats_booked)implements CborSerializable{}
    public final record Bookings(List<ShowActor.Booking> bookings)implements CborSerializable{}
    public final record Response(boolean flag)implements CborSerializable{}

    public final record GetShow(ActorRef<Show> replyTo) implements Command{}
    public final record AddBooking(Booking booking,ActorRef<ShowActor.Booking> replyTo) implements Command{}
    public final record DeleteUserBooking(Integer user_id,Integer show_id, ActorRef<Response> replyTo, ActorRef<WorkerActor.Command> workerReply) implements Command{}

    public final record DeleteAllBookings(ActorRef<WorkerActor.Command> workerReply) implements Command{}
    public final record GetBookings(ActorRef<WorkerActor.Command> workerRef) implements Command{}
   // private final Http http;
    public final record GetTheatre(ActorRef<WorkerActor.Command> workerRef,Integer i, Integer theatre_id,ActorRef<WorkerActor.Showlist> replyTo ) implements Command{}
    private ShowActor(ActorContext<Command> context,Integer id,Integer theatre_id, String title , Integer price,Integer seats_available) {
        super(context);
        this.id=id;
        this.theatre_id=theatre_id;
        this.title=title;
        this.price=price;
        this.seats_available=seats_available;
    }

    public static Behavior<Command> create(Integer id,Integer theatre_id, String title , Integer price,Integer seats_available) {
        return Behaviors.setup(context->new ShowActor(context,id,theatre_id,title ,price,seats_available));
    }

    @Override
    public Receive<Command> createReceive() {
        return newReceiveBuilder()
                .onMessage(GetShow.class, this::onGetShow)
                .onMessage(AddBooking.class,this::onAddBooking)
                .onMessage(DeleteUserBooking.class,this::onDeleteUserBooking)
                .onMessage(GetBookings.class,this::onGetBookings)
                .onMessage(GetTheatre.class,this::onGetTheatre)
                .onMessage(DeleteAllBookings.class, this::onDeleteAllBookings)
                .build();
    }

    private Behavior<Command> onGetShow(GetShow message) {
        getContext().getLog().info("Show details recv");
        getContext().getLog().info("Show details to "+ message.replyTo());
        message.replyTo().tell(new Show(this.id,this.theatre_id,this.title,this.price,this.seats_available));
        getContext().getLog().info("Show details sent");
        return this;
    }
    private Behavior<Command> onAddBooking(AddBooking message) {
        final Http http = Http.get(getContext().getSystem());
        //System.out.println("out if");
        getContext().getLog().info("out if");
        Integer user_id=message.booking.user_id;
        Integer show_id=message.booking.show_id;
        Integer seats_booked=message.booking.seats_booked;
        Integer amount=seats_booked*this.price;
        if(seats_booked>-1 && seats_booked <= this.seats_available && WalletService.walletOperation(user_id,amount,"debit",http))
        {
            //System.out.println("in if");
            getContext().getLog().info("in if");
            count++;
            this.seats_available=this.seats_available-seats_booked;
            Booking newBooking =new Booking(count,user_id,show_id,seats_booked);
            bookings.add(newBooking);
            message.replyTo.tell(newBooking);
        }
        else{
            message.replyTo.tell(new Booking(-1,-1,-1,-1));
        }
        return this;
    }

    private Behavior<Command> onGetBookings(GetBookings message) {
        message.workerRef().tell(new WorkerActor.GetShowBookings(bookings));
        return this;
    }

    private Behavior<Command> onDeleteAllBookings(DeleteAllBookings message) {
        List<Booking> found = new ArrayList<ShowActor.Booking>();
        final Http http = Http.get(getContext().getSystem());
        for(Booking booking : bookings){
            Integer user_id=booking.user_id;
            Integer amount=booking.seats_booked*this.price;
            WalletService.walletOperation(user_id,amount,"credit",http);
            seats_available+=booking.seats_booked;
            found.add(booking);
        }
        bookings.removeAll(found);
        message.workerReply().tell(new WorkerActor.DeleteResponse(true));
        return this;
    }

    private Behavior<Command> onDeleteUserBooking(DeleteUserBooking message) {
        List<Booking> found = new ArrayList<ShowActor.Booking>();
        final Http http = Http.get(getContext().getSystem());
        boolean flag = false;
        for(Booking booking : bookings){
            if(Objects.equals(booking.user_id, message.user_id)){
                seats_available+=booking.seats_booked;
                Integer user_id=booking.user_id;
                Integer amount=booking.seats_booked*this.price;
                WalletService.walletOperation(user_id,amount,"credit",http);
                found.add(booking);
                flag = true;
            }
        }
        bookings.removeAll(found);
        if(message.replyTo != null)
            message.replyTo().tell(new Response(flag));
        else
            message.workerReply().tell(new WorkerActor.Response(flag));
        return this;
    }

    private Behavior<Command> onGetTheatre(GetTheatre message) {
        boolean status=(Objects.equals(message.theatre_id, this.theatre_id));
        message.workerRef().tell(new WorkerActor.TheatreOfShow(status,new Show(this.id,this.theatre_id,this.title,this.price,this.seats_available),message.i,message.replyTo));
        return this;
    }

}

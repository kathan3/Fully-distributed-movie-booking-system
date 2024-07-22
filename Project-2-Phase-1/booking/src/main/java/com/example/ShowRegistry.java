package com.example;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import akka.http.javadsl.Http;


import java.util.*;
import java.io.*;

public class ShowRegistry extends AbstractBehavior<ShowRegistry.Command> {

    sealed interface Command {
    }
    final Http http = Http.get(getContext().getSystem());
    private final Integer id;
    private final Integer theatre_id;
    private final String title;
    private final Integer price;
    private Integer seats_available;
    public List<ShowRegistry.Booking> bookings=new ArrayList<>();
    public final static record GetShow(Integer show_id, ActorRef<Show> replyTo) implements Command {
    }

    public final static record UserBookings(List<ShowRegistry.Booking> bookings) {

    }
    public final static record GetShowTheatreid(Integer show_id, Integer theatre_id, ActorRef<Show> replyTo)
            implements Command {
    }

    public final static record Show(Integer id, Integer theatre_id, String title, Integer price,
            Integer seats_available) {
    }
    public final record GetAllUserBookings(Integer show_id, Integer user_id,
                                           ActorRef<ShowRegistry.UserBookings> replyTo)
            implements Command {
    }
    public final static record Booking(Integer id,Integer user_id,Integer show_id , Integer seats_booked){}
    public final static record Response(String description){}
    public final static record AddBooking(Booking booking,ActorRef<ShowRegistry.Booking> replyTo) implements Command{}
    public final static record DeleteUserBooking(Integer user_id,ActorRef<ShowRegistry.Response> replyTo) implements Command{}
    public final record Deletewithid(Integer user_id,ActorRef<requestProcess.Command> replyTo) implements Command{}

    public final record deleteAllBookings(ActorRef<requestProcess.Command> replyTo) implements Command{}





    private ShowRegistry(ActorContext<Command> context, Integer id, Integer theatre_id, String title, Integer price,
            Integer seats_available) {
        super(context);
        this.id = id;
        this.theatre_id = theatre_id;
        this.title = title;
        this.price = price;
        this.seats_available = seats_available;
    }



    public static Behavior<Command> create(Integer id, Integer theatre_id, String title, Integer price,
            Integer seats_available) {
        return Behaviors.setup(context -> new ShowRegistry(context, id, theatre_id, title, price, seats_available));
    }
    @Override
    public Receive<Command> createReceive() {
        return newReceiveBuilder()
                .onMessage(GetShow.class, this::onGetShow)
                .onMessage(GetShowTheatreid.class, this::onGetShowTheatreid)
                .onMessage(GetAllUserBookings.class, this::onGetAllUserBookings)
                .onMessage(AddBooking.class,this::onAddBooking)
                .onMessage(DeleteUserBooking.class,this::onDeleteUserBooking)
                .onMessage(Deletewithid.class,this::onDeletewithid)
                .onMessage(deleteAllBookings.class,this::ondeleteAllBookings)

                .build();

    }




    private Behavior<Command> onGetShow(GetShow command) {
        if (this.id.equals(command.show_id())) {
            command.replyTo().tell(new Show(this.id, this.theatre_id, this.title, this.price, this.seats_available));
        }
        return this;
    }





    private Behavior<Command> onGetShowTheatreid(GetShowTheatreid command) {
        if (this.theatre_id.equals(command.theatre_id())) {
            command.replyTo().tell(new Show(this.id, this.theatre_id, this.title, this.price, this.seats_available));
        } else {
            command.replyTo().tell(new Show(null, this.theatre_id, this.title, this.price, this.seats_available));
        }
        return this;
    }








    private Behavior<Command> onAddBooking(AddBooking message) {
//        Integer id=message.booking.id;
//        Integer user_id=message.booking.user_id;
//        Integer show_id=message.booking.show_id;
//        Integer seats_booked=message.booking.seats_booked;
//        this.seats_available=this.seats_available-seats_booked;
//        Booking newBooking =new Booking(id,user_id,show_id,seats_booked);
//        bookings.add(newBooking);
//        message.replyTo().tell(newBooking);
//        return this;
        Integer id = message.booking.id;
        Integer user_id = message.booking.user_id;
        Integer show_id = message.booking.show_id;
        Integer seats_booked = message.booking.seats_booked;


        if (this.seats_available < seats_booked) {
            message.replyTo.tell(new ShowRegistry.Booking(null, null, null, null));
        } else {

            if (!UserService.doesUserExist(user_id, http)) {
                message.replyTo.tell(new ShowRegistry.Booking(null, null, null, null));
            } else {
                String walletReductionStatus = WalletService.payment(user_id, seats_booked * this.price, http);
                System.out.println("walletReductionStatus - " + walletReductionStatus);

                if (walletReductionStatus == "FAIL") {
                    message.replyTo.tell(new ShowRegistry.Booking(null, null, null, null));
                } else {

                    ListIterator<Booking> iter = bookings.listIterator();
                    Booking newBooking = new Booking(id, user_id, show_id, seats_booked);
                    while (iter.hasNext()) {
                        Booking currentBooking = iter.next();
                        if (Objects.equals(currentBooking.show_id, show_id)
                                && Objects.equals(currentBooking.user_id, user_id)) {

                            newBooking = new Booking(currentBooking.id, user_id, show_id,
                                    currentBooking.seats_booked + seats_booked);
                            iter.remove();
                        }
                    }

                    this.seats_available = this.seats_available - seats_booked;
                    bookings.add(newBooking);
                    message.replyTo().tell(newBooking);
                }
            }
        }
        return this;
    }






    private Behavior<Command> onGetAllUserBookings(GetAllUserBookings command) {
        ListIterator<Booking> iter = bookings.listIterator();
        List<Booking> userBookings = new ArrayList<>();
        while (iter.hasNext()) {
            Booking currentBooking = iter.next();
            if (Objects.equals(currentBooking.user_id, command.user_id)
                    && Objects.equals(currentBooking.show_id, command.show_id)) {
                userBookings.add(currentBooking);
            }
        }
        command.replyTo().tell(new UserBookings(userBookings));
        return this;
    }




    private Behavior<Command> onDeleteUserBooking(DeleteUserBooking message) {
        List<Booking> deleteList=new ArrayList<>();
        boolean flag=false;
        for(Booking iter : bookings){
            if(Objects.equals(iter.user_id, message.user_id)){
                flag = true;
                // Amount to make these booking to be returned to users wallets
                String walletRefundStatus = WalletService.refund(message.user_id,
                        iter.seats_booked * this.price, http);
                System.out.println("walletRefundStatus - " + walletRefundStatus);
                seats_available+=iter.seats_booked;
                deleteList.add(iter);
            }
        }
        bookings.removeAll(deleteList);
        if (flag) {
            message.replyTo().tell(new ShowRegistry.Response("Done"));
        } else {
            message.replyTo().tell(new ShowRegistry.Response("Not_Found"));
        }
        return this;
    }

    private Behavior<Command> ondeleteAllBookings(deleteAllBookings message) {
        List<Booking> deleteList=new ArrayList<>();
        for(Booking iter : bookings){
            seats_available+=iter.seats_booked;
            String walletRefundStatus = WalletService.refund(iter.user_id,
                    iter.seats_booked * this.price, http);
            System.out.println("walletRefundStatus - " + walletRefundStatus);
            deleteList.add(iter);
        }
        bookings.removeAll(deleteList);
        message.replyTo().tell(new requestProcess.Response("Done"));
        return this;
    }

    private Behavior<Command> onDeletewithid(Deletewithid message) {
        List<Booking> deleteList=new ArrayList<>();
        boolean flag=false;
        for(Booking iter : bookings){
            if(Objects.equals(iter.user_id, message.user_id)){
                flag=true;

                String walletRefundStatus = WalletService.refund(message.user_id,
                        iter.seats_booked * this.price, http);
                System.out.println("walletRefundStatus - " + walletRefundStatus);
                seats_available+=iter.seats_booked;
                deleteList.add(iter);
            }
        }
        bookings.removeAll(deleteList);
        if(flag) {
            message.replyTo().tell(new requestProcess.Response("Done"));
        }
        else{
            message.replyTo().tell(new requestProcess.Response("NOT_FOUND"));
        }
        return this;
    }


}
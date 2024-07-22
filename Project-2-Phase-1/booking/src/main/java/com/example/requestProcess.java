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
import java.io.*;

public class requestProcess extends AbstractBehavior<requestProcess.Command> {

    public Integer count;
    public Integer kite;

    public ActorRef<ShowRegistry.Response> replyTo;

    sealed interface Command {
    }
    public Map<Integer, ActorRef<ShowRegistry.Command>> showActors;

    private requestProcess(ActorContext<Command> context, Map<Integer, ActorRef<ShowRegistry.Command>> showActors) {
        super(context);
        this.count=0;
        this.kite=0;
        this.showActors = showActors;
    }

    public static Behavior<Command> create(Map<Integer, ActorRef<ShowRegistry.Command>> showActors) {
        return Behaviors.setup(context -> new requestProcess(context, showActors));
    }

    @Override
    public Receive<Command> createReceive() {
        return newReceiveBuilder()
                .onMessage(Deletewithid.class, this::onDeletewithid)
                .onMessage(deleteAllBookings.class, this::ondeleteAllBookings)
                .onMessage(Response.class, this::onResponse)
                .build();
    }

    public final static record Deletewithid(Integer user_id,ActorRef<ShowRegistry.Response> replyTo,ActorRef<requestProcess.Command> selfRef) implements Command{}

    public final static record deleteAllBookings(ActorRef<ShowRegistry.Response> replyTo,ActorRef<requestProcess.Command> selfRef) implements Command{}


    public final static record Response(String what) implements Command{}

    private Behavior<Command> onDeletewithid(Deletewithid message) {
        this.replyTo=message.replyTo;
        for (Integer show_id : showActors.keySet()) {
            showActors.get(show_id).tell(new ShowRegistry.Deletewithid(message.user_id, message.selfRef));
        }
        return this;
    }

    private Behavior<Command> ondeleteAllBookings(deleteAllBookings message) {
        this.replyTo = message.replyTo;
        for (Integer show_id : showActors.keySet()) {
            showActors.get(show_id).tell(new ShowRegistry.deleteAllBookings(message.selfRef));
        }
        return this;
    }
    private Behavior<Command> onResponse(requestProcess.Response message) {
        if(Objects.equals(message.what, "Done"))
            count++;
        else{
            kite++;
            count++;
        }
        if(count==20) {
            if(kite!=20) {
                replyTo.tell(new ShowRegistry.Response("Done"));
            }
            else{
                replyTo.tell(new ShowRegistry.Response("NOT_FOUND"));
            }
            return Behaviors.stopped();
        }
        return this;
    }
}

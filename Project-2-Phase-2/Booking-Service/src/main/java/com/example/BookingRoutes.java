package com.example;

import java.time.Duration;
import java.util.concurrent.CompletionStage;

import akka.actor.typed.ActorRef;
import akka.actor.typed.ActorSystem;
import akka.actor.typed.Scheduler;
import akka.actor.typed.javadsl.AskPattern;
import akka.http.javadsl.marshallers.jackson.Jackson;

import static akka.http.javadsl.server.Directives.*;

import akka.http.javadsl.model.StatusCodes;
import akka.http.javadsl.server.PathMatchers;
import akka.http.javadsl.server.Route;

public class BookingRoutes {
  private  ActorRef<BookingRegistry.Command> bookingRegistryActor;
  private final Duration askTimeout;
  private final Scheduler scheduler;

  public BookingRoutes(ActorSystem<?> system, ActorRef<BookingRegistry.Command> bookingRegistryActor) {
    this.bookingRegistryActor = bookingRegistryActor;
    scheduler = system.scheduler();
    askTimeout = system.settings().config().getDuration("my-app.routes.ask-timeout");
  }

  private CompletionStage<BookingRegistry.Theatres> getTheatre() {
  
    return AskPattern.ask(bookingRegistryActor, BookingRegistry.GetTheatre::new, askTimeout, scheduler);
  }

  private CompletionStage<ShowActor.Show> getShow(Integer show_id) {
    System.out.println("Inside getShow");
    return AskPattern.ask(bookingRegistryActor,ref->new BookingRegistry.GetShow(show_id,ref), askTimeout, scheduler);
  }
  private CompletionStage<BookingRegistry.Bookings> getUserBookings(Integer user_id) {
    return AskPattern.ask(bookingRegistryActor,ref->new BookingRegistry.GetUserBookings(user_id,ref), askTimeout, scheduler);
  }
  private CompletionStage<ShowActor.Booking> addBooking(BookingRegistry.Booking booking) {
    return AskPattern.ask(bookingRegistryActor,ref->new BookingRegistry.AddBooking(booking,ref), askTimeout, scheduler);
  }
  private CompletionStage<ShowActor.Response> deleteUserBooking(Integer user_id,Integer show_id) {
      return AskPattern.ask(bookingRegistryActor,ref->new BookingRegistry.DeleteUserBooking(user_id,show_id,ref), askTimeout, scheduler);
  }
  private CompletionStage<ShowActor.Response> deleteAllUserBooking(Integer user_id) {
      return AskPattern.ask(bookingRegistryActor,ref->new BookingRegistry.DeleteAllUserBookings(user_id,ref), askTimeout, scheduler);
  }

private CompletionStage<WorkerActor.Showlist> getShowByTheatre(Integer theatre_id) {
    return AskPattern.ask(bookingRegistryActor,ref->new BookingRegistry.ShowByTheatre(theatre_id,ref), askTimeout, scheduler);
}
    private CompletionStage<WorkerActor.DeleteResponse> deleteAllBookings() {
        return AskPattern.ask(bookingRegistryActor, BookingRegistry.DeleteAllBookings::new, askTimeout, scheduler);
    }

    public Route bookingRoutes() {
        return concat(
                pathPrefix("theatres",
                        () -> pathEnd(() -> get(
                                () -> onSuccess(getTheatre(), theatres->{
                                        if (theatres != null) {
                                            return complete(StatusCodes.OK, theatres.theatres(), Jackson.marshaller());
                                        } else {
                                            return complete(StatusCodes.NOT_FOUND);
                                        }
                                    })
                                ))),
                pathPrefix("shows", () -> path(PathMatchers.segment(), (String show_id) -> get(() -> {
                  return onSuccess(getShow(Integer.parseInt(show_id)), showDetails -> {
                    System.out.println("Show details: " + show_id);
                    if (showDetails.title() != null) {
                      System.out.println("Show details: " + showDetails);
                      return complete(StatusCodes.OK, showDetails, Jackson.marshaller());
                    } else {
                      return complete(StatusCodes.NOT_FOUND, "Show not found");
                    }
                  });
                }))),
                pathPrefix("shows",()->pathPrefix("theatres",()->pathPrefix(PathMatchers.integerSegment(),(theatre_id) -> get(() -> {
                    return onSuccess(getShowByTheatre(theatre_id), showDetails -> {
                        if (showDetails.showlist() != null) {
                            return complete(StatusCodes.OK,showDetails.showlist(),Jackson.marshaller());
                        } else {
                            return complete(StatusCodes.NOT_FOUND);
                        }
                    });
                })))),
                pathPrefix("bookings", () ->
                        pathPrefix("users", () -> pathPrefix(PathMatchers.integerSegment(), (user_id) -> get(
                                () -> onSuccess(getUserBookings(user_id), userBookings -> complete(StatusCodes.OK, userBookings.bookings(),
                                        Jackson.marshaller()))
                        )))
                ),
                pathPrefix("bookings",()->concat(

                        pathPrefix("users",()->pathPrefix(PathMatchers.integerSegment(),(user_id) -> pathPrefix("shows",()->pathPrefix(PathMatchers.integerSegment(), (show_id) -> delete(() -> {
                            return onSuccess(deleteUserBooking(user_id,show_id), showDetails -> {
                                if (showDetails.flag()) {
                                    return complete(StatusCodes.OK);
                                } else {
                                    return complete(StatusCodes.NOT_FOUND, "Show not found");
                                }
                            });
                        }))))),
                        pathPrefix("users",()->pathPrefix(PathMatchers.integerSegment(),(user_id) -> delete(() -> {
                            return onSuccess(deleteAllUserBooking(user_id), userDetails -> {
                                if (userDetails.flag()) {
                                    return complete(StatusCodes.OK);
                                } else {
                                    return complete(StatusCodes.NOT_FOUND, "Bookings not found");
                                }
                            });
                        }))),
                        pathEnd(() -> post(() ->
                        entity(
                                Jackson.unmarshaller(BookingRegistry.Booking.class),
                                booking ->
                                          onSuccess(addBooking(booking), bookingDetails -> {
                                            
                                            if (bookingDetails.id() != -1) {
                                              return complete(StatusCodes.OK, bookingDetails, Jackson.marshaller());
                                            } else {
                                              return complete(StatusCodes.BAD_REQUEST, "Show not found");
                                            }

                                        })
                        ))),
                        pathEnd(() -> delete(
                                () -> onSuccess(deleteAllBookings(), bookings -> complete(StatusCodes.OK))))

                        )
                )

        );
    }

}

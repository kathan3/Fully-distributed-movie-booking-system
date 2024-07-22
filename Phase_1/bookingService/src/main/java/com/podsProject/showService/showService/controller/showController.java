package com.podsProject.showService.showService.controller;
import com.podsProject.showService.showService.DTO.BookingDTO;
import com.podsProject.showService.showService.DTO.ShowResponseDTO;
import com.podsProject.showService.showService.entity.Booking;
import com.podsProject.showService.showService.entity.Show;
import com.podsProject.showService.showService.entity.Theatre;
import com.podsProject.showService.showService.repository.BookingRepository;
import com.podsProject.showService.showService.repository.ShowReoisitory;
import com.podsProject.showService.showService.repository.TheatreRepository;
import com.podsProject.showService.showService.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;

@RestController
public class showController {
    @Autowired
    private TheatreRepository theatreRepo;
    @Autowired
    private ShowReoisitory showRepo;

    @Autowired
    private BookingRepository bookingRepo;
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private BookingService bookingservice;

    @GetMapping("/theatres")
    public ResponseEntity<?> getThreatre() {
        try {
            List<Theatre> rep = this.theatreRepo.findAll();
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(rep);
        }
        catch (Exception e){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/shows/theatres/{theatre_id}")
    public ResponseEntity<?> getThreatre(@PathVariable Integer theatre_id) {
        try {
            List<Show> rep = this.showRepo.findByTheatreId(theatre_id);

            if (rep.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);

            }
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(rep);

        }
        catch (Exception e){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("shows/{show_id}")
    public ResponseEntity<?> getShowByID(@PathVariable Integer show_id) {
        try {
            Optional<Show> showOptional = showRepo.findById(show_id);

            if(showOptional.isEmpty()){
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            ShowResponseDTO response = new ShowResponseDTO();
            Show s = showOptional.get();
            response.setId(s.getId());
            response.setPrice(s.getPrice());
            response.setTitle(s.getTitle());
            response.setTheatre_id(s.getTheatreId());
            response.setSeats_available(s.getSeats_available());
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(response);

        }
        catch (Exception e){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
    @GetMapping("/bookings/users/{user_id}")
    public ResponseEntity<?> getBookings(@PathVariable Integer user_id) {
        try {
            List<Booking> rep = bookingRepo.findByUserId(user_id);
            if (rep.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(rep);

        }
        catch (Exception e){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/bookings")
    public ResponseEntity<?> CreateBooking(@RequestBody BookingDTO book) {
    try {
        return bookingservice.CreateBooking(book);
    }
    catch (Exception e){
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
    }

    @DeleteMapping("/bookings/users/{user_id}")
    public ResponseEntity<?> DeleteBookings(@PathVariable Integer user_id) {
    try {
        return bookingservice.DeleteBookings(user_id);
    }
    catch (Exception e){
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
    }

    @DeleteMapping("/bookings/users/{user_id}/shows/{show_id}")
    public ResponseEntity<?> DeleteBookingOfUser(@PathVariable Integer user_id, @PathVariable Integer show_id) {
        try {
            return bookingservice.DeleteBookingOfUser(user_id, show_id);
        }
        catch (Exception e){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }path("bookings", () -> post(() -> entity(Jackson.unmarshaller(BookingRequest.class), bookingRequest -> {

          // Extracted BookingRequest from JSON payload

          int show_id = bookingRequest.getShowId();

          int user_id = bookingRequest.getUserId();

          int seats_booked = bookingRequest.getSeatsBooked();



          return onSuccess(postBooking(show_id, user_id, seats_booked), bookingDetails -> {

            if (bookingDetails != null) {

              return complete(StatusCodes.OK, bookingDetails, Jackson.marshaller());

            } else {

              return complete(StatusCodes.NOT_FOUND, "Booking not made");

            }

          });

        })))

    @DeleteMapping("/bookings")
    public ResponseEntity<?> DeleteAllBookings() {
        try {
            return bookingservice.DeleteAllBookings();
        }
        catch (Exception e){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

}

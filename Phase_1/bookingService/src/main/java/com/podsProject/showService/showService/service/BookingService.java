package com.podsProject.showService.showService.service;

import com.podsProject.showService.showService.DTO.BookingDTO;
import com.podsProject.showService.showService.DTO.UserDTO;
import com.podsProject.showService.showService.DTO.WalletRequestDTO;
import com.podsProject.showService.showService.DTO.WalletResponseDTO;
import com.podsProject.showService.showService.entity.Booking;
import com.podsProject.showService.showService.entity.Show;
import com.podsProject.showService.showService.repository.BookingRepository;
import com.podsProject.showService.showService.repository.ShowReoisitory;
import jakarta.persistence.criteria.CriteriaBuilder;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.awt.print.Book;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class BookingService {
    @Autowired
    private BookingRepository BookRepo;
    @Autowired
    private ShowReoisitory ShowRepo;
    @Autowired
    private RestTemplate restTemplate;


    //For Creating Booking
    public ResponseEntity<?> CreateBooking(BookingDTO booking) {
        Optional<Show> show = ShowRepo.findById(booking.getShow_id());
        if (show.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Show Doesn't Exist !!!");
        }
        //Checking if Get request to User Service is Exection or not.
        try {
            ResponseEntity<UserDTO> UserResponse = restTemplate.getForEntity(
                    "http://host.docker.internal:8080/users/{user_id}", UserDTO.class, booking.getUser_id());
            Integer AvailableSeats = show.get().getSeats_available();
            Integer RequestedSeats = booking.getSeats_booked();
            if (RequestedSeats > AvailableSeats) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(show.get().getTitle() + " Only have "+AvailableSeats+" seats!!!");
            } else {

                //We are creating HTTP header first and we are inserting WalletRequestDTO class and pass it to wallet service as json payload.

                Integer Amount = RequestedSeats * show.get().getPrice();
                HttpEntity<WalletRequestDTO> entity = GenerateHTTPHeaderForWallet(Amount, "debit");
                String url = "http://host.docker.internal:8082/wallets/{user_id}";
                Map<String, Integer> uriVariables = new HashMap<>();
                uriVariables.put("user_id", booking.getUser_id());

               //Checking Put Request in Wallet service is exception or not
                try {

                    ResponseEntity<WalletResponseDTO> r = restTemplate.exchange(url, HttpMethod.PUT, entity, WalletResponseDTO.class, uriVariables);
                    Show BookedShowClass = show.get();
                    BookedShowClass.setSeats_available(AvailableSeats - RequestedSeats);
                    ShowRepo.save(BookedShowClass);
                    Booking NewBooking = new Booking();
                    NewBooking.setShowId(booking.getShow_id());
                    NewBooking.setUserId(booking.getUser_id());
                    NewBooking.setSeatsBooked(booking.getSeats_booked());
                    BookRepo.save(NewBooking);
                    return ResponseEntity.status(HttpStatus.OK).body("User : "+booking.getUser_id() +" Your booking for "+show.get().getTitle() + " of tickets : "+ RequestedSeats+" is completed successfully");
                } catch (HttpClientErrorException.BadRequest e) {

                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Insufficient Wallet Balance");
                }
            }

        } catch (HttpClientErrorException.NotFound e) {

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User is not found");

        }

    }

// For Deleting all bookings of a particular Usser.
    public ResponseEntity<?> DeleteBookings(Integer user_id) {
        List<Booking> BookingsToBeDeleted = BookRepo.findByUserId(user_id);
        if (BookingsToBeDeleted.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Bookings Not Found");
        }
        Integer RefundAmount = 0;
        for (Booking booking : BookingsToBeDeleted) {
            Optional<Show> show = ShowRepo.findById(booking.getShowId());
            Show showClass = show.get();
            Integer CurrentAvailabeSeats = showClass.getSeats_available();
            Integer UserBookedSeats = booking.getSeatsBooked();
            Integer NewTotalSeats = CurrentAvailabeSeats + UserBookedSeats;
            showClass.setSeats_available(NewTotalSeats);
            RefundAmount += UserBookedSeats * showClass.getPrice();
            ShowRepo.save(showClass);
            BookRepo.deleteById(booking.getId());
        }

        //We are creating HTTP Put request to wallet to add the refund amount to the user's wallet.

        HttpEntity<WalletRequestDTO> entity = GenerateHTTPHeaderForWallet(RefundAmount, "credit");

        try {
            // Assuming user_id is a variable holding the user's ID
            String url = "http://host.docker.internal:8082/wallets/{user_id}";
            Map<String, Integer> uriVariables = new HashMap<>();
            uriVariables.put("user_id", user_id);
            ResponseEntity<WalletResponseDTO> r = restTemplate.exchange(url, HttpMethod.PUT, entity, WalletResponseDTO.class, uriVariables);


            return ResponseEntity.status(HttpStatus.OK).body("User : "+user_id+" your all bookings deleted successfully");
        } catch (HttpClientErrorException.BadRequest e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    //Deleting a particular show of a praticular user
    public ResponseEntity<String> DeleteBookingOfUser(Integer UserId, Integer ShowId) {
        List<Booking> BookingsByUser = BookRepo.findByUserId(UserId);
        if (BookingsByUser.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        Integer RefundAmount = 0;
        for (Booking bookingByUser : BookingsByUser) {
            if (bookingByUser.getShowId() == ShowId) {
                Optional<Show> show = ShowRepo.findById(bookingByUser.getShowId());
                Show showClass = show.get();
                Integer CurrentAvailabeSeats = showClass.getSeats_available();
                Integer SeatsBookedByUser = bookingByUser.getSeatsBooked();
                Integer NewSeats = CurrentAvailabeSeats + SeatsBookedByUser;
                showClass.setSeats_available(NewSeats);
                RefundAmount += SeatsBookedByUser * showClass.getPrice();
                ShowRepo.save(showClass);
                BookRepo.deleteById(bookingByUser.getId());
            }
        }
        //Creating HTTP Put request.
        HttpEntity<WalletRequestDTO> entity = GenerateHTTPHeaderForWallet(RefundAmount, "credit");

        try {

            String url = "http://host.docker.internal:8082/wallets/{user_id}";
            Map<String, Integer> uriVariables = new HashMap<>();
            uriVariables.put("user_id", UserId);
            ResponseEntity<WalletResponseDTO> r = restTemplate.exchange(url, HttpMethod.PUT, entity, WalletResponseDTO.class, uriVariables);
            return ResponseEntity.status(HttpStatus.OK).body("User : " + UserId+" your booking of Show Id : "+ShowId+" deleted Successfully");
        } catch (HttpClientErrorException.BadRequest e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

    }

    //Delete All bookings
    public ResponseEntity<String> DeleteAllBookings() {

        // We are usinng two has maps one of user's total refund and one for show's total seats which we have to add after deleting bookings.

        List<Booking> AllBookings = BookRepo.findAll();
        List<Show> AllShows = ShowRepo.findAll();
        Map<Integer, Integer> UserRefundAmount = new HashMap<>();
        Map<Integer, Integer> TotalShowSeats = new HashMap<>();
        for (Show show : AllShows) {
            TotalShowSeats.put(show.getId(), show.getSeats_available());
        }
        for (Booking booking : AllBookings) {
            Optional<Show> showOptional = ShowRepo.findById(booking.getShowId());
            Show show = showOptional.get();
            if (UserRefundAmount.containsKey(booking.getUserId())) {
                Integer Total_Amount = booking.getSeatsBooked() * show.getPrice();
                Integer CurrentlyUserRefundAmount = UserRefundAmount.get(booking.getUserId());
                UserRefundAmount.put(booking.getUserId(), Total_Amount + CurrentlyUserRefundAmount);
                Integer CurrentSeats = TotalShowSeats.get(show.getId());
                TotalShowSeats.put(show.getId(), CurrentSeats + booking.getSeatsBooked());
            } else {
                Integer Total_Amount = booking.getSeatsBooked() * show.getPrice();
                UserRefundAmount.put(booking.getUserId(), Total_Amount);
                Integer CurrentSeats = TotalShowSeats.get(show.getId());
                TotalShowSeats.put(show.getId(), CurrentSeats + booking.getSeatsBooked());
            }

        }

        for (Map.Entry<Integer, Integer> show : TotalShowSeats.entrySet()) {
            Integer id = show.getKey();
            Integer seats = show.getValue();
            Optional<Show> sw = ShowRepo.findById(id);
            Show ShowClass = sw.get();
            ShowClass.setSeats_available(seats);
            ShowRepo.save(ShowClass);
        }

        //For all users which have booked shows we are creating http put request for them to send refund amount.
        for (Map.Entry<Integer, Integer> entry : UserRefundAmount.entrySet()) {
            int user_id = entry.getKey();
            int Refund = entry.getValue();
            HttpEntity<WalletRequestDTO> entity = GenerateHTTPHeaderForWallet(Refund, "credit");

            try {
                String url = "http://host.docker.internal:8082/wallets/{user_id}";
                Map<String, Integer> uriVariables = new HashMap<>();
                uriVariables.put("user_id", user_id);
                ResponseEntity<WalletResponseDTO> r = restTemplate.exchange(url, HttpMethod.PUT, entity, WalletResponseDTO.class, uriVariables);

                return ResponseEntity.status(HttpStatus.OK).body("All Bookings Deleted Successfully");
            } catch (HttpClientErrorException.BadRequest e) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }

        }

        BookRepo.deleteAll();

        return new ResponseEntity<>(HttpStatus.OK);
    }

    public HttpEntity<WalletRequestDTO> GenerateHTTPHeaderForWallet(Integer RefundAmount, String Action) {
        WalletRequestDTO walletRequest = new WalletRequestDTO();
        walletRequest.setAction(Action);
        walletRequest.setAmount(RefundAmount);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<WalletRequestDTO> entity = new HttpEntity<>(walletRequest, headers);
        return entity;
    }
}

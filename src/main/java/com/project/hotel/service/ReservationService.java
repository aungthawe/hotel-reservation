package com.project.hotel.service;
import com.project.hotel.entity.*;
import com.project.hotel.repository.CustomerRepository;
import com.project.hotel.repository.ReservationRepository;
import com.project.hotel.repository.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
public class ReservationService {

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private RoomService roomService;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private CustomerRepository customerRepository;

    public Reservation saveReservation(User user, Room room, LocalDate checkinDate, LocalDate checkoutDate, LocalDate reservationDate, String nrc, String address, Payment payment) {
        Customer existingCustomer = customerRepository.findByUserId(user.getId());
        Customer customer = new Customer();
        if(existingCustomer == null){
            customer.setUser(user);
            customer.setNrc(nrc);
            customer.setAddress(address);
            customerRepository.save(customer);
        }
        customer = existingCustomer;
        Reservation reservation = new Reservation();
        reservation.setRoom(room);
        reservation.setCustomer(customer);
        reservation.setCheckinDate(checkinDate);
        reservation.setCheckoutDate(checkoutDate);
        reservation.setReservationDate(reservationDate);
        reservation.setTotalAmount(calculateTotalAmount(room, checkinDate, checkoutDate));
        reservation.setPayment(payment);

        return reservationRepository.save(reservation);
    }

    public Double calculateTotalAmount(Room room, LocalDate checkInDate, LocalDate checkOutDate) {
        long days = ChronoUnit.DAYS.between(checkInDate, checkOutDate);
        if (days <= 0) {
            throw new IllegalArgumentException("Checkout date must be after check-in date");
        }

        double pricePerNight = room.getPrice();
        double total = pricePerNight * days;

        Integer firstDiscount = room.getDiscount();
        int discountPercent = firstDiscount != null ? firstDiscount : 0;

        double discount = (discountPercent > 0) ? (discountPercent / 100.0) : 0;

        return total * (1 - discount);
    }

    public Reservation getReservationById(Long reservationId) {
        return reservationRepository.findById(reservationId)
                .orElseThrow(() -> new IllegalStateException("Reservation not found with ID: " + reservationId));
    }

    public List<Reservation> getReservationByCustomer(Customer customer){
        return reservationRepository.findByCustomer(customer);
    }

    public List<Reservation> getAllReservations() {
        return reservationRepository.findAll();
    }

    @Transactional
    public void updateReservationDates(Long id, LocalDate newCheckin, LocalDate newCheckout) {
        Reservation reservation = reservationRepository.findById(id).orElseThrow();
        if (canEditReservation(reservation)) {
            reservation.setCheckinDate(newCheckin);
            reservation.setCheckoutDate(newCheckout);
            reservationRepository.save(reservation);
        }
    }

    @Transactional
    public void cancelReservation(Long reservationId) {

        reservationRepository.deleteById(reservationId);
    }

    public boolean isRoomAvailable(Long roomId, LocalDate checkinDate, LocalDate checkoutDate) {
        Room room = roomRepository.findById(roomId).orElseThrow();

        List<Reservation> existing = reservationRepository.findByRoom(room);
        for (Reservation r : existing) {
            if (!(checkoutDate.isBefore(r.getCheckinDate()) || checkinDate.isAfter(r.getCheckoutDate()))) {
                return false;
            }
        }
        return true;
    }

    public List<LocalDate> getAvailableDatesForRoomInMonth(Long roomId, YearMonth month){
        Room room = roomService.getRoomById(roomId);
        if(room ==null)return Collections.emptyList();

        LocalDate start = LocalDate.now();
        LocalDate end = month.atEndOfMonth();

        //get all reservation for that room
        List<Reservation> reservations = reservationRepository.findByRoomIdAndDateRange(roomId,start,end);

        //collect all reserved dates in hashset
        Set<LocalDate> reservedDates = new HashSet<>();
        for(Reservation r : reservations){
            LocalDate reservedStart  = r.getCheckinDate();
            LocalDate reservedEnd = r.getCheckoutDate();
            for(LocalDate d = reservedStart; !d.isAfter(reservedEnd);d = d.plusDays(1)){
                reservedDates.add(d);
            }
        }

        //now collect all available dates by removing reservedDates
        List<LocalDate> availableDates  = new ArrayList<>();
        for(LocalDate d = start; !d.isAfter(end); d = d.plusDays(1)){
            if(!reservedDates.contains(d)) availableDates.add(d);
        }

        return availableDates;
    }

    public boolean canEditReservation(Reservation reservation){
        LocalDate today = LocalDate.now();
        LocalDate checkin = reservation.getCheckinDate();
        return ChronoUnit.DAYS.between(today, checkin) >= 2;
    }

    public Reservation findById(Long Id){
        return reservationRepository.findById(Id).orElseThrow();
    }

    public boolean isRoomAvailableExcludingReservation(Room room, LocalDate newCheckin, LocalDate newCheckout, Long reservationIdToExclude) {
        List<Reservation> reservations = reservationRepository.findByRoom(room);

        for (Reservation reservation : reservations) {
            // Skip the reservation we're excluding
            if (reservation.getId().equals(reservationIdToExclude)) {
                continue;
            }

            LocalDate existingCheckin = reservation.getCheckinDate();
            LocalDate existingCheckout = reservation.getCheckoutDate();

            // If dates overlap, room is not available
            boolean datesOverlap = !(newCheckout.isBefore(existingCheckin) || newCheckin.isAfter(existingCheckout));
            if (datesOverlap) {
                return false;
            }
        }

        // No overlaps found, room is available
        return true;
    }

}

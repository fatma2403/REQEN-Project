package org.example;

public class ReservationDemo {

    public static void main(String[] args) {

        Kunde customer = new Kunde("Martin Keller", "martin.keller@testmail.com", "secret");

        Ladestation station = new Ladestation(
                1,
                Lademodus.AC,
                Betriebszustand.IN_BETRIEB_BESETZT
        );

        boolean reserved = station.getBetriebszustand() == Betriebszustand.IN_BETRIEB_FREI;

        if (!reserved) {
            System.out.println("Reservation rejected: station already reserved");
        } else {
            station.setBetriebszustand(Betriebszustand.IN_BETRIEB_BESETZT);
            System.out.println("Station reserved for customer: " + customer.getEmail());
        }
    }
}

package org.example;

public class PrepaidSystem {

    private final KundenkontoService kundenkontoService;

    public PrepaidSystem(KundenkontoService kundenkontoService) {
        this.kundenkontoService = kundenkontoService;
    }

    /**
     * Bucht einen Betrag auf das Konto des Kunden.
     */
    public void guthabenAufladen(Kunde kunde, double betrag) {
        kundenkontoService.kontoFuerKundenSicherstellen(kunde);
        kundenkontoService.guthabenAufladen(kunde.getKundenId(), betrag);
    }

    public double aktuellesGuthaben(Kunde kunde) {
        return kundenkontoService.aktuellesGuthaben(kunde.getKundenId());
    }
}

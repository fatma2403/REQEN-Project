package org.example;

/**
 * Einfache Implementierung des Kundenkontos gemäß UML:
 * - kontonummer : int
 * - guthaben    : double
 * - guthabenAbbuchen(betrag) : boolean
 * - aktuellesGuthaben()      : double
 */
public class Kundenkonto {

    private int kontonummer;
    private double guthaben;

    public Kundenkonto() {
    }

    public Kundenkonto(int kontonummer, double guthaben) {
        this.kontonummer = kontonummer;
        this.guthaben = guthaben;
    }

    public int getKontonummer() {
        return kontonummer;
    }

    public void setKontonummer(int kontonummer) {
        this.kontonummer = kontonummer;
    }

    public double getGuthaben() {
        return guthaben;
    }

    public void setGuthaben(double guthaben) {
        this.guthaben = guthaben;
    }

    /**
     * Versucht den angegebenen Betrag vom Guthaben abzubuchen.
     * @return true, wenn genug Guthaben vorhanden war und abgebucht wurde.
     */
    public boolean guthabenAbbuchen(double betrag) {
        if (betrag < 0) {
            return false;
        }
        if (guthaben >= betrag) {
            guthaben -= betrag;
            return true;
        }
        return false;
    }

    /**
     * Liefert das aktuelle Guthaben.
     */
    public double aktuellesGuthaben() {
        return guthaben;
    }

    public void guthabenAufladen(double betrag) {
    }
}

package edu.hm.adjuvant;

/**
 * Klasse zur Gewaehrleistung der Anforderung: Kartenskill aufrufen.
 *
 * @author Anonymous Student
 */
class Output {
  /**
   * generiert Sprachausgabe des Reminders.
   *
   * @param scheduleTimeAsIso Zeit
   * @param adrFrom Ausgangsadresse
   * @param adrTo Uieladresse
   * @param transport Verkehrsmittel
   * @return auszugebender String
   */
  public String generateSpeach(String scheduleTimeAsIso, String name,
                               String adrFrom, String adrTo, String transport) {
    return name + " um " + scheduleTimeAsIso + "." + "Von: " + adrFrom + " Nach: " + adrTo + " "
             + " Mit dem Verkehrsmittel: " + transport
             + ". Wollen Sie die Routeninformationen erhalten sagen Sie bitte:"
             + " Führe eine Routenberechnung durch mit mein Adjuvant";
  }
}

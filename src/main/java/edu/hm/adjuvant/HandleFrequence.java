package edu.hm.adjuvant;

import com.amazon.ask.model.services.reminderManagement.RecurrenceDay;

/**
 * Hilfklasse für Bestimmen der Frequenz der Erinnerungen.
 *
 * @author Anonymous Student
 */
public class HandleFrequence {
  /**
   * Bestimmt die Frequenz.
   *
   * @param givenDay Tag vom User angegeben.
   * @return Frequenz.
   */
  public RecurrenceDay getDay(String givenDay) {
    switch (givenDay.toLowerCase()) {
      case "montag":
        return RecurrenceDay.MO;
      case "dienstag":
        return RecurrenceDay.TU;
      case "mittwoch":
        return RecurrenceDay.WE;
      case "donnerstag":
        return RecurrenceDay.TH;
      case "freitag":
        return RecurrenceDay.FR;
      case "samstag":
        return RecurrenceDay.SA;
      case "sonntag":
        return RecurrenceDay.SU;
      default:
        return RecurrenceDay.UNKNOWN_TO_SDK_VERSION;
    }
  }

}

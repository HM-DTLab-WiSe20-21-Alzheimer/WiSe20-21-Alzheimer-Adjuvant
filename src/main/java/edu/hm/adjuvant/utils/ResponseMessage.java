package edu.hm.adjuvant.utils;

/**
 * ResponseMessage.java
 *
 * @author Stefan Kühnel, stefan.kuehnel@hm.edu
 * @version 2021-1-11
 */

public class ResponseMessage {
  // Privater Konstruktor
  private ResponseMessage() {}

  public static final String ROUTE_CALCULATION_FAILED =
      "Bei der Routenberechnung ist ein Problem aufgetreten.";
  public static final String EMAIL_VERIFICATION_SEND =
      "Für diesen Vorgang ist eine verifizierte E-Mail Adresse erforderlich. "
          +
          "Klicken Sie hierzu bitte in der von Bestätigungsmail von Amazon "
          + "auf die Schaltfläche 'Bestätigen'. "
          + "Im Anschluss können Sie den aktuellen Vorgang ohne Probleme erneut durchführen.";
  public static final String ROUTE_SEND_SUCCESSFULLY =
      "Die E-Mail mit der ermittelten Wegbeschreibung "
          + "wurde an Ihre in Alexa hinterlegte E-Mail Adresse "
          + "versendet. Wir wünschen Ihnen eine gute Fahrt.";
  public static final String MISSING_PERMISSION =
      "Bitte fügen Sie die notwendigen Berechtigungen in der Amazon Alexa App hinzu.";
}

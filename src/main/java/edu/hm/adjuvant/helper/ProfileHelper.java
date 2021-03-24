package edu.hm.adjuvant.helper;

import com.amazon.ask.dispatcher.request.handler.HandlerInput;

/**
 * ProfileHelper.java
 *
 * @author Stefan Kühnel, stefan.kuehnel@hm.edu
 * @version 2021-1-11
 */

public class ProfileHelper {
  // Privater Konstruktor.
  private ProfileHelper() {}

  /**
   * Gibt die E-Mail Adresse des aktuellen Alexa Benutzers aus.
   *
   * @param handlerInput Das HandlerInput.
   * @return Die E-Mail Adresse des Alexa Benutzers.
   */
  public static String getProfileEmail(HandlerInput handlerInput) {
    return handlerInput.getServiceClientFactory().getUpsService().getProfileEmail();
  }

  /**
   * Prüft, ob der Nutzer eine E-Mail in Alexa eingetragen hat, oder nicht.
   *
   * @param handlerInput Das HandlerInput.
   * @return True, wenn eine E-Mail vorhanden ist. False ansonsten.
   */
  public static boolean hasProfileEmail(HandlerInput handlerInput) {
    try {
      final String profileEmail =
          handlerInput.getServiceClientFactory().getUpsService().getProfileEmail();
      return !profileEmail.isEmpty();
    } catch (Exception e) {
      return false;
    }
  }
}

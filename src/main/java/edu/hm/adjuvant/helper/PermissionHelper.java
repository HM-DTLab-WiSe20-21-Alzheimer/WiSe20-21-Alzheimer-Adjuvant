package edu.hm.adjuvant.helper;

import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.amazon.ask.model.Permissions;
import com.amazon.ask.model.Response;
import com.amazon.ask.response.ResponseBuilder;
import edu.hm.adjuvant.utils.ResponseMessage;
import java.util.Arrays;
import java.util.Optional;

/**
 * PermissionHelper.java
 *
 * @author Stefan Kühnel, stefan.kuehnel@hm.edu
 * @version 2021-1-11
 */

public class PermissionHelper {
  // Privater Konstruktor
  private PermissionHelper() {}

  /**
   * Bittet den Nutzer darum, die entsprechende Berechtigung in der Alexa-App zu setzen.
   *
   * @param permission Die zu setzende Berechtigung.
   * @return Die Ausgabe der Alexa-App mit der Bitte um Setzen der notwendigen Berechtigungen.
   */
  public static Optional<Response> request(String permission) {
    return new ResponseBuilder()
        .withSpeech(ResponseMessage.MISSING_PERMISSION)
        .withAskForPermissionsConsentCard(Arrays.asList(permission))
        .build();
  }

  /**
   * Prüft, ob der Nutzer mindestens eine Berechtigungen erteilt hat.
   *
   * @param handlerInput Der HandlerInput.
   * @return True, wenn mindestens eine Berechtigung erteilt wurde. False, ansonsten.
   */
  public static boolean hasPermission(HandlerInput handlerInput) {
    final Permissions permissions = handlerInput
        .getRequestEnvelope()
        .getContext()
        .getSystem()
        .getUser()
        .getPermissions();

    return null != permissions && null != permissions.getConsentToken();
  }
}

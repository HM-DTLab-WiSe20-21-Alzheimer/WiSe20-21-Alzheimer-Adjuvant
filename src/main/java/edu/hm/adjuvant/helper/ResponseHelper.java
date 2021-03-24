package edu.hm.adjuvant.helper;

import com.amazon.ask.model.Response;
import com.amazon.ask.response.ResponseBuilder;
import java.util.Optional;

/**
 * ResponseHelper.java
 *
 * @author Stefan Kühnel, stefan.kuehnel@hm.edu
 * @version 2021-1-11
 */

public class ResponseHelper {
  // Privater Konstruktor
  private ResponseHelper() {}

  /**
   * Gibt eine Nachricht mit Hilfe von Alexa aus.
   *
   * @param message Eine zu sprechende Nachricht.
   * @return Die Rückgabe von Alexa, die die zu sprechende Nachricht enthält.
   */
  public static Optional<Response> say(String message) {
    return new ResponseBuilder()
        .withSpeech(message)
        .build();
  }
}

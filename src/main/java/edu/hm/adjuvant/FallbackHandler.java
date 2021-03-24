package edu.hm.adjuvant;

import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.amazon.ask.dispatcher.request.handler.RequestHandler;
import com.amazon.ask.model.Response;
import com.amazon.ask.request.Predicates;
import java.util.Optional;

/**
 * Hilfshandler wenn der Skill den User nicht verstanden hat.
 */
public class FallbackHandler implements RequestHandler {

  public boolean canHandle(HandlerInput handlerInput) {
    return handlerInput.matches(Predicates.intentName("AMAZON.FallbackIntent"));
  }

  /**
   * Fallback Response when Adjuvant could not understand the User.
   *
   * @param handlerInput Handler Input.
   * @return Optional Response.
   */
  public Optional<Response> handle(HandlerInput handlerInput) {
    return handlerInput.getResponseBuilder()
        .withSpeech(
            "Adjuvant hat dich leider nicht verstanden. Bitte wiederhole deine letzte Aussage. "
                + "Für konkretere Hilfestellung sagen Sie Alexa öffne mein Adjuvant.")
        .build();
  }
}

package edu.hm.adjuvant;

import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.amazon.ask.dispatcher.request.handler.RequestHandler;
import com.amazon.ask.model.Response;
import com.amazon.ask.request.Predicates;
import java.util.Optional;

/**
 * Verlassen des Skills.
 */
public class ExitRequestHandler implements RequestHandler {
  public boolean canHandle(HandlerInput input) {
    return input.matches(Predicates.intentName("AMAZON.StopIntent"));
  }

  public Optional<Response> handle(HandlerInput input) {
    return input.getResponseBuilder().withShouldEndSession(true).build();
  }
}
package edu.hm.adjuvant.helper;

import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.amazon.ask.model.IntentRequest;
import com.amazon.ask.model.Slot;
import java.util.Map;

/**
 * SlotsHelper.java
 *
 * @author Stefan Kühnel, stefan.kuehnel@hm.edu
 * @version 2021-1-11
 */

public class SlotsHelper {
  // Privater Konstruktor
  private SlotsHelper() {}

  /**
   * Gibt die Slots, die sich im HandlerInput befinden zurück.
   *
   * @param handlerInput Das HandlerInput.
   * @return Alle Slots im HandlerInput mit den entsprechenden Werten.
   */
  public static Map<String, Slot> getSlots(HandlerInput handlerInput) {
    final IntentRequest intentRequest =
        (IntentRequest) handlerInput.getRequestEnvelope().getRequest();

    return intentRequest.getIntent().getSlots();
  }
}

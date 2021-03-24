package edu.hm.adjuvant.helper;

import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.amazon.ask.model.Slot;
import java.util.Map;

/**
 * TransportHelper.java
 *
 * @author Stefan Kühnel, stefan.kuehnel@hm.edu
 * @version 2021-1-11
 */

public class TransportHelper {
  // Privater Konstruktor.
  private TransportHelper() {}

  /**
   * Konvertiert ein Transportmittel in ein API konformes Transportmittel.
   *
   * @param handlerInput Der HandlerInput.
   * @return Der Code des Transportmittels für die GeoCode Routing API.
   */
  public static String getTransport(HandlerInput handlerInput) {
    final Map<String, Slot> slots = SlotsHelper.getSlots(handlerInput);

    final String transport = slots.get("mode").getValue();

    switch (transport.toLowerCase()) {
      case "fußweg":
        return "walk";

      case "fahrrad":
        return "bicycle";

      case "öffentlicher nahverkehr":
        return "transit";

      default:
        return "drive";
    }
  }
}

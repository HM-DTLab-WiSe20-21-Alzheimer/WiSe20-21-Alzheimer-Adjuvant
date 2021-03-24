package edu.hm.adjuvant.helper;

import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.amazon.ask.model.Slot;
import java.util.Map;

/**
 * AddressHelper.java
 *
 * @author Stefan Kühnel, stefan.kuehnel@hm.edu
 * @version 2021-1-11
 */

public class AddressHelper {
  // Privater Konstruktor
  private AddressHelper() {}

  /**
   * Gibt die Werte der Slots zum Startort als String aus.
   *
   * @param handlerInput Das HandlerInput.
   * @return String mit allen Werten der Slots zum Startort.
   */
  public static String getOrigin(HandlerInput handlerInput) {
    final Map<String, Slot> slots = SlotsHelper.getSlots(handlerInput);

    final String street = slots.get("originAddress").getValue();
    final String houseNumber = slots.get("originAddressHouseNumber").getValue();
    final String city = slots.get("originAddressCity").getValue();

    // Die Adresse des Startortes.
    return String.join(" ", street, houseNumber, city).replace(" null", "");
  }

  /**
   * Gibt die Werte der Slots zum Zielort als String aus.
   *
   * @param handlerInput Das HandlerInput.
   * @return String mit allen Werten der Slots zum Zielort.
   */
  public static String getDestination(HandlerInput handlerInput) {
    final Map<String, Slot> slots = SlotsHelper.getSlots(handlerInput);

    final String street = slots.get("destinationAddress").getValue();
    final String houseNumber = slots.get("destinationAddressHouseNumber").getValue();
    final String city = slots.get("destinationAddressCity").getValue();

    // Die Adresse des Zielortes.
    return String.join(" ", street, houseNumber, city).replace(" null", "");
  }
}

package edu.hm.adjuvant.helper;

import static org.junit.Assert.assertEquals;


import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.amazon.ask.model.Intent;
import com.amazon.ask.model.IntentRequest;
import com.amazon.ask.model.RequestEnvelope;
import com.amazon.ask.model.Slot;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;

/**
 * TransportHelperTest.java
 * @author Stefan Kühnel, stefan.kuehnel@hm.edu
 * @version 2021-1-11
 */

class TransportHelperTest {
  @Test
  void testGetTransport_VerifyFunctionality_ConvertFootpathToWalk() {
    // Arrange
    final Map<String, Slot> slots = new HashMap<>();
    slots.put("mode", Slot.builder().withValue("Fußweg").build());

    final Intent intent = Intent.builder().withSlots(slots).build();
    final IntentRequest intentRequest = IntentRequest.builder().withIntent(intent).build();
    final RequestEnvelope requestEnvelope = RequestEnvelope.builder().withRequest(intentRequest).build();
    final HandlerInput handlerInput = HandlerInput.builder().withRequestEnvelope(requestEnvelope).build();

    // Act
    final String transport = TransportHelper.getTransport(handlerInput);

    // Assert
    assertEquals("walk", transport);
  }

  @Test
  void testGetTransport_VerifyFunctionality_ConvertPublicTransportToTransit() {
    // Arrange
    final Map<String, Slot> slots = new HashMap<>();
    slots.put("mode", Slot.builder().withValue("öffentlicher Nahverkehr").build());

    final Intent intent = Intent.builder().withSlots(slots).build();
    final IntentRequest intentRequest = IntentRequest.builder().withIntent(intent).build();
    final RequestEnvelope requestEnvelope = RequestEnvelope.builder().withRequest(intentRequest).build();
    final HandlerInput handlerInput = HandlerInput.builder().withRequestEnvelope(requestEnvelope).build();

    // Act
    final String transport = TransportHelper.getTransport(handlerInput);

    // Assert
    assertEquals("transit", transport);
  }

  @Test
  void testGetTransport_VerifyFunctionality_ConvertCarToDrive() {
    // Arrange
    final Map<String, Slot> slots = new HashMap<>();
    slots.put("mode", Slot.builder().withValue("Auto").build());

    final Intent intent = Intent.builder().withSlots(slots).build();
    final IntentRequest intentRequest = IntentRequest.builder().withIntent(intent).build();
    final RequestEnvelope requestEnvelope = RequestEnvelope.builder().withRequest(intentRequest).build();
    final HandlerInput handlerInput = HandlerInput.builder().withRequestEnvelope(requestEnvelope).build();

    // Act
    final String transport = TransportHelper.getTransport(handlerInput);

    // Assert
    assertEquals("drive", transport);
  }

  @Test
  void testGetTransport_VerifyFunctionality_ConvertBikeToBicycle() {
    // Arrange
    final Map<String, Slot> slots = new HashMap<>();
    slots.put("mode", Slot.builder().withValue("Fahrrad").build());

    final Intent intent = Intent.builder().withSlots(slots).build();
    final IntentRequest intentRequest = IntentRequest.builder().withIntent(intent).build();
    final RequestEnvelope requestEnvelope = RequestEnvelope.builder().withRequest(intentRequest).build();
    final HandlerInput handlerInput = HandlerInput.builder().withRequestEnvelope(requestEnvelope).build();

    // Act
    final String transport = TransportHelper.getTransport(handlerInput);

    // Assert
    assertEquals("bicycle", transport);
  }
}

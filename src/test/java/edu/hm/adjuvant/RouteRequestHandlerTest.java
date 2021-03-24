package edu.hm.adjuvant;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.amazon.ask.dispatcher.request.handler.RequestHandler;
import com.amazon.ask.model.Context;
import com.amazon.ask.model.Intent;
import com.amazon.ask.model.IntentRequest;
import com.amazon.ask.model.Permissions;
import com.amazon.ask.model.RequestEnvelope;
import com.amazon.ask.model.Response;
import com.amazon.ask.model.Slot;
import com.amazon.ask.model.User;
import com.amazon.ask.model.interfaces.system.SystemState;
import com.amazon.ask.model.services.ServiceClientFactory;
import com.amazon.ask.model.services.ups.UpsServiceClient;

import edu.hm.adjuvant.utils.ResponseMessage;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.Test;

/**
 * RouteRequestHandlerTest.java
 * @author Stefan Kühnel, stefan.kuehnel@hm.edu
 * @version 2021-1-11
 */

class RouteRequestHandlerTest {
  private RequestHandler getSUT() {
    return new RouteRequestHandler();
  }

  private HandlerInput handlerInput() {
    return handlerInput("RouteRequestIntent");
  }

  private HandlerInput handlerInput(String intentName) {
    // Erstellung des Mocks für den Intent.
    final Intent intent = mock(Intent.class);

    // Der RouteRequestIntent soll genutzt werden.
    when(intent.getName()).thenReturn(intentName);

    // Initialisierung weiterer notwendiger Objekte auf Basis des RouteRequestHandlers.
    final IntentRequest intentRequest = IntentRequest.builder().withIntent(intent).build();
    final RequestEnvelope requestEnvelope = RequestEnvelope.builder().withRequest(intentRequest).build();

    return HandlerInput.builder().withRequestEnvelope(requestEnvelope).build();
  }

  private Map<String, Slot> getSlots() {
    final Map<String, Slot> slots = new HashMap<>();

    slots.put("originAddress", Slot.builder().withValue("Lothstraße").build());
    slots.put("originAddressHouseNumber", Slot.builder().withValue("34").build());
    slots.put("originAddressCity", Slot.builder().withValue("München").build());

    slots.put("destinationAddress", Slot.builder().withValue("Lindwurmstraße").build());
    slots.put("destinationAddressHouseNumber", Slot.builder().withValue("90").build());
    slots.put("destinationAddressCity", Slot.builder().withValue("München").build());

    slots.put("mode", Slot.builder().withValue("öffentlicher Nahverkehr").build());

    return slots;
  }

  private Map<String, Slot> getInvalidSlots() {
    final Map<String, Slot> slots = new HashMap<>();

    slots.put("originAddress", Slot.builder().withValue("Lothstraße").build());
    slots.put("originAddressHouseNumber", Slot.builder().withValue("34").build());
    slots.put("originAddressCity", Slot.builder().withValue("München").build());

    slots.put("destinationAddress", Slot.builder().withValue("Martin-Luther-Straße").build());
    slots.put("destinationAddressHouseNumber", Slot.builder().withValue("1").build());
    slots.put("destinationAddressCity", Slot.builder().withValue("Rehau").build());

    slots.put("mode", Slot.builder().withValue("öffentlicher Nahverkehr").build());

    return slots;
  }

  @Test
  void testCanHandle_canHandleCorrectlyNamedIntent() {
    // Arrange
    final RequestHandler sut = getSUT();
    final HandlerInput handlerInput = handlerInput();

    // Act
    final boolean canHandle = sut.canHandle(handlerInput);

    // Assert
    assertTrue(canHandle);
  }

  @Test
  void testCanHandle_CanNotHandleIncorrectlyNamedIntent() {
    // Arrange
    final RequestHandler sut = getSUT();
    final HandlerInput handlerInput = handlerInput("InvalidRequestIntent");

    // Act
    final boolean canHandle = sut.canHandle(handlerInput);

    // Assert
    assertFalse(canHandle);
  }

  @Test
  void testHandle_AskForPermissionWhenNoPermissionsAreSet() {
    // Arrange
    final RequestHandler routeRequestHandler = new RouteRequestHandler();
    final Intent intent = Intent.builder().withName("RouteRequestIntent").build();
    final IntentRequest intentRequest = IntentRequest.builder().withIntent(intent).build();
    final Permissions permissions = Permissions.builder().build();
    final User user = User.builder().withPermissions(permissions).build();
    final SystemState systemState = SystemState.builder().withUser(user).build();
    final Context context = Context.builder().withSystem(systemState).build();
    final RequestEnvelope requestEnvelope = RequestEnvelope.builder().withContext(context).withRequest(intentRequest).build();
    final HandlerInput handlerInput = HandlerInput.builder().withRequestEnvelope(requestEnvelope).build();

    // Act
    final Optional<Response> response = routeRequestHandler.handle(handlerInput);

    // Assert
    assertTrue(response.isPresent());

    final String have = response.get().getOutputSpeech().toString();
    assertTrue(have.contains(ResponseMessage.MISSING_PERMISSION));
  }

  @Test
  void testHandle_EmptyProfileEmail() {
    // Arrange
    final RequestHandler routeRequestHandler = new RouteRequestHandler();
    final Map<String, Slot> slots = getSlots();
    final Permissions permissions = Permissions.builder().withConsentToken("token").build();

    final Intent intent = mock(Intent.class);
    final User user = mock(User.class);
    final Context context = mock(Context.class);
    final ServiceClientFactory serviceClientFactory = mock(ServiceClientFactory.class);
    final UpsServiceClient upsServiceClient = mock(UpsServiceClient.class);

    when(intent.getSlots()).thenReturn(slots);
    when(intent.getName()).thenReturn("RouteRequestIntent");
    when(user.getPermissions()).thenReturn(permissions);
    when(context.getSystem()).thenReturn(SystemState.builder().withUser(user).build());
    when(upsServiceClient.getProfileEmail()).thenReturn("");
    when(serviceClientFactory.getUpsService()).thenReturn(upsServiceClient);

    final IntentRequest intentRequest = IntentRequest.builder().withIntent(intent).build();
    final RequestEnvelope requestEnvelope = RequestEnvelope.builder().withRequest(intentRequest).withContext(context).build();
    final HandlerInput handlerInput = HandlerInput.builder().withRequestEnvelope(requestEnvelope).withServiceClientFactory(serviceClientFactory).build();

    // Act
    final Optional<Response> response = routeRequestHandler.handle(handlerInput);

    // Assert
    assertTrue(response.isPresent());

    final String have = response.get().getOutputSpeech().toString();
    assertTrue(have.contains(ResponseMessage.MISSING_PERMISSION));
  }

  @Test
  void testHandle_NoProfileEmail() {
    // Arrange
    final RequestHandler routeRequestHandler = new RouteRequestHandler();
    final Map<String, Slot> slots = getSlots();
    final Permissions permissions = Permissions.builder().withConsentToken("token").build();

    final Intent intent = mock(Intent.class);
    final User user = mock(User.class);
    final Context context = mock(Context.class);
    final ServiceClientFactory serviceClientFactory = mock(ServiceClientFactory.class);
    final UpsServiceClient upsServiceClient = mock(UpsServiceClient.class);

    when(intent.getSlots()).thenReturn(slots);
    when(intent.getName()).thenReturn("RouteRequestIntent");
    when(user.getPermissions()).thenReturn(permissions);
    when(context.getSystem()).thenReturn(SystemState.builder().withUser(user).build());
    when(upsServiceClient.getProfileEmail()).thenReturn(null);
    when(serviceClientFactory.getUpsService()).thenReturn(upsServiceClient);

    final IntentRequest intentRequest = IntentRequest.builder().withIntent(intent).build();
    final RequestEnvelope requestEnvelope = RequestEnvelope.builder().withRequest(intentRequest).withContext(context).build();
    final HandlerInput handlerInput = HandlerInput.builder().withRequestEnvelope(requestEnvelope).withServiceClientFactory(serviceClientFactory).build();

    // Act
    final Optional<Response> response = routeRequestHandler.handle(handlerInput);

    // Assert
    assertTrue(response.isPresent());

    final String have = response.get().getOutputSpeech().toString();
    assertTrue(have.contains(ResponseMessage.MISSING_PERMISSION));
  }

  @Test
  void testHandle_HasProfileEmail_RouteCouldNotBeFound() {
    // Arrange
    final RequestHandler routeRequestHandler = new RouteRequestHandler();
    final Map<String, Slot> slots = getInvalidSlots();  // Keine Route verfügbar.
    final Permissions permissions = Permissions.builder().withConsentToken("token").build();

    final Intent intent = mock(Intent.class);
    final User user = mock(User.class);
    final Context context = mock(Context.class);
    final ServiceClientFactory serviceClientFactory = mock(ServiceClientFactory.class);
    final UpsServiceClient upsServiceClient = mock(UpsServiceClient.class);

    when(intent.getSlots()).thenReturn(slots);
    when(intent.getName()).thenReturn("RouteRequestIntent");
    when(user.getPermissions()).thenReturn(permissions);
    when(context.getSystem()).thenReturn(SystemState.builder().withUser(user).build());
    when(upsServiceClient.getProfileEmail()).thenReturn("stefan.kuehnel@hm.edu");
    when(serviceClientFactory.getUpsService()).thenReturn(upsServiceClient);

    final IntentRequest intentRequest = IntentRequest.builder().withIntent(intent).build();
    final RequestEnvelope requestEnvelope = RequestEnvelope.builder().withRequest(intentRequest).withContext(context).build();
    final HandlerInput handlerInput = HandlerInput.builder().withRequestEnvelope(requestEnvelope).withServiceClientFactory(serviceClientFactory).build();

    // Act
    final Optional<Response> response = routeRequestHandler.handle(handlerInput);

    // Assert
    assertTrue(response.isPresent());

    final String have = response.get().getOutputSpeech().toString();
    assertTrue(have.contains(ResponseMessage.ROUTE_CALCULATION_FAILED));
  }

  @Test
  void testHandle_HasProfileEmail_RouteCouldBeFound() {
    // Arrange
    final RequestHandler routeRequestHandler = new RouteRequestHandler();
    final Map<String, Slot> slots = getSlots();  // Route verfügbar.
    final Permissions permissions = Permissions.builder().withConsentToken("token").build();

    final Intent intent = mock(Intent.class);
    final User user = mock(User.class);
    final Context context = mock(Context.class);
    final ServiceClientFactory serviceClientFactory = mock(ServiceClientFactory.class);
    final UpsServiceClient upsServiceClient = mock(UpsServiceClient.class);

    when(intent.getSlots()).thenReturn(slots);
    when(intent.getName()).thenReturn("RouteRequestIntent");
    when(user.getPermissions()).thenReturn(permissions);
    when(context.getSystem()).thenReturn(SystemState.builder().withUser(user).build());
    when(upsServiceClient.getProfileEmail()).thenReturn("adjuvant-email-design-test@byom.de");
    when(serviceClientFactory.getUpsService()).thenReturn(upsServiceClient);

    final IntentRequest intentRequest = IntentRequest.builder().withIntent(intent).build();
    final RequestEnvelope requestEnvelope = RequestEnvelope.builder().withRequest(intentRequest).withContext(context).build();
    final HandlerInput handlerInput = HandlerInput.builder().withRequestEnvelope(requestEnvelope).withServiceClientFactory(serviceClientFactory).build();

    // Act
    final Optional<Response> response = routeRequestHandler.handle(handlerInput);

    // Assert
    assertTrue(response.isPresent());

    final String have = response.get().getOutputSpeech().toString();
    assertTrue(have.contains(ResponseMessage.ROUTE_SEND_SUCCESSFULLY));

    // Hier wird es ermöglicht, die E-Mail über ein Webinterface abzurufen.
    // So kann überprüft werden, ob die E-Mail den konkreten Wünschen entspricht, ohne einen entsprechenden Testlauf mit der eigenen Mail zu starten.
    System.out.println("Eine Beispiel E-Mail zur Überprüfung der korrekten Darstellung ist an adjuvant-email-design-test@byom.de versendet worden.");
    System.out.println("Das entsprechende Webinterface können Sie unter https://byom.de abrufen.");
  }
}

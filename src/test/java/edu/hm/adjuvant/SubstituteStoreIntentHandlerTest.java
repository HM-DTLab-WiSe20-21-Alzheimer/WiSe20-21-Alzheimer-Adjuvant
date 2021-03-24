package edu.hm.adjuvant;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.amazon.ask.model.Context;
import com.amazon.ask.model.Intent;
import com.amazon.ask.model.IntentRequest;
import com.amazon.ask.model.Permissions;
import com.amazon.ask.model.RequestEnvelope;
import com.amazon.ask.model.Response;
import com.amazon.ask.model.Slot;
import com.amazon.ask.model.User;
import com.amazon.ask.model.interfaces.system.SystemState;
import com.amazon.ask.model.services.DefaultApiConfiguration;
import com.amazon.ask.model.services.ServiceClientFactory;
import com.amazon.ask.model.services.reminderManagement.ReminderManagementServiceClient;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.Test;


/**
 * Test für SubstituteStoreIntentHandler
 *
 * @author Anonymous Student
 */
class SubstituteStoreIntentHandlerTest {
  final private String intentName = "StoreFrequent";


  private Map<String, Slot> getOneTimeSlots() {
    Slot remindSlot = Slot.builder().withValue("Abendessen").build();
    Slot timeSlot = Slot.builder().withValue("18:00").build();
    Slot freqSlot = Slot.builder().withValue("einmalig").build();
    Slot dateSlot = Slot.builder().withValue("2021-06-07").build();
    Slot weekDaySlot = Slot.builder().withValue("weiter").build();
    Map<String, Slot> slots = new HashMap<>();
    slots.put("remind", remindSlot);
    slots.put("time", timeSlot);
    slots.put("frequency", freqSlot);
    slots.put("date", dateSlot);
    slots.put("weekDay", weekDaySlot);
    return slots;
  }

  private Map<String, Slot> getDailySlots() {
    Slot remindSlot = Slot.builder().withValue("Abendessen").build();
    Slot timeSlot = Slot.builder().withValue("18:00").build();
    Slot freqSlot = Slot.builder().withValue("täglich").build();
    Slot dateSlot = Slot.builder().withValue("12.12.2099").build();
    Slot weekDaySlot = Slot.builder().withValue("weiter").build();
    Map<String, Slot> slots = new HashMap<>();
    slots.put("remind", remindSlot);
    slots.put("time", timeSlot);
    slots.put("frequency", freqSlot);
    slots.put("date", dateSlot);
    slots.put("weekDay", weekDaySlot);
    return slots;
  }

  private Map<String, Slot> getWeeklySlots() {
    Slot remindSlot = Slot.builder().withValue("Abendessen").build();
    Slot timeSlot = Slot.builder().withValue("18:00").build();
    Slot freqSlot = Slot.builder().withValue("wöchentlich").build();
    Slot dateSlot = Slot.builder().withValue("12.12.2099").build();
    Slot weekDaySlot = Slot.builder().withValue("Freitag").build();
    Map<String, Slot> slots = new HashMap<>();
    slots.put("remind", remindSlot);
    slots.put("time", timeSlot);
    slots.put("frequency", freqSlot);
    slots.put("date", dateSlot);
    slots.put("weekDay", weekDaySlot);
    return slots;
  }

  @Test
  void testCI() {
    final String want = "CI Test funktioniert.";
    final String have = "CI Test funktioniert.";
    assertEquals(want, have);
  }

  @Test
  void testCanHandleStoreRequest() {
    var handler = new SubstituteStoreIntentHandler();
    Intent intent = mock(Intent.class);
    when(intent.getName()).thenReturn(intentName);
    var intentRequest = IntentRequest.builder().withIntent(intent).build();
    var requestEnvelope = RequestEnvelope.builder().withRequest(intentRequest).build();
    var handlerInput1 = HandlerInput.builder().withRequestEnvelope(requestEnvelope).build();
    assertTrue(handler.canHandle(handlerInput1));
    verify(intent, times(1)).getName();
  }

  @Test
  void testCanNtHandleRandomRequest() {
    var handler = new SubstituteStoreIntentHandler();
    Intent intent = mock(Intent.class);
    when(intent.getName()).thenReturn("blubb");
    var intentRequest = IntentRequest.builder().withIntent(intent).build();
    var requestEnvelope = RequestEnvelope.builder().withRequest(intentRequest).build();
    var handlerInput1 = HandlerInput.builder().withRequestEnvelope(requestEnvelope).build();
    assertFalse(handler.canHandle(handlerInput1));
  }

  @Test
  void testMissingPermission() {
    var handler = new SubstituteStoreIntentHandler();
    String problemPermission =
        "Adjuvant braucht eine Erlaubnis um Ihre Termine verwalten zu können. "
            + " Bitte bestätigen Sie die Anfrage in Ihrer Alexa App "
            + "und versuchen Sie nochmal.";
    Map<String, Slot> slots = getDailySlots();
    Intent intent = mock(Intent.class);
    when(intent.getName()).thenReturn(intentName);
    when(intent.getSlots()).thenReturn(slots);
    User user = mock(User.class);
    when(user.getPermissions()).thenReturn(
        Permissions.builder().withConsentToken("alexa::alerts:reminders:skill:readwrite").build());
    Context context = mock(Context.class);
    when(context.getSystem()).thenReturn(SystemState.builder().withUser(user).build());
    ReminderManagementServiceClient management =
        new ReminderManagementServiceClient(DefaultApiConfiguration.builder()
            .build());
    ServiceClientFactory factory = mock(ServiceClientFactory.class);
    when(factory.getReminderManagementService()).thenReturn(management);
    var intentRequest = IntentRequest.builder().withIntent(intent).build();
    var requestEnvelope =
        RequestEnvelope.builder().withRequest(intentRequest).withContext(context).build();
    var handlerInput1 =
        HandlerInput.builder().withRequestEnvelope(requestEnvelope).withServiceClientFactory(
            factory).build();
    assertTrue(handler.canHandle(handlerInput1));
    Optional<Response> response = handler.handle(handlerInput1);
    assertTrue(response.isPresent());
    assertTrue(response.get().toString().contains(problemPermission));
    verify(intent, times(1)).getName();
  }

  @Test
  void firstMissingPermission() {
    var handler = new SubstituteStoreIntentHandler();
    String problemPermission =
        "Adjuvant braucht eine Erlaubnis um Ihre Termine verwalten zu können."
            + "Bitte bestätigen Sie die Anfrage in Ihrer Alexa App.";
    Map<String, Slot> slots = getDailySlots();
    Intent intent = mock(Intent.class);
    when(intent.getName()).thenReturn(intentName);
    when(intent.getSlots()).thenReturn(slots);
    User user = mock(User.class);

    Context context = mock(Context.class);
    when(context.getSystem()).thenReturn(SystemState.builder().withUser(user).build());
    ReminderManagementServiceClient management =
        new ReminderManagementServiceClient(DefaultApiConfiguration.builder()
            .build());
    ServiceClientFactory factory = mock(ServiceClientFactory.class);
    when(factory.getReminderManagementService()).thenReturn(management);
    var intentRequest = IntentRequest.builder().withIntent(intent).build();
    var requestEnvelope =
        RequestEnvelope.builder().withRequest(intentRequest).withContext(context).build();
    var handlerInput1 =
        HandlerInput.builder().withRequestEnvelope(requestEnvelope).withServiceClientFactory(
            factory).build();
    assertTrue(handler.canHandle(handlerInput1));
    Optional<Response> response = handler.handle(handlerInput1);
    assertTrue(response.isPresent());
    assertTrue(response.get().toString().contains(problemPermission));
    verify(intent, times(1)).getName();
  }

  @Test
  void noArgument() {
    var handler = new SubstituteStoreIntentHandler();
    String problemInput = "Ich konnte mir leider nichts merken. Bitte einen Termin angeben.";
    Map<String, Slot> slots = getDailySlots();
    slots.clear();
    Intent intent = mock(Intent.class);
    when(intent.getName()).thenReturn(intentName);
    when(intent.getSlots()).thenReturn(slots);
    User user = mock(User.class);
    when(user.getPermissions()).thenReturn(
        Permissions.builder().withConsentToken("alexa::alerts:reminders:skill:readwrite").build());
    Context context = mock(Context.class);
    when(context.getSystem()).thenReturn(SystemState.builder().withUser(user).build());
    ReminderManagementServiceClient management =
        new ReminderManagementServiceClient(DefaultApiConfiguration.builder()
            .build());
    ServiceClientFactory factory = mock(ServiceClientFactory.class);
    when(factory.getReminderManagementService()).thenReturn(management);
    var intentRequest = IntentRequest.builder().withIntent(intent).build();
    var requestEnvelope =
        RequestEnvelope.builder().withRequest(intentRequest).withContext(context).build();
    var handlerInput1 =
        HandlerInput.builder().withRequestEnvelope(requestEnvelope).withServiceClientFactory(
            factory).build();
    assertTrue(handler.canHandle(handlerInput1));
    Optional<Response> response = handler.handle(handlerInput1);
    assertTrue(response.isPresent());
    assertTrue(response.get().toString().contains(problemInput));
    verify(intent, times(1)).getName();
  }

  @Test
  void noName() {
    var handler = new SubstituteStoreIntentHandler();
    String problemInput = "Ich konnte mir leider nichts merken. Bitte einen Termin angeben.";
    Map<String, Slot> slots = getDailySlots();
    slots.remove("remind");
    Intent intent = mock(Intent.class);
    when(intent.getName()).thenReturn(intentName);
    when(intent.getSlots()).thenReturn(slots);
    User user = mock(User.class);
    when(user.getPermissions()).thenReturn(
        Permissions.builder().withConsentToken("alexa::alerts:reminders:skill:readwrite").build());
    Context context = mock(Context.class);
    when(context.getSystem()).thenReturn(SystemState.builder().withUser(user).build());
    ReminderManagementServiceClient management =
        new ReminderManagementServiceClient(DefaultApiConfiguration.builder()
            .build());
    ServiceClientFactory factory = mock(ServiceClientFactory.class);
    when(factory.getReminderManagementService()).thenReturn(management);
    var intentRequest = IntentRequest.builder().withIntent(intent).build();
    var requestEnvelope =
        RequestEnvelope.builder().withRequest(intentRequest).withContext(context).build();
    var handlerInput1 =
        HandlerInput.builder().withRequestEnvelope(requestEnvelope).withServiceClientFactory(
            factory).build();
    assertTrue(handler.canHandle(handlerInput1));
    Optional<Response> response = handler.handle(handlerInput1);
    assertTrue(response.isPresent());
    assertTrue(response.get().toString().contains(problemInput));
    verify(intent, times(1)).getName();
  }

  @Test
  void dailySpeakTest() {
    var handler = new SubstituteStoreIntentHandler();
    Map<String, Slot> slots = getDailySlots();
    String ExpectedResponse =
        "Folgende tägliche Errinerung wurde abgespeichert: " + slots.get("remind").getValue() +
            " um " + slots.get("time").getValue()
            + " Uhr.";
    Intent intent = mock(Intent.class);
    when(intent.getName()).thenReturn(intentName);
    when(intent.getSlots()).thenReturn(slots);
    Permissions perm = Permissions.builder().withConsentToken("bla").build();
    User user = mock(User.class);
    when(user.getPermissions()).thenReturn(perm);
    Context context = mock(Context.class);
    when(context.getSystem()).thenReturn(SystemState.builder().withUser(user).build());
    ReminderManagementServiceClient management = mock(ReminderManagementServiceClient.class);
    ServiceClientFactory factory = mock(ServiceClientFactory.class);
    when(factory.getReminderManagementService()).thenReturn(management);
    var intentRequest = IntentRequest.builder().withIntent(intent).build();
    var requestEnvelope =
        RequestEnvelope.builder().withRequest(intentRequest).withContext(context).build();
    var handlerInput1 =
        HandlerInput.builder().withRequestEnvelope(requestEnvelope).withServiceClientFactory(
            factory).build();
    Optional<Response> response = handler.handle(handlerInput1);
    assertTrue(response.isPresent());
    assertTrue(response.get().toString().contains(ExpectedResponse));
  }

  @Test
  void oneTimeSpeakTest() {
    var handler = new SubstituteStoreIntentHandler();
    Map<String, Slot> slots = getOneTimeSlots();
    String ExpectedResponse =
        "Folgender einmaliger Termin wurde abgespeichert: " + slots.get("remind").getValue() +
            " am " + slots.get("date").getValue()
            + " um " + slots.get("time").getValue() + " Uhr.";
    Intent intent = mock(Intent.class);
    when(intent.getName()).thenReturn(intentName);
    when(intent.getSlots()).thenReturn(slots);
    Permissions perm = Permissions.builder().withConsentToken("bla").build();
    User user = mock(User.class);
    when(user.getPermissions()).thenReturn(perm);
    Context context = mock(Context.class);
    when(context.getSystem()).thenReturn(SystemState.builder().withUser(user).build());
    ReminderManagementServiceClient management = mock(ReminderManagementServiceClient.class);
    ServiceClientFactory factory = mock(ServiceClientFactory.class);
    when(factory.getReminderManagementService()).thenReturn(management);
    var intentRequest = IntentRequest.builder().withIntent(intent).build();
    var requestEnvelope =
        RequestEnvelope.builder().withRequest(intentRequest).withContext(context).build();
    var handlerInput1 =
        HandlerInput.builder().withRequestEnvelope(requestEnvelope).withServiceClientFactory(
            factory).build();
    Optional<Response> response = handler.handle(handlerInput1);
    assertTrue(response.isPresent());
    assertTrue(response.get().toString().contains(ExpectedResponse));
  }

  @Test
  void weeklySpeakTest() {
    var handler = new SubstituteStoreIntentHandler();
    Map<String, Slot> slots = getWeeklySlots();
    String ExpectedResponse =
        "Folgende wöchentliche Errinerung wurde abgespeichert: " + slots.get("remind").getValue() +
            " um " + slots.get("time").getValue()
            + " Uhr jeden " + slots.get("weekDay").getValue();
    Intent intent = mock(Intent.class);
    when(intent.getName()).thenReturn(intentName);
    when(intent.getSlots()).thenReturn(slots);
    Permissions perm = Permissions.builder().withConsentToken("bla").build();
    User user = mock(User.class);
    when(user.getPermissions()).thenReturn(perm);
    Context context = mock(Context.class);
    when(context.getSystem()).thenReturn(SystemState.builder().withUser(user).build());
    ReminderManagementServiceClient management = mock(ReminderManagementServiceClient.class);
    ServiceClientFactory factory = mock(ServiceClientFactory.class);
    when(factory.getReminderManagementService()).thenReturn(management);
    var intentRequest = IntentRequest.builder().withIntent(intent).build();
    var requestEnvelope =
        RequestEnvelope.builder().withRequest(intentRequest).withContext(context).build();
    var handlerInput1 =
        HandlerInput.builder().withRequestEnvelope(requestEnvelope).withServiceClientFactory(
            factory).build();
    Optional<Response> response = handler.handle(handlerInput1);
    assertTrue(response.isPresent());
    assertTrue(response.get().toString().contains(ExpectedResponse));
  }

  @Test
  void wrongSpeakTest() {
    var handler = new SubstituteStoreIntentHandler();
    Map<String, Slot> slots = getWeeklySlots();
    slots.replace("frequency", Slot.builder().withValue("").build());
    Intent intent = mock(Intent.class);
    when(intent.getName()).thenReturn(intentName);
    when(intent.getSlots()).thenReturn(slots);
    Permissions perm = Permissions.builder().withConsentToken("bla").build();
    User user = mock(User.class);
    when(user.getPermissions()).thenReturn(perm);
    Context context = mock(Context.class);
    when(context.getSystem()).thenReturn(SystemState.builder().withUser(user).build());
    ReminderManagementServiceClient management = mock(ReminderManagementServiceClient.class);
    ServiceClientFactory factory = mock(ServiceClientFactory.class);
    when(factory.getReminderManagementService()).thenReturn(management);
    var intentRequest = IntentRequest.builder().withIntent(intent).build();
    var requestEnvelope =
        RequestEnvelope.builder().withRequest(intentRequest).withContext(context).build();
    var handlerInput1 =
        HandlerInput.builder().withRequestEnvelope(requestEnvelope).withServiceClientFactory(
            factory).build();
    Optional<Response> response = handler.handle(handlerInput1);
    assertTrue(response.isPresent());
    assertTrue(response.get().toString().contains("Fehler in der Frequenz"));
  }

}

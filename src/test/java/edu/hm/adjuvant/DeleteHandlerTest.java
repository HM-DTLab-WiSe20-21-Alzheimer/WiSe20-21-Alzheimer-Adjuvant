package edu.hm.adjuvant;

import static org.junit.jupiter.api.Assertions.*;
import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.amazon.ask.model.Context;
import com.amazon.ask.model.Permissions;
import com.amazon.ask.model.RequestEnvelope;
import com.amazon.ask.model.Response;
import com.amazon.ask.model.Slot;
import com.amazon.ask.model.User;
import com.amazon.ask.model.interfaces.system.SystemState;
import com.amazon.ask.model.services.ServiceClientFactory;
import com.amazon.ask.model.services.reminderManagement.AlertInfo;
import com.amazon.ask.model.services.reminderManagement.AlertInfoSpokenInfo;
import com.amazon.ask.model.services.reminderManagement.GetRemindersResponse;
import com.amazon.ask.model.services.reminderManagement.Recurrence;
import com.amazon.ask.model.services.reminderManagement.RecurrenceFreq;
import com.amazon.ask.model.services.reminderManagement.Reminder;
import com.amazon.ask.model.services.reminderManagement.ReminderManagementServiceClient;
import com.amazon.ask.model.services.reminderManagement.SpokenText;
import com.amazon.ask.model.services.reminderManagement.Trigger;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.*;
import com.amazon.ask.model.Intent;
import com.amazon.ask.model.IntentRequest;
import org.mockito.Mockito;

/**
 * Testklasse für DeleteHandler.
 *
 * @author Anonymous Student
 */

class DeleteHandlerTest {

  final String toDelete ="Spazieren";
  final String time="15:00";


  private Map<String,Slot> getTheSlots(){
    Slot storeSlot = Slot.builder().withValue(toDelete).build();
    Slot timeSlot = Slot.builder().withValue(time).build();
    Map<String,Slot> slots = new HashMap<>();
    slots.put("toDelete",storeSlot);
    slots.put("time",timeSlot);
    return slots;
  }

  @Test
  void testCanHandleDelete() {
    var handler = new DeleteHandler();
    Intent intent = mock(Intent.class);
    when(intent.getName()).thenReturn("DeleteIntent");
    var intentRequest = IntentRequest.builder().withIntent(intent).build();
    var requestEnvelope = RequestEnvelope.builder().withRequest(intentRequest).build();
    var handlerInput1 = HandlerInput.builder().withRequestEnvelope(requestEnvelope).build();
    assertTrue(handler.canHandle(handlerInput1));
    verify(intent, times(1)).getName();
  }

  @Test
  void testCannotHandleDelete() {
    var handler = new DeleteHandler();
    var intent = Intent.builder().withName("GetAlllIntent").build();
    var intentRequest = IntentRequest.builder().withIntent(intent).build();
    var requestEnvelope = RequestEnvelope.builder().withRequest(intentRequest).build();
    var handlerInput = HandlerInput.builder().withRequestEnvelope(requestEnvelope).build();
    assertFalse(handler.canHandle(handlerInput));
  }

  @Test
  void permission() {
    var handler = new DeleteHandler();
    String problemPermission="Adjuvant braucht eine Erlaubnis um Ihre Termine verwalten zu können. "
        + " Bitte bestätigen Sie die Anfrage in Ihrer Alexa App.";
    Map<String, Slot> slots= getTheSlots();
    Intent intent = mock(Intent.class);
    when(intent.getName()).thenReturn("DeleteIntent");
    when(intent.getSlots()).thenReturn(slots);
    Permissions perm= Permissions.builder().build();
    User user=mock(User.class);
    when(user.getPermissions()).thenReturn(perm);
    Context context=mock(Context.class);
    when(context.getSystem()).thenReturn(SystemState.builder().withUser(user).build());
    ReminderManagementServiceClient management=mock(ReminderManagementServiceClient.class);
    ServiceClientFactory factory =mock(ServiceClientFactory.class);
    when(factory.getReminderManagementService()).thenReturn(management);
    var intentRequest = IntentRequest.builder().withIntent(intent).build();
    var requestEnvelope = RequestEnvelope.builder().withRequest(intentRequest).withContext(context).build();
    var handlerInput1 = HandlerInput.builder().withRequestEnvelope(requestEnvelope).withServiceClientFactory(
        factory).build();
    assertTrue(handler.canHandle(handlerInput1));
    Optional<Response> response = handler.handle(handlerInput1);
    assertTrue(response.isPresent());
    assertTrue(response.get().toString().contains(problemPermission));
    verify(intent, times(1)).getName();
  }

  @Test
  void noArgument() {
    var handler = new DeleteHandler();
    String problemInput="Ich konnte leider nichts verstehen. Bitte einen Termin angeben.";
    Map<String,Slot> slots= getTheSlots();
    slots.clear();
    Intent intent = mock(Intent.class);
    when(intent.getName()).thenReturn("DeleteIntent");
    when(intent.getSlots()).thenReturn(slots);
    Permissions perm= Permissions.builder().withConsentToken("bla").build();
    User user=mock(User.class);
    when(user.getPermissions()).thenReturn(perm);
    Context context=mock(Context.class);
    when(context.getSystem()).thenReturn(SystemState.builder().withUser(user).build());
    ReminderManagementServiceClient management=mock(ReminderManagementServiceClient.class);
    ServiceClientFactory factory =mock(ServiceClientFactory.class);
    when(factory.getReminderManagementService()).thenReturn(management);
    var intentRequest = IntentRequest.builder().withIntent(intent).build();
    var requestEnvelope = RequestEnvelope.builder().withRequest(intentRequest).withContext(context).build();
    var handlerInput1 = HandlerInput.builder().withRequestEnvelope(requestEnvelope).withServiceClientFactory(
        factory).build();
    assertTrue(handler.canHandle(handlerInput1));
    Optional<Response> response = handler.handle(handlerInput1);
    assertTrue(response.isPresent());
    assertTrue(response.get().toString().contains(problemInput));
    verify(intent, times(1)).getName();
  }

  @Test
  void notCorrectDelete() {
      var handler = Mockito.spy(new DeleteHandler());
      Intent intent = mock(Intent.class);
      when(intent.getName()).thenReturn("DeleteIntent");
      Map<String, Slot> slots= getTheSlots();
      when(intent.getSlots()).thenReturn(slots);
      Permissions perm= Permissions.builder().withConsentToken("token").build();
      User user=mock(User.class);
      when(user.getPermissions()).thenReturn(perm);
      Context context=mock(Context.class);
      when(context.getSystem()).thenReturn(SystemState.builder().withUser(user).build());
      ReminderManagementServiceClient management=mock(ReminderManagementServiceClient.class);
      var response=mock(GetRemindersResponse.class);
      List<SpokenText> text=new ArrayList<>();
      text.add(SpokenText.builder().withText("Spazieren gehen").build());
      List<Reminder> reminders=new ArrayList<>();
      OffsetDateTime time=LocalDateTime.now().atOffset(ZoneOffset.UTC);
      Reminder reminder=Reminder.builder()
          .withAlertInfo(AlertInfo.builder()
              .withSpokenInfo(AlertInfoSpokenInfo.builder().withContent(text).build()).build())
          .withCreatedTime(time)
          .withTrigger(Trigger.builder()
              .withRecurrence(Recurrence.builder()
                  .withFreq(RecurrenceFreq.DAILY).withStartDateTime(time.toLocalDateTime()).build())
              .withScheduledTime(LocalDateTime.now())
              .build())
          .build();
      reminders.add(reminder);
      when(response.getAlerts()).thenReturn(reminders);
      when(management.getReminders()).thenReturn(response);
      ServiceClientFactory factory =mock(ServiceClientFactory.class);
      when(factory.getReminderManagementService()).thenReturn(management);

      var intentRequest = IntentRequest.builder().withIntent(intent).build();
      var requestEnvelope = RequestEnvelope.builder().withRequest(intentRequest).withContext(context).build();
      var handlerInput1 = HandlerInput.builder().withRequestEnvelope(requestEnvelope).withServiceClientFactory(
          factory).build();
      assertTrue(handler.canHandle(handlerInput1));
      assertTrue(handler.handle(handlerInput1).isPresent());
      assertTrue(handler.handle(handlerInput1).get().getOutputSpeech().toString().contains("Der Termin wurde nicht gefunden."));
    }

  @Test
  void correctDelete() {
    var handler = Mockito.spy(new DeleteHandler());
    Intent intent = mock(Intent.class);
    when(intent.getName()).thenReturn("DeleteIntent");
    Map<String, Slot> slots= getTheSlots();
    when(intent.getSlots()).thenReturn(slots);
    Permissions perm= Permissions.builder().withConsentToken("token").build();
    User user=mock(User.class);
    when(user.getPermissions()).thenReturn(perm);
    Context context=mock(Context.class);
    when(context.getSystem()).thenReturn(SystemState.builder().withUser(user).build());
    ReminderManagementServiceClient management=mock(ReminderManagementServiceClient.class);
    var response=mock(GetRemindersResponse.class);
    List<SpokenText> text=new ArrayList<>();
    text.add(SpokenText.builder().withText("Spazieren um " + time + " Uhr").build());
    List<Reminder> reminders=new ArrayList<>();
    OffsetDateTime time2=LocalDateTime.now().atOffset(ZoneOffset.UTC);
    String str = "2016-03-04 " + time;
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    LocalDateTime dateTime = LocalDateTime.parse(str, formatter);

    Reminder reminder=Reminder.builder()
        .withAlertInfo(AlertInfo.builder()
            .withSpokenInfo(AlertInfoSpokenInfo.builder().withContent(text).build()).build())
        .withCreatedTime(time2)
        .withTrigger(Trigger.builder()
            .withRecurrence(Recurrence.builder()
                .withFreq(RecurrenceFreq.DAILY).withStartDateTime(time2.toLocalDateTime()).build())
            .withScheduledTime(dateTime)
            .build())
        .build();
    reminders.add(reminder);
    when(response.getAlerts()).thenReturn(reminders);
    when(management.getReminders()).thenReturn(response);
    ServiceClientFactory factory =mock(ServiceClientFactory.class);
    when(factory.getReminderManagementService()).thenReturn(management);

    var intentRequest = IntentRequest.builder().withIntent(intent).build();
    var requestEnvelope = RequestEnvelope.builder().withRequest(intentRequest).withContext(context).build();
    var handlerInput1 = HandlerInput.builder().withRequestEnvelope(requestEnvelope).withServiceClientFactory(
        factory).build();
    assertTrue(handler.canHandle(handlerInput1));
    assertTrue(handler.handle(handlerInput1).isPresent());
    assertTrue(handler.handle(handlerInput1).get().getOutputSpeech().toString().contains("Folgender Termin wurde gelöscht"));
  }
  }
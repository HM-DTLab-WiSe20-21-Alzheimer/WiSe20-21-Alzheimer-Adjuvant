package edu.hm.adjuvant;

import static org.junit.jupiter.api.Assertions.*;
import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.amazon.ask.model.Context;
import com.amazon.ask.model.Permissions;
import com.amazon.ask.model.RequestEnvelope;
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
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.*;
import com.amazon.ask.model.Intent;
import com.amazon.ask.model.IntentRequest;
import org.mockito.Mockito;

/**
 * Testklasse für AllRemindersHandler.
 *
 * @author Anonymous Student
 */

class AllRemindersHandlerTest {

  @Test
  void testCanHandleGetAllReminders() {
    var handler = new AllRemindersHandler();
    Intent intent = mock(Intent.class);
    when(intent.getName()).thenReturn("GetAllIntent");
    var intentRequest = IntentRequest.builder().withIntent(intent).build();
    var requestEnvelope = RequestEnvelope.builder().withRequest(intentRequest).build();
    var handlerInput1 = HandlerInput.builder().withRequestEnvelope(requestEnvelope).build();
    assertTrue(handler.canHandle(handlerInput1));
    verify(intent, times(1)).getName();
  }

  @Test
  void testCannotHandleGetAllReminders() {
    var handler = new AllRemindersHandler();
    var intent = Intent.builder().withName("DeleteIntent").build();
    var intentRequest = IntentRequest.builder().withIntent(intent).build();
    var requestEnvelope = RequestEnvelope.builder().withRequest(intentRequest).build();
    var handlerInput = HandlerInput.builder().withRequestEnvelope(requestEnvelope).build();
    assertFalse(handler.canHandle(handlerInput));
  }

  @Test
  void correctGet() {
    var handler = Mockito.spy(new AllRemindersHandler());
    Intent intent = mock(Intent.class);
    when(intent.getName()).thenReturn("GetAllIntent");
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
    OffsetDateTime time= LocalDateTime.now().atOffset(ZoneOffset.UTC);
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
    assertTrue(handler.handle(handlerInput1).get().getOutputSpeech().toString().contains("Termin 1 Spazieren gehen"));
  }
}
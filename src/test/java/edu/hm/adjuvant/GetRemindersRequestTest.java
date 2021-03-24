package edu.hm.adjuvant;

import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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
import com.amazon.ask.model.Slot;
import com.amazon.ask.model.User;
import com.amazon.ask.model.interfaces.system.SystemState;
import com.amazon.ask.model.services.ServiceClientFactory;
import com.amazon.ask.model.services.ServiceException;
import com.amazon.ask.model.services.reminderManagement.AlertInfo;
import com.amazon.ask.model.services.reminderManagement.AlertInfoSpokenInfo;
import com.amazon.ask.model.services.reminderManagement.GetRemindersResponse;
import com.amazon.ask.model.services.reminderManagement.Recurrence;
import com.amazon.ask.model.services.reminderManagement.RecurrenceFreq;
import com.amazon.ask.model.services.reminderManagement.Reminder;
import com.amazon.ask.model.services.reminderManagement.ReminderManagementServiceClient;
import com.amazon.ask.model.services.reminderManagement.SpokenText;
import com.amazon.ask.model.services.reminderManagement.Trigger;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.TableCollection;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ListTablesResult;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import com.amazonaws.services.dynamodbv2.model.ScanResult;
import com.amazonaws.services.dynamodbv2.xspec.S;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.model.Body;
import com.amazonaws.services.simpleemail.model.Content;
import com.amazonaws.services.simpleemail.model.Destination;
import com.amazonaws.services.simpleemail.model.Message;
import com.amazonaws.services.simpleemail.model.SendEmailRequest;
import com.amazonaws.services.simpleemail.model.SendEmailResult;
import com.amazonaws.services.simpleemail.model.VerifyEmailAddressRequest;
import com.amazonaws.services.simpleemail.model.VerifyEmailAddressResult;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

/**
 * Testklasse für GetRemindersRequestHandler.
 *
 * @author Anonymous Student
 */
 class GetRemindersRequestTest {
  @Test
   void testCanHandleGetIntent() {
    var handler = new GetRemindersRequestHandler();
    Intent intent = mock(Intent.class);
    when(intent.getName()).thenReturn("GetIntent");
    var intentRequest = IntentRequest.builder().withIntent(intent).build();
    var requestEnvelope = RequestEnvelope.builder().withRequest(intentRequest).build();
    var handlerInput1 = HandlerInput.builder().withRequestEnvelope(requestEnvelope).build();
    assertTrue(handler.canHandle(handlerInput1));
    verify(intent, times(1)).getName();
  }

  @Test
   void testCannotHandleStoreContactIntent() {
    var handler = new GetRemindersRequestHandler();
    var intent = Intent.builder().withName("StoreContactIntent").build();
    var intentRequest = IntentRequest.builder().withIntent(intent).build();
    var requestEnvelope = RequestEnvelope.builder().withRequest(intentRequest).build();
    var handlerInput = HandlerInput.builder().withRequestEnvelope(requestEnvelope).build();
    assertFalse(handler.canHandle(handlerInput));
  }
  @Test  void testNoPermissions(){
    var handler = new GetRemindersRequestHandler();
    Intent intent = mock(Intent.class);
    when(intent.getName()).thenReturn("GetIntent");
    Permissions perm= Permissions.builder().build();
    User user=mock(User.class);
    when(user.getPermissions()).thenReturn(perm);
    Context context=mock(Context.class);
    when(context.getSystem()).thenReturn(SystemState.builder().withUser(user).build());
    ReminderManagementServiceClient management=mock(ReminderManagementServiceClient.class);
    ServiceClientFactory factory =mock(ServiceClientFactory.class);
    when(factory.getReminderManagementService()).thenReturn(management);
    when(management.getReminders()).thenThrow(ServiceException.class);
    var intentRequest = IntentRequest.builder().withIntent(intent).build();
    var requestEnvelope = RequestEnvelope.builder().withRequest(intentRequest).withContext(context).build();
    var handlerInput1 = HandlerInput.builder().withRequestEnvelope(requestEnvelope).withServiceClientFactory(factory).build();
    assertTrue(handler.canHandle(handlerInput1));
    assertTrue(handler.handle(handlerInput1).isPresent());
    assertTrue(handler.handle(handlerInput1).get().getOutputSpeech().toString().contains("Adjuvant braucht eine Erlaubnis um Ihre Termine verwalten zu können. "
        + " Bitte bestätigen Sie die Anfrage in Ihrer Alexa App "
        + "und versuchen Sie nochmal."));
    assertNotNull(handler.handle(handlerInput1).get().getCard());
  }
  @Test  void testPermissionsNoEmail(){
    var handler = Mockito.spy(new GetRemindersRequestHandler());
    Intent intent = mock(Intent.class);
    when(intent.getName()).thenReturn("GetIntent");
    Permissions perm= Permissions.builder().withConsentToken("token").build();
    User user=mock(User.class);
    when(user.getPermissions()).thenReturn(perm);
    Context context=mock(Context.class);
    when(context.getSystem()).thenReturn(SystemState.builder().withUser(user).build());
    ReminderManagementServiceClient management=mock(ReminderManagementServiceClient.class);
    var response=mock(GetRemindersResponse.class);
    when(response.getAlerts()).thenReturn(new ArrayList<>());
    when(management.getReminders()).thenReturn(response);
    ServiceClientFactory factory =mock(ServiceClientFactory.class);
    when(factory.getReminderManagementService()).thenReturn(management);

    var client =mock(AmazonDynamoDB.class, Mockito.RETURNS_DEEP_STUBS);
    when(client.listTables()).thenReturn(mock(ListTablesResult.class));
    when(handler.makeClient()).thenReturn(client);
    var result=mock(ScanResult.class);
    when(result.getItems()).thenReturn(new ArrayList<>());
    when(client.scan(new ScanRequest().withTableName("AdjuvantKontakte").withLimit(1))).thenReturn(result);

    var intentRequest = IntentRequest.builder().withIntent(intent).build();
    var requestEnvelope = RequestEnvelope.builder().withRequest(intentRequest).withContext(context).build();
    var handlerInput1 = HandlerInput.builder().withRequestEnvelope(requestEnvelope).withServiceClientFactory(
        factory).build();
    assertTrue(handler.canHandle(handlerInput1));
    assertTrue(handler.handle(handlerInput1).isPresent());
    assertTrue(handler.handle(handlerInput1).get().getOutputSpeech().toString().contains("Bitte fügen Sie zuerst eine Kontakperson hinzu."
        + " Sagen Sie Alexa ich will eine Kontakperson abspeichern mit mein Adjuvant. "
        + "Falls Sie weitere Hilfe benötigen sagen Sie Alexa öffne mein Adjuvant."));
    assertNull(handler.handle(handlerInput1).get().getCard());
  }

  @Test  void testPermissionsEmail(){
    var handler = Mockito.spy(new GetRemindersRequestHandler());
    Intent intent = mock(Intent.class);
    when(intent.getName()).thenReturn("GetIntent");
    Permissions perm= Permissions.builder().withConsentToken("token").build();
    User user=mock(User.class);
    when(user.getPermissions()).thenReturn(perm);
    Context context=mock(Context.class);
    when(context.getSystem()).thenReturn(SystemState.builder().withUser(user).build());
    ReminderManagementServiceClient management=mock(ReminderManagementServiceClient.class);
    var response=mock(GetRemindersResponse.class);
    when(response.getAlerts()).thenReturn(new ArrayList<>());
    when(management.getReminders()).thenReturn(response);
    ServiceClientFactory factory =mock(ServiceClientFactory.class);
    when(factory.getReminderManagementService()).thenReturn(management);

    var client =mock(AmazonDynamoDB.class, Mockito.RETURNS_DEEP_STUBS);
    when(client.listTables()).thenReturn(mock(ListTablesResult.class));
    when(handler.makeClient()).thenReturn(client);
    var result=mock(ScanResult.class);
    Map<String,AttributeValue> map=new HashMap<>();
    map.put("EMail",new AttributeValue().withS("test@example.com"));
    List<Map<String,AttributeValue>> list=new ArrayList<>();
    list.add(map);
    when(result.getItems()).thenReturn(list);
    when(client.scan(new ScanRequest().withTableName("AdjuvantKontakte").withLimit(1))).thenReturn(result);
    when(client.scan(new ScanRequest().withTableName("AdjuvantKontakte").withLimit(1)).getLastEvaluatedKey()).thenReturn(map);
    var clientSes=mock(AmazonSimpleEmailService.class,Mockito.RETURNS_DEEP_STUBS);
    SendEmailRequest request = new SendEmailRequest()
        .withDestination(
            new Destination().withToAddresses("test@example.com"))
        .withMessage(new Message()
            .withBody(new Body()
                .withHtml(new Content()
                    .withCharset("UTF-8").withData("<h1>Adjuvant Termine</h1>"
                        + "<p> " + ""+ " </p>"))
                .withText(new Content()
                    .withCharset("UTF-8").withData("")))
            .withSubject(new Content()
                .withCharset("UTF-8").withData("Adjuvant Termine")))
        .withSource("projekt-adjuvant_amazon-developer@sma.9bn.de");
    when(clientSes.sendEmail(request)).thenReturn(new SendEmailResult());
    when(handler.makeSes()).thenReturn(clientSes);

    var intentRequest = IntentRequest.builder().withIntent(intent).build();
    var requestEnvelope = RequestEnvelope.builder().withRequest(intentRequest).withContext(context).build();
    var handlerInput1 = HandlerInput.builder().withRequestEnvelope(requestEnvelope).withServiceClientFactory(
        factory).build();
    assertTrue(handler.canHandle(handlerInput1));
    assertTrue(handler.handle(handlerInput1).isPresent());
    assertTrue(handler.handle(handlerInput1).get().getOutputSpeech().toString().contains("Die EMail wurde gesendet."));
    assertNull(handler.handle(handlerInput1).get().getCard());
  }

  @Test  void testPermissionsEMailVerification(){
    var handler = Mockito.spy(new GetRemindersRequestHandler());
    Intent intent = mock(Intent.class);
    when(intent.getName()).thenReturn("GetIntent");
    Permissions perm= Permissions.builder().withConsentToken("token").build();
    User user=mock(User.class);
    when(user.getPermissions()).thenReturn(perm);
    Context context=mock(Context.class);
    when(context.getSystem()).thenReturn(SystemState.builder().withUser(user).build());
    ReminderManagementServiceClient management=mock(ReminderManagementServiceClient.class);
    var response=mock(GetRemindersResponse.class);
    when(response.getAlerts()).thenReturn(new ArrayList<>());
    when(management.getReminders()).thenReturn(response);
    ServiceClientFactory factory =mock(ServiceClientFactory.class);
    when(factory.getReminderManagementService()).thenReturn(management);

    var client =mock(AmazonDynamoDB.class, Mockito.RETURNS_DEEP_STUBS);
    when(client.listTables()).thenReturn(mock(ListTablesResult.class));
    when(handler.makeClient()).thenReturn(client);
    var result=mock(ScanResult.class);
    Map<String,AttributeValue> map=new HashMap<>();
    map.put("EMail",new AttributeValue().withS("test@example.com"));
    List<Map<String,AttributeValue>> list=new ArrayList<>();
    list.add(map);
    when(result.getItems()).thenReturn(list);
    when(client.scan(new ScanRequest().withTableName("AdjuvantKontakte").withLimit(1))).thenReturn(result);
    when(client.scan(new ScanRequest().withTableName("AdjuvantKontakte").withLimit(1)).getLastEvaluatedKey()).thenReturn(map);
    var clientSes=mock(AmazonSimpleEmailService.class,Mockito.RETURNS_DEEP_STUBS);
    SendEmailRequest request = new SendEmailRequest()
        .withDestination(
            new Destination().withToAddresses("test@example.com"))
        .withMessage(new Message()
            .withBody(new Body()
                .withHtml(new Content()
                    .withCharset("UTF-8").withData("<h1>Adjuvant Termine</h1>"
                        + "<p> " + ""+ " </p>"))
                .withText(new Content()
                    .withCharset("UTF-8").withData("")))
            .withSubject(new Content()
                .withCharset("UTF-8").withData("Adjuvant Termine")))
        .withSource("projekt-adjuvant_amazon-developer@sma.9bn.de");
    when(clientSes.sendEmail(request)).thenThrow(ClassCastException.class);
    when(clientSes.verifyEmailAddress(new VerifyEmailAddressRequest().withEmailAddress("test@example.com"))).thenReturn(new VerifyEmailAddressResult());
    when(handler.makeSes()).thenReturn(clientSes);

    var intentRequest = IntentRequest.builder().withIntent(intent).build();
    var requestEnvelope = RequestEnvelope.builder().withRequest(intentRequest).withContext(context).build();
    var handlerInput1 = HandlerInput.builder().withRequestEnvelope(requestEnvelope).withServiceClientFactory(
        factory).build();
    assertTrue(handler.canHandle(handlerInput1));
    assertTrue(handler.handle(handlerInput1).isPresent());
    assertTrue(handler.handle(handlerInput1).get().getOutputSpeech().toString().contains("Die EMail Adresse wurde verifiziert. "
        + "Die Kontaktperson muss den Link in der gesendeten EMail bestätigen. "
        + "Bitte beantragen Sie das Auslesen danach nochmal mit "
        + "Alexa schicke alle meine Termine mit mein Adjuvant."));
    assertNull(handler.handle(handlerInput1).get().getCard());
  }
  @Test  void testPermissionsEMailVerificationWithContent(){
    var handler = Mockito.spy(new GetRemindersRequestHandler());
    Intent intent = mock(Intent.class);
    when(intent.getName()).thenReturn("GetIntent");
    Permissions perm= Permissions.builder().withConsentToken("token").build();
    User user=mock(User.class);
    when(user.getPermissions()).thenReturn(perm);
    Context context=mock(Context.class);
    when(context.getSystem()).thenReturn(SystemState.builder().withUser(user).build());
    ReminderManagementServiceClient management=mock(ReminderManagementServiceClient.class);
    var response=mock(GetRemindersResponse.class);
    List<SpokenText> text=new ArrayList<>();
    text.add(SpokenText.builder().withText("Test Test").build());
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

    var client =mock(AmazonDynamoDB.class, Mockito.RETURNS_DEEP_STUBS);
    when(client.listTables()).thenReturn(mock(ListTablesResult.class));
    when(handler.makeClient()).thenReturn(client);
    var result=mock(ScanResult.class);
    Map<String,AttributeValue> map=new HashMap<>();
    map.put("EMail",new AttributeValue().withS("test@example.com"));
    List<Map<String,AttributeValue>> list=new ArrayList<>();
    list.add(map);
    when(result.getItems()).thenReturn(list);
    when(client.scan(new ScanRequest().withTableName("AdjuvantKontakte").withLimit(1))).thenReturn(result);
    when(client.scan(new ScanRequest().withTableName("AdjuvantKontakte").withLimit(1)).getLastEvaluatedKey()).thenReturn(map);
    var clientSes=mock(AmazonSimpleEmailService.class,Mockito.RETURNS_DEEP_STUBS);
    String body="1"
        + ") "
        + "Test Test"
        + " (erstellt am "
        + time.toLocalDate().toString() + " ): <br> Erinnerung wird ausgelesen um "
        + time
        + " Uhr ; <br> FREQUENZ: "
        + time.toLocalTime().toString()
        + "<br>";
    SendEmailRequest request = new SendEmailRequest()
        .withDestination(
            new Destination().withToAddresses("test@example.com"))
        .withMessage(new Message()
            .withBody(new Body()
                .withHtml(new Content()
                    .withCharset("UTF-8").withData("<h1>Adjuvant Termine</h1>"
                        + "<p> " + body+ " </p>"))
                .withText(new Content()
                    .withCharset("UTF-8").withData(body)))
            .withSubject(new Content()
                .withCharset("UTF-8").withData("Adjuvant Termine")))
        .withSource("projekt-adjuvant_amazon-developer@sma.9bn.de");
    when(clientSes.sendEmail(request)).thenThrow(ClassCastException.class);
    when(clientSes.verifyEmailAddress(new VerifyEmailAddressRequest().withEmailAddress("test@example.com"))).thenReturn(new VerifyEmailAddressResult());
    when(handler.makeSes()).thenReturn(clientSes);

    var intentRequest = IntentRequest.builder().withIntent(intent).build();
    var requestEnvelope = RequestEnvelope.builder().withRequest(intentRequest).withContext(context).build();
    var handlerInput1 = HandlerInput.builder().withRequestEnvelope(requestEnvelope).withServiceClientFactory(
        factory).build();
    assertTrue(handler.canHandle(handlerInput1));
    assertTrue(handler.handle(handlerInput1).isPresent());
    assertTrue(handler.handle(handlerInput1).get().getOutputSpeech().toString().contains("Die EMail wurde gesendet."));
    assertNull(handler.handle(handlerInput1).get().getCard());
  }
  @Test  void testPermissionsEMailVerificationWithContentWeekly(){
    var handler = Mockito.spy(new GetRemindersRequestHandler());
    Intent intent = mock(Intent.class);
    when(intent.getName()).thenReturn("GetIntent");
    Permissions perm= Permissions.builder().withConsentToken("token").build();
    User user=mock(User.class);
    when(user.getPermissions()).thenReturn(perm);
    Context context=mock(Context.class);
    when(context.getSystem()).thenReturn(SystemState.builder().withUser(user).build());
    ReminderManagementServiceClient management=mock(ReminderManagementServiceClient.class);
    var response=mock(GetRemindersResponse.class);
    List<SpokenText> text=new ArrayList<>();
    text.add(SpokenText.builder().withText("Test Test").build());
    List<Reminder> reminders=new ArrayList<>();
    OffsetDateTime time=LocalDateTime.now().atOffset(ZoneOffset.UTC);
    Reminder reminder=Reminder.builder()
        .withAlertInfo(AlertInfo.builder()
            .withSpokenInfo(AlertInfoSpokenInfo.builder().withContent(text).build()).build())
        .withCreatedTime(time)
        .withTrigger(Trigger.builder()
            .withRecurrence(Recurrence.builder()
                .withFreq(RecurrenceFreq.WEEKLY).withStartDateTime(time.toLocalDateTime()).build())
            .withScheduledTime(LocalDateTime.now())
            .build())
        .build();
    reminders.add(reminder);
    when(response.getAlerts()).thenReturn(reminders);
    when(management.getReminders()).thenReturn(response);
    ServiceClientFactory factory =mock(ServiceClientFactory.class);
    when(factory.getReminderManagementService()).thenReturn(management);

    var client =mock(AmazonDynamoDB.class, Mockito.RETURNS_DEEP_STUBS);
    when(client.listTables()).thenReturn(mock(ListTablesResult.class));
    when(handler.makeClient()).thenReturn(client);
    var result=mock(ScanResult.class);
    Map<String,AttributeValue> map=new HashMap<>();
    map.put("EMail",new AttributeValue().withS("test@example.com"));
    List<Map<String,AttributeValue>> list=new ArrayList<>();
    list.add(map);
    when(result.getItems()).thenReturn(list);
    when(client.scan(new ScanRequest().withTableName("AdjuvantKontakte").withLimit(1))).thenReturn(result);
    when(client.scan(new ScanRequest().withTableName("AdjuvantKontakte").withLimit(1)).getLastEvaluatedKey()).thenReturn(map);
    var clientSes=mock(AmazonSimpleEmailService.class,Mockito.RETURNS_DEEP_STUBS);
    String body="1"
        + ") "
        + "Test Test"
        + " (erstellt am "
        + time.toLocalDate().toString() + " ): <br> Erinnerung wird ausgelesen um "
        + time
        + " Uhr ; <br> FREQUENZ: "
        + time.toLocalTime().toString() + " jeden "+time.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.GERMAN)
        + "<br>";
    SendEmailRequest request = new SendEmailRequest()
        .withDestination(
            new Destination().withToAddresses("test@example.com"))
        .withMessage(new Message()
            .withBody(new Body()
                .withHtml(new Content()
                    .withCharset("UTF-8").withData("<h1>Adjuvant Termine</h1>"
                        + "<p> " + body+ " </p>"))
                .withText(new Content()
                    .withCharset("UTF-8").withData(body)))
            .withSubject(new Content()
                .withCharset("UTF-8").withData("Adjuvant Termine")))
        .withSource("projekt-adjuvant_amazon-developer@sma.9bn.de");
    when(clientSes.sendEmail(request)).thenThrow(ClassCastException.class);
    when(clientSes.verifyEmailAddress(new VerifyEmailAddressRequest().withEmailAddress("test@example.com"))).thenReturn(new VerifyEmailAddressResult());
    when(handler.makeSes()).thenReturn(clientSes);

    var intentRequest = IntentRequest.builder().withIntent(intent).build();
    var requestEnvelope = RequestEnvelope.builder().withRequest(intentRequest).withContext(context).build();
    var handlerInput1 = HandlerInput.builder().withRequestEnvelope(requestEnvelope).withServiceClientFactory(
        factory).build();
    assertTrue(handler.canHandle(handlerInput1));
    assertTrue(handler.handle(handlerInput1).isPresent());
    assertTrue(handler.handle(handlerInput1).get().getOutputSpeech().toString().contains("Die EMail wurde gesendet."));
    assertNull(handler.handle(handlerInput1).get().getCard());
  }

}

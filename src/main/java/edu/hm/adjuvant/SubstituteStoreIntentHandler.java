package edu.hm.adjuvant;

import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.amazon.ask.dispatcher.request.handler.RequestHandler;
import com.amazon.ask.model.IntentRequest;
import com.amazon.ask.model.Permissions;
import com.amazon.ask.model.Response;
import com.amazon.ask.model.Slot;
import com.amazon.ask.model.services.ServiceException;
import com.amazon.ask.model.services.reminderManagement.AlertInfo;
import com.amazon.ask.model.services.reminderManagement.AlertInfoSpokenInfo;
import com.amazon.ask.model.services.reminderManagement.PushNotification;
import com.amazon.ask.model.services.reminderManagement.PushNotificationStatus;
import com.amazon.ask.model.services.reminderManagement.Recurrence;
import com.amazon.ask.model.services.reminderManagement.RecurrenceDay;
import com.amazon.ask.model.services.reminderManagement.RecurrenceFreq;
import com.amazon.ask.model.services.reminderManagement.ReminderRequest;
import com.amazon.ask.model.services.reminderManagement.SpokenText;
import com.amazon.ask.model.services.reminderManagement.Trigger;
import com.amazon.ask.model.services.reminderManagement.TriggerType;
import com.amazon.ask.request.Predicates;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Eintragen der Termine zuhause.
 *
 * @author Anonymous Student
 */
public class SubstituteStoreIntentHandler implements RequestHandler {
  private static final String TIMEZONE = "Europe/Berlin";

  @Override
  public boolean canHandle(HandlerInput handlerInput) {
    return handlerInput.matches(Predicates.intentName("StoreFrequent"));
  }

  @Override
  public Optional<Response> handle(HandlerInput handlerInput) {
    Permissions permissions =
        handlerInput.getRequestEnvelope().getContext().getSystem().getUser().getPermissions();
    if (permissions != null && null != permissions.getConsentToken()) {
      IntentRequest intent = (IntentRequest) handlerInput.getRequestEnvelope().getRequest();
      Slot nameSlot = intent.getIntent().getSlots().get("remind");
      Slot timeSlot = intent.getIntent().getSlots().get("time");
      Slot freqSlot = intent.getIntent().getSlots().get("frequency");
      Slot dateSlot = intent.getIntent().getSlots().get("date");
      Slot weekDaySlot = intent.getIntent().getSlots().get("weekDay");

      if (nameSlot == null || nameSlot.getValue() == null && nameSlot.equals(timeSlot)) {
        return handlerInput.getResponseBuilder()
            .withSpeech("Ich konnte mir leider nichts merken. Bitte einen Termin angeben.")
            .build();
      } else {
        String remind = nameSlot.getValue();
        String time = timeSlot.getValue();
        String freq = freqSlot.getValue();
        String date = dateSlot.getValue();
        String weekDay = weekDaySlot.getValue();
        String result = setReminder(time, remind, freq, date, weekDay, handlerInput);
        if (!result.isEmpty()) {
          List<String> list = new ArrayList<>();
          list.add("alexa::alerts:reminders:skill:readwrite");
          return handlerInput.getResponseBuilder()
              .withSpeech(
                  "Adjuvant braucht eine Erlaubnis um Ihre Termine verwalten zu können. "
                      + " Bitte bestätigen Sie die Anfrage in Ihrer Alexa App "
                      + "und versuchen Sie nochmal."
              ).withAskForPermissionsConsentCard(list)
              .build();
        } else {
          String spokenText;
          switch (freq) {
            case "einmalig":
              spokenText =
                  "Folgender einmaliger Termin wurde abgespeichert: " + remind + " am " + date
                      + " um " + time + " Uhr.";
              break;
            case "täglich":
              spokenText =
                  "Folgende tägliche Errinerung wurde abgespeichert: " + remind + " um " + time
                      + " Uhr.";
              break;
            case "wöchentlich":
              spokenText =
                  "Folgende wöchentliche Errinerung wurde abgespeichert: " + remind + " um " + time
                      + " Uhr jeden " + weekDay;
              break;
            default:
              spokenText = "Fehler in der Frequenz";
              break;
          }
          return handlerInput.getResponseBuilder()
              .withSpeech(spokenText)
              .build();
        }
      }
    } else {
      List<String> list = new ArrayList<>();
      list.add("alexa::alerts:reminders:skill:readwrite");
      return handlerInput.getResponseBuilder()
          .withSpeech(
              "Adjuvant braucht eine Erlaubnis um Ihre Termine verwalten zu können."
                  + "Bitte bestätigen Sie die Anfrage in Ihrer Alexa App."
          ).withAskForPermissionsConsentCard(list)
          .build();
    }

  }

  private String setReminder(String scheduleTimeAsIso, String remind, String freq, String date,
                             String weekDay, HandlerInput input) {

    SpokenText spokenText = SpokenText.builder()
        .withText(remind + " um " + scheduleTimeAsIso + " Uhr.")
        .withLocale(input.getRequestEnvelope().getRequest().getLocale())
        .build();

    AlertInfoSpokenInfo alertInfoSpokenInfo = AlertInfoSpokenInfo.builder()
        .addContentItem(spokenText)
        .build();

    AlertInfo alertInfo = AlertInfo.builder()
        .withSpokenInfo(alertInfoSpokenInfo)
        .build();


    Recurrence recurrence;
    Trigger trigger;
    if (freq.equals("einmalig")) {
      DateTimeFormatter f = DateTimeFormatter.ISO_DATE;
      LocalDate datum = LocalDate.parse(date, f);
      LocalTime zeit = LocalTime.parse(scheduleTimeAsIso).minusMinutes(10);
      LocalDateTime tagzeit = LocalDateTime.of(datum, zeit);


      trigger = Trigger.builder()
          .withType(TriggerType.SCHEDULED_ABSOLUTE)
          .withScheduledTime(tagzeit)
          .withTimeZoneId(TIMEZONE)
          .build();
    } else if (freq.equals("täglich")) {
      LocalDateTime startTime = LocalDateTime.now().minusMinutes(10);
      LocalDateTime triggerTime =
          LocalDateTime.of(LocalDate.now(), LocalTime.parse(scheduleTimeAsIso).minusMinutes(10));

      recurrence = Recurrence.builder()
          .withFreq(RecurrenceFreq.DAILY)
          .withStartDateTime(startTime)
          .build();

      trigger = Trigger.builder()
          .withType(TriggerType.SCHEDULED_ABSOLUTE)
          .withScheduledTime(triggerTime)
          .withRecurrence(recurrence)
          .withTimeZoneId(TIMEZONE)
          .build();
    } else {
      LocalDateTime startTime = LocalDateTime.now().minusMinutes(10);
      LocalDateTime triggerTime =
          LocalDateTime.of(LocalDate.now(), LocalTime.parse(scheduleTimeAsIso).minusMinutes(10));

      RecurrenceDay recurrenceDay = new HandleFrequence().getDay(weekDay);

      recurrence = Recurrence.builder()
          .withFreq(RecurrenceFreq.WEEKLY)
          .addByDayItem(recurrenceDay)
          .withStartDateTime(startTime)
          .build();

      trigger = Trigger.builder()
          .withType(TriggerType.SCHEDULED_ABSOLUTE)
          .withScheduledTime(triggerTime)
          .withRecurrence(recurrence)
          .withTimeZoneId(TIMEZONE)
          .build();
    }


    PushNotification pushNotification = PushNotification.builder()
        .withStatus(PushNotificationStatus.ENABLED)
        .build();

    ReminderRequest reminderRequest = ReminderRequest.builder()
        .withAlertInfo(alertInfo)
        .withRequestTime(OffsetDateTime.now())
        .withTrigger(trigger)
        .withPushNotification(pushNotification)
        .build();

    try {
      input.getServiceClientFactory().getReminderManagementService()
          .createReminder(reminderRequest);
    } catch (ServiceException e) {
      return "not empty";
    }
    return "";
  }
}
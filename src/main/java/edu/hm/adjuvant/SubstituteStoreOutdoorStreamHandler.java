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
 * Termine außer Haus.
 *
 * @author Anonymous Student
 */
class SubstituteStoreOutdoorStreamHandler implements RequestHandler {
  private String name;
  private String frequency;
  private String date;
  private String time;
  private String cityFrom;
  private String streetFrom;
  private String numberFrom;
  private String cityTo;
  private String streetTo;
  private String numberTo;
  private String transport;

  @Override
  public boolean canHandle(HandlerInput handlerInput) {
    return handlerInput.matches(Predicates.intentName("StoreOutdoorIntent"));
  }

  @Override
  public Optional<Response> handle(HandlerInput handlerInput) {
    Permissions permissions =
        handlerInput.getRequestEnvelope().getContext().getSystem().getUser().getPermissions();
    if (permissions != null && null != permissions.getConsentToken()) {
      IntentRequest intent = (IntentRequest) handlerInput.getRequestEnvelope().getRequest();
      Slot nameSlot = intent.getIntent().getSlots().get("toStore");
      Slot dateSlot = intent.getIntent().getSlots().get("date");
      Slot frequencySlot = intent.getIntent().getSlots().get("frequency");
      Slot timeSlot = intent.getIntent().getSlots().get("time");
      Slot fromCitySlot = intent.getIntent().getSlots().get("fromCity");
      Slot fromStreetSlot = intent.getIntent().getSlots().get("fromStreet");
      Slot fromNumberSlot = intent.getIntent().getSlots().get("fromNumber");
      Slot toCitySlot = intent.getIntent().getSlots().get("toCity");
      Slot toStreetSlot = intent.getIntent().getSlots().get("toStreet");
      Slot toNumberSlot = intent.getIntent().getSlots().get("toNumber");
      Slot wayOfTransport = intent.getIntent().getSlots().get("wayOfTransport");

      if (nameSlot == null || timeSlot == null || wayOfTransport == null) {
        return handlerInput.getResponseBuilder()
            .withSpeech("Ich konnte mir leider nichts merken. Bitte einen Termin angeben.")
            .build();
      } else {
        name = nameSlot.getValue();
        frequency = frequencySlot.getValue();
        date = dateSlot.getValue();
        time = timeSlot.getValue();
        cityFrom = fromCitySlot.getValue();
        streetFrom = fromStreetSlot.getValue();
        numberFrom = fromNumberSlot.getValue();
        cityTo = toCitySlot.getValue();
        streetTo = toStreetSlot.getValue();
        numberTo = toNumberSlot.getValue();
        transport = wayOfTransport.getValue();

        String result = setReminder(handlerInput);

        if (result.isEmpty()) {
          return handlerInput.getResponseBuilder()
              .withSpeech(
                  "Folgender Termin wurde abgespeichert: " + name + " um " + time + "."
                      + "Ausgangspunkt ist: " + cityFrom + " " + streetFrom + " "
                      + numberFrom + " Ihr Zielort ist: " + cityTo + " "
                      + streetTo + " " + numberTo + " mit: " + transport)
              .build();
        } else if (result.contains("ReminderManagement")) {
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
          return handlerInput.getResponseBuilder()
              .withSpeech(
                  result)
              .build();
        }
      }
    } else {
      List<String> list = new ArrayList<>();
      list.add("alexa::alerts:reminders:skill:readwrite");
      return handlerInput.getResponseBuilder()
          .withSpeech(
              "Adjuvant braucht eine Erlaubnis um Ihre Termine verwalten zu können. "
                  + "Bitte bestätigen Sie die Anfrage in Ihrer Alexa "
                  + "App und versuchen Sie erneut."
          ).withAskForPermissionsConsentCard(list)
          .build();
    }

  }

  private String setReminder(HandlerInput input) {
    String error = "";
    Trigger trigger;
    final String adressFrom = cityFrom + " " + streetFrom + " " + numberFrom;
    final String adressTo = cityTo + " " + streetTo + " " + numberTo;
    SpokenText spokenText = SpokenText.builder()
        .withText(
            new Output().generateSpeach(time, name, adressFrom, adressTo, transport))
        .withLocale(input.getRequestEnvelope().getRequest().getLocale())
        .build();

    AlertInfoSpokenInfo alertInfoSpokenInfo = AlertInfoSpokenInfo.builder()
        .addContentItem(spokenText)
        .build();

    AlertInfo alertInfo = AlertInfo.builder()
        .withSpokenInfo(alertInfoSpokenInfo)
        .build();

    RecurrenceDay day = new HandleFrequence().getDay(frequency);
    int timeBefor = (new GetRoutingTime()
        .getSeconds((streetFrom + " " + numberFrom + " " + " " + cityFrom),
            (streetTo + " " + numberTo + " " + cityTo), transport)) / 60;
    if (timeBefor > 0) {
      Recurrence recurrence;
      if (frequency.equalsIgnoreCase("weiter")) { //einmalig
        DateTimeFormatter f = DateTimeFormatter.ISO_DATE;
        LocalDate datum = LocalDate.parse(date, f);
        LocalTime zeit = LocalTime.parse(time).minusMinutes(timeBefor);
        error = checkDate(datum, zeit);
        if (!error.isEmpty()) {
          return error;
        }
        LocalDateTime tagzeit = LocalDateTime.of(datum, zeit);


        trigger = Trigger.builder()
            .withType(TriggerType.SCHEDULED_ABSOLUTE)
            .withScheduledTime(tagzeit)
            .withTimeZoneId("Europe/Berlin")
            .build();
      } else {
        LocalDateTime startTime = LocalDateTime.now().minusMinutes(timeBefor);
        LocalDateTime triggerTime = LocalDateTime
            .of(LocalDate.now(), LocalTime.parse(time).minusMinutes(timeBefor));
        if (!day.equals(RecurrenceDay.UNKNOWN_TO_SDK_VERSION)) { //woechentlich
          recurrence = Recurrence.builder()
              .withFreq(RecurrenceFreq.WEEKLY)
              .addByDayItem(day)
              .withStartDateTime(startTime)
              .build();

        } else { //taeglich
          recurrence = Recurrence.builder()
              .withFreq(RecurrenceFreq.DAILY)
              .withStartDateTime(startTime)
              .build();
        }
        trigger = Trigger.builder()
            .withType(TriggerType.SCHEDULED_ABSOLUTE)
            .withScheduledTime(triggerTime)
            .withRecurrence(recurrence)
            .withTimeZoneId("Europe/Berlin")
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
        error = "ReminderManagement";
      }
    } else {
      error = "Es ist ein Fehler in der Routenberechnung aufgetreten,"
          + " die Adressangabe ist falsch oder Ziel ist gleich Ausgangspunkt"
          + " bitte speichern Sie den Termin erneut.";
    }
    return error;
  }

  private String checkDate(LocalDate date, LocalTime time) {
    LocalDate localDate = LocalDate.now();
    LocalTime localTime = LocalTime.now();
    String error = "";
    if (!localDate.isBefore(date) || (localDate == date && time.isBefore(localTime))) {
      error = "Termin liegt in der Vergangenheit oder Ziel kann nicht mehr erreicht werden";
    }
    return error;
  }


}
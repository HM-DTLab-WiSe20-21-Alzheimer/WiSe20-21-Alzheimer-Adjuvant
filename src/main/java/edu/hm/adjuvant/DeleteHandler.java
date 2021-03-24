package edu.hm.adjuvant;

import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.amazon.ask.dispatcher.request.handler.RequestHandler;
import com.amazon.ask.model.IntentRequest;
import com.amazon.ask.model.Permissions;
import com.amazon.ask.model.Response;
import com.amazon.ask.model.Slot;
import com.amazon.ask.model.services.reminderManagement.Reminder;
import com.amazon.ask.request.Predicates;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Löschen eines Reminders.
 *
 * @author Anonymous Student
 */
public class DeleteHandler implements RequestHandler {
  @Override
  public boolean canHandle(HandlerInput handlerInput) {
    return handlerInput.matches(Predicates.intentName("DeleteIntent"));
  }

  @Override
  public Optional<Response> handle(HandlerInput handlerInput) {
    Permissions permissions =
        handlerInput.getRequestEnvelope().getContext().getSystem().getUser().getPermissions();
    if (permissions != null && null != permissions.getConsentToken()) {
      IntentRequest intent = (IntentRequest) handlerInput.getRequestEnvelope().getRequest();
      Slot nameSlot = intent.getIntent().getSlots().get("toDelete");
      Slot timeSlot = intent.getIntent().getSlots().get("time");
      if (nameSlot == null || timeSlot == null || nameSlot.equals(timeSlot)) {
        return handlerInput.getResponseBuilder()
            .withSpeech("Ich konnte leider nichts verstehen. Bitte einen Termin angeben.")
            .build();
      } else {
        String data = nameSlot.getValue();
        String time = timeSlot.getValue();
        List<Reminder> allReminders;
        allReminders =
            handlerInput.getServiceClientFactory().getReminderManagementService()
                .getReminders().getAlerts();
        for (Reminder reminder : allReminders) {
          String spokenText = reminder.getAlertInfo().getSpokenInfo().getContent().toString();
          if (spokenText.contains(data)
              &&
              spokenText.contains(time)) {
            handlerInput.getServiceClientFactory().getReminderManagementService()
                .deleteReminder(reminder.getAlertToken());
            return handlerInput.getResponseBuilder()
                .withSpeech(
                    "Folgender Termin wurde gelöscht: " + data + " um " + time + " Uhr.")
                .build();
          }
        }
      }
    } else {
      List<String> list = new ArrayList<>();
      list.add("alexa::alerts:reminders:skill:readwrite");
      return handlerInput.getResponseBuilder()
          .withSpeech(
              "Adjuvant braucht eine Erlaubnis um Ihre Termine verwalten zu können. "
                  + " Bitte bestätigen Sie die Anfrage in Ihrer Alexa App."
          ).withAskForPermissionsConsentCard(list)
          .build();
    }

    return handlerInput.getResponseBuilder()
        .withSpeech(
            "Der Termin wurde nicht gefunden.")
        .build();
  }
}

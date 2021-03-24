package edu.hm.adjuvant;

import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.amazon.ask.dispatcher.request.handler.RequestHandler;
import com.amazon.ask.model.Response;
import com.amazon.ask.model.services.reminderManagement.Reminder;
import com.amazon.ask.request.Predicates;
import java.util.List;
import java.util.Optional;

/**
 * Ausgeben aller Reminder.
 *
 * @author Anonymous Student
 */

public class AllRemindersHandler implements RequestHandler {
  @Override
  public boolean canHandle(HandlerInput input) {
    return input.matches(Predicates.intentName("GetAllIntent"));
  }

  @Override
  public Optional<Response> handle(HandlerInput input) {
    List<Reminder> allReminders =
        input.getServiceClientFactory().getReminderManagementService()
            .getReminders().getAlerts();
    int count = 0;
    StringBuilder stringBuilder = new StringBuilder();
    for (Reminder reminder : allReminders) {
      count = count + 1;
      String text =
          reminder.getAlertInfo().getSpokenInfo().getContent().get(0).getText();
      text = text.replace("Wollen Sie die Routeninformationen "
          + "erhalten sagen Sie bitte:", "");
      text = text.replace("Führe eine Routenberechnung durch mit mein Adjuvant", "");
      stringBuilder.append("Termin " + count + " " + text);
    }
    return input.getResponseBuilder().withSpeech(stringBuilder.toString()).build();
  }
}



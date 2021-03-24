package edu.hm.adjuvant;

import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.amazon.ask.dispatcher.request.handler.RequestHandler;
import com.amazon.ask.model.Response;
import com.amazon.ask.model.services.ServiceException;
import com.amazon.ask.model.services.reminderManagement.Recurrence;
import com.amazon.ask.model.services.reminderManagement.Reminder;
import com.amazon.ask.model.services.reminderManagement.Trigger;
import com.amazon.ask.request.Predicates;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClientBuilder;
import com.amazonaws.services.simpleemail.model.Body;
import com.amazonaws.services.simpleemail.model.Content;
import com.amazonaws.services.simpleemail.model.Destination;
import com.amazonaws.services.simpleemail.model.Message;
import com.amazonaws.services.simpleemail.model.SendEmailRequest;
import com.amazonaws.services.simpleemail.model.VerifyEmailAddressRequest;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

/**
 * Abschicken der Termine zu der Kontaktperson.
 *
 * @author Anonymous Student
 */
public class GetRemindersRequestHandler implements RequestHandler {

  @Override
  public boolean canHandle(HandlerInput input) {
    return input.matches(Predicates.intentName("GetIntent"));
  }

  @Override
  public Optional<Response> handle(HandlerInput input) {
    List<Reminder> reminders;
    try {
      reminders =
          input.getServiceClientFactory().getReminderManagementService().getReminders().getAlerts();
    } catch (ServiceException e) {
      List<String> list = new ArrayList<>();
      list.add("alexa::alerts:reminders:skill:readwrite");
      return input.getResponseBuilder()
          .withSpeech(
              "Adjuvant braucht eine Erlaubnis um Ihre Termine verwalten zu können. "
                  + " Bitte bestätigen Sie die Anfrage in Ihrer Alexa App "
                  + "und versuchen Sie nochmal."
          ).withAskForPermissionsConsentCard(list)
          .build();
    }
    StringBuilder stringBuilder = new StringBuilder();
    int num = 0;
    for (Reminder reminder : reminders) {
      num = num + 1;
      try {
        String spokenText = reminder.getAlertInfo().getSpokenInfo().getContent().get(0).getText();
        spokenText = spokenText.replace("Wollen Sie die Routeninformationen "
            + "erhalten sagen Sie bitte:", "");
        spokenText = spokenText.replace("Führe eine Routenberechnung durch mit mein Adjuvant", "");
        String createdTime = reminder.getCreatedTime().toLocalDate().toString();
        Trigger trigger = reminder.getTrigger();
        Recurrence recurrence = trigger.getRecurrence();
        String recurrenceText = trigger.getScheduledTime().toLocalDate().toString();
        if (recurrence != null) {
          recurrenceText = recurrence.getFreqAsString();
          if (recurrenceText.equals("WEEKLY")) {
            recurrenceText = recurrenceText + " jeden "
                + trigger.getScheduledTime().getDayOfWeek().getDisplayName(TextStyle.FULL,
                Locale.GERMAN);
          }
        }
        String time = trigger.getScheduledTime().toLocalTime().toString();
        stringBuilder.append(num
                + ") "
                + spokenText
                + " (erstellt am "
                + createdTime + " ): <br> Erinnerung wird ausgelesen um "
                + time
                + " Uhr ; <br> FREQUENZ: "
                + recurrenceText
                + "<br>");
      } catch (NullPointerException ignored) {
        ignored.hashCode();
      }

    }
    String speech = sendEmail(stringBuilder.toString());
    return input.getResponseBuilder().withSpeech(speech).build();
  }

  private String sendEmail(String body) {
    String adr = getEmail();
    String charset = "UTF-8";
    if (adr.isEmpty()) {
      return
          "Bitte fügen Sie zuerst eine Kontakperson hinzu."
              + " Sagen Sie Alexa ich will eine Kontakperson abspeichern mit mein Adjuvant. "
              + "Falls Sie weitere Hilfe benötigen sagen Sie Alexa öffne mein Adjuvant.";
    } else {
      try {

        AmazonSimpleEmailService client = makeSes();
        SendEmailRequest request = new SendEmailRequest()
            .withDestination(
                new Destination().withToAddresses(adr))
            .withMessage(new Message()
                .withBody(new Body()
                    .withHtml(new Content()
                        .withCharset(charset).withData("<h1>Adjuvant Termine</h1>"
                            + "<p> " + body + " </p>"))
                    .withText(new Content()
                        .withCharset(charset).withData(body)))
                .withSubject(new Content()
                    .withCharset(charset).withData("Adjuvant Termine")))
            .withSource("projekt-adjuvant_amazon-developer@sma.9bn.de");
        // Comment or remove the next line if you are not using a
        // configuration set
        client.sendEmail(request);
      } catch (Exception ex) {
        AmazonSimpleEmailService clientSes = makeSes();
        VerifyEmailAddressRequest request = new VerifyEmailAddressRequest().withEmailAddress(adr);
        clientSes.verifyEmailAddress(request);
        return "Die EMail Adresse wurde verifiziert. "
            + "Die Kontaktperson muss den Link in der gesendeten EMail bestätigen. "
            + "Bitte beantragen Sie das Auslesen danach nochmal mit "
            + "Alexa schicke alle meine Termine mit mein Adjuvant.";
      }
      return "Die EMail wurde gesendet.";
    }
  }

  private String getEmail() {
    String adr = "";
    AmazonDynamoDB client = makeClient();
    ScanRequest scanRequest = new ScanRequest().withTableName("AdjuvantKontakte").withLimit(1);
    if (!client.scan(scanRequest).getItems().isEmpty()) {
      AttributeValue value = client.scan(scanRequest).getLastEvaluatedKey().get("EMail");
      adr = value.getS();
    }
    return adr;
  }

  public AmazonDynamoDB makeClient() {
    return AmazonDynamoDBClientBuilder.standard().withRegion(Regions.EU_CENTRAL_1).build();
  }

  /**
   * AmazonSimpleEmailService, Hilfsklasse.
   *
   * @return AmazonSimpleEmailService
   */
  public AmazonSimpleEmailService makeSes() {
    return AmazonSimpleEmailServiceClientBuilder.standard()
        // Replace US_WEST_2 with the AWS Region you're using for
        // Amazon SES.
        .withRegion(Regions.EU_CENTRAL_1).build();
  }

}

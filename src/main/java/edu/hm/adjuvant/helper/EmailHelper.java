package edu.hm.adjuvant.helper;

import com.amazon.ask.model.Response;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClientBuilder;
import com.amazonaws.services.simpleemail.model.Body;
import com.amazonaws.services.simpleemail.model.Content;
import com.amazonaws.services.simpleemail.model.Destination;
import com.amazonaws.services.simpleemail.model.Message;
import com.amazonaws.services.simpleemail.model.SendEmailRequest;
import com.amazonaws.services.simpleemail.model.VerifyEmailAddressRequest;
import edu.hm.adjuvant.utils.ResponseMessage;
import java.util.Optional;

/**
 * EmailHelper.java
 *
 * @author Stefan Kühnel, stefan.kuehnel@hm.edu
 * @version 2021-1-11
 */

public class EmailHelper {
  private static final String STANDARD_CHARSET = "UTF-8";

  // Privater Konstruktor
  private EmailHelper() {}

  /**
   * Versendet eine E-Mail an eine externe E-Mail Adresse.
   *
   * @param to      Die E-Mail Adresse des Empfängers.
   * @param from    Die E-Mail Adresse des Absenders.
   * @param subject Der Betreff.
   * @param text    Inhalt der Nachricht als Text.
   * @param html    Inhalt der Nachricht als HTML.
   * @return Rückgabe, ob der Versand geglückt ist, oder nicht.
   */
  public static Optional<Response> send(String to, String from, String subject, String text,
                                        String html) {
    try {
      // Initialisierung des E-Mail Dienstes von Amazon.
      final AmazonSimpleEmailService amazonSimpleEmailService =
          AmazonSimpleEmailServiceClientBuilder
              .standard().withRegion(Regions.EU_CENTRAL_1)
              .build();

      // Der Body enthält sowohl HTML, als auch Text.
      final Body body = new Body()
          .withHtml(new Content().withCharset(STANDARD_CHARSET).withData(html))
          .withText(new Content().withCharset(STANDARD_CHARSET).withData(text));

      // Nachricht mit entsprechendem Betreff.
      final Message message = new Message()
          .withBody(body)
          .withSubject(
              new Content().withCharset(STANDARD_CHARSET).withData(subject));

      // Die Empfänger der Nachricht.
      final Destination destination = new Destination()
          .withToAddresses(to);

      // Erstellung der Anfrage zum Senden der E-Mail.
      final SendEmailRequest sendEmailRequest = new SendEmailRequest()
          .withDestination(destination)
          .withSource(from)
          .withMessage(message);

      // Senden der E-Mail.
      amazonSimpleEmailService.sendEmail(sendEmailRequest);

      return ResponseHelper.say(ResponseMessage.ROUTE_SEND_SUCCESSFULLY);
    } catch (Exception e) {
      // Automatisches versenden einer Bestätigungsemail.
      verify(to);

      return ResponseHelper.say(ResponseMessage.EMAIL_VERIFICATION_SEND);
    }
  }

  private static void verify(String email) {
    // Initialisierung des E-Mail Dienstes von Amazon.
    AmazonSimpleEmailService amazonSimpleEmailService = AmazonSimpleEmailServiceClientBuilder
        .standard().withRegion(Regions.EU_CENTRAL_1)
        .build();

    // Die noch nicht verifizierte E-Mail soll automatisch verifiziert werden.
    VerifyEmailAddressRequest verifyEmailAddressRequest = new VerifyEmailAddressRequest()
        .withEmailAddress(email);

    // Anfrage zum Senden der Verifizierungsanfrage an die übergebene E-Mail Adresse.
    amazonSimpleEmailService.verifyEmailAddress(verifyEmailAddressRequest);
  }
}

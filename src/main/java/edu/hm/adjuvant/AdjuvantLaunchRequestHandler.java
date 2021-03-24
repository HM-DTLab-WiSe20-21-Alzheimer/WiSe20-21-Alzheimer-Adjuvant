package edu.hm.adjuvant;

import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.amazon.ask.dispatcher.request.handler.impl.LaunchRequestHandler;
import com.amazon.ask.model.LaunchRequest;
import com.amazon.ask.model.Response;
import com.amazon.ask.request.Predicates;
import java.util.Optional;

/**
 * Starten des Skills und Hilfestellung.
 */
public class AdjuvantLaunchRequestHandler implements LaunchRequestHandler {

  public boolean canHandle(HandlerInput input, LaunchRequest launchRequest) {
    return input.matches(Predicates.requestType(LaunchRequest.class));
  }

  /**
   * Launch Request Response with Information about Adjuvant.
   *
   * @param input         User Input.
   * @param launchRequest User Launch Request.
   * @return Optional Response.
   */
  public Optional<Response> handle(HandlerInput input, LaunchRequest launchRequest) {
    String text =
        "A Anton \r\n"
            + "B Berta \r\n"
            + "C Cäsar \r\n"
            + "D Dora \r\n"
            + "E Emil \n"
            + "F Friedrich \n"
            + "G Gustav \n"
            + "H Heinrich \n"
            + "I Ida \n"
            + "J Julius \n"
            + "K Kaufmann \n"
            + "L Ludwig \n"
            + "M Martha \n"
            + "N Nordpol \n"
            + "O Otto \n"
            + "P Paula \n"
            + "Q Quelle \n"
            + "R Richard \n"
            + "S Samuel \n"
            + "T Theodor \n"
            + "U Ulrich \n"
            + "V Viktor \n"
            + "W Wilhelm \n"
            + "X Xanthippe \n"
            + "Y Ypsilon \n"
            + "Z Zacharias";
    return input.getResponseBuilder()
        .withSpeech("Adjuvant ist ein Skill, der Nutzer bei ihrer Terminverwaltung unterstützt. "
            + "Wenn Sie einen Termin zu Hause speichern wollen,"
            + " sagen Sie Alexa speichere einen Termin zuhause mit mein Adjuvant. "
            + "Wenn Sie einen Termin außer Haus speichern wollen,"
            + " sagen Sie Alexa merke dir einen Termin außer Haus mit mein Adjuvant. "
            + "Wenn Sie Ihre Termine Ihrer Kontaktperson schicken wollen, "
            + "sagen Sie Alexa schicke alle meine Termine mit mein Adjuvant. "
            + "Wenn Sie einen Termin löschen wollen, sagen Sie Alexa lösche was mit mein Adjuvant. "
            + "Wenn Sie selbst Ihre Termine einsehen wollen,"
            + " sagen Sie Alexa lese alle Termine aus mit mein Adjuvant. "
            + "Beim Abspeichern der Kontaktpersonen wird die sogenannte Buchstabiertafel benötigt. "
            + " Diese finden Sie in Form einer Karte auf Ihrem Echo Gerät "
            + "oder auf der Amazon Internet Seite. "
            + "Um die EMail Adresse der Kontakperson abzuspeichern, "
            + "sagen Sie Alexa ich will eine Kontakperson abspeichern mit mein Adjuvant. ")
        .withSimpleCard("Buchstabiertafel", text)
        .build();
  }
}
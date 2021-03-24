package edu.hm.adjuvant;

import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.amazon.ask.dispatcher.request.handler.RequestHandler;
import com.amazon.ask.model.Response;
import com.amazon.ask.request.Predicates;
import java.util.Optional;

/**
 * Hilfestellung beim Abspeichern der Kontaktperson.
 * Ausgabe der Buchstabiertafel.
 *
 * @author Anonymous Student
 */
public class GetAlphabetRequestHandler implements RequestHandler {
  @Override
  public boolean canHandle(HandlerInput handlerInput) {
    return handlerInput.matches(Predicates.intentName("GetAlphabetIntent"));
  }

  @Override
  public Optional<Response> handle(HandlerInput handlerInput) {
    String text =
        "A Anton \r\n" + "B Berta \r\n" + "C Cäsar \r\n" + "D Dora \r\n" + "E Emil \n"
            + "F Friedrich \n" + "G Gustav \n" + "H Heinrich \n" + "I Ida \n" + "K Kaufmann \n"
            + "J Julius \n" + "L Ludwig \n" + "M Martha \n" + "N Nordpol \n" + "O Otto \n"
            + "P Paula \n" + "Q Quelle \n" + "R Richard \n" + "S Samuel \n" + "T Theodor \n"
            + "U Ulrich \n" + "V Viktor \n" + "W Wilhelm \n" + "X Xanthippe \n" + "Y Ypsilon \n"
            + "Z Zacharias \n";
    return handlerInput.getResponseBuilder()
        .withSpeech(
            "Um die benötigte EMail Adresse von der Kontaktperson abzuspeichern,"
                + " wird die sogenannte Buchstabiertafel benutzt. "
                + "Dabei wird jeder Buchstabe durch ein Wort repräsentiert. "
                + "Zahlen können normal angegeben werden und "
                + "Punkt wird durch das Wort Punkt repräsentiert. "
                + "Der erste Teil der EMail ist der bis zum et Zeichen,"
                + " der zweite ist nach dem et Zeichen. "
                + "Das et Zeichen selbst muss nicht angegeben werden. "
                + "Die Buchstabiertafel wurde auf Ihrem Amazon Echo Gerät"
                + " und auf der Amazon Internet Seite ausgegeben. "
                + "Dort sehen Sie welche Wörter Sie benutzen müssen. "
                + "Sobald Sie bereit für das Abspeichern sind, "
                + "sagen Sie Alexa füge eine Kontaktperson hinzu mit mein Adjuvant.")
        .withSimpleCard("Buchstabiertafel", text)
        .build();
  }
}

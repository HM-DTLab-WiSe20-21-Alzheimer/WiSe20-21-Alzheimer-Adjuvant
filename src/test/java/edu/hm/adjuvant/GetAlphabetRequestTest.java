package edu.hm.adjuvant;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.amazon.ask.model.Intent;
import com.amazon.ask.model.IntentRequest;
import com.amazon.ask.model.RequestEnvelope;
import org.junit.jupiter.api.Test;

/**
 * Testklasse für GetAlphabetRequestHandler.
 * @author Anonymous Student
 */

 class GetAlphabetRequestTest {
  @Test
   void testCanHandleGetAlphabetRequest() {
    var handler = new GetAlphabetRequestHandler();
    Intent intent = mock(Intent.class);
    when(intent.getName()).thenReturn("GetAlphabetIntent");
    var intentRequest = IntentRequest.builder().withIntent(intent).build();
    var requestEnvelope = RequestEnvelope.builder().withRequest(intentRequest).build();
    var handlerInput1 = HandlerInput.builder().withRequestEnvelope(requestEnvelope).build();
    assertTrue(handler.canHandle(handlerInput1));
    verify(intent, times(1)).getName();
  }

  @Test
   void testCannotHandleGetAlphabetRequest() {
    var handler = new GetAlphabetRequestHandler();
    var intent = Intent.builder().withName("GetIntent").build();
    var intentRequest = IntentRequest.builder().withIntent(intent).build();
    var requestEnvelope = RequestEnvelope.builder().withRequest(intentRequest).build();
    var handlerInput = HandlerInput.builder().withRequestEnvelope(requestEnvelope).build();
    assertFalse(handler.canHandle(handlerInput));
  }

  @Test void returnTextHasAlphabet(){
    var handler = new GetAlphabetRequestHandler();
    var intent = Intent.builder().withName("GetAlphabetIntent").build();
    var intentRequest = IntentRequest.builder().withIntent(intent).build();
    var requestEnvelope = RequestEnvelope.builder().withRequest(intentRequest).build();
    var handlerInput = HandlerInput.builder().withRequestEnvelope(requestEnvelope).build();
    String text="Um die benötigte EMail Adresse von der Kontaktperson abzuspeichern,"
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
        + "sagen Sie Alexa füge eine Kontaktperson hinzu mit mein Adjuvant.";
    assertTrue(handler.handle(handlerInput).isPresent());
    assertTrue(handler.handle(handlerInput).get().getOutputSpeech().toString().contains(text));
  }
  @Test void returnCardHasAlphabet(){
    var handler = new GetAlphabetRequestHandler();
    var intent = Intent.builder().withName("GetAlphabetIntent").build();
    var intentRequest = IntentRequest.builder().withIntent(intent).build();
    var requestEnvelope = RequestEnvelope.builder().withRequest(intentRequest).build();
    var handlerInput = HandlerInput.builder().withRequestEnvelope(requestEnvelope).build();
    String text =
        "A Anton \r\n" + "B Berta \r\n" + "C Cäsar \r\n" + "D Dora \r\n" + "E Emil \n"
            + "F Friedrich \n" + "G Gustav \n" + "H Heinrich \n" + "I Ida \n" + "K Kaufmann \n"
            + "J Julius \n" + "L Ludwig \n" + "M Martha \n" + "N Nordpol \n" + "O Otto \n"
            + "P Paula \n" + "Q Quelle \n" + "R Richard \n" + "S Samuel \n" + "T Theodor \n"
            + "U Ulrich \n" + "V Viktor \n" + "W Wilhelm \n" + "X Xanthippe \n" + "Y Ypsilon \n"
            + "Z Zacharias \n";
    assertTrue(handler.handle(handlerInput).isPresent());
    assertTrue(handler.handle(handlerInput).get().getCard().toString().replaceAll("\\s+", "").contains(text.replaceAll("\\s+", "")));
    assertTrue(handler.handle(handlerInput).get().getCard().toString().contains("Buchstabiertafel"));
  }
}

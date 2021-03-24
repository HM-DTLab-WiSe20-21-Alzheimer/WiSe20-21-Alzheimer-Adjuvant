package edu.hm.adjuvant;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.amazon.ask.model.Intent;
import com.amazon.ask.model.IntentRequest;
import com.amazon.ask.model.LaunchRequest;
import com.amazon.ask.model.RequestEnvelope;
import com.amazon.ask.request.Predicates;
import org.junit.jupiter.api.Test;

/**
 * Testklasse für FallbackIntent, LaunchRequest und ExitRequest.
 *
 * @author Anonymous Student
 */

 class FallBackAndLaunchAndExitTest {
  @Test
   void testCanHandleFallback() {
    var handler = new FallbackHandler();
    Intent intent = mock(Intent.class);
    when(intent.getName()).thenReturn("AMAZON.FallbackIntent");
    var intentRequest = IntentRequest.builder().withIntent(intent).build();
    var requestEnvelope = RequestEnvelope.builder().withRequest(intentRequest).build();
    var handlerInput1 = HandlerInput.builder().withRequestEnvelope(requestEnvelope).build();
    assertTrue(handler.canHandle(handlerInput1));
    verify(intent, times(1)).getName();
  }

  @Test
   void testCannotHandleFallback() {
    var handler = new FallbackHandler();
    var intent = Intent.builder().withName("GetIntent").build();
    var intentRequest = IntentRequest.builder().withIntent(intent).build();
    var requestEnvelope = RequestEnvelope.builder().withRequest(intentRequest).build();
    var handlerInput = HandlerInput.builder().withRequestEnvelope(requestEnvelope).build();
    assertFalse(handler.canHandle(handlerInput));
  }

  @Test
   void testHandleFallback() {
    var handler = new FallbackHandler();
    Intent intent = mock(Intent.class);
    when(intent.getName()).thenReturn("GetIntent");
    var intentRequest = IntentRequest.builder().withIntent(intent).build();
    var requestEnvelope = RequestEnvelope.builder().withRequest(intentRequest).build();
    var handlerInput1 = HandlerInput.builder().withRequestEnvelope(requestEnvelope).build();
    assertTrue(handler.handle(handlerInput1).isPresent());
    assertTrue(handler.handle(handlerInput1).get().toString().contains("Adjuvant hat dich leider nicht verstanden. Bitte wiederhole deine letzte Aussage. "
        + "Für konkretere Hilfestellung sagen Sie Alexa öffne mein Adjuvant."));
  }

  @Test
   void testCanHandleLaunch() {
    var handler = new AdjuvantLaunchRequestHandler();
    var requestEnvelope = RequestEnvelope.builder().withRequest(LaunchRequest.builder().build()).build();
    var handlerInput1 = HandlerInput.builder().withRequestEnvelope(requestEnvelope).withRequest(LaunchRequest.builder().build()).build();
    assertTrue(handler.canHandle(handlerInput1,(LaunchRequest.builder().build())));
  }

  @Test
   void testCannotHandleLaunch() {
    var handler = new AdjuvantLaunchRequestHandler();
    var intent = Intent.builder().withName("GetIntent").build();
    var intentRequest = IntentRequest.builder().withIntent(intent).build();
    var requestEnvelope = RequestEnvelope.builder().withRequest(intentRequest).build();
    var handlerInput = HandlerInput.builder().withRequestEnvelope(requestEnvelope).build();
    assertFalse(handler.canHandle(handlerInput));
  }

  @Test
   void testHandleLaunch() {
    var handler = new AdjuvantLaunchRequestHandler();
    var requestEnvelope = RequestEnvelope.builder().withRequest(LaunchRequest.builder().build()).build();
    var handlerInput1 = HandlerInput.builder().withRequestEnvelope(requestEnvelope).withRequest(LaunchRequest.builder().build()).build();
    assertTrue(handler.handle(handlerInput1, LaunchRequest.builder().build()).isPresent());
    System.out.println(handler.handle(handlerInput1, LaunchRequest.builder().build()));
    assertTrue(handler.handle(handlerInput1, LaunchRequest.builder().build()).get().toString().replaceAll("\\s+","").contains(("Adjuvant ist ein Skill, der Nutzer bei ihrer Terminverwaltung unterstützt. "
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
        + "sagen Sie Alexa ich will eine Kontakperson abspeichern mit mein Adjuvant. ").replaceAll("\\s+","")));
    assertNotNull(handler.handle(handlerInput1, LaunchRequest.builder().build()).get().getCard());
  }

  @Test
   void testCanHandleExit() {
    var handler = new ExitRequestHandler();
    Intent intent = mock(Intent.class);
    when(intent.getName()).thenReturn("AMAZON.StopIntent");
    var intentRequest = IntentRequest.builder().withIntent(intent).build();
    var requestEnvelope = RequestEnvelope.builder().withRequest(intentRequest).build();
    var handlerInput1 = HandlerInput.builder().withRequestEnvelope(requestEnvelope).build();
    assertTrue(handler.canHandle(handlerInput1));
    verify(intent, times(1)).getName();
  }

  @Test
   void testCannotHandleExit() {
    var handler = new ExitRequestHandler();
    var intent = Intent.builder().withName("GetIntent").build();
    var intentRequest = IntentRequest.builder().withIntent(intent).build();
    var requestEnvelope = RequestEnvelope.builder().withRequest(intentRequest).build();
    var handlerInput = HandlerInput.builder().withRequestEnvelope(requestEnvelope).build();
    assertFalse(handler.canHandle(handlerInput));
  }

  @Test
   void testHandleExit() {
    var handler = new ExitRequestHandler();
    Intent intent = mock(Intent.class);
    when(intent.getName()).thenReturn("AMAZON.StopIntent");
    var intentRequest = IntentRequest.builder().withIntent(intent).build();
    var requestEnvelope = RequestEnvelope.builder().withRequest(intentRequest).build();
    var handlerInput1 = HandlerInput.builder().withRequestEnvelope(requestEnvelope).build();
    assertTrue(handler.handle(handlerInput1).isPresent());
    assertTrue(handler.handle(handlerInput1).get().getShouldEndSession());
  }


}

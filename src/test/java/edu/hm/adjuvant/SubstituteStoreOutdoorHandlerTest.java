package edu.hm.adjuvant;

import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.amazon.ask.model.*;
import com.amazon.ask.model.interfaces.system.SystemState;
import com.amazon.ask.model.services.ApiConfiguration;
import com.amazon.ask.model.services.BaseServiceClient;
import com.amazon.ask.model.services.DefaultApiConfiguration;
import com.amazon.ask.model.services.ServiceClientFactory;
import com.amazon.ask.model.services.reminderManagement.*;
import com.amazonaws.services.dynamodbv2.xspec.S;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.util.*;


/**
 * Test zu SubstituteStoreOutdoorHandler.
 *
 * @author Anonymous Student
 */
class SubstituteStoreOutdoorHandlerTest {
    final String intentName = "StoreOutdoorIntent";
    final String name = "Drachen zähmen";
    final String time = "15:00";
    final String cityFrom = "München";
    final String streetFrom = "Lothstraße";
    final String numberFrom = "1";
    final String cityTo = "München";
    final String streetTo = "Lothstraße";
    final String numberTo = "40";
    final String transport = "laufen";


    private Map<String, Slot> getSlots() {
        Slot nameSlot = Slot.builder().withValue(name).build();
        Slot fromCitySlot = Slot.builder().withValue(cityFrom).build();
        Slot fromStreetSlot = Slot.builder().withValue(streetFrom).build();
        Slot fromNumberSlot = Slot.builder().withValue(numberFrom).build();
        Slot toCitySlot = Slot.builder().withValue(cityTo).build();
        Slot toStreetSlot = Slot.builder().withValue(streetTo).build();
        Slot toNumberSlot = Slot.builder().withValue(numberTo).build();
        Slot wayOfTransport = Slot.builder().withValue(transport).build();
        Slot timeSlot = Slot.builder().withValue(time).build();
        Slot freqSlot = Slot.builder().withValue("täglich").build();
        Slot dateSlot = Slot.builder().withValue("2099-01-01").build();
        Map<String, Slot> slots = new HashMap<>();
        slots.put("toStore", nameSlot);
        slots.put("time", timeSlot);
        slots.put("frequency", freqSlot);
        slots.put("date", dateSlot);
        slots.put("fromCity", fromCitySlot);
        slots.put("fromStreet", fromStreetSlot);
        slots.put("fromNumber", fromNumberSlot);
        slots.put("toCity", toCitySlot);
        slots.put("toStreet", toStreetSlot);
        slots.put("toNumber", toNumberSlot);
        slots.put("wayOfTransport", wayOfTransport);
        return slots;
    }

    @Test
    void testCanHandleStoreOutdoorRequest() {
        var handler = new SubstituteStoreOutdoorStreamHandler();
        Intent intent = mock(Intent.class);
        when(intent.getName()).thenReturn(intentName);
        var intentRequest = IntentRequest.builder().withIntent(intent).build();
        var requestEnvelope = RequestEnvelope.builder().withRequest(intentRequest).build();
        var handlerInput1 = HandlerInput.builder().withRequestEnvelope(requestEnvelope).build();
        assertTrue(handler.canHandle(handlerInput1));
        verify(intent, times(1)).getName();
    }

    @Test
    void testCanNtHandleRandomRequest() {
        var handler = new SubstituteStoreOutdoorStreamHandler();
        Intent intent = mock(Intent.class);
        when(intent.getName()).thenReturn("blubb");
        var intentRequest = IntentRequest.builder().withIntent(intent).build();
        var requestEnvelope = RequestEnvelope.builder().withRequest(intentRequest).build();
        var handlerInput1 = HandlerInput.builder().withRequestEnvelope(requestEnvelope).build();
        assertFalse(handler.canHandle(handlerInput1));
    }

    @Test
    void testMissingPermission() {
        var handler = new SubstituteStoreOutdoorStreamHandler();
        String problemPermission =
                "Adjuvant braucht eine Erlaubnis um Ihre Termine verwalten zu können. "
                        + " Bitte bestätigen Sie die Anfrage in Ihrer Alexa App "
                        + "und versuchen Sie nochmal.";
        Map<String, Slot> slots = getSlots();
        Intent intent = mock(Intent.class);
        when(intent.getName()).thenReturn(intentName);
        when(intent.getSlots()).thenReturn(slots);
        User user = mock(User.class);
        when(user.getPermissions()).thenReturn(
                Permissions.builder().withConsentToken("alexa::alerts:reminders:skill:readwrite").build());
        Context context = mock(Context.class);
        when(context.getSystem()).thenReturn(SystemState.builder().withUser(user).build());
        ReminderManagementServiceClient management =
                new ReminderManagementServiceClient(DefaultApiConfiguration.builder()
                        .build());
        ServiceClientFactory factory = mock(ServiceClientFactory.class);
        when(factory.getReminderManagementService()).thenReturn(management);
        var intentRequest = IntentRequest.builder().withIntent(intent).build();
        var requestEnvelope =
                RequestEnvelope.builder().withRequest(intentRequest).withContext(context).build();
        var handlerInput1 =
                HandlerInput.builder().withRequestEnvelope(requestEnvelope).withServiceClientFactory(
                        factory).build();
        assertTrue(handler.canHandle(handlerInput1));
        Optional<Response> response = handler.handle(handlerInput1);
        assertTrue(response.isPresent());
        assertTrue(response.get().toString().contains(problemPermission));
        verify(intent, times(1)).getName();
    }

    @Test
    void firstMissingPermission() {
        var handler = new SubstituteStoreOutdoorStreamHandler();
        String problemPermission =
                "Adjuvant braucht eine Erlaubnis um Ihre Termine verwalten zu können. "
                        + "Bitte bestätigen Sie die Anfrage in Ihrer Alexa App und versuchen Sie erneut.";
        Map<String, Slot> slots = getSlots();
        Intent intent = mock(Intent.class);
        when(intent.getName()).thenReturn(intentName);
        when(intent.getSlots()).thenReturn(slots);
        User user = mock(User.class);

        Context context = mock(Context.class);
        when(context.getSystem()).thenReturn(SystemState.builder().withUser(user).build());
        ReminderManagementServiceClient management =
                new ReminderManagementServiceClient(DefaultApiConfiguration.builder()
                        .build());
        ServiceClientFactory factory = mock(ServiceClientFactory.class);
        when(factory.getReminderManagementService()).thenReturn(management);
        var intentRequest = IntentRequest.builder().withIntent(intent).build();
        var requestEnvelope =
                RequestEnvelope.builder().withRequest(intentRequest).withContext(context).build();
        var handlerInput1 =
                HandlerInput.builder().withRequestEnvelope(requestEnvelope).withServiceClientFactory(
                        factory).build();
        assertTrue(handler.canHandle(handlerInput1));
        Optional<Response> response = handler.handle(handlerInput1);
        assertTrue(response.isPresent());
        assertTrue(response.get().toString().contains(problemPermission));
        verify(intent, times(1)).getName();
    }


    @Test
    void noArgument() {
        var handler = new SubstituteStoreOutdoorStreamHandler();
        String problemInput = "Ich konnte mir leider nichts merken. Bitte einen Termin angeben.";
        Map<String, Slot> slots = getSlots();
        slots.clear();
        Intent intent = mock(Intent.class);
        when(intent.getName()).thenReturn(intentName);
        when(intent.getSlots()).thenReturn(slots);
        Permissions perm = Permissions.builder().withConsentToken("bla").build();
        User user = mock(User.class);
        when(user.getPermissions()).thenReturn(perm);
        Context context = mock(Context.class);
        when(context.getSystem()).thenReturn(SystemState.builder().withUser(user).build());
        ReminderManagementServiceClient management = mock(ReminderManagementServiceClient.class);
        ServiceClientFactory factory = mock(ServiceClientFactory.class);
        when(factory.getReminderManagementService()).thenReturn(management);
        var intentRequest = IntentRequest.builder().withIntent(intent).build();
        var requestEnvelope = RequestEnvelope.builder().withRequest(intentRequest).withContext(context).build();
        var handlerInput1 = HandlerInput.builder().withRequestEnvelope(requestEnvelope).withServiceClientFactory(
                factory).build();
        assertTrue(handler.canHandle(handlerInput1));
        Optional<Response> response = handler.handle(handlerInput1);
        assertTrue(response.isPresent());
        assertTrue(response.get().toString().contains(problemInput));
        verify(intent, times(1)).getName();
    }

    @Test
    void noName() {
        var handler = new SubstituteStoreOutdoorStreamHandler();
        String problemInput = "Ich konnte mir leider nichts merken. Bitte einen Termin angeben.";
        Map<String, Slot> slots = getSlots();
        slots.remove("toStore");
        Intent intent = mock(Intent.class);
        when(intent.getName()).thenReturn(intentName);
        when(intent.getSlots()).thenReturn(slots);
        Permissions perm = Permissions.builder().withConsentToken("bla").build();
        User user = mock(User.class);
        when(user.getPermissions()).thenReturn(perm);
        Context context = mock(Context.class);
        when(context.getSystem()).thenReturn(SystemState.builder().withUser(user).build());
        ReminderManagementServiceClient management = mock(ReminderManagementServiceClient.class);
        ServiceClientFactory factory = mock(ServiceClientFactory.class);
        when(factory.getReminderManagementService()).thenReturn(management);
        var intentRequest = IntentRequest.builder().withIntent(intent).build();
        var requestEnvelope = RequestEnvelope.builder().withRequest(intentRequest).withContext(context).build();
        var handlerInput1 = HandlerInput.builder().withRequestEnvelope(requestEnvelope).withServiceClientFactory(
                factory).build();
        assertTrue(handler.canHandle(handlerInput1));
        Optional<Response> response = handler.handle(handlerInput1);
        assertTrue(response.isPresent());
        assertTrue(response.get().toString().contains(problemInput));
        verify(intent, times(1)).getName();
    }

    @Test
    void wrongAdress() {
        var handler = new SubstituteStoreOutdoorStreamHandler();
        String error="Es ist ein Fehler in der Routenberechnung aufgetreten," +
                " die Adressangabe ist falsch oder Ziel ist gleich Ausgangspunkt" +
                " bitte speichern Sie den Termin erneut.";
        Map<String, Slot> slots = getSlots();
        Slot fromCitySlot = Slot.builder().withValue(".").build();
        slots.replace("fromCity",fromCitySlot);
        Slot fromStreetSlot = Slot.builder().withValue(".").build();
        slots.replace("fromStreet",fromStreetSlot);
        Slot toCitySlot = Slot.builder().withValue(".").build();
        slots.replace("toCity",toCitySlot);
        Slot toStreetSlot = Slot.builder().withValue(".").build();
        slots.replace("toStreet",toStreetSlot);
        Intent intent = mock(Intent.class);
        when(intent.getName()).thenReturn(intentName);
        when(intent.getSlots()).thenReturn(slots);
        Permissions perm = Permissions.builder().withConsentToken("bla").build();
        User user = mock(User.class);
        when(user.getPermissions()).thenReturn(perm);
        Context context = mock(Context.class);
        when(context.getSystem()).thenReturn(SystemState.builder().withUser(user).build());
        ReminderManagementServiceClient management = mock(ReminderManagementServiceClient.class);
        ServiceClientFactory factory = mock(ServiceClientFactory.class);
        when(factory.getReminderManagementService()).thenReturn(management);
        var intentRequest = IntentRequest.builder().withIntent(intent).build();
        var requestEnvelope = RequestEnvelope.builder().withRequest(intentRequest).withContext(context).build();
        var handlerInput1 = HandlerInput.builder().withRequestEnvelope(requestEnvelope).withServiceClientFactory(
                factory).build();
        assertTrue(handler.canHandle(handlerInput1));
        Optional<Response> response = handler.handle(handlerInput1);
        assertTrue(response.isPresent());
        assertTrue(response.get().toString().contains(error));
        verify(intent, times(1)).getName();
    }

    @Test
    void speakKorrect1() {
        String routenInfo = "Folgender Termin wurde abgespeichert: " + name + " um " + time + "."
                + "Ausgangspunkt ist: " + cityFrom + " " + streetFrom + " " + numberFrom
                + " Ihr Zielort ist: " + cityTo + " " + streetTo + " " + numberTo + " mit: " + transport;
        var handler = new SubstituteStoreOutdoorStreamHandler();
        Map<String, Slot> slots = getSlots();
        Intent intent = mock(Intent.class);
        when(intent.getName()).thenReturn(intentName);
        when(intent.getSlots()).thenReturn(slots);
        Permissions perm = Permissions.builder().withConsentToken("bla").build();
        User user = mock(User.class);
        when(user.getPermissions()).thenReturn(perm);
        Context context = mock(Context.class);
        when(context.getSystem()).thenReturn(SystemState.builder().withUser(user).build());
        ReminderManagementServiceClient management = mock(ReminderManagementServiceClient.class);
        ServiceClientFactory factory = mock(ServiceClientFactory.class);
        when(factory.getReminderManagementService()).thenReturn(management);
        var intentRequest = IntentRequest.builder().withIntent(intent).build();
        var requestEnvelope = RequestEnvelope.builder().withRequest(intentRequest).withContext(context).build();
        var handlerInput1 = HandlerInput.builder().withRequestEnvelope(requestEnvelope).withServiceClientFactory(
                factory).build();
        Optional<Response> response = handler.handle(handlerInput1);
        assertTrue(response.isPresent());
        assertTrue(response.get().toString().contains(routenInfo));
    }

    @Test
    void speakKorrect2() {
        String bike = "fahrrad";
        Slot wayOfTransport = Slot.builder().withValue(bike).build();
        String routenInfo = "Folgender Termin wurde abgespeichert: " + name + " um " + time + "."
                + "Ausgangspunkt ist: " + cityFrom + " " + streetFrom + " " + numberFrom
                + " Ihr Zielort ist: " + cityTo + " " + streetTo + " " + numberTo + " mit: " + bike;
        var handler = new SubstituteStoreOutdoorStreamHandler();
        Map<String, Slot> slots = getSlots();
        slots.replace("wayOfTransport", wayOfTransport);
        Intent intent = mock(Intent.class);
        when(intent.getName()).thenReturn(intentName);
        when(intent.getSlots()).thenReturn(slots);
        Permissions perm = Permissions.builder().withConsentToken("bla").build();
        User user = mock(User.class);
        when(user.getPermissions()).thenReturn(perm);
        Context context = mock(Context.class);
        when(context.getSystem()).thenReturn(SystemState.builder().withUser(user).build());
        ReminderManagementServiceClient management = mock(ReminderManagementServiceClient.class);
        ServiceClientFactory factory = mock(ServiceClientFactory.class);
        when(factory.getReminderManagementService()).thenReturn(management);
        var intentRequest = IntentRequest.builder().withIntent(intent).build();
        var requestEnvelope = RequestEnvelope.builder().withRequest(intentRequest).withContext(context).build();
        var handlerInput1 = HandlerInput.builder().withRequestEnvelope(requestEnvelope).withServiceClientFactory(
                factory).build();
        Optional<Response> response = handler.handle(handlerInput1);
        assertTrue(response.isPresent());
        assertTrue(response.get().toString().contains(routenInfo));
    }

    @Test
    void weekly() {
        String monday = "montag";
        Slot mondaySlot = Slot.builder().withValue(monday).build();
        String routenInfo = "Folgender Termin wurde abgespeichert: " + name + " um " + time + "."
                + "Ausgangspunkt ist: " + cityFrom + " " + streetFrom + " " + numberFrom
                + " Ihr Zielort ist: " + cityTo + " " + streetTo + " " + numberTo + " mit: " + transport;
        var handler = new SubstituteStoreOutdoorStreamHandler();
        Map<String, Slot> slots = getSlots();
        slots.replace("frequency", mondaySlot);
        Intent intent = mock(Intent.class);
        when(intent.getName()).thenReturn(intentName);
        when(intent.getSlots()).thenReturn(slots);
        Permissions perm = Permissions.builder().withConsentToken("bla").build();
        User user = mock(User.class);
        when(user.getPermissions()).thenReturn(perm);
        Context context = mock(Context.class);
        when(context.getSystem()).thenReturn(SystemState.builder().withUser(user).build());
        ReminderManagementServiceClient management = mock(ReminderManagementServiceClient.class);
        ServiceClientFactory factory = mock(ServiceClientFactory.class);
        when(factory.getReminderManagementService()).thenReturn(management);
        var intentRequest = IntentRequest.builder().withIntent(intent).build();
        var requestEnvelope = RequestEnvelope.builder().withRequest(intentRequest).withContext(context).build();
        var handlerInput1 = HandlerInput.builder().withRequestEnvelope(requestEnvelope).withServiceClientFactory(
                factory).build();
        Optional<Response> response = handler.handle(handlerInput1);
        assertTrue(response.isPresent());
        assertTrue(response.get().toString().contains(routenInfo));
    }

    @Test
    void once() {
        String goAhead = "weiter";
        Slot goAheadSlot = Slot.builder().withValue(goAhead).build();
        String routenInfo = "Folgender Termin wurde abgespeichert: " + name + " um " + time + "."
                + "Ausgangspunkt ist: " + cityFrom + " " + streetFrom + " " + numberFrom
                + " Ihr Zielort ist: " + cityTo + " " + streetTo + " " + numberTo + " mit: " + transport;
        var handler = new SubstituteStoreOutdoorStreamHandler();
        Map<String, Slot> slots = getSlots();
        slots.replace("frequency", goAheadSlot);
        Intent intent = mock(Intent.class);
        when(intent.getName()).thenReturn(intentName);
        when(intent.getSlots()).thenReturn(slots);
        Permissions perm = Permissions.builder().withConsentToken("bla").build();
        User user = mock(User.class);
        when(user.getPermissions()).thenReturn(perm);
        Context context = mock(Context.class);
        when(context.getSystem()).thenReturn(SystemState.builder().withUser(user).build());
        ReminderManagementServiceClient management = mock(ReminderManagementServiceClient.class);
        ServiceClientFactory factory = mock(ServiceClientFactory.class);
        when(factory.getReminderManagementService()).thenReturn(management);
        var intentRequest = IntentRequest.builder().withIntent(intent).build();
        var requestEnvelope = RequestEnvelope.builder().withRequest(intentRequest).withContext(context).build();
        var handlerInput1 = HandlerInput.builder().withRequestEnvelope(requestEnvelope).withServiceClientFactory(
                factory).build();
        Optional<Response> response = handler.handle(handlerInput1);
        assertTrue(response.isPresent());
        assertTrue(response.get().toString().contains(routenInfo));
    }

    @Test
    void wrongDateOnce() {
        Slot goAheadSlot = Slot.builder().withValue("weiter").build();
        Slot dateSlot = Slot.builder().withValue("2009-01-01").build();
        String error = "Termin liegt in der Vergangenheit oder Ziel kann nicht mehr erreicht werden";
        var handler = new SubstituteStoreOutdoorStreamHandler();
        Map<String, Slot> slots = getSlots();
        slots.replace("date", dateSlot);
        slots.replace("frequency", goAheadSlot);
        Intent intent = mock(Intent.class);
        when(intent.getName()).thenReturn(intentName);
        when(intent.getSlots()).thenReturn(slots);
        Permissions perm = Permissions.builder().withConsentToken("bla").build();
        User user = mock(User.class);
        when(user.getPermissions()).thenReturn(perm);
        Context context = mock(Context.class);
        when(context.getSystem()).thenReturn(SystemState.builder().withUser(user).build());
        ReminderManagementServiceClient management = mock(ReminderManagementServiceClient.class);
        ServiceClientFactory factory = mock(ServiceClientFactory.class);
        when(factory.getReminderManagementService()).thenReturn(management);
        var intentRequest = IntentRequest.builder().withIntent(intent).build();
        var requestEnvelope = RequestEnvelope.builder().withRequest(intentRequest).withContext(context).build();
        var handlerInput1 = HandlerInput.builder().withRequestEnvelope(requestEnvelope).withServiceClientFactory(
                factory).build();
        Optional<Response> response = handler.handle(handlerInput1);
        assertTrue(response.isPresent());
        assertTrue(response.get().toString().contains(error));
    }


    @Test
    void helpGetRoute() {
        String compare=new Output().generateSpeach("1","2","3",
                "4","5");
        String functionCall="Wollen Sie die Routeninformationen erhalten sagen Sie bitte:"
                + " Führe eine Routenberechnung durch mit mein Adjuvant";
        Assert.assertTrue(compare.contains(functionCall));
        Assert.assertTrue(compare.contains("1"));
        Assert.assertTrue(compare.contains("2"));
        Assert.assertTrue(compare.contains("3"));
        Assert.assertTrue(compare.contains("4"));
        Assert.assertTrue(compare.contains("5"));
    }


}

package edu.hm.adjuvant;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.amazon.ask.model.Context;
import com.amazon.ask.model.Intent;
import com.amazon.ask.model.IntentRequest;
import com.amazon.ask.model.RequestEnvelope;
import com.amazon.ask.model.Slot;
import com.amazon.ask.model.User;
import com.amazon.ask.model.interfaces.system.SystemState;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.TableCollection;
import com.amazonaws.services.dynamodbv2.document.internal.IteratorSupport;
import com.amazonaws.services.dynamodbv2.model.ListTablesResult;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.Mockito;
import org.slf4j.Marker;

/**
 * Tests für die Klasse SetContactRequestHandler.
 *
 * @author Anonymous Student
 */
 class StoreContactRequestTest {
  @Test
   void testCanHandleStoreContactIntent() {
    var handler = new SetContactRequestHandler();
    Intent intent = mock(Intent.class);
    when(intent.getName()).thenReturn("StoreContactIntent");
    var intentRequest = IntentRequest.builder().withIntent(intent).build();
    var requestEnvelope = RequestEnvelope.builder().withRequest(intentRequest).build();
    var handlerInput1 = HandlerInput.builder().withRequestEnvelope(requestEnvelope).build();
    assertTrue(handler.canHandle(handlerInput1));
    verify(intent, times(1)).getName();
  }

  @Test
   void testCannotHandleStoreContactIntent() {
    var handler = new SetContactRequestHandler();
    var intent = Intent.builder().withName("GetIntent").build();
    var intentRequest = IntentRequest.builder().withIntent(intent).build();
    var requestEnvelope = RequestEnvelope.builder().withRequest(intentRequest).build();
    var handlerInput = HandlerInput.builder().withRequestEnvelope(requestEnvelope).build();
    assertFalse(handler.canHandle(handlerInput));
  }
 @Test void storeEMailToK(){
  var handler = Mockito.spy(new SetContactRequestHandler());
  Slot first= Slot.builder().withValue("Anton Berta Cäsar Cesar Dora Emil Friedrich").build();
  Slot second= Slot.builder().withValue("Gustav heinrich Ida Julius kaufmann Kaufman").build();
  Map<String,Slot> slots=new HashMap<>();
  slots.put("firstPart",first);
  slots.put("secondPart",second);
  Intent intent = mock(Intent.class);
  when(intent.getName()).thenReturn("StoreContactIntent");
  when(intent.getSlots()).thenReturn(slots);
  User user=mock(User.class);
  Context context=mock(Context.class);
  when(context.getSystem()).thenReturn(SystemState.builder().withUser(user).build());
  var client =mock(AmazonDynamoDB.class,Mockito.RETURNS_DEEP_STUBS);
  when(client.listTables()).thenReturn(mock(ListTablesResult.class));
  when(handler.makeClient()).thenReturn(client);
  var dynamo=mock(DynamoDB.class,Mockito.RETURNS_DEEP_STUBS);
  var tables=mock(TableCollection.class);
  when(dynamo.listTables()).thenReturn(tables);
  when(handler.makeDynamo(client)).thenReturn(dynamo);
  var iterator=mock(IteratorSupport.class);
  when(tables.iterator()).thenReturn(iterator);
  when(iterator.hasNext()).thenReturn(false,false);
  when(iterator.next()).thenReturn(mock(Table.class));
  var intentRequest = IntentRequest.builder().withIntent(intent).build();
  var requestEnvelope = RequestEnvelope.builder().withRequest(intentRequest).withContext(context).build();
  var handlerInput = HandlerInput.builder().withRequestEnvelope(requestEnvelope).build();
  assertTrue(handler.handle(handlerInput).isPresent());
  assertTrue(handler.handle(handlerInput).get().getCard().toString().contains("abccdef@ghijkk"));
 }
 @Test void storeEMailToV(){
  var handler = Mockito.spy(new SetContactRequestHandler());
  Slot first= Slot.builder().withValue("ludwig marta martha Mata Nordpol oto Otto paula moje").build();
  Slot second= Slot.builder().withValue("quelle richard Samuel theodor Ulrich viktor").build();
  Map<String,Slot> slots=new HashMap<>();
  slots.put("firstPart",first);
  slots.put("secondPart",second);
  Intent intent = mock(Intent.class);
  when(intent.getName()).thenReturn("StoreContactIntent");
  when(intent.getSlots()).thenReturn(slots);
  User user=mock(User.class);
  Context context=mock(Context.class);
  when(context.getSystem()).thenReturn(SystemState.builder().withUser(user).build());
  var client =mock(AmazonDynamoDB.class,Mockito.RETURNS_DEEP_STUBS);
  when(client.listTables()).thenReturn(mock(ListTablesResult.class));
  when(handler.makeClient()).thenReturn(client);
  var dynamo=mock(DynamoDB.class,Mockito.RETURNS_DEEP_STUBS);
  var tables=mock(TableCollection.class);
  when(dynamo.listTables()).thenReturn(tables);
  when(handler.makeDynamo(client)).thenReturn(dynamo);
  var iterator=mock(IteratorSupport.class);
  when(tables.iterator()).thenReturn(iterator);
  when(iterator.hasNext()).thenReturn(false,false);
  when(iterator.next()).thenReturn(mock(Table.class));
  var intentRequest = IntentRequest.builder().withIntent(intent).build();
  var requestEnvelope = RequestEnvelope.builder().withRequest(intentRequest).withContext(context).build();
  var handlerInput = HandlerInput.builder().withRequestEnvelope(requestEnvelope).build();
  assertTrue(handler.handle(handlerInput).isPresent());
  assertTrue(handler.handle(handlerInput).get().getCard().toString().contains("lmmmnoop@qrstuv"));
 }

 @Test void storeEMailToY(){
  var handler = Mockito.spy(new SetContactRequestHandler());
  Slot first= Slot.builder().withValue("wilhelm xanthippe xanthipe xantippe xantipe").build();
  Slot second= Slot.builder().withValue("santippe santipe santhippe santhipe ypsilon bla").build();
  Map<String,Slot> slots=new HashMap<>();
  slots.put("firstPart",first);
  slots.put("secondPart",second);
  Intent intent = mock(Intent.class);
  when(intent.getName()).thenReturn("StoreContactIntent");
  when(intent.getSlots()).thenReturn(slots);
  User user=mock(User.class);
  Context context=mock(Context.class);
  when(context.getSystem()).thenReturn(SystemState.builder().withUser(user).build());
  var client =mock(AmazonDynamoDB.class,Mockito.RETURNS_DEEP_STUBS);
  when(client.listTables()).thenReturn(mock(ListTablesResult.class));
  when(handler.makeClient()).thenReturn(client);
  var dynamo=mock(DynamoDB.class,Mockito.RETURNS_DEEP_STUBS);
  var tables=mock(TableCollection.class);
  when(dynamo.listTables()).thenReturn(tables);
  when(handler.makeDynamo(client)).thenReturn(dynamo);
  var iterator=mock(IteratorSupport.class);
  when(tables.iterator()).thenReturn(iterator);
  when(iterator.hasNext()).thenReturn(false,false);
  when(iterator.next()).thenReturn(mock(Table.class));
  var intentRequest = IntentRequest.builder().withIntent(intent).build();
  var requestEnvelope = RequestEnvelope.builder().withRequest(intentRequest).withContext(context).build();
  var handlerInput = HandlerInput.builder().withRequestEnvelope(requestEnvelope).build();
  assertTrue(handler.handle(handlerInput).isPresent());
  assertTrue(handler.handle(handlerInput).get().getCard().toString().contains("wxxxx@xxxxy"));
 }

 @Test void storeEMailToEnd() {
  var handler = Mockito.spy(new SetContactRequestHandler());
  Slot first= Slot.builder().withValue("zacharias zacarias y. null eins zwei drei nani").build();
  Slot second= Slot.builder().withValue("vier fünf sechs sieben acht neun punkt automata").build();
  Map<String,Slot> slots=new HashMap<>();
  slots.put("firstPart",first);
  slots.put("secondPart",second);
  Intent intent = mock(Intent.class);
  when(intent.getName()).thenReturn("StoreContactIntent");
  when(intent.getSlots()).thenReturn(slots);
  User user=mock(User.class);
  Context context=mock(Context.class);
  when(context.getSystem()).thenReturn(SystemState.builder().withUser(user).build());
  var client =mock(AmazonDynamoDB.class,Mockito.RETURNS_DEEP_STUBS);
  when(client.listTables()).thenReturn(mock(ListTablesResult.class));
  when(handler.makeClient()).thenReturn(client);
  var dynamo=mock(DynamoDB.class,Mockito.RETURNS_DEEP_STUBS);
  var tables=mock(TableCollection.class);
  when(dynamo.listTables()).thenReturn(tables);
  when(handler.makeDynamo(client)).thenReturn(dynamo);
  var iterator=mock(IteratorSupport.class);
  when(tables.iterator()).thenReturn(iterator);
  when(iterator.hasNext()).thenReturn(false,false);
  when(iterator.next()).thenReturn(mock(Table.class));
  var intentRequest = IntentRequest.builder().withIntent(intent).build();
  var requestEnvelope = RequestEnvelope.builder().withRequest(intentRequest).withContext(context).build();
  var handlerInput = HandlerInput.builder().withRequestEnvelope(requestEnvelope).build();
  assertTrue(handler.handle(handlerInput).isPresent());
  assertTrue(handler.handle(handlerInput).get().getCard().toString().replaceAll("\\s+","").contains("zzy0123@456789.om".replaceAll("\\s+","")));
 }


}

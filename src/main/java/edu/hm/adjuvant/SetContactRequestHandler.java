package edu.hm.adjuvant;

import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.amazon.ask.dispatcher.request.handler.RequestHandler;
import com.amazon.ask.model.IntentRequest;
import com.amazon.ask.model.Response;
import com.amazon.ask.model.Slot;
import com.amazon.ask.request.Predicates;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.TableCollection;
import com.amazonaws.services.dynamodbv2.model.AttributeDefinition;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.DeleteItemRequest;
import com.amazonaws.services.dynamodbv2.model.KeySchemaElement;
import com.amazonaws.services.dynamodbv2.model.KeyType;
import com.amazonaws.services.dynamodbv2.model.ListTablesResult;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import com.amazonaws.services.dynamodbv2.model.ScalarAttributeType;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Eintragen der Kontaktperson.
 *
 * @author Anonymous Student
 */
class SetContactRequestHandler implements RequestHandler {
  private static final String DATA = "AdjuvantKontakte";
  private static final String ATTRIBUTE = "EMail";

  @Override
  public boolean canHandle(HandlerInput handlerInput) {
    return handlerInput.matches(Predicates.intentName("StoreContactIntent"));
  }

  @Override
  public Optional<Response> handle(HandlerInput handlerInput) {
    AmazonDynamoDB client = makeClient();
    DynamoDB dynamoDb = makeDynamo(client);
    TableCollection<ListTablesResult> tables = dynamoDb.listTables();
    var iterator = tables.iterator();
    boolean exist = false;
    while (iterator.hasNext()) {
      Table table = iterator.next();
      if (table.getTableName().equals(DATA)) {
        exist = true;
      }
    }
    if (!exist) {
      ProvisionedThroughput mode =
          new ProvisionedThroughput().withReadCapacityUnits(Long.parseLong("1"))
              .withWriteCapacityUnits(Long.parseLong("1"));
      AttributeDefinition att1 = new AttributeDefinition()
          .withAttributeName(ATTRIBUTE).withAttributeType(ScalarAttributeType.S);
      KeySchemaElement key1 =
          new KeySchemaElement().withKeyType(KeyType.HASH).withAttributeName(ATTRIBUTE);
      CreateTableRequest request = new CreateTableRequest().withTableName(DATA)
          .withKeySchema(key1).withAttributeDefinitions(att1)
          .withProvisionedThroughput(mode);
      Table table = dynamoDb.createTable(request);
      try {
        table.waitForActive();
      } catch (InterruptedException ignored) {
        ignored.hashCode();
        Thread.currentThread().interrupt();
      }
    }
    IntentRequest intent = (IntentRequest) handlerInput.getRequestEnvelope().getRequest();
    Slot first = intent.getIntent().getSlots().get("firstPart");
    Slot second = intent.getIntent().getSlots().get("secondPart");
    String firstPart = first.getValue();
    String secondPart = second.getValue();
    String email = createEmail(firstPart, secondPart);
    Item item = new Item().with(ATTRIBUTE, email);

    ScanRequest scanRequest = new ScanRequest().withTableName(DATA).withLimit(1);
    try {
      AttributeValue value = client.scan(scanRequest).getItems().get(0).get(ATTRIBUTE);
      Map<String, AttributeValue> key = new HashMap<>();
      key.put(ATTRIBUTE, value);
      client.deleteItem((new DeleteItemRequest()).withTableName(DATA).withKey(key));
    } catch (NullPointerException | IndexOutOfBoundsException ignored) {
      ignored.hashCode();
    }

    dynamoDb.getTable(DATA).putItem(item);
    return handlerInput.getResponseBuilder().withSpeech("Die Kontaktperson wurde hinzugefügt. "
        + "Auf Ihrem Echo Gerät oder auf der Amazon Internet Seite wurde eine Karte ausgegeben,"
        + " wo die angegebene EMail Adresse gezeigt wird. "
        + "Falls die EMail Adresse falsch abgespeichert wurde, "
        + "versuchen Sie es nochmal indem Sie erneut sagen "
        + "Alexa füge eine Kontaktperson hinzu mit mein Adjuvant. "
        + "Sie können nur eine Kontakperson abspeichern, "
        + "also wird die alte EMail Adresse automatisch ersetzt. "
        + "Wenn Sie Hilfe bei der Angabe brauchen, "
        + "sagen Sie Alexa ich will eine Kontaktperson abspeichern mit mein Adjuvant. "
        + "Um Ihre Termine zu verschicken sagen Sie "
        + "Alexa schicke alle meine Termine mit mein Adjuvant.")
        .withSimpleCard("Kontaktperson", email).build();
  }

  public DynamoDB makeDynamo(AmazonDynamoDB client) {
    return new DynamoDB(client);
  }

  public AmazonDynamoDB makeClient() {
    return AmazonDynamoDBClientBuilder.standard().withRegion(Regions.EU_CENTRAL_1).build();
  }

  private Map<String, String> createMap() {
    Map<String, String> alphabet = new HashMap<>();
    alphabet.put("anton", "a");
    alphabet.put("berta", "b");
    alphabet.put("cäsar", "c");
    alphabet.put("cesar", "c");
    alphabet.put("dora", "d");
    alphabet.put("emil", "e");
    alphabet.put("friedrich", "f");
    alphabet.put("gustav", "g");
    alphabet.put("heinrich", "h");
    alphabet.put("ida", "i");
    alphabet.put("julius", "j");
    alphabet.put("kaufmann", "k");
    alphabet.put("kaufman", "k");
    alphabet.put("ludwig", "l");
    alphabet.put("marta", "m");
    alphabet.put("martha", "m");
    alphabet.put("mata", "m");
    alphabet.put("nordpol", "n");
    alphabet.put("otto", "o");
    alphabet.put("oto", "o");
    alphabet.put("paula", "p");
    alphabet.put("quelle", "q");
    alphabet.put("richard", "r");
    alphabet.put("samuel", "s");
    alphabet.put("theodor", "t");
    alphabet.put("ulrich", "u");
    alphabet.put("viktor", "v");
    alphabet.put("wilhelm", "w");
    alphabet.put("xanthippe", "x");
    alphabet.put("xanthipe", "x");
    alphabet.put("xantippe", "x");
    alphabet.put("xantipe", "x");
    alphabet.put("santhippe", "x");
    alphabet.put("santhipe", "x");
    alphabet.put("santippe", "x");
    alphabet.put("santipe", "x");
    alphabet.put("ypsilon", "y");
    alphabet.put("y.", "y");
    alphabet.put("zacharias", "z");
    alphabet.put("zacarias", "z");
    alphabet.put("automata", "om");
    alphabet.put("punkt", ".");
    alphabet.put("null", "0");
    alphabet.put("eins", "1");
    alphabet.put("zwei", "2");
    alphabet.put("drei", "3");
    alphabet.put("vier", "4");
    alphabet.put("fünf", "5");
    alphabet.put("sechs", "6");
    alphabet.put("sieben", "7");
    alphabet.put("acht", "8");
    alphabet.put("neun", "9");
    return alphabet;
  }

  private String createEmail(String first, String second) {
    String email = "";
    Map<String, String> alphabet = createMap();
    String[] firstLetters = first.split(" ");
    email = email + part(firstLetters, alphabet);
    email = email + "@";
    email = email + part(second.split(" "), alphabet);
    return email;
  }

  private String part(String[] letters, Map<String, String> alphabet) {
    StringBuilder part = new StringBuilder();
    for (String letter : letters) {
      if (alphabet.containsKey(letter.toLowerCase())) {
        part.append(alphabet.get(letter.toLowerCase()));
      }
    }
    return part.toString();
  }
}

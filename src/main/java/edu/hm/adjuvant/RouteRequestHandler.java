package edu.hm.adjuvant;

import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.amazon.ask.dispatcher.request.handler.RequestHandler;
import com.amazon.ask.model.Response;
import com.amazon.ask.request.Predicates;
import edu.hm.adjuvant.helper.AddressHelper;
import edu.hm.adjuvant.helper.EmailHelper;
import edu.hm.adjuvant.helper.PermissionHelper;
import edu.hm.adjuvant.helper.ProfileHelper;
import edu.hm.adjuvant.helper.ResponseHelper;
import edu.hm.adjuvant.helper.RouteHelper;
import edu.hm.adjuvant.helper.TransportHelper;
import edu.hm.adjuvant.utils.Permission;
import edu.hm.adjuvant.utils.ResponseMessage;
import java.util.Objects;
import java.util.Optional;
import zone.stefan.dev.geocode.GeoCode;
import zone.stefan.dev.geocode.model.endpoint.RoutingEndpoint;

/**
 * RouteRequestHandler.java
 *
 * @author Stefan Kühnel, stefan.kuehnel@hm.edu
 * @version 2020-05-01
 */

public class RouteRequestHandler implements RequestHandler {
  @Override
  public boolean canHandle(final HandlerInput handlerInput) {
    return handlerInput.matches(Predicates.intentName("RouteRequestIntent"));
  }

  @Override
  public Optional<Response> handle(final HandlerInput handlerInput) {
    // Test, ob der Benutzer mindestens eine Berechtigung gesetzt hat.
    if (!PermissionHelper.hasPermission(handlerInput)) {
      return PermissionHelper.request(Permission.EMAIL_PERMISSION);
    }

    try {
      // Test, ob die Berechtigung zur Abfrage der Mail Adresse vorhanden ist.
      if (!ProfileHelper.hasProfileEmail(handlerInput)) {
        return PermissionHelper.request(Permission.EMAIL_PERMISSION);
      }

      // Definition der notwendigen Routeninformationen.
      final String origin = AddressHelper.getOrigin(handlerInput);
      final String destination = AddressHelper.getDestination(handlerInput);
      final String transport = TransportHelper.getTransport(handlerInput);

      // Abfrage der Daten zur Route.
      final RoutingEndpoint routingEndpoint = GeoCode.getRoute(origin, destination, transport);

      // Bei der Routenberechnung ist ein Problem aufgetreten.
      Objects.requireNonNull(routingEndpoint);

      // Vorbereitungen zum Senden der E-Mail.
      final String to = ProfileHelper.getProfileEmail(handlerInput);
      final String from = "projekt-adjuvant_amazon-developer@sma.9bn.de";
      final String subject = "Routenberechnung mit Adjuvant";
      final String text = RouteHelper.createText(routingEndpoint);
      final String html = RouteHelper.createHtml(routingEndpoint);

      // Absenden der E-Mail.
      return EmailHelper.send(to, from, subject, text, html);
    } catch (NullPointerException npe) {
      return ResponseHelper.say(ResponseMessage.ROUTE_CALCULATION_FAILED);
    }
  }
}
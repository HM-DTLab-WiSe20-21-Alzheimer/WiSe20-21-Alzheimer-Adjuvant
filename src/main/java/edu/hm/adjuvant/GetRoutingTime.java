package edu.hm.adjuvant;

import java.util.Objects;
import zone.stefan.dev.geocode.GeoCode;
import zone.stefan.dev.geocode.model.endpoint.RoutingEndpoint;

/**
 * Hilfsklasse für Berechnung der Fahrtzeit.
 *
 * @author Anonymous Student
 */
class GetRoutingTime {

  private String getTransport(String transport) {
    switch (transport.toLowerCase()) {
      case "auto":
        return "drive";
      case "laufen":
        return "walk";
      case "fahrrad":
        return "bicycle";
      default:
        return "transit";
    }
  }

  /**
   * Berechnung der Fahrtzeit.
   *
   * @param from      Start
   * @param to        Ziel
   * @param transport Verkehrsmittel
   * @return Sekunden.
   */
  public int getSeconds(String from, String to, String transport) {
    final String transportItem = getTransport(transport);
    final int delay = 900;
    try {
      final RoutingEndpoint routingEndpoint = GeoCode.getRoute(
          from,
          to,
          transportItem
      );
      Objects.requireNonNull(routingEndpoint);
      return routingEndpoint.getSummary().getDuration().getSeconds() + delay;

    } catch (NullPointerException e) {
      return -1;
    }
  }
}

package edu.hm.adjuvant.helper;

import static j2html.TagCreator.a;
import static j2html.TagCreator.b;
import static j2html.TagCreator.body;
import static j2html.TagCreator.br;
import static j2html.TagCreator.div;
import static j2html.TagCreator.h1;
import static j2html.TagCreator.hr;
import static j2html.TagCreator.i;
import static j2html.TagCreator.img;
import static j2html.TagCreator.join;
import static j2html.TagCreator.li;
import static j2html.TagCreator.ol;
import static j2html.TagCreator.p;
import static j2html.TagCreator.small;

import j2html.tags.ContainerTag;
import j2html.tags.UnescapedText;
import java.util.List;
import java.util.stream.Collectors;
import zone.stefan.dev.geocode.model.Step;
import zone.stefan.dev.geocode.model.Summary;
import zone.stefan.dev.geocode.model.endpoint.RoutingEndpoint;

/**
 * RouteHelper.java
 *
 * @author Stefan Kühnel, stefan.kuehnel@hm.edu
 * @version 2021-1-11
 */

public class RouteHelper {
  // Privater Konstruktor.
  private RouteHelper() {}

  /**
   * Erstellt eine textbasierte Zusammenfassung mit Informationen über eine Route.
   *
   * @param routingEndpoint Eine Instanz des GeoCode RoutingEndpoints.
   * @return Zusammenfassung als reiner Text.
   */
  public static String createText(RoutingEndpoint routingEndpoint) {
    final String headline = "** WEGBESCHREIBUNG";

    final String divider = "------------------------------------------------------------ \r\n";

    final String origin = "Startort: " + routingEndpoint.getSummary().getLocation().getOrigin();
    final String destination =
        "Zielort: " + routingEndpoint.getSummary().getLocation().getDestination();
    final String description = "Zusammenfassung: " + routingEndpoint.getSummary().getText();

    final String route = "- BERECHNETE ROUTE: \r\n"
        + routingEndpoint.getRoute().stream()
        .map(Step::getInstruction)
        .collect(Collectors.joining("\r\n"));

    final String disclaimer = "- Haftungsausschluss:\r\n "
        + "Diese Wegbeschreibung dient ausschließlich Informationszwecken. "
        + "Es werden keine Garantien in Bezug auf Vollständigkeit und "
        + "Genauigkeit übernommen. Bauarbeiten, Verkehrsaufkommen oder "
        + "sonstige Ereignisse können dazu führen, dass die tatsächlichen "
        + "Bedingungen von diesen Ergebnissen abweichen. "
        + "Karten- und Verkehrsdaten © 2021 Here Technologies.";

    final String copyright = "© 2021 Stefan Kühnel, Alle Rechte vorbehalten, "
        + "soweit nicht ausdrücklich anders vermerkt.";

    return
        headline + "\r\n"
            + divider
            + origin + "\r\n"
            + destination + "\r\n"
            + description + "\r\n"
            + divider
            + route + "\r\n"
            + divider
            + disclaimer + "\r\n"
            + copyright;
  }

  /**
   * Erstellt eine HTML basierte Zusammenfassung mit Informationen über eine Route.
   *
   * @param routingEndpoint Eine Instanz des GeoCode RoutingEndpoints.
   * @return Zusammenfassung als HTML QuellCode.
   */
  public static String createHtml(RoutingEndpoint routingEndpoint) {
    // Zusammenfassung der Route.
    final Summary summary = routingEndpoint.getSummary();

    // Informationen zur Route.
    final List<Step> route = routingEndpoint.getRoute();

    final ContainerTag body = body(
        getTemplate(summary, route)
    );

    return body.render();
  }

  /**
   * Ausgabe des Templates der E-Mail.
   *
   * @param summary Zusammenfassung des GeoCode RoutingEndpoints.
   * @param route   Liste mit einzelnen Schritten der Route.
   * @return Template der E-Mail.
   */
  private static ContainerTag getTemplate(Summary summary, List<Step> route) {
    final ContainerTag headlineItem = getHeadline();
    final ContainerTag summaryItem = getSummary(summary);
    final ContainerTag routeItem = getRoute(route);
    final ContainerTag disclaimerItem = getDisclaimer();
    final ContainerTag copyrightItem = getCopyright();
    final ContainerTag poweredByItem = getPoweredBy();

    final ContainerTag template = div(
        headlineItem,
        summaryItem,
        hr().withStyle("border-style: dashed;"),
        routeItem,
        hr().withStyle("border-style: dashed;"),
        disclaimerItem,
        copyrightItem,
        poweredByItem
    );

    return template.withStyle("font-family: Arial;");
  }

  /**
   * Ausgabe der Ueberschrift.
   *
   * @return Ueberschrift.
   */
  private static ContainerTag getHeadline() {
    return h1("Wegbeschreibung");
  }

  /**
   * Ausgabe der Zusammenfassung zur Route.
   *
   * @param summary Zusammenfassung des GeoCode RoutingEndpoints.
   * @return Routenzusammenfassung.
   */
  private static ContainerTag getSummary(Summary summary) {
    // Angaben zum Startort.
    final UnescapedText origin = join(
        b("Startort: "),
        summary.getLocation().getOrigin()
    );

    // Angaben zum Zielort.
    final UnescapedText destination = join(
        b("Zielort: "),
        summary.getLocation().getDestination()
    );

    // Zusammenfassung der Route.
    final UnescapedText description = join(
        b("Zusammenfassung: "),
        summary.getText()
    );

    return div(
        p(origin),
        p(destination),
        p(description)
    );
  }

  /**
   * Ausgabe der Routeninformationen.
   *
   * @param route Liste mit einzelnen Schritten der Route.
   * @return Routeninformationen.
   */
  private static ContainerTag getRoute(List<Step> route) {
    final String timeStyle =
        "padding: 5px; background-color: #eee; border-radius: 5px; display: inline-block;";

    return div(
        b("Berechnete Route:"),
        ol(
            route.stream().map(
                step -> li(
                    p(
                        join(
                            step.getInstruction(),
                            small(
                                i(step.getDuration())
                                    .withStyle(timeStyle)
                            )
                        )
                    )
                )
            ).toArray(ContainerTag[]::new)
        )
    );
  }

  /**
   * Ausgabe von PoweredBy Informationen.
   *
   * @return PoweredBy Badge.
   */
  private static ContainerTag getPoweredBy() {
    // Definition der CSS Spezifikationen.
    final String textStyle = "margin-left: 10px; color: black;";
    final String buttonStyle = "display: flex;"
        + "align-items: center; "
        + "text-decoration: none; "
        + "background-color: rgb(248, 249, 250); "
        + "padding: 10px 5px; "
        + "border-radius: 5px;"
        + "width: max-content;";

    // Definition der Inhalte.
    final String image = "https://geocode.dev.stefan.zone/logo.svg";
    final String url = "https://geocode.dev.stefan.zone";
    final String text = "Unterstützt durch GeoCode.";

    return a()
        .withHref(url)
        .withTarget("_blank")
        .withStyle(buttonStyle)
        .with(
            join(
                img()
                    .withSrc(image)
                    .attr("width", "25"),
                small(text)
                    .withStyle(textStyle)
            )
        );
  }

  /**
   * Ausgabe des Haftungsausschlusses.
   *
   * @return Haftungssausschluss.
   */
  private static ContainerTag getDisclaimer() {
    final String headline = "Haftungsausschluss";
    final String text = "Diese Wegbeschreibung dient ausschließlich Informationszwecken. "
        + "Es werden keine Garantien in Bezug auf Vollständigkeit und "
        + "Genauigkeit übernommen. Bauarbeiten, Verkehrsaufkommen oder "
        + "sonstige Ereignisse können dazu führen, dass die tatsächlichen "
        + "Bedingungen von diesen Ergebnissen abweichen. "
        + "Karten- und Verkehrsdaten © 2021 Here Technologies.";

    return div(
        b(headline),
        br(),
        small(text)
    );
  }

  /**
   * Ausgabe der Informationen zum Urheberrecht.
   *
   * @return Urheberrecht.
   */
  private static ContainerTag getCopyright() {
    final String copyrightProfile =
        "© 2021 Stefan Kühnel, Alle Rechte vorbehalten, soweit nicht ausdrücklich anders vermerkt.";

    return p(small(copyrightProfile));
  }
}

package edu.hm.adjuvant;

import com.amazon.ask.SkillStreamHandler;
import com.amazon.ask.Skills;

/**
 * Hauptklasse. StreamHandler.
 */
public class SimpleAlexaSkillStreamHandler extends SkillStreamHandler {
  /**
   * Skill instance configured with handlers and other configuration.
   */
  public SimpleAlexaSkillStreamHandler() {
    super(Skills.standard()
        .addRequestHandler(new ExitRequestHandler())
        .addRequestHandler(new SubstituteStoreIntentHandler())
        .addRequestHandler(new AdjuvantLaunchRequestHandler())
        .addRequestHandler(new FallbackHandler())
        .addRequestHandler(new GetRemindersRequestHandler())
        .addRequestHandler(new SetContactRequestHandler())
        .addRequestHandler(new GetAlphabetRequestHandler())
        .addRequestHandler(new AllRemindersHandler())
        .addRequestHandler(new DeleteHandler())
        .addRequestHandler(new SubstituteStoreOutdoorStreamHandler())
        .addRequestHandler(new RouteRequestHandler())
        .build());

  }
}
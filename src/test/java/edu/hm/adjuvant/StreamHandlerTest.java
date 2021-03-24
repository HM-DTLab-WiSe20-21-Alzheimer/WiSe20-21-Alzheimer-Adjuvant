package edu.hm.adjuvant;

import com.amazon.ask.SkillStreamHandler;
import org.junit.Assert;
import org.junit.jupiter.api.Test;

/**
 * Test für SimpleAlexaStreamHandler.
 *
 * @author Anonymous Student
 */
 class StreamHandlerTest {
  @Test  void standardTest(){
    SkillStreamHandler streamHandler=new SimpleAlexaSkillStreamHandler();
    Assert.assertNotNull(streamHandler);
  }
}

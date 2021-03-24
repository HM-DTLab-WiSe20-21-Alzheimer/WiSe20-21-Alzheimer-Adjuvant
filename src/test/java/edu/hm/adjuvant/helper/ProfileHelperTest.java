package edu.hm.adjuvant.helper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.amazon.ask.model.Context;
import com.amazon.ask.model.Intent;
import com.amazon.ask.model.IntentRequest;
import com.amazon.ask.model.Permissions;
import com.amazon.ask.model.RequestEnvelope;
import com.amazon.ask.model.User;
import com.amazon.ask.model.interfaces.system.SystemState;
import com.amazon.ask.model.services.ServiceClientFactory;
import com.amazon.ask.model.services.ups.UpsServiceClient;
import org.junit.jupiter.api.Test;

/**
 * ProfileHelperTest.java
 * @author Stefan Kühnel, stefan.kuehnel@hm.edu
 * @version 2021-1-11
 */

class ProfileHelperTest {
  @Test
  void testHasProfileEmail_RequireReturn_TrueWhenProfileEmailIsSet() {
    // Arrange
    final Permissions permissions = Permissions.builder().withConsentToken("token").build();

    final Intent intent = mock(Intent.class);
    final User user = mock(User.class);
    final Context context = mock(Context.class);
    final ServiceClientFactory serviceClientFactory = mock(ServiceClientFactory.class);
    final UpsServiceClient upsServiceClient = mock(UpsServiceClient.class);

    when(intent.getName()).thenReturn("RouteRequestIntent");
    when(user.getPermissions()).thenReturn(permissions);
    when(context.getSystem()).thenReturn(SystemState.builder().withUser(user).build());
    when(upsServiceClient.getProfileEmail()).thenReturn("stefan.kuehnel@hm.edu");
    when(serviceClientFactory.getUpsService()).thenReturn(upsServiceClient);

    final IntentRequest intentRequest = IntentRequest.builder().withIntent(intent).build();
    final RequestEnvelope
        requestEnvelope = RequestEnvelope.builder().withRequest(intentRequest).withContext(context).build();
    final HandlerInput handlerInput = HandlerInput.builder().withRequestEnvelope(requestEnvelope).withServiceClientFactory(serviceClientFactory).build();

    // Act
    final boolean hasProfileEmail = ProfileHelper.hasProfileEmail(handlerInput);

    // Assert
    assertTrue(hasProfileEmail);
  }

  @Test
  void testHasProfileEmail_RequireReturn_FalseWhenNoProfileEmailIsSet() {
    // Arrange
    final Permissions permissions = Permissions.builder().withConsentToken("token").build();

    final Intent intent = mock(Intent.class);
    final User user = mock(User.class);
    final Context context = mock(Context.class);
    final ServiceClientFactory serviceClientFactory = mock(ServiceClientFactory.class);
    final UpsServiceClient upsServiceClient = mock(UpsServiceClient.class);

    when(intent.getName()).thenReturn("RouteRequestIntent");
    when(user.getPermissions()).thenReturn(permissions);
    when(context.getSystem()).thenReturn(SystemState.builder().withUser(user).build());
    when(upsServiceClient.getProfileEmail()).thenReturn("");
    when(serviceClientFactory.getUpsService()).thenReturn(upsServiceClient);

    final IntentRequest intentRequest = IntentRequest.builder().withIntent(intent).build();
    final RequestEnvelope
        requestEnvelope = RequestEnvelope.builder().withRequest(intentRequest).withContext(context).build();
    final HandlerInput handlerInput = HandlerInput.builder().withRequestEnvelope(requestEnvelope).withServiceClientFactory(serviceClientFactory).build();

    // Act
    final boolean hasProfileEmail = ProfileHelper.hasProfileEmail(handlerInput);

    // Assert
    assertFalse(hasProfileEmail);
  }

  @Test
  void testHasProfileEmail_RequireReturn_FalseWhenPermissionsAreNotSet() {
    // Arrange
    final Intent intent = mock(Intent.class);
    final User user = mock(User.class);
    final Context context = mock(Context.class);
    final ServiceClientFactory serviceClientFactory = mock(ServiceClientFactory.class);
    final UpsServiceClient upsServiceClient = mock(UpsServiceClient.class);

    when(intent.getName()).thenReturn("RouteRequestIntent");
    when(context.getSystem()).thenReturn(SystemState.builder().withUser(user).build());
    when(serviceClientFactory.getUpsService()).thenReturn(upsServiceClient);

    final IntentRequest intentRequest = IntentRequest.builder().withIntent(intent).build();
    final RequestEnvelope
        requestEnvelope = RequestEnvelope.builder().withRequest(intentRequest).withContext(context).build();
    final HandlerInput handlerInput = HandlerInput.builder().withRequestEnvelope(requestEnvelope).withServiceClientFactory(serviceClientFactory).build();

    // Act
    final boolean hasProfileEmail = ProfileHelper.hasProfileEmail(handlerInput);

    // Assert
    assertFalse(hasProfileEmail);
  }

  @Test
  void testGetProfileEmail_VerifyFunctionality_SuppliedAndReceivedProfileEmailAreEqual() {
    // Arrange
    final Permissions permissions = Permissions.builder().withConsentToken("token").build();
    final String email = "stefan.kuehnel@hm.edu";

    final Intent intent = mock(Intent.class);
    final User user = mock(User.class);
    final Context context = mock(Context.class);
    final ServiceClientFactory serviceClientFactory = mock(ServiceClientFactory.class);
    final UpsServiceClient upsServiceClient = mock(UpsServiceClient.class);

    when(intent.getName()).thenReturn("RouteRequestIntent");
    when(user.getPermissions()).thenReturn(permissions);
    when(context.getSystem()).thenReturn(SystemState.builder().withUser(user).build());
    when(upsServiceClient.getProfileEmail()).thenReturn(email);
    when(serviceClientFactory.getUpsService()).thenReturn(upsServiceClient);

    final IntentRequest intentRequest = IntentRequest.builder().withIntent(intent).build();
    final RequestEnvelope
        requestEnvelope = RequestEnvelope.builder().withRequest(intentRequest).withContext(context).build();
    final HandlerInput handlerInput = HandlerInput.builder().withRequestEnvelope(requestEnvelope).withServiceClientFactory(serviceClientFactory).build();

    // Act
    final String profileEmail = ProfileHelper.getProfileEmail(handlerInput);

    // Assert
    assertEquals(email, profileEmail);
  }
}

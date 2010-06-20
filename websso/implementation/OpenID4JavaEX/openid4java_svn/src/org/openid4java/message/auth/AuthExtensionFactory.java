/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.openid4java.message.auth;

import org.openid4java.message.MessageException;
import org.openid4java.message.MessageExtension;
import org.openid4java.message.MessageExtensionFactory;
import org.openid4java.message.ParameterList;

/**
 *
 * @author dgourlay
 */
public class AuthExtensionFactory implements MessageExtensionFactory {

    /**
     * Gets the Type URI that identifies the Auth 1.0 extension.
     */
    public String getTypeUri() {
        return AuthMessage.OPENID_NS_AUTH;
    }

    /**
     * Builds a MessageExtension from a parameter list containing the
     * extension-specific parameters.
     * <p>
     * The parameters MUST NOT contain the openid.<extension_alias> prefix.
     *
     * @param parameterList     The extension parameters with the
     *                          openid.<extension_alias> prefix removed.
     * @param isRequest         Indicates whether the parameters were extracted
     *                          from an openid request (true), or from an openid
     *                          response (false). This may assist the factory
     *                          implementation in determining what object type
     *                          to instantiate.
     * @return                  MessageExtension implementation for the supplied
     *                          extension parameters.
     */
    public MessageExtension getExtension(
            ParameterList parameterList, boolean isRequest)
            throws MessageException {

        String authMode = null;
        if (parameterList.hasParameter("mode")) {
            authMode = parameterList.getParameterValue("mode");

            if ("request".equals(authMode)) {
                return AuthRequest.createAuthRequest(parameterList);
            } else if ("response".equals(authMode)) {
                //return AuthResponse.createAuthResponse(parameterList);
            }


        }

        throw new MessageException("Invalid value for auth mode: "
                + authMode);
    }
}

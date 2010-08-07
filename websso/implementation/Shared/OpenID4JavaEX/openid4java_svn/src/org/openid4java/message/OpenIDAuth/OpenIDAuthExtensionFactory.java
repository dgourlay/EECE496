package org.openid4java.message.OpenIDAuth;

import org.openid4java.message.MessageException;
import org.openid4java.message.MessageExtension;
import org.openid4java.message.MessageExtensionFactory;
import org.openid4java.message.ParameterList;

/**
 * Custom Extension Factory for OpenIDAuth messages. Creates OpenIDAuth message
 * objects, sets the type URI to http://lersse.ece.ubc.ca/openid/ext/ua/auth/1.0
 *
 * @author Derek Gourlay
 */
public class OpenIDAuthExtensionFactory implements MessageExtensionFactory {

    /**
     * Gets the Type URI that identifies the Auth 1.0 extension.
     */
    public String getTypeUri() {
        return OpenIDAuthMessage.OPENID_NS_AUTH;
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

        OpenIDAuthRequest aReq;

        aReq = OpenIDAuthRequest.createAuthRequest(parameterList);

        return aReq;


    }
}

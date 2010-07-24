/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.openid4java.message.OpenIDAuth;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openid4java.message.Message;
import org.openid4java.message.MessageException;
import org.openid4java.message.MessageExtension;
import org.openid4java.message.MessageExtensionFactory;
import org.openid4java.message.ParameterList;
import org.openid4java.message.sreg.SRegMessage;

/**
 *
 * @author dgourlay
 */
public class OpenIDAuthMessage implements MessageExtension, MessageExtensionFactory {

    private static Log _log = LogFactory.getLog(SRegMessage.class);
    private static final boolean DEBUG = _log.isDebugEnabled();

    /* Auth Message Namespace  */
    public static final String OPENID_NS_AUTH = "http://lersse.ece.ubc.ca/openid/ext/ua/auth/1.0";
    private String _typeUri = OPENID_NS_AUTH;

    
    protected ParameterList _parameters;

    public OpenIDAuthMessage() {
        _parameters = new ParameterList();

        if (DEBUG) {
            _log.debug("Created empty AuthMessage.");
        }
    }

    public OpenIDAuthMessage(ParameterList params) {
        _parameters = params;

        if (DEBUG) {
            _log.debug("Created AuthMessage from parameter list:\n" + params);
        }
    }

    /**
     * Gets the TypeURI that identifies a extension to the OpenID protocol.
     */
    public String getTypeUri() {
        return _typeUri;
    }

    /**
     * Gets the extension-specific parameters.
     * <p>
     * Implementations MUST NOT prefix the parameter names with
     * "openid.<extension_alias>". The alias is managed internally by the Message class,
     * when a extension is attached to an OpenID messaage.
     *
     * @see Message
     */
    public ParameterList getParameters() {
        return _parameters;
    }

    /**
     * Gets a the value of the parameter with the specified name.
     *
     * @param name      The name of the parameter,
     *                  without the openid.<extension_alias> prefix.
     * @return          The parameter value, or null if not found.
     */
    public String getParameterValue(String name) {
        return _parameters.getParameterValue(name);
    }

    /**
     * Sets the extension-specific parameters.
     * <p>
     * Implementations MUST NOT prefix the parameter names with
     * "openid.<extension_alias>". The alias is managed internally by the Message class,
     * when a extension is attached to an OpenID messaage.

     * @param params
     * @see Message
     */
    public void setParameters(ParameterList params) {
        _parameters = params;
    }

    /**
     * Used by the core OpenID authentication implementation to learn whether
     * an extension provies authentication services.
     * <p>
     * If the extension provides authentication services,
     * the 'openid.identity' and 'openid.signed' parameters are optional.
     *
     * @return  True if the extension provides authentication services,
     *          false otherwise.
     */
    public boolean providesIdentifier() {
        return true;
    }

    /**
     * Flag for indicating that an extension must be signed.
     *
     * @return  True if all the extension's parameters MUST be signed
     *          in positive assertions, or false if there isn't such a
     *          requirement.
     */
    public boolean signRequired() {
        return true;
    }

     public MessageExtension getExtension(
            ParameterList parameterList, boolean isRequest)
            throws MessageException
    {
        String authMode = null;
        
        if (parameterList.hasParameter("mode"))
        {
            authMode = parameterList.getParameterValue("mode");

            if ("request".equals(authMode))
                return OpenIDAuthRequest.createAuthRequest(parameterList);

            else if ("response".equals(authMode))
                return OpenIDAuthResponse.createAuthResponse(parameterList);
        }

        throw new MessageException("Invalid value for OpenIDAuth mode: "
                                   + authMode);
    }
}

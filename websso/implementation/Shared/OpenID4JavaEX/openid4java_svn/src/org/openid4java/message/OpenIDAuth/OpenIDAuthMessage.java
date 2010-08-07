package org.openid4java.message.OpenIDAuth;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openid4java.message.Message;
import org.openid4java.message.MessageException;
import org.openid4java.message.MessageExtension;
import org.openid4java.message.MessageExtensionFactory;
import org.openid4java.message.ParameterList;
/**
 * Base class for the OpenIDAuth implementation.
 * <p>
 * Encapsulates:
 * <ul>
 * <li> the Type URI that identifies the OpenIDAuth extension
 * <li> a list of extension-specific parameters, with the
 * openid.<extension_alias> prefix removed
 * <li> methods for handling the extension-specific support of parameters with
 * multpile values
 * </ul>
 *
 * @see Message MessageExtension
 * @author Derek Gourlay
 */
public class OpenIDAuthMessage implements MessageExtension, MessageExtensionFactory {

    private static Log _log = LogFactory.getLog(OpenIDAuthMessage.class);
    private static final boolean DEBUG = _log.isDebugEnabled();

    /* OpenIDAuth Message Namespace  */
    public static final String OPENID_NS_AUTH = "http://lersse.ece.ubc.ca/openid/ext/ua/auth/1.0";
    private String _typeUri = OPENID_NS_AUTH;

    /*
     * The OpenIDAuth extension-specific parameters.
     * <p>
     * The openid.<extension_alias> prefix is not part of the parameter names
     */
    protected ParameterList _parameters;


    /**
     * Constructs an empty (no parameters) OpenIDAuth extension.
     */
    public OpenIDAuthMessage() {
        _parameters = new ParameterList();

        if (DEBUG) {
            _log.debug("Created empty AuthMessage.");
        }
    }


    /**
     * Constructs an OpenIDAuth extension with a specified list of
     * parameters.
     * <p>
     * The parameter names in the list should not contain the
     * openid.<extension_alias>.
     */
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
     * Gets ParameterList containing the OpenIDAuth extension-specific
     * parameters.
     * <p>
     * The openid.<extension_alias> prefix is not part of the parameter names,
     * as it is handled internally by the Message class.
     * <p>
     * The openid.ns.<extension_type_uri> parameter is also handled by
     * the Message class.
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

    //REQ
    /**
     * Flag for indicating that an extension must be signed.
     *
     * @return  True if all the extension's parameters MUST be signed
     *          in positive assertions, or false if there isn't such a
     *          requirement.
     */
    public boolean signRequired() {
        return false;
    }

    /**
     * Instantiates the apropriate OpenIDAuth object
     * (request / response) for the supplied parameter list.
     *
     * @param parameterList         The OpenIDAuth specific parameters
     *                              (without the openid.<ext_alias> prefix)
     *                              extracted from the openid message.
     * @param isRequest             Indicates whether the parameters were
     *                              extracted from an OpenID request (true),
     *                              or from an OpenID response.
     * @return                      MessageExtension implementation for
     *                              the supplied extension parameters.
     */
    public MessageExtension getExtension(
            ParameterList parameterList, boolean isRequest)
            throws MessageException {

        if (isRequest) {
            return OpenIDAuthRequest.createAuthRequest(_parameters);
        }else{
            return OpenIDAuthResponse.createAuthResponse(_parameters);
        }

    }
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.openid4java.message.OpenIDAuth;

import java.util.Iterator;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openid4java.message.MessageException;
import org.openid4java.message.Parameter;
import org.openid4java.message.ParameterList;
import org.openid4java.message.sreg.SRegRequest;

/**
 *
 * @author dgourlay
 */
public class OpenIDAuthRequest extends OpenIDAuthMessage {

    private static Log _log = LogFactory.getLog(OpenIDAuthRequest.class);
    private static final boolean DEBUG = _log.isDebugEnabled();

    /**
     * Constructs a Auth Request with an empty parameter list.
     */
    public OpenIDAuthRequest() {
        if (DEBUG) {
            _log.debug("Created empty Auth request.");
        }
    }

    /**
     * Constructs a Auth Request with an empty parameter list.
     */
    public static OpenIDAuthRequest createFetchRequest() {
        return new OpenIDAuthRequest();
    }

    /**
     * Constructs a AuthRequest from a parameter list.
     * <p>
     * The parameter list can be extracted from a received message with the
     * getExtensionParams method of the Message class, and MUST NOT contain
     * the "openid.<extension_alias>." prefix.
     */
    protected OpenIDAuthRequest(ParameterList params) {
        _parameters = params;
    }

    /**
     * Constructs a AuthRequest from a parameter list.
     * <p>
     * The parameter list can be extracted from a received message with the
     * getExtensionParams method of the Message class, and MUST NOT contain
     * the "openid.<extension_alias>." prefix.
     */
    public static OpenIDAuthRequest createAuthRequest(ParameterList params)
            throws MessageException {
        OpenIDAuthRequest req = new OpenIDAuthRequest(params);

        if (!req.isValid()) {
            throw new MessageException("Invalid parameters for a Auth request");
        }

        if (DEBUG) {
            _log.debug("Created Auth request from parameter list:\n" + params);
        }

        return req;
    }

    /**
     * Gets the optional policy URL parameter if available, or null otherwise.
     */
    public String getUpdateUrl() {
        return _parameters.hasParameter("policy_url")
                ? _parameters.getParameterValue("policy_url") : null;
    }

    /**
     * Checks the validity of the extension.
     * <p>
     * Used when constructing a extension from a parameter list.
     *
     * @return      True if the extension is valid, false otherwise.
     */
    public boolean isValid() {
        return true;
    }
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.openid4java.message.auth;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openid4java.message.MessageException;
import org.openid4java.message.Parameter;
import org.openid4java.message.ParameterList;
import org.openid4java.message.sreg.SRegResponse;

/**
 *
 * @author dgourlay
 */
public class OpenIDAuthResponse extends OpenIDAuthMessage {

    private static Log _log = LogFactory.getLog(SRegResponse.class);
    private static final boolean DEBUG = _log.isDebugEnabled();

    protected final static List AUTH_FIELDS = Arrays.asList( new String[] {
            "session-id", "email"
    });



     /**
     * Constructs a SReg Response with an empty parameter list.
     */
    protected OpenIDAuthResponse()
    {
        if (DEBUG) _log.debug("Created empty OpenIDAuth response.");
    }

      /**
     * Constructs an Auth Response with an empty parameter list.
     */
    public static OpenIDAuthResponse createAuthResponse()
    {
        return new OpenIDAuthResponse();
    }

   /**
     * Constructs a Auth Response from a parameter list.
     * <p>
     * The parameter list can be extracted from a received message with the
     * getExtensionParams method of the Message class, and MUST NOT contain
     * the "openid.<extension_alias>." prefix.
     */
    protected OpenIDAuthResponse(ParameterList params)
    {
        _parameters = params;
    }


    public static OpenIDAuthResponse createAuthResponse(ParameterList params)
            throws MessageException
    {
        OpenIDAuthResponse resp = new OpenIDAuthResponse(params);

        if (! resp.isValid())
            throw new MessageException("Invalid parameters for a OpenIDAuth response");

        if (DEBUG)
            _log.debug("Created OpenIDAuth response from parameter list:\n" + params);

        return resp;
    }


    /**
     * Checks the validity of the extension.
     * <p>
     * Used when constructing a extension from a parameter list.
     *
     * @return      True if the extension is valid, false otherwise.
     */
    private boolean isValid()
    {
        Iterator it = _parameters.getParameters().iterator();
        while (it.hasNext())
        {
            String paramName = ((Parameter) it.next()).getKey();

            if (! AUTH_FIELDS.contains(paramName))
            {
                _log.warn("Invalid parameter name in OpenIDAuth response: " + paramName);
                return false;
            }
        }

        return true;
    }
}

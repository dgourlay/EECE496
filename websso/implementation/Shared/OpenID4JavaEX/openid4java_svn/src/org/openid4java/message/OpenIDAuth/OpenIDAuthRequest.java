package org.openid4java.message.OpenIDAuth;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openid4java.message.MessageException;
import org.openid4java.message.Parameter;
import org.openid4java.message.ParameterList;

/**
 * Implements the extension for OpenIDAuth fetch requests.  This class
 * represents the request sent initially from a user agent to RP.
 *
 * @see OpenIDAuthMessage, Message
 * @author Derek Gourlay
 */
public class OpenIDAuthRequest extends OpenIDAuthMessage {

    private static Log _log = LogFactory.getLog(OpenIDAuthRequest.class);
    private static final boolean DEBUG = _log.isDebugEnabled();

    protected final static List AUTH_FIELDS = Arrays.asList( new String[] {
            "user-id", "session-id"
    });


    /**
     * Constructs an OpenIDAuth Request with an empty parameter list.
     */
    public OpenIDAuthRequest() {
        if (DEBUG) {
            _log.debug("Created empty Auth request.");
        }
    }

    /**
     * Constructs an OpenIDAuth Request with an empty parameter list.
     */
    public static OpenIDAuthRequest createFetchRequest() {
        return new OpenIDAuthRequest();
    }

    /**
     * Constructs a OpenIDAuth Request from a parameter list.
     * <p>
     * The parameter list can be extracted from a received message with the
     * getExtensionParams method of the Message class, and MUST NOT contain
     * the "openid.<extension_alias>." prefix.
     */
    protected OpenIDAuthRequest(ParameterList params) {
        _parameters = params;
    }

    /**
     * Constructs a OpenIDAuth Request from a parameter list.
     * <p>
     * The parameter list can be extracted from a received message with the
     * getExtensionParams method of the Message class, and MUST NOT contain
     * the "openid.<extension_alias>." prefix.
     */
    public static OpenIDAuthRequest createAuthRequest(ParameterList params)
            throws MessageException {
        OpenIDAuthRequest req = new OpenIDAuthRequest(params);

        if (!req.isValid()) {
            throw new MessageException("Invalid parameters for a Auth-Extension request");
        }

        if (DEBUG) {
            _log.debug("Created Auth-Extension request from parameter list:\n" + params);
        }

        return req;
    }


    /**
     * Constructs a AuthRequest from a String containing the value of the
     * WWW-Authenticate field.
     * <p>
     * The parameter list can be extracted from a received message with the
     * getExtensionParams method of the Message class, and MUST NOT contain
     * the "openid.<extension_alias>." prefix.
     *
     * @param   authField   String containing the value of the WWW-Authenticate
     *                      field sent by UserAgent.
     *
     */
    public static OpenIDAuthRequest createAuthRequest(String authField){

        OpenIDAuthRequest  req = new OpenIDAuthRequest();

        // Create a pattern to match user-id & session-id
        Pattern userPatern = Pattern.compile("user-id=\"([^\"]*)\"");
        Pattern sessionPattern = Pattern.compile("session-id=\"([^\"]*)\"");
        // Create a matcher with an input string
        Matcher m = userPatern.matcher(authField);
        
        if(m.find()){
            req.addAttribute("user-id", m.group(1));
        }else{
            return null;
        }
        m = sessionPattern.matcher(authField);
        if(m.find()){
            req.addAttribute("session-id", m.group(1));
        }else{
            return null;
        }
        
        return req;
        
    }

    /**
     * Adds an attribute to the OpenIDAuth request, if it already exists replace
     * it.
     *
     * @param       name        Attribue name
     * @param       value       Attribute value
     */
    public void addAttribute(String name, String value)
    {

        Parameter currentVal = _parameters.getParameter(name);

        if(currentVal == null){
            _parameters.set( new Parameter(name, value) );

        }else{
            _parameters.removeParameters(name);
            _parameters.set( new Parameter(name, value) );
        }

    }

    /**
     * Returns a map with the requested attributes.
     *
     * @return      List of attribute names.
     */
    public List getAttributes(boolean required)
    {
        List attributes = _parameters.getParameters();
        return attributes;

    }



    /**
     * Checks the validity of the extension.
     * <p>
     * Used when constructing a extension from a parameter list.
     *
     * @return      True if the extension is valid, false otherwise.
     */
    public boolean isValid() {

        Iterator it = _parameters.getParameters().iterator();
        while (it.hasNext())
        {
            String paramName = ((Parameter) it.next()).getKey();

            if (! AUTH_FIELDS.contains(paramName))
            {
                _log.warn("Invalid parameter name in OpenIDAuth Request: " + paramName);
                return false;
            }
        }

        return true;
    }
}

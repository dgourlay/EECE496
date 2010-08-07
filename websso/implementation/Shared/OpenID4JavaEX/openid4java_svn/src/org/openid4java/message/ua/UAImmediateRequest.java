package org.openid4java.message.ua;

import org.openid4java.message.MessageException;
import org.openid4java.message.Parameter;
import org.openid4java.message.ParameterList;

import java.util.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Implements the extension for UAImmediate fetch requests.
 *
 */
public class UAImmediateRequest extends UAMessage
{
    private static Log _log = LogFactory.getLog(UAImmediateRequest.class);
    private static final boolean DEBUG = _log.isDebugEnabled();


    /**
     * Creates a UAImmediateRequest with an empty parameter list.
     */
    protected UAImmediateRequest()
    {
    	//_parameters.set(new Parameter("openid.mode", "checkid_immediate"));
        if (DEBUG) _log.debug("Created empty UAImmediate request.");
    }


    /**
     * Creates a UAImmediateRequest with inputted parameter list.
     */
    protected UAImmediateRequest(ParameterList params)
    {
        _parameters = params;
    }

    /**
     * Creates a new UAImmediateRequest with inputted parameter list and returns it.
     *
     * @param params            inputted parameterlist
     *
     * @return                  UAImmediateRequest with parameterlist as the parameters
     *
     */
    public static UAImmediateRequest createUAImmediateRequest(ParameterList params)
            throws MessageException
    {
        UAImmediateRequest req = new UAImmediateRequest(params);

        if (! req.isValid())
            throw new MessageException("Invalid parameters for a UAImmediate request");

        if (DEBUG)
            _log.debug("Created UAImmediate request from parameter list:\n" + params);

        return req;
    }

      /**
     * Adds a parameter to the UAAssociate response.
     *
     * @param       attr        Key of the parameter
     * @param       value       Value of the key
     */
    public void addAttribute(String attr, String value) throws MessageException
    {
        _parameters.set(new Parameter(attr, value));


        if (DEBUG)
            _log.debug("Added new attribute to UAAssociate response: " + attr +
                       " value: " + value);
    }

    /**
     * Returns the value of the parameter enc_type.
     *
     * @return     the value of the parameter enc_type.
     */
    public String getEncType()
    {
    	return _parameters.hasParameter("enc_type") ?
                _parameters.getParameterValue("enc_type") : null;
    }

    /**
     * Returns the value of the parameter enc_pwd_hash.
     *
     * @return     the value of the parameter enc_pwd_hash.
     */
    public String getEncPwdHash()
    {
    	return _parameters.hasParameter("enc_pwd_hash") ?
                _parameters.getParameterValue("enc_pwd_hash") : null;
    }


    /**
     * Checks the validity of the extension.
     *
     * @return      True if the extension is valid, false otherwise.
     */
    public boolean isValid()
    {
        if ( ! _parameters.hasParameter("enc_type") )  {
            _log.warn("enc_type must be present");
            return false;
        }
        if ( ! _parameters.hasParameter("enc_pwd_hash") )  {
            _log.warn("enc_pwd_hash must be present");
            return false;
        }

        Iterator it = _parameters.getParameters().iterator();
        while (it.hasNext())
        {
            String paramName = ((Parameter) it.next()).getKey();
            if (! paramName.equals("enc_type") &&
                    ! paramName.equals("end_pwd_hash"))
            {
                _log.warn("Invalid parameter name in UAImmediateRequest: " + paramName);
//                return false;
            }
        }

        return true;
    }
}
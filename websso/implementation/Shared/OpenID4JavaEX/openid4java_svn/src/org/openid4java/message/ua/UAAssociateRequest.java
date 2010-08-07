package org.openid4java.message.ua;

import org.openid4java.message.MessageException;
import org.openid4java.message.Parameter;
import org.openid4java.message.ParameterList;

import java.util.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Implements the extension for UAAssociate Requests.
 *
 */
public class UAAssociateRequest extends UAMessage
{
    private static Log _log = LogFactory.getLog(UAAssociateRequest.class);
    private static final boolean DEBUG = _log.isDebugEnabled();


    /**
     * Creates a UAAssociateRequest with empty parameter list.
     */
    protected UAAssociateRequest()
    {
        if (DEBUG) _log.debug("Created empty UAAssociate request.");
    }

    /**
     * Creates UAAssociateRequest with inputted parameter list.
     */
    protected UAAssociateRequest(ParameterList params)
    {
        _parameters = params;
    }

    /**
     * Creates UAAssociateRequest from inputted parameterlist and returns it
     *
     * @param params               parameterlist for UAAssociateRequest
     *
     * @return                     UAAssociateRequest with inputted parameter list
     */
    public static UAAssociateRequest createUAAssociateRequest(ParameterList params)
            throws MessageException
    {
        UAAssociateRequest req = new UAAssociateRequest(params);

        if (! req.isValid())
            throw new MessageException("Invalid parameters for a UAAssociate request");

        if (DEBUG)
            _log.debug("Created UAAssociate request from parameter list:\n" + params);

        return req;
    }


    /**
     * Returns the value of the claimed_id Parameter.
     *
     *
     * @return      Value of claimed_id Parameter
     */

    public String getClaimedID()
    {
    	return _parameters.hasParameter("claimed_id") ?
                _parameters.getParameterValue("claimed_id") : null;
    }

    /**
     * Sets the value of the parameter claimed_id to the input
     *
     * @param claimed_id        ID of the user attempting to log in.
     *
     */

    public void setClaimedID(String claimed_id)
    {
    	if (DEBUG) _log.debug("Setting UAAssociateRequest claimed_id: " + claimed_id);

        _parameters.set(new Parameter("claimed_id", claimed_id));
    }


    /**
     * Checks the validity of the extension.
     *
     * @return      True if the extension is valid, false otherwise.
     */
    public boolean isValid()
    {

        if ( ! _parameters.hasParameter("claimed_id") )  {
            _log.warn("claimed_id must be present");
            return false;
        }

        Iterator it = _parameters.getParameters().iterator();
        while (it.hasNext())
        {
            String paramName = ((Parameter) it.next()).getKey();
            if (! paramName.equals("claimed_id"))
            {
                _log.warn("Invalid parameter name in UAAssociateRequest: " + paramName);
//                return false;
            }
        }

        return true;
    }
}
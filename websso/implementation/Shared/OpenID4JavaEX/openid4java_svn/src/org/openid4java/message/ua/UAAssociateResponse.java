package org.openid4java.message.ua;


import org.openid4java.message.MessageException;
import org.openid4java.message.Parameter;
import org.openid4java.message.ParameterList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Implements the extension for UAAssociate responses
 *
 */
public class UAAssociateResponse extends UAMessage
{
    private static Log _log = LogFactory.getLog(UAAssociateResponse.class);
    private static final boolean DEBUG = _log.isDebugEnabled();


    /**
     * Creates a UAAssociateResponse with an empty parameter list.
     */
    protected UAAssociateResponse()
    {
        if (DEBUG) _log.debug("Created empty fetch response.");
    }


    /**
     * Creates a UAAssociateResponse with the inputted parameter list.
     */
    protected UAAssociateResponse(ParameterList params)
    {
        _parameters = params;
    }

    /**
     * Creates a UAAssociateRequest from inputted parameter list.
     *
     * @return   UAAssociateResponse with inputted parameter list.
     */
    public static UAAssociateResponse createUAAssociateResponse(ParameterList params)
            throws MessageException
    {
        UAAssociateResponse response = new UAAssociateResponse(params);


        if (DEBUG)
            _log.debug("Created UAAssociate response from parameter list:\n" + params);

        return response;
    }

    /**
     * Constructs a UAAssociateResponse with an empty parameter list and returns it.
     *
     * @return  Empty UAAssociateResponse
     */
        public static UAAssociateResponse createUAAssociateResponse()
            throws MessageException
    {
        UAAssociateResponse response = new UAAssociateResponse();

        return response;
    }


    /**
     * Adds an parameter to the UAAssociate response.
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


}
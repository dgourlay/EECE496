package org.openid4java.message.ua;

import org.openid4java.message.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Base class for the UAMessage implementation.
 *
 * @see Message MessageExtension
 *
 */
public class UAMessage implements MessageExtension, MessageExtensionFactory
{
	private static Log _log = LogFactory.getLog(UAMessage.class);
	private static final boolean DEBUG = _log.isDebugEnabled();

	/**
	 * The type URI for the UA class.
	 */
	public static final String OPENID_NS_UA = "http://lersse.ece.ubc.ca/openid/ext/ua/associate/1.0";

	/**
	 * ParameterList for the UAMessage extension
	 *
	 */
	protected ParameterList _parameters;

	/**
	 * Creates a UAMessage with empty parameter list.
	 */
	public UAMessage()
	{
		_parameters = new ParameterList();

		if (DEBUG) _log.debug("Created empty UAMessage.");
	}

	/**
	 * Creates a UAMessage with inputted parameter list.
	 */
	public UAMessage(ParameterList params)
	{
		_parameters = params;

		if (DEBUG)
			_log.debug("Created UAMessage from parameter list:\n" + params);
	}

	/**
	 * Returns the type URI of the UAMessage extension.
	 */
	public String getTypeUri()
	{
		return OPENID_NS_UA;
	}

	/**
	 * Returns the parameter list for UAMessage extension.
         *
	 */
	public ParameterList getParameters()
	{
		return _parameters;
	}

	/**
	 * Returns the value of the inputted key.
	 *
	 * @param name      parameter key
	 * @return          parameter value for the inputted  key
	 */
	public String getParameterValue(String name)
	{
		return _parameters.getParameterValue(name);
	}

	/**
	 * Sets the parameter list of UAMessage extension to the inputted parameter list
         *
         * @param params        parameter list
         *
	 */
	public void setParameters(ParameterList params)
	{
		_parameters = params;
	}

	/**
	 * UAMessage does prodive authentication.
	 *
	 * @return true
	 */
	public boolean providesIdentifier()
	{
		return true;
	}

	/**
	 * UAMessage parameters are REQUIRED to be signed.
	 *
	 * @return true
	 */
	public boolean signRequired()
	{
		return true;
	}

	/**
	 * Creates the appropriate Message extensions based on parameters
	 *
	 * @param parameterList         Parameterlist from the Message
	 * @param isRequest             Indicates whether the parameters were extracted
         *                              from an openid request (true), or from an openid
         *                              response (false). This may assist the factory
         *                              implementation in determining what object type
         *                              to instantiate.
         * @return                      MessageExtension implementation for the supplied
         *                              extension parameters.
	 * @throws MessageException     Could not find correct extension
	 */

        //isRequest is needed because it is in the MessageExtensionFactor getExtension, but isn't really used.
	public MessageExtension getExtension(
			ParameterList parameterList, boolean isRequest)
            throws MessageException
	{
			if (parameterList.hasParameter("enc_pwd_hash"))
				return UAImmediateRequest.createUAImmediateRequest(parameterList);
			else if (parameterList.hasParameter("claimed_id"))
				return UAAssociateRequest.createUAAssociateRequest(parameterList);
			else
                            return UAAssociateResponse.createUAAssociateResponse(parameterList);

	}
}
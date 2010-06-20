/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.openid4java.message.auth;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openid4java.message.sreg.SRegRequest;

/**
 *
 * @author dgourlay
 */
public class AuthRequest extends AuthMessage {

    private static Log _log = LogFactory.getLog(SRegRequest.class);
    private static final boolean DEBUG = _log.isDebugEnabled();

    /**
     * Constructs a Auth Request with an empty parameter list.
     */
    public AuthRequest() {
        if (DEBUG) {
            _log.debug("Created empty Auth request.");
        }
    }
}

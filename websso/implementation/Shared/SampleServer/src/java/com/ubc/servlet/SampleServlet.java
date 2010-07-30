/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ubc.servlet;

import com.ubc.util.XrdsDocumentBuilder;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.openid4java.association.AssociationException;
import org.openid4java.message.AuthRequest;
import org.openid4java.message.AuthSuccess;
import org.openid4java.message.DirectError;
import org.openid4java.message.Message;
import org.openid4java.message.MessageExtension;
import org.openid4java.message.OpenIDAuth.OpenIDAuthMessage;
import org.openid4java.message.OpenIDAuth.OpenIDAuthRequest;
import org.openid4java.message.OpenIDAuth.OpenIDAuth_CheckImed_Reply;
import org.openid4java.message.ParameterList;
import org.openid4java.message.ax.AxMessage;
import org.openid4java.message.ax.FetchRequest;
import org.openid4java.message.ax.FetchResponse;
import org.openid4java.message.sreg.SRegMessage;
import org.openid4java.message.sreg.SRegRequest;
import org.openid4java.message.sreg.SRegResponse;
import org.openid4java.server.ServerException;
import org.openid4java.server.ServerManager;

/**
 *
 * @author dgourlay
 */
public class SampleServlet extends HttpServlet {

    private final static int ID_INDEX = 0;
    private final static int IS_AUTH_INDEX = 1;
    private final static int EMAIL_INDEX = 2;
    private final static int PASSWORD_INDEX = 3;
    private final static int ASSOC_HANDLE_INDEX = 4;
    private HashMap<String, ArrayList<String>> userDataStore;
    private HashMap<String, ArrayList<String>> assocHandleStore;
    // instantiate a ServerManager object
    public ServerManager manager = new ServerManager();

    public SampleServlet() {
        this("http://localhost:8080/SampleServer/SampleServlet");
        userDataStore = new HashMap<String, ArrayList<String>>();
        assocHandleStore = new HashMap<String, ArrayList<String>>();
        populateUsers();
    }

    public SampleServlet(String endPointUrl) {

        manager.setOPEndpointUrl(endPointUrl);
        // for a working demo, not enforcing RP realm discovery
        // since this new feature is not deployed
        manager.getRealmVerifier().setEnforceRpId(false);
    }

    public String processRequest(HttpServletRequest httpReq,
            HttpServletResponse httpResp)
            throws Exception {


        //Check for discovery
        if (httpReq.getParameterMap().isEmpty()) {
            httpResp.setContentType("application/xrds+xml");
            OutputStream outputStream = httpResp.getOutputStream();
            String xrdsResponse = createXrdsResponse();
            //
            outputStream.write(xrdsResponse.getBytes());
            outputStream.close();
            return "";
        }


        // extract the parameters from the request
        ParameterList request = new ParameterList(httpReq.getParameterMap());

        String mode = request.hasParameter("openid.mode")
                ? request.getParameterValue("openid.mode") : null;

        Message response;
        String responseText;

        if ("associate".equals(mode)) {
            // --- process an association request ---
            response = manager.associationResponse(request);
            responseText = response.keyValueFormEncoding();

            //
        } else if ("checkid_setup".equals(mode)
                || "checkid_immediate".equals(mode)) {

            String opLocalId = null;

            AuthRequest authReq =
                    AuthRequest.createAuthRequest(request, manager.getRealmVerifier());


            // interact with the user and obtain data needed to continue

            ArrayList<String> userData = userInteraction(request);

            if (userData == null) {
                throw new Exception();
            }

            String userSelectedClaimedId = userData.get(ID_INDEX);
            Boolean authenticatedAndApproved = Boolean.parseBoolean(userData.get(IS_AUTH_INDEX));
            String email = userData.get(EMAIL_INDEX);


            // if the user chose a different claimed_id than the one in request
            if (userSelectedClaimedId != null
                    && userSelectedClaimedId.equals(authReq.getClaimed())) {
                //opLocalId = lookupLocalId(userSelectedClaimedId);
            }

            opLocalId = userSelectedClaimedId;
            response = manager.authResponse(request,
                    opLocalId,
                    userSelectedClaimedId,
                    authenticatedAndApproved.booleanValue(),
                    false); // Sign after we added extensions.

            if (response instanceof DirectError) {
                return directResponse(httpResp, response.keyValueFormEncoding());
            } else {
                //Auth Extension only extends checkid_immediate
                if (authReq.hasExtension(OpenIDAuthMessage.OPENID_NS_AUTH) && mode.equals("checkid_immediate")) {

                    MessageExtension ext = authReq.getExtension(OpenIDAuthMessage.OPENID_NS_AUTH);
                    if (ext instanceof OpenIDAuthRequest) {

                        OpenIDAuthRequest aReq = (OpenIDAuthRequest) ext;

                        String userID = aReq.getParameterValue("user-id");
                        String assocHandle = aReq.getParameterValue("session-id");

                        boolean userValid = isValidUser(userID);
                        boolean userAssociated = isAssociatedUser(userID, assocHandle);


                        if (userValid && userAssociated) {
                            //generate nonce
                            String nonce = manager.getNonceGenerator().next();

                            //KLUDGE FOR TESTING
                            //byte[] keyBytes = manager.getSharedAssociations().load(assocHandle).getMacKey().getEncoded();
                            //String key = byteToString(keyBytes);

                            String key = "batman";
                            
                            String id = userSelectedClaimedId;

                            String signed01 = getHash(id + assocHandle + nonce + key);

                            OpenIDAuth_CheckImed_Reply crep = OpenIDAuth_CheckImed_Reply.createFetchRequest();
                            crep.addAttribute("nonce", nonce);
                            crep.addAttribute("signature", signed01);

                            response.addExtension(crep);
                            System.out.println("woo");
                        }
                    }

                }


                if (authReq.hasExtension(AxMessage.OPENID_NS_AX)) {
                    MessageExtension ext = authReq.getExtension(AxMessage.OPENID_NS_AX);
                    if (ext instanceof FetchRequest) {
                        FetchRequest fetchReq = (FetchRequest) ext;
                        Map required = fetchReq.getAttributes(true);
                        //Map optional = fetchReq.getAttributes(false);
                        if (required.containsKey("email")) {
                            Map userDataExt = new HashMap();
                            //userDataExt.put("email", userData.get(3));

                            FetchResponse fetchResp =
                                    FetchResponse.createFetchResponse(fetchReq, userDataExt);
                            // (alternatively) manually add attribute values
                            fetchResp.addAttribute("email",
                                    "http://schema.openid.net/contact/email", email);
                            response.addExtension(fetchResp);
                        }
                    } else //if (ext instanceof StoreRequest)
                    {
                        throw new UnsupportedOperationException("TODO");
                    }
                }
                if (authReq.hasExtension(SRegMessage.OPENID_NS_SREG)) {
                    MessageExtension ext = authReq.getExtension(SRegMessage.OPENID_NS_SREG);
                    if (ext instanceof SRegRequest) {
                        SRegRequest sregReq = (SRegRequest) ext;
                        List required = sregReq.getAttributes(true);
                        //List optional = sregReq.getAttributes(false);
                        if (required.contains("email")) {
                            // data released by the user
                            Map userDataSReg = new HashMap();
                            //userData.put("email", "user@example.com");

                            SRegResponse sregResp = SRegResponse.createSRegResponse(sregReq, userDataSReg);
                            // (alternatively) manually add attribute values
                            sregResp.addAttribute("email", email);
                            response.addExtension(sregResp);
                        }
                    } else {
                        throw new UnsupportedOperationException("TODO");
                    }
                }

                // Sign the auth success message.
                // This is required as AuthSuccess.buildSignedList has a `todo' tag now.
                manager.sign((AuthSuccess) response);

                // caller will need to decide which of the following to use:

                // option1: GET HTTP-redirect to the return_to URL
                return response.keyValueFormEncoding();
                //return response.getDestinationUrl(true);
                //return response.wwwFormEncoding();

                // option2: HTML FORM Redirection
                //RequestDispatcher dispatcher =
                //        getServletContext().getRequestDispatcher("formredirection.jsp");
                //httpReq.setAttribute("prameterMap", response.getParameterMap());
                //httpReq.setAttribute("destinationUrl", response.getDestinationUrl(false));
                //dispatcher.forward(httpReq, httpResp);
                //return null;
            }
        } else if ("check_authentication".equals(mode)) {
            // --- processing a verification request ---
            response = manager.verify(request);
            responseText = response.keyValueFormEncoding();
        } else {
            // --- error response ---
            response = DirectError.createDirectError("Unknown request");
            responseText = response.keyValueFormEncoding();
        }
        // return the result to the user
        return responseText;
    }

    protected ArrayList<String> userInteraction(ParameterList request) throws ServerException {

        ArrayList<String> userData;

        if (request.getParameterValue("openid.ext1.user-id") != null) {

            userData = userDataStore.get(request.getParameterValue("openid.ext1.user-id"));

        } else if (request.getParameterValue("openid.claimed_id") != null) {

            userData = new ArrayList<String>();
            userData.add(request.getParameterValue("openid.claimed_id"));

        } else {
            userData = null;

        }
        
        return userData;
    }

    protected boolean isValidUser(String userName) {

        //Does the database contain this user?
        if (userDataStore.containsKey(userName)) {
            ArrayList<String> user = userDataStore.get(userName);
            //If so, is the user authenticated?
            if (Boolean.parseBoolean(user.get(IS_AUTH_INDEX))) {
                return true;

            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    protected boolean isAssociatedUser(String userName, String assoc_handle) {

        //Does the provided assoc_handle match the handle stored for the user?
        if (assocHandleStore.containsKey(assoc_handle)) {
            if (userDataStore.get(userName) == assocHandleStore.get(assoc_handle)) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    protected void populateUsers() {

        //Create first user
        ArrayList<String> user1 = new ArrayList<String>();

        user1.add("http://localhost:8080/SampleServer/SampleServlet");  //id
        user1.add("true");  //logged in
        user1.add("derekgourlay@gmail.com");  //email
        user1.add("password");  // password
        user1.add("123456789"); //assoc_handle


        userDataStore.put(user1.get(0), user1);
        assocHandleStore.put(user1.get(4), user1);

        ArrayList<String> user2 = new ArrayList<String>();

        user2.add("http://localhost:8080/SampleServer/myles");  //id
        user2.add("false");  //logged in
        user2.add("myles.archer2@gmail.com");  //email
        user2.add("password");  // password
        user2.add(""); //assoc_handle

        userDataStore.put(user2.get(0), user2);

    }

    public String getHash(String password) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        MessageDigest hash = MessageDigest.getInstance("SHA-256");
        //digest.reset();



        byte[] input = hash.digest(password.getBytes("UTF-8"));


        return byteToString(input);



    }

    public String byteToString(byte[] input) {

        StringBuffer sb = new StringBuffer();


        for (int i = 0; i
                < input.length; i++) {
            sb.append(Integer.toString((input[i] & 0xff) + 0x100, 16).substring(1));


        }
        return sb.toString();



    }

    private String directResponse(HttpServletResponse httpResp, String response)
            throws IOException {
        ServletOutputStream os = httpResp.getOutputStream();
        os.write(response.getBytes());
        os.close();



        return null;


    }

    public String createXrdsResponse() {

        XrdsDocumentBuilder documentBuilder = new XrdsDocumentBuilder();
        documentBuilder.addServiceElement("http://specs.openid.net/auth/2.0/server", manager.getOPEndpointUrl(), "10");
        documentBuilder.addServiceElement("http://specs.openid.net/auth/2.0/signon", manager.getOPEndpointUrl(), "20");
        documentBuilder.addServiceElement(AxMessage.OPENID_NS_AX, manager.getOPEndpointUrl(), "30");
        documentBuilder.addServiceElement(SRegMessage.OPENID_NS_SREG, manager.getOPEndpointUrl(), "40");



        return documentBuilder.toXmlString();


    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /** 
     * Handles the HTTP <code>GET</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            String responseText = processRequest(request, response);

            response.setContentType("text/html");
            OutputStream outputStream = response.getOutputStream();
            //
            outputStream.write(responseText.getBytes());
            outputStream.close();


        } catch (Exception e) {
            e.printStackTrace();


        }
    }

    /** 
     * Handles the HTTP <code>POST</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            String responseText = processRequest(request, response);
            response.setContentType("text/html");
            OutputStream outputStream = response.getOutputStream();
            outputStream.write(responseText.getBytes());
            outputStream.close();


        } catch (Exception e) {
            e.printStackTrace();


        }
    }

    /** 
     * Returns a short description of the servlet.
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";

    }// </editor-fold>
}

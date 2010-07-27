/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ubc.servlet;

import com.ubc.util.XrdsDocumentBuilder;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.openid4java.message.AuthRequest;
import org.openid4java.message.AuthSuccess;
import org.openid4java.message.DirectError;
import org.openid4java.message.Message;
import org.openid4java.message.MessageExtension;
import org.openid4java.message.OpenIDAuth.OpenIDAuthMessage;
import org.openid4java.message.OpenIDAuth.OpenIDAuthRequest;
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

    // instantiate a ServerManager object
    public ServerManager manager = new ServerManager();

    public SampleServlet() {
        this("http://localhost:8080/SampleServer/SampleServlet");
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
        } else if ("checkid_setup".equals(mode)
                || "checkid_immediate".equals(mode)) {
            // interact with the user and obtain data needed to continue
            List userData = userInteraction(request);

            //TESTING
            AuthRequest authReq =
                    AuthRequest.createAuthRequest(request, manager.getRealmVerifier());


            if (authReq.hasExtension(OpenIDAuthMessage.OPENID_NS_AUTH)) {

                MessageExtension ext = authReq.getExtension(OpenIDAuthMessage.OPENID_NS_AUTH);
                if (ext instanceof OpenIDAuthRequest) {

                    OpenIDAuthRequest aReq = (OpenIDAuthRequest) ext;
                    System.out.println("Success");


                }
            }








            //TESTING
            String userSelectedClaimedId = (String) userData.get(0);
            Boolean authenticatedAndApproved = (Boolean) userData.get(1);
            String email = (String) userData.get(2);

            // --- process an authentication request ---
            //AuthRequest authReq =
            //      AuthRequest.createAuthRequest(request, manager.getRealmVerifier());

            String opLocalId = null;
            // if the user chose a different claimed_id than the one in request
            if (userSelectedClaimedId != null
                    && userSelectedClaimedId.equals(authReq.getClaimed())) {
                //opLocalId = lookupLocalId(userSelectedClaimedId);
            }

            response = manager.authResponse(request,
                    opLocalId,
                    userSelectedClaimedId,
                    authenticatedAndApproved.booleanValue(),
                    false); // Sign after we added extensions.

            if (response instanceof DirectError) {
                return directResponse(httpResp, response.keyValueFormEncoding());
            } else {
                if (authReq.hasExtension(OpenIDAuthMessage.OPENID_NS_AUTH)) {

                    MessageExtension ext = authReq.getExtension(OpenIDAuthMessage.OPENID_NS_AUTH);
                    if (ext instanceof OpenIDAuthRequest) {

                        OpenIDAuthRequest aReq = (OpenIDAuthRequest) ext;
                        System.out.println("Success");


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
                return response.getDestinationUrl(true);

                // option2: HTML FORM Redirection
                //RequestDispatcher dispatcher =
                //        getServletContext().getRequestDispatcher("formredirection.jsp");
                //httpReq.setAttribute("prameterMap", response.getParameterMap());
                //httpReq.setAttribute("destinationUrl", response.getDestinationUrl(false));
                //dispatcher.forward(request, response);
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

    protected List userInteraction(ParameterList request) throws ServerException {

        //TODO:  IMPLEMENT
        ArrayList<String> values = new ArrayList<String>();

        return values;
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
            processRequest(request, response);
            System.out.println("Ran doGet");
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
            processRequest(request, response);
            System.out.println("Ran doPost");
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

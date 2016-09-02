package org.eclipse.smarthome.binding.wemo.test

import static org.hamcrest.CoreMatchers.*
import static org.junit.Assert.*
import static org.junit.matchers.JUnitMatchers.*

import javax.servlet.ServletException
import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

import org.eclipse.smarthome.binding.wemo.internal.http.WemoHttpCall;
import org.eclipse.smarthome.test.OSGiTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.osgi.service.http.HttpService

class WemoHttpCallOSGiTest extends OSGiTest{

    def ORG_OSGI_SERVICE_HTTP_PORT = 8080
    def DESTINATION_URL = "http://127.0.0.1:${ORG_OSGI_SERVICE_HTTP_PORT}"
    def SERVLET_URL = "/test"
    def servlet = null

    protected registerServlet(String ServletURL, HttpServlet servlet) {
        //Register Servlet that will mock the device action responses
        HttpService httpService = getService(HttpService.class)
        assertThat(httpService, is(notNullValue()))
        httpService.registerServlet(ServletURL, servlet, null, null)
    }

    protected void unregisterServlet(def servletURL) {
        //Unregister the servlet
        HttpService httpService = getService(HttpService.class)
        httpService.unregister(servletURL)
    }
    @Before
    void setUp() {
        servlet =  new MockHttpServlet()
        registerServlet(SERVLET_URL,servlet)
    }

    @Test
    void 'assert post request contains correct content header'() {
        String wemoLightID = "testID"
        
        String soapHeader = "\"urn:Belkin:service:bridge:1#GetDeviceStatus\""
        String content = "<?xml version=\"1.0\"?>" +
                    "<s:Envelope xmlns:s=\"http://schemas.xmlsoap.org/soap/envelope/\" s:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\">" +
                    "<s:Body>" + "<u:GetDeviceStatus xmlns:u=\"urn:Belkin:service:bridge:1\">" + "<DeviceIDs>" +
                    wemoLightID + "</DeviceIDs>" + "</u:GetDeviceStatus>" + "</s:Body>" + "</s:Envelope>";
        
        WemoHttpCall.executeCall(DESTINATION_URL + SERVLET_URL, soapHeader, content)
        waitForAssert ({
            assertThat servlet.contentType, is(notNullValue())
            assertThat servlet.contentType, is(WemoHttpCall.contentHeader)
        },2000)
    }

    @After
    void tearDown() {
        unregisterServlet(SERVLET_URL);
    }

    class MockHttpServlet extends HttpServlet {
        def contentType
        
        @Override
        protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
            contentType = req.getContentType()
        }
    }
}

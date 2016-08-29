package org.eclipse.smarthome.binding.wemo.test;

import static org.hamcrest.CoreMatchers.*
import static org.junit.Assert.*
import static org.junit.matchers.JUnitMatchers.*

import org.eclipse.smarthome.test.OSGiTest
import org.junit.Test
import org.osgi.service.http.HttpService

public class WemoHandlerOSGiTest extends OSGiTest {

    @Test
    void 'test HttpService is available'() {
        HttpService httpService = getService(HttpService.class)
        assertThat(httpService, is(notNullValue()))
    }
}

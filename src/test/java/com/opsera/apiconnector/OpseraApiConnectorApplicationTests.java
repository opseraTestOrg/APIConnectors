package com.opsera.apiconnector;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author Purusothaman
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class OpseraApiConnectorApplicationTests {

    @Test
    public void testAssertNull() {
        assertNull("should be null", null);
    }

    @Test
    public void testAssertNotNull() {
        assertNotNull("should be not null");
    }

}

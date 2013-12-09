/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package chronic;

import chronic.bundle.Bundle;
import chronic.entitytype.OrgRoleType;
import chronic.type.AlertType;
import chronic.type.StatusType;
import java.util.ResourceBundle;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author evans
 */
public class BundleTest {
    static Logger logger = LoggerFactory.getLogger(BundleTest.class);
    
    @Test
    public void test() {
        verify(AlertType.class);
        verify(StatusType.class);
        verify(OrgRoleType.class);
    } 

    private void verify(Class enumClass) {
        ResourceBundle bundle = Bundle.get(enumClass);
        for (Object value : enumClass.getEnumConstants()) {
            logger.info("label {} {}", value.toString(), bundle.getString(value.toString()));
        }
    }         
}
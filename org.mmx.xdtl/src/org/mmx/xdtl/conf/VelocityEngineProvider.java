package org.mmx.xdtl.conf;

import java.io.InputStream;
import java.util.Properties;

import org.apache.velocity.app.VelocityEngine;

import com.google.inject.Provider;

public class VelocityEngineProvider implements Provider<VelocityEngine> {

    @Override
    public VelocityEngine get() {
        try {
            return createVelocityEngine();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private VelocityEngine createVelocityEngine() throws Exception {
        VelocityEngine velocityEngine = new VelocityEngine();

        Properties props = new Properties();        
        InputStream is = this.getClass().getResourceAsStream("/velocity.xml");
        if (is != null) {
            try {
                props.loadFromXML(is);
            } finally {
                is.close();
            }
        }
        
        velocityEngine.init(props);
        return velocityEngine;
    }
}

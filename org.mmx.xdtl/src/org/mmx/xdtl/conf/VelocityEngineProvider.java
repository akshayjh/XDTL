package org.mmx.xdtl.conf;

import org.apache.velocity.app.VelocityEngine;

import com.google.inject.Provider;

public class VelocityEngineProvider implements Provider<VelocityEngine> {

    @Override
    public VelocityEngine get() {
        return createVelocityEngine();
    }

    private VelocityEngine createVelocityEngine() {
        return new VelocityEngine();
    }
}

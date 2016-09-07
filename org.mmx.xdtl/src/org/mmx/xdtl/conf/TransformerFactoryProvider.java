package org.mmx.xdtl.conf;

import javax.xml.transform.ErrorListener;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;

import org.apache.log4j.Logger;
import org.mmx.xdtl.log.XdtlLogger;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.name.Named;

public class TransformerFactoryProvider implements Provider<TransformerFactory> {
    private String m_factoryClassName;

    @Inject
    public TransformerFactoryProvider(@Named("transformerFactory.class") String factoryClassName) {
        m_factoryClassName = factoryClassName;
    }

    @Override
    public TransformerFactory get() {
        return createTransformerFactory(); // Always create new instance since factory attributes may change.
    }

    private TransformerFactory createTransformerFactory() {
        TransformerFactory result;

        if (m_factoryClassName != null && m_factoryClassName.length() != 0) {
            result = TransformerFactory.newInstance(m_factoryClassName, null);
        } else {
            result = TransformerFactory.newInstance();
        }

        result.setErrorListener(new TransformerErrorListener());
        return result;
    }

    private static class TransformerErrorListener implements ErrorListener {
        private static final Logger logger = XdtlLogger.getLogger("xdtl.xml.transform.ErrorListener");

        @Override
        public void warning(TransformerException exception)
                throws TransformerException {
            logger.warn(exception.getMessageAndLocation());
        }

        @Override
        public void error(TransformerException exception)
                throws TransformerException {
            logger.error(exception.getMessageAndLocation());
            throw exception;
        }

        @Override
        public void fatalError(TransformerException exception)
                throws TransformerException {
            logger.error(exception.getMessageAndLocation()); // It's not fatal to XDTL
            throw exception;
        }
    }
}

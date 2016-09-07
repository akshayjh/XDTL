package org.mmx.xdtl.parser.element;

import org.mmx.xdtl.model.Element;
import org.mmx.xdtl.model.Config;
import org.mmx.xdtl.parser.AbstractElementHandler;
import org.mmx.xdtl.parser.Attributes;

public class ConfigHandler extends AbstractElementHandler {
    private Config m_config;

    @Override
    public Element endElement() {
        return m_config;
    }

    @Override
    public void startElement(Attributes attr) {
        m_config = new Config(attr.getStringValue("href"),
                attr.getStringValue("encoding"));
    }
}

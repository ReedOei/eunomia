package com.reedoei.eunomia.util;

import org.dom4j.Element;

public interface XmlSerializable<T> {
    Element toXml();
    T fromXml(final Element element);
}

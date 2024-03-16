package org.tims.tirechange.model;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JacksonXmlRootElement(localName = "availableTime")
public class LondonTireChangeTime {

    @JacksonXmlProperty(isAttribute = true)
    private String uuid;

    @JacksonXmlProperty
    private String time;
}

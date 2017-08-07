package com.gcp.poc.f2b.generator.model;

import com.fasterxml.jackson.xml.annotate.JacksonXmlProperty;
import com.fasterxml.jackson.xml.annotate.JacksonXmlRootElement;

@JacksonXmlRootElement(localName = "counterPartyEntity")
public class Party {
    @JacksonXmlProperty(localName = "messageHeader")
    private PartyHeader header;

    @JacksonXmlProperty(localName = "messageBody")
    private PartyBody body;
}


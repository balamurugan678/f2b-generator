package com.gcp.poc.f2b.generator.model;

import com.fasterxml.jackson.xml.annotate.JacksonXmlProperty;
import com.fasterxml.jackson.xml.annotate.JacksonXmlRootElement;

@JacksonXmlRootElement(localName = "counterPartyEntity")
public class Party {

    // Note: this is set in code, not from XML
    private String partyId;

    @JacksonXmlProperty(localName = "messageHeader")
    private PartyHeader header;

    @JacksonXmlProperty(localName = "messageBody")
    private PartyBody body;

    public PartyHeader getHeader() {
        return header;
    }

    public PartyBody getBody() {
        return body;
    }

    public String getPartyId() {
        return partyId;
    }

    public void setPartyId(String partyId) {
        this.partyId = partyId;
    }
}


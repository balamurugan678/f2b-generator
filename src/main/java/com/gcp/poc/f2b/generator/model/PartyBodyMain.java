package com.gcp.poc.f2b.generator.model;

public class PartyBodyMain {
    private String partyId;
    private String partyBIC;
    private String version;
    private String verificationStatus;
    private String entityStatus;
    private String effectiveFrom;

    public String getPartyId() {
        return partyId;
    }

    public void setPartyId(String partyId) {
        this.partyId = partyId;
    }

    public String getPartyBIC() {
        return partyBIC;
    }

    public void setPartyBIC(String partyBIC) {
        this.partyBIC = partyBIC;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getVerificationStatus() {
        return verificationStatus;
    }

    public void setVerificationStatus(String verificationStatus) {
        this.verificationStatus = verificationStatus;
    }

    public String getEntityStatus() {
        return entityStatus;
    }

    public void setEntityStatus(String entityStatus) {
        this.entityStatus = entityStatus;
    }

    public String getEffectiveFrom() {
        return effectiveFrom;
    }

    public void setEffectiveFrom(String effectiveFrom) {
        this.effectiveFrom = effectiveFrom;
    }
}

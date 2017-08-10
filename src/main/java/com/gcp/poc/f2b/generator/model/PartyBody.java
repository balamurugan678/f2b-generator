package com.gcp.poc.f2b.generator.model;

public class PartyBody {
    private PartyBodyMain main;
    private String legalName;

    private String subsidiary;
    private String countryOfHeadOffice;
    private String countryOfIncorporation;
    private String primarySicCode;
    private String legality;
    private String customerBusinessType;
    private String altName;

    public PartyBodyMain getMain() {
        return main;
    }

    public void setMain(PartyBodyMain main) {
        this.main = main;
    }

    public String getLegalName() {
        return legalName;
    }

    public void setLegalName(String legalName) {
        this.legalName = legalName;
    }

    public String getSubsidiary() {
        return subsidiary;
    }

    public void setSubsidiary(String subsidiary) {
        this.subsidiary = subsidiary;
    }

    public String getCountryOfHeadOffice() {
        return countryOfHeadOffice;
    }

    public void setCountryOfHeadOffice(String countryOfHeadOffice) {
        this.countryOfHeadOffice = countryOfHeadOffice;
    }

    public String getCountryOfIncorporation() {
        return countryOfIncorporation;
    }

    public void setCountryOfIncorporation(String countryOfIncorporation) {
        this.countryOfIncorporation = countryOfIncorporation;
    }

    public String getPrimarySicCode() {
        return primarySicCode;
    }

    public void setPrimarySicCode(String primarySicCode) {
        this.primarySicCode = primarySicCode;
    }

    public String getLegality() {
        return legality;
    }

    public void setLegality(String legality) {
        this.legality = legality;
    }

    public String getCustomerBusinessType() {
        return customerBusinessType;
    }

    public void setCustomerBusinessType(String customerBusinessType) {
        this.customerBusinessType = customerBusinessType;
    }

    public String getAltName() {
        return altName;
    }

    public void setAltName(String altName) {
        this.altName = altName;
    }
}


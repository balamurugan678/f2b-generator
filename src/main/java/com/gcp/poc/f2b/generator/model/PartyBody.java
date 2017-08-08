package com.gcp.poc.f2b.generator.model;

public class PartyBody {
    private PartyBodyMain main;
    private String legalName;

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
}


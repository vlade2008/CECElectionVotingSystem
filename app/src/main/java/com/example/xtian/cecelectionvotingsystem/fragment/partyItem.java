package com.example.xtian.cecelectionvotingsystem.fragment;

/**
 * Created by Xtian on 11/25/2015.
 */
public class partyItem {

    String partyId;
    String partyName;

    public partyItem(String partyId, String partyName) {
        super();
        this.partyId=partyId;
        this.partyName=partyName;
    }



    public String getPartyId() {
        return partyId;
    }

    public void setPartyId(String partyId) {
        this.partyId = partyId;
    }

    public String getPartyName() {
        return partyName;
    }

    public void setPartyName(String partyName) {
        this.partyName = partyName;
    }


}

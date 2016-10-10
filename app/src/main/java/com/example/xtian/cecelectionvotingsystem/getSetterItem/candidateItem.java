package com.example.xtian.cecelectionvotingsystem.getSetterItem;

import android.graphics.Bitmap;

/**
 * Created by Xtian on 12/1/2015.
 */
public class candidateItem {

    Bitmap imageIcon;

    String student_ID ;    //listview display candidate ID
    String student_Name ;  //listview display candidate name
    String position_Name; //listview display candidate position_Name
    String party_Name;  //listview display candidate party_Name
    String candidate_vote;  //listview display candidate party_Name

    public Bitmap getImageIcon() {
        return imageIcon;
    }

    public void setImageIcon(Bitmap imageIcon) {
        this.imageIcon = imageIcon;
    }

    public String getStudent_ID() {
        return student_ID;
    }

    public void setStudent_ID(String student_ID) {
        this.student_ID = student_ID;
    }

    public String getStudent_Name() {
        return student_Name;
    }

    public void setStudent_Name(String student_Name) {
        this.student_Name = student_Name;
    }

    public String getPosition_Name() {
        return position_Name;
    }

    public void setPosition_Name(String position_Name) {
        this.position_Name = position_Name;
    }

    public String getParty_Name() {
        return party_Name;
    }

    public void setParty_Name(String party_Name) {
        this.party_Name = party_Name;
    }

    public String getCandidate_vote() {
        return candidate_vote;
    }

    public void setCandidate_vote(String candidate_vote) {
        this.candidate_vote = candidate_vote;
    }

    public candidateItem(String student_id, String student_name, String position_name, String party_name, String candidate_vote, Bitmap imageIcon) {
        super();
        this.student_ID=student_id;
        this.student_Name=student_name;
        this.position_Name=position_name;
        this.party_Name=party_name;
        this.candidate_vote=candidate_vote;
        this.imageIcon=imageIcon;
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package QuizzGame;

import java.io.Serializable;

/**
 *
 * @author Leandro
 */
public class Participant implements Serializable,Comparable<Participant>{
    private String IPAddress;
    private int score;
    private String name;
    
    private int id;
    private double timestamp;
    
    private boolean ready;
    private boolean token;

    public double getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(double timestamp) {
        this.timestamp = timestamp;
    }

    public boolean hasToken() {
        return token;
    }

    public void setToken(boolean token) {
        this.token = token;
    }

    public boolean isReady() {
        return ready;
    }

    public void setReady(boolean ready) {
        this.ready = ready;
    }

    public String getIPAddress() {
        return IPAddress;
    }

    public void setIPAddress(String IPAddress) {
        this.IPAddress = IPAddress;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    
    public void addScore(int score){
    
        this.setScore(this.getScore()+score);
    
    }
    
    public void subtractScore(int score){
    
        this.setScore(this.getScore()-score);
    
    }
    
    public Participant(String name,String IPAddress, int id){
    
        this.setIPAddress(IPAddress);
        this.setName(name);
        this.setId(id);
        this.setScore(0);
        this.setReady(false);
    
    }
    
    @Override
    public int compareTo(Participant participant){
    
        return Integer.valueOf(this.score).compareTo(participant.getScore());
    
    }
    
    @Override
    public String toString(){
    
        String string = "\n\nParticipant ID: "+this.getId();
        string += "\nParticipant Name: "+this.getName();
        string += "\nScore: "+this.getScore();
        string += "\nIs questioner: "+this.hasToken();
        
        return string;
    
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package QuizzGame;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Scanner;

/**
 *
 * @author Leandro
 */
public class QuizzGame implements Comm, Serializable{
    
    private ArrayList<Participant> participants = new ArrayList<Participant>();
    private int id;
    
    private Question currentQuestion;
    
    private Participant currentServer;
    private Participant nextServer;
    
    private boolean enabled;
    boolean everybodyReady;
    
    public void printParticipants(){
        
        for(Participant participant:participants)
            System.out.println(participant);
    
    }
    
    public int generateId(){
    
        this.id++;
        return id;
        
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    
    public QuizzGame(){
    
        this.id = 0;
        everybodyReady = false;
        
    }
    
    public void setup(Participant participant){
    
        this.currentServer = participant;
        if(participants.size()>1)
            this.nextServer = participants.get(participants.indexOf(this.currentServer)+1);
        participants.get(participants.indexOf(this.currentServer)).setReady(true);
    
    }
    
    @Override
    public Participant addParticipant(String IPAddress, String name){
    
        Participant participant = new Participant(name,IPAddress,this.generateId());
        this.participants.add(participant);
        System.out.println(name+" entered the game");
        return participant;
        
    }
    
    public void changeReady(Participant participant,boolean state){
    
        participants.get(participants.indexOf(participant)).setReady(state);
        
        String string = participant.getName()+" is";
        if(!state)
            string += " not";
        string += " ready.";
        
        System.out.println(string);
    
    }
    
    @Override
    public boolean amINext(Participant participant){
    
        int nextIndex = this.participants.indexOf(nextServer);
        int requestIndex = this.participants.indexOf(participant);
        if(nextIndex==requestIndex)
            return true;
        return false;
    
            
    }
    
    @Override
    public QuizzGame cycleServer(){
    
        this.currentServer = this.nextServer;
        if(this.participants.indexOf(this.currentServer)==this.participants.size()-1)
            this.nextServer = this.participants.get(0);
        else
            this.nextServer = this.participants.get(this.participants.indexOf(this.currentServer)+1);
        
        this.enabled = false;
        
        return this;
    
    }
    
    @Override
    public boolean isEverybodyReady() {
        return everybodyReady;
    }

    @Override
    public Question getCurrentQuestion() {
        return currentQuestion;
    }
    
    @Override
    public void sendAnswer(String answer,Participant participant){
    
        if(this.currentQuestion.checkAnswer(answer)){
            participants.get(participants.indexOf(participant)).addScore(1);
            participants.get(participants.indexOf(currentServer)).subtractScore(1);
        }
        else{
            participants.get(participants.indexOf(participant)).subtractScore(1);
            participants.get(participants.indexOf(currentServer)).addScore(1);
        }
        System.out.println(participants.get(participants.indexOf(participant)));
    }
    
    public void serverMenu(){
    
        Scanner scanner = new Scanner(System.in);
        System.out.println("Please, write your question.");
        String question = scanner.nextLine();
        System.out.println("What is the answer to that question?");
        String answer = scanner.nextLine();
        
        Question questionAnswer = new Question(question,answer);
        
    
    }
    
    public boolean checkParticipantsReady(){
    
        for(Participant participant:participants)
            if(!participant.isReady())
                return false;
        return true;
    
    }
    
    public void makeQuestion(Question question){
    
        
        
        while(!everybodyReady){
        
            
            everybodyReady = this.checkParticipantsReady();
        }
    
        
    
    }
}

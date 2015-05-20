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
    
    public void printParticipants(){
        System.out.println("Participants: ");
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

    @Override
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    
    public QuizzGame(){
    
        this.id = 0;
        this.currentQuestion = null;
        
    }
    
    public void setup(Participant participant){
    
        this.currentServer = participant;
        if(participants.size()>1)
            this.nextServer = participants.get(participants.indexOf(this.findParticipantById(this.currentServer.getId()))+1);
        
    
    }
    
    public Participant findParticipantById(int id){
    
        for(Participant p:participants)
            if(p.getId()==id)
                return p;
        return null;
    
    }
    
    @Override
    public Participant addParticipant(String IPAddress, String name){
    
        Participant participant = new Participant(name,IPAddress,this.generateId());
        this.participants.add(participant);
        System.out.println(name+" entered the game");
        return participant;
        
    }
    
    @Override
    public void changeReady(Participant participant,boolean state){
    
        
        participants.get(participants.indexOf(this.findParticipantById(participant.getId()))).setReady(state);
        
        if(state)
            System.out.println(participant.getName()+" is ready.");
    
    }
    
    @Override
    public boolean amINext(Participant participant){
    
        int nextIndex = this.participants.indexOf(this.findParticipantById(nextServer.getId()));
        int requestIndex = this.participants.indexOf(this.findParticipantById(participant.getId()));
        if(nextIndex==requestIndex)
            return true;
        return false;
    
            
    }
    
    @Override
    public boolean isQuestionReady(){
    
        if(currentQuestion == null)
            return false;
        return true;
        
    }
    
    @Override
    public QuizzGame cycleServer(){
    
        this.currentServer = this.nextServer;
        if(this.participants.indexOf(this.findParticipantById(this.currentServer.getId()))==this.participants.size()-1)
            this.nextServer = this.participants.get(0);
        else
            this.nextServer = this.participants.get(this.participants.indexOf(this.findParticipantById(this.currentServer.getId()))+1);
        
        return this;
    
    }
    
    @Override
    public Question getCurrentQuestion() {
        return currentQuestion;
    }
    
    @Override
    public boolean sendAnswer(String answer,Participant participant,double elapsedTime){
    
        participants.get(participants.indexOf(this.findParticipantById(participant.getId()))).setReady(false);
        participants.get(participants.indexOf(this.findParticipantById(this.currentServer.getId()))).setReady(false);
        
        if(this.currentQuestion.checkAnswer(answer)){
            participants.get(participants.indexOf(this.findParticipantById(participant.getId()))).addScore(1);
            participants.get(participants.indexOf(this.findParticipantById(currentServer.getId()))).subtractScore(1);
            System.out.println(participants.get(participants.indexOf(this.findParticipantById(participant.getId()))).getName()+" got the question right!");
            return true;
        }
        else{
            participants.get(participants.indexOf(this.findParticipantById(participant.getId()))).subtractScore(1);
            participants.get(participants.indexOf(this.findParticipantById(currentServer.getId()))).addScore(1);
            System.out.println(participants.get(participants.indexOf(this.findParticipantById(participant.getId()))).getName()+" got the question wrong!");
            return false;
        }
    }
    
    public void serverMenu(){
    
        Scanner scanner = new Scanner(System.in);
        System.out.println("Are you ready to write your question? (if yes, press enter)");
        scanner.nextLine();
        
        participants.get(participants.indexOf(this.findParticipantById(this.currentServer.getId()))).setReady(true);
        while(!checkParticipantsReady());
        this.printParticipants();
        
        System.out.println("Please, write your question.");
        String question = scanner.nextLine();
        System.out.println("What is the answer to that question?");
        String answer = scanner.nextLine();
        
        this.currentQuestion = new Question(question,answer);
        while(!this.checkParticipantsNotReady());
        this.currentQuestion = null;
    
    }
    
    @Override
    public boolean checkParticipantsReady(){
    
        for(Participant participant:participants)
            if(!participant.isReady())
                return false;
        return true;
    
    }
    
    @Override
    public boolean checkParticipantsNotReady(){
    
        for(Participant participant:participants)
            if(participant.isReady())
                return false;
        return true;
    
    }
    
}

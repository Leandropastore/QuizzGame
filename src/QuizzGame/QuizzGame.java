/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package QuizzGame;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

/**
 *
 * @author Leandro
 */
public class QuizzGame implements Comm, Serializable {

    private ArrayList<Participant> participants = new ArrayList<Participant>();
    private int id;

    private Question currentQuestion;

    private Participant currentQuestioner;
    private Participant nextQuestioner;
    private Participant lastAnswer;

    private boolean cycled;

    @Override
    public boolean isCycled() {
        return cycled;
    }
    
    @Override
    public void setCycled(boolean cycled) {
        this.cycled = cycled;
    }
    
    @Override
    public String printParticipants() {
        String string = "Participants:";
        for (Participant participant : participants) 
            string += participant.toString();
        return string;

    }

    public int generateId() {

        this.id++;
        return id;

    }

    @Override
    public void setCurrentQuestion(Question currentQuestion) {
        this.currentQuestion = currentQuestion;
    }

    public QuizzGame() {

        this.id = 0;
        this.currentQuestion = null;
        this.cycled = false;

    }
    
    public void setup(Participant participant) {

        this.currentQuestioner = participant;
        if (participants.size() > 1) {
            this.nextQuestioner = participants.get(participants.indexOf(this.findParticipantById(this.currentQuestioner.getId())) + 1);
        }

    }

    public Participant findBiggestTimestamp() {

        Participant score = participants.get(0);
        for (Participant p : participants) {
            if (p.getTimestamp() > score.getTimestamp()&&!p.hasToken()) {
                score = p;
            }
        }
        return score;

    }
    
    public Participant findSmallestTimestamp() {

        Participant score = participants.get(0);
        for (Participant p : participants) {
            if (p.getTimestamp() < score.getTimestamp()&&!p.hasToken()) {
                score = p;
            }
        }
        return score;

    }
    
    @Override
    public Participant findParticipantById(int id) {

        for (Participant p : participants) {
            if (p.getId() == id) {
                return p;
            }
        }
        return null;

    }

    public void placeToken(boolean state, Participant participant) {

        participants.get(participants.indexOf(this.findParticipantById(participant.getId()))).setToken(state);

    }

    @Override
    public Participant addParticipant(String IPAddress, String name) {

        Participant participant = new Participant(name, IPAddress, this.generateId());
        this.participants.add(participant);
        System.out.println(name + " entered the game");
        return participant;

    }

    @Override
    public void removeParticipant(Participant participant) {

        
        if(participant.getId()==this.nextQuestioner.getId()){
            if(participants.indexOf(this.findParticipantById(participant.getId()))==participants.size()-1)
                this.nextQuestioner = participants.get(0);
            else
                this.nextQuestioner = participants.get(participants.indexOf(this.findParticipantById(participant.getId()))+1);
        }
        this.participants.remove(this.findParticipantById(participant.getId()));
        System.out.println(participant.getName() + " left the game");

    }
    
    @Override
    public void changeReady(Participant participant, boolean state) {

        participants.get(participants.indexOf(this.findParticipantById(participant.getId()))).setReady(state);

        if (state) {
            System.out.println(participant.getName() + " is ready.");
        }

    }

    @Override
    public boolean isQuestionReady() {

        if (currentQuestion == null) {
            return false;
        }
        return true;

    }

    @Override
    public void cycleQuestioner() {

        participants.get(participants.indexOf(this.findBiggestTimestamp())).subtractScore(1);
        participants.get(participants.indexOf(this.findSmallestTimestamp())).addScore(1);
        this.placeToken(false, this.findParticipantById(this.currentQuestioner.getId()));
        
        if(participants.indexOf(this.findParticipantById(this.nextQuestioner.getId()))!=0){
            this.currentQuestioner = this.nextQuestioner;
            this.participants.get(participants.indexOf(this.findParticipantById(currentQuestioner.getId()))).setToken(true);
            if (this.participants.indexOf(this.findParticipantById(this.currentQuestioner.getId())) == this.participants.size() - 1) {
                this.nextQuestioner = this.participants.get(0);
            } else {
                this.nextQuestioner = this.participants.get(this.participants.indexOf(this.findParticipantById(this.currentQuestioner.getId())) + 1);
            }
        }
        else{
            Collections.sort(participants);
            this.currentQuestioner = participants.get(0);
            this.nextQuestioner = participants.get(1);
            this.participants.get(participants.indexOf(this.findParticipantById(currentQuestioner.getId()))).setToken(true);
        }
    }

    @Override
    public Question getCurrentQuestion() {
        return currentQuestion;
    }

    @Override
    public Participant checkLastAnswer() {

        Participant temp;
        if (this.lastAnswer != null) {
            temp = this.lastAnswer;
            this.lastAnswer = null;
            return temp;
        }
        return null;

    }

    @Override
    public boolean sendAnswer(String answer, Participant participant, double elapsedTime) {

        participants.get(participants.indexOf(this.findParticipantById(participant.getId()))).setTimestamp(elapsedTime);
        if (this.currentQuestion.checkAnswer(answer)) {
            participants.get(participants.indexOf(this.findParticipantById(participant.getId()))).addScore(1);
            participants.get(participants.indexOf(this.findParticipantById(currentQuestioner.getId()))).subtractScore(1);
            this.lastAnswer = participant;
            this.lastAnswer.setReady(true);
            System.out.println(participants.get(participants.indexOf(this.findParticipantById(participant.getId()))).getName() + " got the question right!");
            participants.get(participants.indexOf(this.findParticipantById(participant.getId()))).setReady(false);
            participants.get(participants.indexOf(this.findParticipantById(this.currentQuestioner.getId()))).setReady(false);
            return true;
        } else {
            participants.get(participants.indexOf(this.findParticipantById(participant.getId()))).subtractScore(1);
            participants.get(participants.indexOf(this.findParticipantById(currentQuestioner.getId()))).addScore(1);
            this.lastAnswer = participant;
            this.lastAnswer.setReady(false);
            System.out.println(participants.get(participants.indexOf(this.findParticipantById(participant.getId()))).getName() + " got the question wrong!");
            participants.get(participants.indexOf(this.findParticipantById(participant.getId()))).setReady(false);
            participants.get(participants.indexOf(this.findParticipantById(this.currentQuestioner.getId()))).setReady(false);
            return false;
        }
    }

    @Override
    public boolean checkParticipantsReady() {

        for (Participant participant : participants) {
            if (!participant.isReady()) {
                return false;
            }
        }
        return true;

    }

    @Override
    public boolean checkParticipantsNotReady() {

        for (Participant participant : participants) {
            if (participant.isReady()) {
                return false;
            }
        }
        return true;

    }

    @Override
    public void setLastAnswer(Participant participant) {
    
        this.lastAnswer = participant;
    
    }

}

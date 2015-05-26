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
    
    public void printParticipants() {
        System.out.println("Participants: ");
        for (Participant participant : participants) {
            System.out.println(participant);
        }

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

    @Override
    public Participant findParticipantById(int id) {

        for (Participant p : participants) {
            if (p.getId() == id) {
                return p;
            }
        }
        return null;

    }

    @Override
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
    public void changeReady(Participant participant, boolean state) {

        participants.get(participants.indexOf(this.findParticipantById(participant.getId()))).setReady(state);

        if (state) {
            System.out.println(participant.getName() + " is ready.");
        }

    }

    @Override
    public boolean amINext(Participant participant) {

        int nextIndex = this.participants.indexOf(this.findParticipantById(nextQuestioner.getId()));
        int requestIndex = this.participants.indexOf(this.findParticipantById(participant.getId()));
        if (nextIndex == requestIndex) {
            return true;
        }
        return false;

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

        this.currentQuestioner = this.nextQuestioner;
        this.participants.get(participants.indexOf(this.findParticipantById(currentQuestioner.getId()))).setToken(true);
        if (this.participants.indexOf(this.findParticipantById(this.currentQuestioner.getId())) == this.participants.size() - 1) {
            this.nextQuestioner = this.participants.get(0);
            System.out.println("weeeeeeeeeeeee if");
            System.out.println(this.currentQuestioner);
            System.out.println(this.nextQuestioner);
        } else {
            this.nextQuestioner = this.participants.get(this.participants.indexOf(this.findParticipantById(this.currentQuestioner.getId())) + 1);
            System.out.println("weeeeeeeeeeeee else");
            System.out.println(this.currentQuestioner);
            System.out.println(this.nextQuestioner);
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
    /*
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
        
    
     }*/

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
    public String fetchNextServerAddress() {

        return this.nextQuestioner.getIPAddress();

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

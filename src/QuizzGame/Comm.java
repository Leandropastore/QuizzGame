/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package QuizzGame;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 *
 * @author Leandro
 */
public interface Comm extends Remote{
    
    public Participant addParticipant(String IPAddress, String name) throws RemoteException;
    public void cycleQuestioner() throws RemoteException;
    public Question getCurrentQuestion() throws RemoteException;
    public void setCurrentQuestion(Question currentQuestion) throws RemoteException;
    public boolean sendAnswer(String answer,Participant participant,double elapsedTime) throws RemoteException;
    public void changeReady(Participant participant,boolean state) throws RemoteException;
    public boolean checkParticipantsReady() throws RemoteException;
    public boolean checkParticipantsNotReady() throws RemoteException;
    public boolean isQuestionReady() throws RemoteException;
    public Participant findParticipantById(int id) throws RemoteException;
    public Participant checkLastAnswer() throws RemoteException;
    public void setLastAnswer(Participant participant) throws RemoteException;
    public boolean isCycled() throws RemoteException;
    public void setCycled(boolean cycled) throws RemoteException;
    public String printParticipants() throws RemoteException;
    public void removeParticipant(Participant participant) throws RemoteException;
}

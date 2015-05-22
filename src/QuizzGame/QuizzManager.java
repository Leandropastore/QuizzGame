/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package QuizzGame;

import java.net.Inet4Address;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Leandro
 */
public class QuizzManager {

    public static void main(String[] args) throws java.net.UnknownHostException, RemoteException {

        QuizzGame local = new QuizzGame();
        Participant localParticipant = null;
        Participant lastAnswer = null;
        Comm remote = null;
        String answer;
        String response;
        String name;
        String ipAddress = null;
        Registry serverRegistry;
        Registry clientRegistry;

        long tStart;
        long tEnd;
        long tDelta;
        double elapsedSeconds;

        Scanner scanner = new Scanner(System.in);

        System.out.println("\nAre you starting as a server? (yes / no)");
        response = scanner.nextLine();
        if (response.equals("no")) {
            System.out.println("\nPlease, provide the IP address to connect: ");
            ipAddress = scanner.nextLine();
        }

        System.out.println("\nWhat is your name?");
        name = scanner.nextLine();

        System.out.println(Inet4Address.getLocalHost().getHostAddress());

        if (response.equals("yes")) {
            try {
                Comm stub = (Comm) UnicastRemoteObject.exportObject(local, 0);
                LocateRegistry.createRegistry(1099);
                serverRegistry = LocateRegistry.getRegistry();
                serverRegistry.rebind("quizzgame", stub);
                localParticipant = local.addParticipant(Inet4Address.getLocalHost().getHostAddress(), name);
                local.placeToken(true, localParticipant);
                System.out.println("Press enter when you have finished waiting for other players.");
                scanner.nextLine();
                local.setup(localParticipant);
            } catch (RemoteException r) {
                System.err.println("Error: " + r);
            }
        } else {
            try {
                clientRegistry = LocateRegistry.getRegistry(ipAddress);
                remote = (Comm) clientRegistry.lookup("quizzgame");
                localParticipant = remote.addParticipant(response, name);
            } catch (RemoteException ex) {
                Logger.getLogger(QuizzManager.class.getName()).log(Level.SEVERE, null, ex);
            } catch (NotBoundException ex) {
                Logger.getLogger(QuizzManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        while (true) {

            if (remote != null) {
                if (remote.findParticipantById(localParticipant.getId()).hasToken()) {
                    System.out.println("Are you ready to write your question? (if yes, press enter)");
                    scanner.nextLine();

                    remote.changeReady(localParticipant, true);
                    while (!remote.checkParticipantsReady());

                    System.out.println("Please, write your question.");
                    String question = scanner.nextLine();
                    System.out.println("What is the answer to that question?");
                    answer = scanner.nextLine();

                    remote.setCurrentQuestion(new Question(question, answer));
                    while (!remote.checkParticipantsNotReady()){
                        lastAnswer = remote.checkLastAnswer();
                        if(lastAnswer!=null){
                            if(lastAnswer.isReady()){
                                System.out.println(lastAnswer.getName()+" got the question right!");
                            }
                            else{
                                System.out.println(lastAnswer.getName()+" got the question wrong!");
                            }
                        }
                    }
                    
                    remote.setCurrentQuestion(null);
                    remote.placeToken(false, localParticipant);
                } else {
                    if (!remote.findParticipantById(localParticipant.getId()).isReady()) {

                        System.out.println("Press enter if you are ready...");
                        scanner.nextLine();
                        remote.changeReady(localParticipant, true);
                        if (!remote.checkParticipantsReady()) {
                            System.out.println("Waiting for other participants...");
                        }

                    }
                    if (remote.checkParticipantsReady() && remote.isQuestionReady()) {
                        System.out.println(remote.getCurrentQuestion().getQuestion());
                        System.out.println("Answer?");
                        tStart = System.currentTimeMillis();
                        answer = scanner.nextLine();
                        tEnd = System.currentTimeMillis();
                        tDelta = tEnd - tStart;
                        elapsedSeconds = tDelta / 1000.0;
                        if (remote.sendAnswer(answer, localParticipant, elapsedSeconds)) {
                            System.out.println("You got it right!");
                        } else {
                            System.out.println("You got it wrong! =(");
                        }
                        localParticipant.setReady(false);
                        while(!remote.checkParticipantsNotReady());
                        if (remote.amINext(localParticipant)) {
                            remote.cycleQuestioner();
                        }
                    }
                }

            } 
            else {
                if (local.findParticipantById(localParticipant.getId()).hasToken()) {
                    System.out.println("Are you ready to write your question? (if yes, press enter)");
                    scanner.nextLine();

                    local.changeReady(localParticipant, true);
                    while (!local.checkParticipantsReady());

                    System.out.println("Please, write your question.");
                    String question = scanner.nextLine();
                    System.out.println("What is the answer to that question?");
                    answer = scanner.nextLine();

                    local.setCurrentQuestion(new Question(question, answer));
                    while (!local.checkParticipantsNotReady());
                    local.setCurrentQuestion(null);
                    local.setLastAnswer(null);
                    local.placeToken(false, localParticipant);
                } else {
                    if (!localParticipant.isReady()) {

                        System.out.println("Press enter if you are ready...");
                        scanner.nextLine();
                        local.changeReady(localParticipant, true);
                        localParticipant.setReady(true);
                        if (!local.checkParticipantsReady()) {
                            System.out.println("Waiting for other participants...");
                        }

                    }
                    if (local.checkParticipantsReady() && local.isQuestionReady()) {
                        System.out.println(local.getCurrentQuestion().getQuestion());
                        System.out.println("Answer?");
                        tStart = System.currentTimeMillis();
                        answer = scanner.nextLine();
                        tEnd = System.currentTimeMillis();
                        tDelta = tEnd - tStart;
                        elapsedSeconds = tDelta / 1000.0;
                        if (local.sendAnswer(answer, localParticipant, elapsedSeconds)) {
                            System.out.println("You got it right!");
                        } else {
                            System.out.println("You got it wrong! =(");
                        }
                        localParticipant.setReady(false);
                        while(!local.checkParticipantsNotReady());
                        if (local.amINext(localParticipant)) {
                            local.cycleQuestioner();
                        }
                    }

                }

            }
        }

    }

}

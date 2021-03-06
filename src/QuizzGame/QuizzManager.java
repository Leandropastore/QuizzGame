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

        boolean running = true;

        Scanner scanner = new Scanner(System.in);
        //                  VARIABLES
//------------------------------------------------------------------------------
        //                  SETUP
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
                localParticipant = remote.addParticipant(Inet4Address.getLocalHost().getHostAddress(), name);
            } catch (RemoteException ex) {
                Logger.getLogger(QuizzManager.class.getName()).log(Level.SEVERE, null, ex);
            } catch (NotBoundException ex) {
                Logger.getLogger(QuizzManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        }//                            SETUP
//----------------------------------------------------------------------------------
        while (running) {//            CLIENT

            if (remote != null) {
                if (remote.findParticipantById(localParticipant.getId()).hasToken()) {
                    System.out.println("Are you ready to write your question? (if yes, press enter)");
                    scanner.nextLine();

                    remote.changeReady(localParticipant, true);
                    while (!remote.checkParticipantsReady()) {

                        try {
                            Thread.sleep(300);
                        } catch (InterruptedException ex) {
                            Thread.currentThread().interrupt();
                        }

                    }
                    remote.setCycled(false);

                    remote.setLastAnswer(null);
                    System.out.println("Please, write your question.");
                    String question = scanner.nextLine();
                    System.out.println("What is the answer to that question?");
                    answer = scanner.nextLine();

                    remote.setCurrentQuestion(new Question(question, answer));

                    while (!remote.checkParticipantsNotReady()) {
                        lastAnswer = remote.checkLastAnswer();
                        if (lastAnswer != null) {
                            if (lastAnswer.isReady()) {
                                System.out.println(lastAnswer.getName() + " got the question right!");
                            } else {
                                System.out.println(lastAnswer.getName() + " got the question wrong!");
                            }
                        }
                        try {
                            Thread.sleep(300);
                        } catch (InterruptedException ex) {
                            Thread.currentThread().interrupt();
                        }
                    }

                    remote.setCurrentQuestion(null);
                    remote.cycleQuestioner();
                    remote.setCycled(true);

                } else {
                    if (!remote.findParticipantById(localParticipant.getId()).isReady() && !remote.findParticipantById(localParticipant.getId()).hasToken()) {

                        System.out.println(remote.printParticipants());
                        System.out.println("Press enter if you are ready...");
                        scanner.nextLine();
                        remote.changeReady(localParticipant, true);
                        if (!remote.checkParticipantsReady()) {
                            System.out.println("Waiting for other participants...");
                        }
                        while (!remote.checkParticipantsReady()) {

                            try {
                                Thread.sleep(300);
                            } catch (InterruptedException ex) {
                                Thread.currentThread().interrupt();
                            }

                        }
                        while (!remote.isQuestionReady()) {

                            try {
                                Thread.sleep(300);
                            } catch (InterruptedException ex) {
                                Thread.currentThread().interrupt();
                            }

                        }
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
                        while (!remote.checkParticipantsNotReady()) {

                            try {
                                Thread.sleep(300);
                            } catch (InterruptedException ex) {
                                Thread.currentThread().interrupt();
                            }

                        }
                        while (!remote.isCycled()) {

                            try {
                                Thread.sleep(300);
                            } catch (InterruptedException ex) {
                                Thread.currentThread().interrupt();
                            }

                        }
                        if (!remote.findParticipantById(localParticipant.getId()).hasToken()) {
                            System.out.println("Do you want to leave? (yes / no)");
                            response = scanner.nextLine();
                            if (response.equals("yes")) {
                                running = false;
                                remote.removeParticipant(localParticipant);
                            }
                        }
                    }
                }
//                           CLIENT
            } //--------------------------------------------------------------------------------------------
            else {//         SERVER
                if (local.findParticipantById(localParticipant.getId()).hasToken()) {
                    System.out.println("Are you ready to write your question? (if yes, press enter)");
                    scanner.nextLine();

                    local.changeReady(localParticipant, true);
                    while (!local.checkParticipantsReady()) {

                        try {
                            Thread.sleep(300);
                        } catch (InterruptedException ex) {
                            Thread.currentThread().interrupt();
                        }

                    }
                    local.setCycled(false);

                    System.out.println("Please, write your question.");
                    String question = scanner.nextLine();
                    System.out.println("What is the answer to that question?");
                    answer = scanner.nextLine();

                    local.setCurrentQuestion(new Question(question, answer));
                    
                    while (!local.checkParticipantsNotReady()) {

                        try {
                            Thread.sleep(300);
                        } catch (InterruptedException ex) {
                            Thread.currentThread().interrupt();
                        }

                    }

                    local.setCurrentQuestion(null);
                    local.cycleQuestioner();
                    local.setCycled(true);

                } else {
                    if (!local.findParticipantById(localParticipant.getId()).isReady() && !local.findParticipantById(localParticipant.getId()).hasToken()) {

                        System.out.println(local.printParticipants());
                        System.out.println("Press enter if you are ready...");
                        scanner.nextLine();
                        local.changeReady(localParticipant, true);
                        if (!local.checkParticipantsReady()) {
                            System.out.println("Waiting for other participants...");
                        }
                        while (!local.checkParticipantsReady()) {

                            try {
                                Thread.sleep(300);
                            } catch (InterruptedException ex) {
                                Thread.currentThread().interrupt();
                            }

                        }
                        while (!local.isQuestionReady()) {

                            try {
                                Thread.sleep(300);
                            } catch (InterruptedException ex) {
                                Thread.currentThread().interrupt();
                            }

                        }
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
                        while (!local.checkParticipantsNotReady()) {
                            try {
                                Thread.sleep(300);
                            } catch (InterruptedException ex) {
                                Thread.currentThread().interrupt();
                            }
                        }
                        while (!local.isCycled()) {
                            try {
                                Thread.sleep(300);
                            } catch (InterruptedException ex) {
                                Thread.currentThread().interrupt();
                            }
                        }
                        if (!local.findParticipantById(localParticipant.getId()).hasToken()) {
                            System.out.println("Do you want to leave? (yes / no)");
                            response = scanner.nextLine();
                            if (response.equals("yes")) {
                                running = false;
                                local.removeParticipant(localParticipant);
                            }
                        }
                    }
                }

            }
        }

    }

}

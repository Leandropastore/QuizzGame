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
    
    public static void main(String[] args) throws java.net.UnknownHostException, RemoteException{
    
        QuizzGame local = new QuizzGame();
        Participant localParticipant = null;
        Comm remote = null;
        String answer;
        
        long tStart;
        long tEnd;
        long tDelta;
        double elapsedSeconds;
        
        Scanner scanner = new Scanner(System.in);
        System.out.println("\nAre you starting as a server? (yes / no)");
            String response = scanner.nextLine();
            if(response.equals("yes"))
                local.setEnabled(true);
            else if(response.equals("no")){
                local.setEnabled(false);
                System.out.println("\nPlease, provide the IP address to connect: ");
                response = scanner.nextLine();
            }
        
        System.out.println("\nWhat is your name?");
        String name = scanner.nextLine();
        
        System.out.println(Inet4Address.getLocalHost().getHostAddress());
        
        
        if(local.isEnabled()){
            try{
                Comm stub = (Comm)UnicastRemoteObject.exportObject(local, 0);
                LocateRegistry.createRegistry(1099);
                Registry registry = LocateRegistry.getRegistry();
                registry.rebind("quizzgame", stub);
                localParticipant = local.addParticipant(Inet4Address.getLocalHost().getHostAddress(), name);
                System.out.println("Press enter when you have finished waiting");
                scanner.nextLine();
                local.setup(localParticipant);
            }
            catch(RemoteException r){

                System.err.println("Error: "+r);

            }
        }
        else{
            try {
                Registry registry = LocateRegistry.getRegistry(response);
                remote = (Comm)registry.lookup("quizzgame");
                localParticipant = remote.addParticipant(response,name);
            } catch (RemoteException ex) {
                Logger.getLogger(QuizzManager.class.getName()).log(Level.SEVERE, null, ex);
            } catch (NotBoundException ex) {
                Logger.getLogger(QuizzManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        
        while(true){
        
            if(local.isEnabled()){
                local.serverMenu();
            }
            else{
                if(!localParticipant.isReady()){
                
                    System.out.println("Press enter if you are ready...");
                    scanner.nextLine();
                    remote.changeReady(localParticipant, true);
                    localParticipant.setReady(true);
                    if(!remote.checkParticipantsReady())
                        System.out.println("Waiting for other participants...");
                
                }
                if(remote.checkParticipantsReady()&&remote.isQuestionReady()){
                    System.out.println(remote.getCurrentQuestion());
                    System.out.println("Answer?");
                    tStart = System.currentTimeMillis();
                    answer = scanner.nextLine();
                    tEnd = System.currentTimeMillis();
                    tDelta = tEnd - tStart;
                    elapsedSeconds = tDelta / 1000.0;
                    if(remote.sendAnswer(answer,localParticipant,elapsedSeconds))
                        System.out.println("You got it right!");
                    else
                        System.out.println("You got it wrong! =(");
                    localParticipant.setReady(false);
                }
                try{
                    TimeUnit.SECONDS.sleep(1);
                }catch(InterruptedException e){
                    System.err.println("Error: "+e);
                }
                
            }
        
        }
    
    }
    
}

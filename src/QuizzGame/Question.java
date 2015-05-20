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
public class Question implements Serializable{
    
    private String question;
    private String answer;

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = this.format(answer);
    }
    
    public Question(String question,String answer){
    
        this.setAnswer(answer);
        this.setQuestion(question);
    
    }
    
    public boolean checkAnswer(String answer){
    
        answer = this.format(answer);
        if(this.getAnswer().equals(answer))
            return true;
        else
            return false;
    
    }
    
    public String format(String string){
    
        string = string.toLowerCase();
        string = string.replaceAll(" ", "");
        string = string.replaceAll("-", "");
        string = string.replaceAll(".", "");
        string = string.replaceAll(",", "");
        string = string.replaceAll("'", "");
        string = string.replaceAll("\"", "");
        
        return string;
    
    }
    
    @Override
    public String toString(){
    
        String string = "Question: "+this.getQuestion();
        string = "\nAnswer: "+this.getAnswer();
        
        return string;
    
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package simplecalculator;
import java.text.DecimalFormat;
import java.util.Stack;
/**
 *
 * @author Nguyen Huy Dinh
 */
public class SimpleCalculator {
    NewJFrame jFrame;
    private final Stack stack_expression = new Stack();
    private String back_code;
    private String numberInput; 
    private String Ctext = "CE";
    private String operator; 
    private double numberOut;
    private double number2;
    private double last_number;
    private double memory;
    private boolean isRealNumber;
    private boolean error;
    private boolean input;
    
    SimpleCalculator(NewJFrame jFrame){
        this.jFrame = jFrame;
        error = false;
        back_code = "";
        numberInput = "";
        isRealNumber = false;
        operator ="";
        numberOut = 0.0;
        stack_expression.push(0.0);
        memory = 0;
        input = false;
    }
    
    private boolean checkCodeIsNumber(String code){
        switch (code){
            case "0": case "1": case "2": case "3": case "4": case "5": case "6": case "7": case "8": case "9": case ".":
                return true;
        }
        return false;
    }
    
    private void cancelError(){
        if(error){
            error = false;
            jFrame.activeButton(true, "+","-","x","/","1/x","+/-","%","√",".","MC","M+","M-","MR");
        }
    }
    
    private void calc1(String code){
        Double num = (Double)stack_expression.pop();
        switch(code){
            case "+/-": num = -num; break;
            case "1/x":
                if(num != 0) num = 1.0/num; 
                else error = true;
                break;
            case "%":   num = num/100; break;
            case "√":   
                if(num>=0) num = Math.sqrt(num);
                else error = true;
                break;
            case "M+":
                jFrame.activeButton(true, "MC","MR");
                memory += num; break;
            case "M-":
                jFrame.activeButton(true, "MC","MR");
                memory -= num; break;
        }
        if(!error){
            stack_expression.push(num);
            last_number = num;
            numberOut = num;
        }
    }
    
    private void calc2(){
        Double num2 = (Double) stack_expression.pop();
        String op = (String) stack_expression.pop();
        Double num1 = (Double) stack_expression.pop();
        switch(op){
            case "+": num1 += num2; break;
            case "-": num1 -= num2; break;
            case "x": num1 *= num2; break;
            case "/":
                if(num2 != 0) num1 /= num2; 
                else error = true;
                break;
        }
        if(!error){
            stack_expression.push(num1);
            last_number = num1;
            number2 = num2;
            numberOut = num1;
        }
    }
    
    private void setCodeWithNumber(String code){
        cancelError();
        input = true;
        String temp = numberInput;
        if(".".equals(code)){
            if(!isRealNumber){
                if(temp.length() == 0) temp = "0";
                temp += ".";
                isRealNumber = true;
            }
        }
        else{
            if(temp.length() == 1 && temp.charAt(0) == '0') temp = code;
            else temp += code;
        }
        if((temp.length() < 13 && isRealNumber)||(temp.length() < 12 && !isRealNumber)){
            numberInput = temp;
            numberOut = Double.parseDouble(numberInput);
        }   
    }
    
    private void setCodeWithOperator(String code){
        isRealNumber = false;
        input = false;
        
        if(checkCodeIsNumber(back_code)){
            if(stack_expression.size() == 1 || stack_expression.size() == 3) stack_expression.pop();
            Double number = Double.parseDouble(numberInput);
            stack_expression.push(number);
            last_number = number;
            numberInput = "";
        }
        switch(code){
            case "+": case "-": case "x": case "/":
                if(stack_expression.size() == 3) calc2();
                else if(stack_expression.size() == 2) stack_expression.pop();
                stack_expression.push(code);
                operator = code;
                break;  
            case "=":
                cancelError();
                if(stack_expression.size() == 2){
                    stack_expression.push(last_number);
                }
                else if(stack_expression.size() == 1){
                    if(operator.length() != 0){
                        stack_expression.push(operator);
                        stack_expression.push(number2);        
                    }
                    else break;
                }
                calc2();
                break;
            case "+/-": case "1/x": case "%": case "√": case "M+": case "M-":
                if(stack_expression.size() == 2) stack_expression.push(last_number);
                calc1(code);
                break;
            case "MC": 
                jFrame.activeButton(false, "MC","MR");
                memory = 0; break;
            case "MR": 
                if(stack_expression.size() == 1 || stack_expression.size() == 3) stack_expression.pop();
                stack_expression.push(memory);
                last_number = memory;
                numberOut = memory;
                break;
            case "C": 
                cancelError();
                if("CE".equals(Ctext)){
                    numberInput = "";
                    Ctext = "C";
                    last_number = 0;
                    if(stack_expression.size() == 1){
                        stack_expression.pop();
                        stack_expression.push(0.0);
                    } 
                    else if(stack_expression.size() == 3) stack_expression.pop();
                }
                else{
                    back_code = "";
                    numberInput = "";
                    last_number = 0;
                    number2 = 0;
                    operator ="";
                    while(!stack_expression.empty()) stack_expression.pop();
                    stack_expression.push(0.0);
                }
                numberOut = 0.0;
                break; 
        }
        
    }

    String formatNumberOut(){
        double temp = numberOut;
        boolean type = true;
        if(temp < 0){
            temp = -temp;
            type = false;
        }
        String partten = "#";
        DecimalFormat decimalFormat = new DecimalFormat(partten);
        int numLength = decimalFormat.format(temp).length();
        String out = "";
        if(numLength == 1 && temp < 1){
            partten = "#.########################################################################################################################";
            decimalFormat.applyPattern(partten);
            String num = decimalFormat.format(temp);
            int count = 0;
            while(count < num.length() && (num.charAt(count) == '0' || num.charAt(count) == '.') ) count++;
            if(count < 5){
                decimalFormat.applyPattern("#.##########");
                out = decimalFormat.format(temp);
            }
            else if(count < 101){
                decimalFormat.applyPattern("#.#######");
                out = decimalFormat.format(temp*Math.pow(10, count - 2)) + "E-" + (count-1);
            }
            else{
                numberOut = 0;
                last_number = 0;
                stack_expression.pop();
                stack_expression.push(0.0);
                out = "0";
            }
        }
        else if(numLength < 11){
            partten = "#.";
            for(int i = 0;i < 11 - numLength;i++){
                partten += "#";
            }
            decimalFormat.applyPattern(partten);
            out = decimalFormat.format(temp);
        }
        else if(numLength == 11){
            partten = "#";
            decimalFormat.applyPattern(partten);
            out = decimalFormat.format(temp);
        }
        else if(numLength < 101){
            temp = Math.round(temp/(Math.pow(10, numLength - 9)));
            temp = temp / 100000000;
            decimalFormat.applyPattern("#.########");
            out = decimalFormat.format(temp) + "E" + (numLength - 1);
            
        }
        else error = true;
        if(!type && !"0".equals(out)) out = "-" + out;
        return out; 
    }
    
    void setCode(String code){
        if(!"C".equals(code)) Ctext = "CE";
        if(checkCodeIsNumber(code)) setCodeWithNumber(code);
        else setCodeWithOperator(code);
        back_code = code;
        jFrame.setTextButtonC(Ctext);
        String textOut;
        if(input) textOut = numberInput;
        else textOut = formatNumberOut();
        if(error){
            jFrame.setTextJText("ERROR!");
            jFrame.activeButton(false, "+","-","x","/","1/x","+/-","%","√",".","MC","M+","M-","MR");
            back_code = "";
            numberInput = "";
            last_number = 0;
            number2 = 0;
            operator = "";
            numberOut = 0.0;
            while(!stack_expression.empty()) stack_expression.pop();
            stack_expression.push(0.0);
        }
        else jFrame.setTextJText(textOut);
    }
}


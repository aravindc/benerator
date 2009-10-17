/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.databene.domain.br;

import java.util.ArrayList;
import java.util.Random;
import org.databene.benerator.sample.WeightedCSVSampleGenerator;

/**
 *
 * @author Eric Chaves
 */
public class CNPJGenerator extends WeightedCSVSampleGenerator<String>{

    private static final String LOCAL  = "org/databene/domain/br/cnpj_sufix.csv";
    /**
     * flag indicating should return CPF in numeric or formatted form.
     * defaults to true
     */
    private boolean formatted;
    private Random random;

    public CNPJGenerator(){
        this(false);
    }

    public CNPJGenerator(boolean formatted){
       super(LOCAL,"UTF-8");
       this.random = new Random();
       this.formatted = formatted;
    }

    @Override
    public String generate() {
        String sufix = super.generate();
        if (sufix == null)
            sufix = "0000";
        return generateCNPJ(sufix);
    }

    private String generateCNPJ(String sufix){

       StringBuilder buf = new StringBuilder();
       ArrayList<Integer> digits = new ArrayList<Integer>();
       for (int i=0; i < 8; i++)
           digits.add(random.nextInt(9));
       for (int i=0; i < 4; i++)
           digits.add(Integer.parseInt(sufix.substring(i, i+1)));
       addDigits(digits);

        for(int i=0; i < digits.size(); i++)
            buf.append(digits.get(i));
        if (this.formatted){
            buf.insert(2, '.');
            buf.insert(6, '.');
            buf.insert(10, '/');
            buf.insert(15, '-');
        }
        return buf.toString();
   }

    private void addDigits(ArrayList<Integer> digits){
       int sum=0;
       sum = (5*digits.get(0))+(4*digits.get(1))+(3*digits.get(2))+(2*digits.get(3))+
             (9*digits.get(4))+(8*digits.get(5))+(7*digits.get(6))+(6*digits.get(7))+
             (5*digits.get(8))+(4*digits.get(9))+(3*digits.get(10))+(2*digits.get(11));
       digits.add((sum % 11 < 2) ? 0: 11-(sum % 11));

       sum = (6*digits.get(0))+(5*digits.get(1))+(4*digits.get(2))+(3*digits.get(3))+
             (2*digits.get(4))+(9*digits.get(5))+(8*digits.get(6))+(7*digits.get(7))+
             (6*digits.get(8))+(5*digits.get(9))+(4*digits.get(10))+(3*digits.get(11))+
             (2*digits.get(12));
       digits.add((sum % 11 < 2) ? 0: 11-(sum % 11));
   }
}

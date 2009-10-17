package org.databene.domain.br;

import java.util.ArrayList;
import org.databene.benerator.Generator;
import org.databene.benerator.InvalidGeneratorSetupException;
import java.util.Random;

/**
 *
 * @author Eric Chaves
 */
public class CPFGenerator implements Generator<String> {

    /**
     * flag indicating should return CPF in numeric or formatted form.
     * defaults to true
     */
    private boolean formatted;
    private Random random;    

    public CPFGenerator(){
        this(false);
    }

    public CPFGenerator(boolean formatted){
       this.random = new Random();
       this.formatted = formatted;
    }

    public Class<String> getGeneratedType() {
        return String.class;
    }

    public String generate() throws InvalidGeneratorSetupException{
        return generateCPF();
    }


    public boolean available(){
        return true;
    }

   public void validate() throws InvalidGeneratorSetupException{
       return;
   }

   public void reset(){
       return;
   }

   public void close(){
       return;
   }

   private String generateCPF(){
       StringBuilder buf = new StringBuilder();
       ArrayList<Integer> digits = new ArrayList<Integer>();

        for (int i=0; i < 9; i++)
            digits.add(random.nextInt(9));
        addDigit(digits);
        addDigit(digits);

        for(int i=0; i < digits.size(); i++)
            buf.append(digits.get(i));
        if (this.formatted){            
            buf.insert(3, '.');
            buf.insert(7, '.');
            buf.insert(11, '-');
        }
        return buf.toString();
   }
   private void addDigit(ArrayList<Integer> digits){
       int sum=0;
       for (int i=0, j=digits.size()+1; i < digits.size(); i++,j-- )
         sum += (int)digits.get(i) * j;
       digits.add((sum % 11 < 2) ? 0: 11-(sum % 11));
   }
}

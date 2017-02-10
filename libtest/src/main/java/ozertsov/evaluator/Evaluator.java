package ozertsov.evaluator;

/**
 * Created by alexander on 08.02.17.
 */
public class Evaluator {

    private volatile int value;

    public Evaluator(int startvalue){
        value = startvalue;
    }

    public void addThree2Times(){
        value += 3;
        value += 3;
    }

    public void multThree(){
        value *= 3;
    }

    public int getValue(){
        return value;
    }

}

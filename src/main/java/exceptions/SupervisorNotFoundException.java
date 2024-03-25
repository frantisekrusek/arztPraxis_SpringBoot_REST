package exceptions;

public class  SupervisorNotFoundException extends RuntimeException{

    public SupervisorNotFoundException (Long id){
        super("Could not find supervisor " + id);
    }
}

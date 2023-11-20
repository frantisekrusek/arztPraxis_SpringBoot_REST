package exceptions;

public class  TemplateNotFoundException extends RuntimeException{

    public TemplateNotFoundException (Long id){
        super("Could not find template " + id);
    }
}

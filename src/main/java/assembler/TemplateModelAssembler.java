package assembler;

import controller.TemplateController;
import model.Template;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Component
public class TemplateModelAssembler implements RepresentationModelAssembler<Template, EntityModel<Template>> {


    @Override
    public EntityModel<Template> toModel(Template template) {

        return EntityModel.of(template,
                linkTo(methodOn(TemplateController.class).one(template.getId())).withSelfRel(),
                linkTo(methodOn(TemplateController.class).all()).withRel("templates"));
    }
}

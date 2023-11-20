package controller;

import assembler.TemplateModelAssembler;
import database.TemplateRepository;
import exceptions.TemplateNotFoundException;
import model.appointment.Template;
import org.springframework.boot.autoconfigure.jms.JmsProperties;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
public class TemplateController {

    private final TemplateRepository templateRepository;
    private final TemplateModelAssembler templateModelAssembler;

    TemplateController (TemplateRepository templateRepository, TemplateModelAssembler templateModelAssembler){
        this.templateRepository = templateRepository;
        this.templateModelAssembler = templateModelAssembler;
    }

    @GetMapping ("/templates/{id}")
    public EntityModel<Template> one (@PathVariable Long id){

        Template template = templateRepository.findById(id).
                orElseThrow(() -> new TemplateNotFoundException(id));
        return templateModelAssembler.toModel(template);
    }

    @GetMapping ("/templates")
    public CollectionModel<EntityModel<Template>> all(){

        List<EntityModel<Template>> templates = templateRepository.findAll().stream().
                map(templateModelAssembler::toModel).collect(Collectors.toList());


        return CollectionModel.of(templates, linkTo(methodOn(TemplateController.class).all()).withSelfRel());
    }


}

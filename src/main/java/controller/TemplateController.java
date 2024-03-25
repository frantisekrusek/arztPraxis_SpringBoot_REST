package controller;

import assembler.TemplateModelAssembler;
import database.TemplateRepository;
import exceptions.TemplateNotFoundException;
import model.appointment.Template;
import org.springframework.boot.autoconfigure.jms.JmsProperties;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping ("/templates")
    public ResponseEntity<EntityModel<Template>> newTemplate(@RequestBody Template template){
        Template newTemplate = templateRepository.save(template);
        return ResponseEntity.
                created(linkTo(methodOn(TemplateController.class).
                        one(newTemplate.getId())).
                        toUri()).
                body(templateModelAssembler.toModel(newTemplate));
    }

    @DeleteMapping("templates/{id}")
    public ResponseEntity<?> deleteTemplate(@PathVariable Long id){
        templateRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("templates/{id}")
    public ResponseEntity<?> replaceTemplate(@RequestBody Template newTemplate, @PathVariable Long id){

        Template updatedTemplate = templateRepository.findById(id)
                .map(template -> {
                    template.setActive(newTemplate.isActive());
                    template.setWeekday(newTemplate.getWeekday());
                    template.setStartTime(newTemplate.getStartTime());
                    return templateRepository.save(template);
                })
                .orElseGet(() -> {
                    newTemplate.setId(id);
                    return templateRepository.save(newTemplate);
                });

        EntityModel<Template> entityModel = templateModelAssembler.toModel(updatedTemplate);

        return ResponseEntity
                .created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(entityModel);
    }



}

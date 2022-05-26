package com.bezkoder.spring.hibernate.manytomany.controller;

import java.util.ArrayList;
import java.util.List;

import com.bezkoder.spring.hibernate.manytomany.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.bezkoder.spring.hibernate.manytomany.exception.ResourceNotFoundException;
import com.bezkoder.spring.hibernate.manytomany.model.Tag;
import com.bezkoder.spring.hibernate.manytomany.model.Tutorial;
import com.bezkoder.spring.hibernate.manytomany.repository.TagRepository;
import com.bezkoder.spring.hibernate.manytomany.repository.TutorialRepository;

@CrossOrigin(origins = "http://localhost:8081")
@RestController
@RequestMapping("/api")
public class TagController {

  @Autowired
  private TutorialRepository tutorialRepository;

  @Autowired
  private TagRepository tagRepository;

  @Autowired
  private TagService tagService;

  private static final String NOT_FOUND_TUTORIAL_WITH_ID = "Not found Tutorial with id = ";
  private static final String NOT_FOUND_TAG_WITH_ID  = "Not found Tag with id = ";

  @GetMapping("/tags")
  public ResponseEntity<List<Tag>> getAllTags() {

    List<Tag> tags = new ArrayList<>(tagRepository.findAll());

    if (tags.isEmpty()) {
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    return new ResponseEntity<>(tags, HttpStatus.OK);
  }
  
  @GetMapping("/tutorials/{tutorialId}/tags")
  public ResponseEntity<List<Tag>> getAllTagsByTutorialId(@PathVariable(value = "tutorialId") Long tutorialId) {
    if (!tutorialRepository.existsById(tutorialId)) {
      throw new ResourceNotFoundException(NOT_FOUND_TUTORIAL_WITH_ID + tutorialId);
    }

    List<Tag> tags = tagRepository.findTagsByTutorialsId(tutorialId);
    return new ResponseEntity<>(tags, HttpStatus.OK);
  }

  @GetMapping("/tags/{id}")
  public ResponseEntity<Tag> getTagsById(@PathVariable(value = "id") Long id) {
    Tag tag = tagRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException(NOT_FOUND_TAG_WITH_ID + id));

    return new ResponseEntity<>(tag, HttpStatus.OK);
  }
  
  @GetMapping("/tags/{tagId}/tutorials")
  public ResponseEntity<List<Tutorial>> getAllTutorialsByTagId(@PathVariable(value = "tagId") Long tagId) {
    if (!tagRepository.existsById(tagId)) {
      throw new ResourceNotFoundException("Not found Tag  with id = " + tagId);
    }

    List<Tutorial> tutorials = tutorialRepository.findTutorialsByTagsId(tagId);
    return new ResponseEntity<>(tutorials, HttpStatus.OK);
  }

  @PostMapping("/tutorials/{tutorialId}/tags")
  public ResponseEntity<Tag> addTag(@PathVariable(value = "tutorialId") Long tutorialId, @RequestBody Tag tagRequest) {
    Tag tag = tutorialRepository.findById(tutorialId).map(tutorial -> {
      long tagId = tagRequest.getId();
      
      // tag is existed
      if (tagId != 0L) {
        Tag myTag = tagRepository.findById(tagId)
            .orElseThrow(() -> new ResourceNotFoundException(NOT_FOUND_TAG_WITH_ID + tagId));
        tutorial.addTag(myTag);
        tutorialRepository.save(tutorial);
        return myTag;
      }
      
      // add and create new Tag
      tutorial.addTag(tagRequest);
      return tagRepository.save(tagRequest);
    }).orElseThrow(() -> new ResourceNotFoundException(NOT_FOUND_TUTORIAL_WITH_ID + tutorialId));

    return new ResponseEntity<>(tag, HttpStatus.CREATED);
  }

  @PutMapping("/tags/{id}")
  public ResponseEntity<Tag> updateTag(@PathVariable("id") long id, @RequestBody Tag tagRequest) {
    Tag tag = tagRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("TagId " + id + "not found"));

    tag.setName(tagRequest.getName());

    return new ResponseEntity<>(tagRepository.save(tag), HttpStatus.OK);
  }
 
  @DeleteMapping("/tutorials/{tutorialId}/tags/{tagId}")
  public ResponseEntity<HttpStatus> deleteTagFromTutorial(@PathVariable(value = "tutorialId") Long tutorialId, @PathVariable(value = "tagId") Long tagId) {
    Tutorial tutorial = tutorialRepository.findById(tutorialId)
        .orElseThrow(() -> new ResourceNotFoundException(NOT_FOUND_TUTORIAL_WITH_ID + tutorialId));
    
    tutorial.removeTag(tagId);
    tutorialRepository.save(tutorial);
    
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }
  
  @DeleteMapping("/tags/{id}")
  public ResponseEntity<HttpStatus> deleteTag(@PathVariable("id") long id) {
    tagRepository.deleteById(id);

    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  @EventListener(ApplicationReadyEvent.class)
  public void resetDbs(){
    tagService.resetDb();
  }

}

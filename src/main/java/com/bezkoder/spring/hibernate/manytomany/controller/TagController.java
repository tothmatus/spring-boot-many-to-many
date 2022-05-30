package com.bezkoder.spring.hibernate.manytomany.controller;

import java.util.List;

import com.bezkoder.spring.hibernate.manytomany.service.TagService;
import com.bezkoder.spring.hibernate.manytomany.service.TutorialService;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.bezkoder.spring.hibernate.manytomany.exception.ResourceNotFoundException;
import com.bezkoder.spring.hibernate.manytomany.model.Tag;
import com.bezkoder.spring.hibernate.manytomany.model.Tutorial;
import com.bezkoder.spring.hibernate.manytomany.repository.TutorialRepository;

@CrossOrigin(origins = "http://localhost:8081")
@RestController
@RequestMapping("/api")
public class TagController {

  private final TutorialRepository tutorialRepository;

  private final TagService tagService;
  private final TutorialService tutorialService;


  public TagController(TutorialRepository tutorialRepository, TagService tagService, TutorialService tutorialService) {
    this.tutorialRepository = tutorialRepository;
    this.tagService = tagService;
    this.tutorialService = tutorialService;
  }

  @GetMapping("/tags")
  public ResponseEntity<List<Tag>> getAllTags() {

    List<Tag> tags = tagService.getAllTags();

    if (tags.isEmpty()) {
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    return new ResponseEntity<>(tags, HttpStatus.OK);
  }
  
  @GetMapping("/tutorials/{tutorialId}/tags")
  public ResponseEntity<List<Tag>> getAllTagsByTutorialId(@PathVariable(value = "tutorialId") Long tutorialId) {
    tutorialService.tutorialExists(tutorialId);
    List<Tag> tags = tagService.findTagsByTutorialsId(tutorialId);
    return new ResponseEntity<>(tags, HttpStatus.OK);
  }

  @GetMapping("/tags/{id}")
  public ResponseEntity<Tag> getTagsById(@PathVariable(value = "id") Long id) {
    Tag tag = tagService.getTagsById(id);
    return new ResponseEntity<>(tag, HttpStatus.OK);
  }
  
  @GetMapping("/tags/{tagId}/tutorials")
  public ResponseEntity<List<Tutorial>> getAllTutorialsByTagId(@PathVariable(value = "tagId") Long tagId) {
    tagService.tagExist(tagId);
    List<Tutorial> tutorials = tutorialService.findTutorialsByTagsId(tagId);
    return new ResponseEntity<>(tutorials, HttpStatus.OK);
  }

  @PostMapping("/tutorials/{tutorialId}/tags")
  public ResponseEntity<Tag> addTag(@PathVariable(value = "tutorialId") Long tutorialId, @RequestBody Tag tagRequest) {
    Tag tag = tagService.addTag(tutorialId, tagRequest);
    return new ResponseEntity<>(tag, HttpStatus.CREATED);
  }

  @PutMapping("/tags/{id}")
  public ResponseEntity<Tag> updateTag(@PathVariable("id") long id, @RequestBody Tag tagRequest) {
    Tag tag = tagService.updateTag(id, tagRequest);
    return new ResponseEntity<>(tag, HttpStatus.OK);
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

package com.bezkoder.spring.hibernate.manytomany.service;

import com.bezkoder.spring.hibernate.manytomany.exception.ResourceNotFoundException;
import com.bezkoder.spring.hibernate.manytomany.model.Tag;
import com.bezkoder.spring.hibernate.manytomany.model.Tutorial;
import com.bezkoder.spring.hibernate.manytomany.repository.TagRepository;
import com.bezkoder.spring.hibernate.manytomany.repository.TutorialRepository;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
public class TagService {
    private final TutorialRepository tutorialRepository;
    private final TagRepository tagRepository;
    private static final String NOT_FOUND_TAG_WITH_ID  = "Not found Tag with id = ";
    private static final String NOT_FOUND_TUTORIAL_WITH_ID = "Not found Tutorial with id = ";

    public TagService(TutorialRepository tutorialRepository, TagRepository tagRepository) {
        this.tutorialRepository = tutorialRepository;
        this.tagRepository = tagRepository;
    }

    public List<Tag> getAllTags(){
        return tagRepository.findAll();
    }

    public List<Tag> findTagsByTutorialsId(Long tutorialId){
        return tagRepository.findTagsByTutorialsId(tutorialId);
    }

    public Tag getTagsById(Long id) {
        return tagRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(NOT_FOUND_TAG_WITH_ID + id));
    }

    public void tagExist(Long tagId) {
        if (!tagRepository.existsById(tagId)) {
            throw new ResourceNotFoundException("Not found Tag  with id = " + tagId);
        }
    }

    public Tag addTag(Long tutorialId, Tag tagRequest) {
        return tutorialRepository.findById(tutorialId).map(tutorial -> {
            long tagId = tagRequest.getId();

            // tag exists
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
    }

    @Transactional
    public void resetDb(){
        tagRepository.save(new Tag("coding"));
        tagRepository.save(new Tag("language"));
        tagRepository.save(new Tag("OOP"));
        tagRepository.save(new Tag("object based"));

        tutorialRepository.save(new Tutorial("Java", "Java Tutorial", true));
        tutorialRepository.save(new Tutorial("Objective C", "C Tutorial", false));
        tutorialRepository.save(new Tutorial("Swing", "Swing Tutorial", true));
        tutorialRepository.save(new Tutorial("Blockchain", "Blockchain Tutorial", true));
        tutorialRepository.save(new Tutorial("Swagger", "Swagger Tutorial", true));

        Tutorial tutorial = tutorialRepository.getById(1L);
        tutorial.addTag(tagRepository.getById(1L));
        tutorial.addTag(tagRepository.getById(2L));
        tutorial.addTag(tagRepository.getById(3L));
        tutorialRepository.save(tutorial);

        Tutorial tutorial2 = tutorialRepository.getById(2L);
        tutorial2.addTag(tagRepository.getById(1L));
        tutorial2.addTag(tagRepository.getById(2L));
        tutorialRepository.save(tutorial2);
    }

    public Tag updateTag(long id, Tag tagRequest) {
        Tag tag = tagRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("TagId " + id + "not found"));
        tag.setName(tagRequest.getName());
        tagRepository.save(tag);
        return tag;
    }
}

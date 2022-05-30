package com.bezkoder.spring.hibernate.manytomany.service;

import com.bezkoder.spring.hibernate.manytomany.exception.ResourceNotFoundException;
import com.bezkoder.spring.hibernate.manytomany.model.Tutorial;
import com.bezkoder.spring.hibernate.manytomany.repository.TagRepository;
import com.bezkoder.spring.hibernate.manytomany.repository.TutorialRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TutorialService {
    private final TutorialRepository tutorialRepository;
    private final TagRepository tagRepository;
    private static final String NOT_FOUND_TUTORIAL_WITH_ID = "Not found Tutorial with id = ";


    public TutorialService(TutorialRepository tutorialRepository, TagRepository tagRepository) {
        this.tutorialRepository = tutorialRepository;
        this.tagRepository = tagRepository;
    }

    public void tutorialExists(Long tutorialId) {
        if (!tutorialRepository.existsById(tutorialId)) {
            throw new ResourceNotFoundException(NOT_FOUND_TUTORIAL_WITH_ID + tutorialId);
        }
    }

    public List<Tutorial> findTutorialsByTagsId(Long tagId) {
        return tutorialRepository.findTutorialsByTagsId(tagId);
    }
}

package com.bezkoder.spring.hibernate.manytomany.service;

import com.bezkoder.spring.hibernate.manytomany.model.Tag;
import com.bezkoder.spring.hibernate.manytomany.model.Tutorial;
import com.bezkoder.spring.hibernate.manytomany.repository.TagRepository;
import com.bezkoder.spring.hibernate.manytomany.repository.TutorialRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
public class TagService {
    @Autowired
    TutorialRepository tutorialRepository;
    @Autowired
    TagRepository tagRepository;

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
}

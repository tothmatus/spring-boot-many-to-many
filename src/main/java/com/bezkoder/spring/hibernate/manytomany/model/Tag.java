package com.bezkoder.spring.hibernate.manytomany.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "tags")
public class Tag {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  @Column(name = "name")
  private String name;

  @ManyToMany(fetch = FetchType.LAZY,
      cascade = {
          CascadeType.PERSIST,
          CascadeType.MERGE
      },
      mappedBy = "tags")
  @JsonIgnore
  private Set<Tutorial> tutorials = new HashSet<>();

  public Tag() {

  }
  public Tag(String name) {
    this.name = name;
  }

  public Tag(String name, Set<Tutorial> tutorials) {
    this.name = name;
    this.tutorials = tutorials;
  }

  public long getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Set<Tutorial> getTutorials() {
    return tutorials;
  }

  public void setTutorials(Set<Tutorial> tutorials) {
    this.tutorials = tutorials;
  }  
  
}

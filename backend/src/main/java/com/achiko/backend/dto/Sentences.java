package com.achiko.backend.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Sentences {
    private String sentence1;
    private String sentence2;

    public Sentences() {}

    public Sentences(String sentence1, String sentence2) {
        this.sentence1 = sentence1;
        this.sentence2 = sentence2;
    }
}
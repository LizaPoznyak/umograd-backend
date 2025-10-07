package com.umograd.content.domain.task;

import java.util.List;

public class TaskContent {
    private final String type;
    private final String question;
    private final List<String> options;
    private final String answer;

    public TaskContent(String type, String question, List<String> options, String answer) {
        this.type = type;
        this.question = question;
        this.options = options;
        this.answer = answer;
    }

    public String type() { return type; }
    public String question() { return question; }
    public List<String> options() { return options; }
    public String answer() { return answer; }
}

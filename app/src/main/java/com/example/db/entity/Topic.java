package com.example.db.entity;

public class Topic {
    private TopicNames topicNames;
    private ExperianceLevel experianceLevel;

    public Topic() {
    }

    public Topic(TopicNames topicNames, ExperianceLevel experianceLevel) {
        this.topicNames = topicNames;
        this.experianceLevel = experianceLevel;
    }

    public TopicNames getTopicNames() {
        return topicNames;
    }

    public void setTopicNames(TopicNames topicNames) {
        this.topicNames = topicNames;
    }

    public ExperianceLevel getExperianceLevel() {
        return experianceLevel;
    }

    public void setExperianceLevel(ExperianceLevel experianceLevel) {
        this.experianceLevel = experianceLevel;
    }

    @Override
    public String toString() {
        return "Topic{" +
                "topicNames=" + topicNames +
                ", experianceLevel=" + experianceLevel +
                '}';
    }
}

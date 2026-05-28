package com.lembe.ai.event;

public record ProgressPhotoUploadedEvent(Long userSeq, Long milestoneSeq, Long photoSeq) {}

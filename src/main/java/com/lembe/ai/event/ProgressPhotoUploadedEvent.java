package com.lembe.ai.event;

public record ProgressPhotoUploadedEvent(String ucode, Long milestoneSeq, Long photoSeq) {}

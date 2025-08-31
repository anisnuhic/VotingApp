package com.youtube.voting.model;

import java.time.LocalDateTime;

public class Video {
    private int id;
    private String title;
    private String youtubeId;
    private String embedUrl;
    private int positiveVotes;
    private int negativeVotes;
    private LocalDateTime createdAt;
    private double wilsonScore = 0.0;
    
    // NOVA POLJA ZA CUSTOM SLIKE
    private String customImagePath;    // putanja do custom slike
    private boolean useCustomImage;    // da li koristiti custom sliku umesto YT thumbnail
    private String description;        // opis videa
    
    // Konstruktori
    public Video() {
        this.createdAt = LocalDateTime.now();
        this.useCustomImage = false;
    }
    
    public Video(int id, String title, String youtubeId, String embedUrl, int positiveVotes, int negativeVotes) {
        this();
        this.id = id;
        this.title = title;
        this.youtubeId = youtubeId;
        this.embedUrl = embedUrl;
        this.positiveVotes = positiveVotes;
        this.negativeVotes = negativeVotes;
    }
    
    // Osnovni getteri i setteri
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getYoutubeId() {
        return youtubeId;
    }
    
    public void setYoutubeId(String youtubeId) {
        this.youtubeId = youtubeId;
    }
    
    public String getEmbedUrl() {
        return embedUrl;
    }
    
    public void setEmbedUrl(String embedUrl) {
        this.embedUrl = embedUrl;
    }
    
    public int getPositiveVotes() {
        return positiveVotes;
    }
    
    public void setPositiveVotes(int positiveVotes) {
        this.positiveVotes = positiveVotes;
    }
    
    public int getNegativeVotes() {
        return negativeVotes;
    }
    
    public void setNegativeVotes(int negativeVotes) {
        this.negativeVotes = negativeVotes;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public String getCustomImagePath() {
        return customImagePath;
    }
    
    public void setCustomImagePath(String customImagePath) {
        this.customImagePath = customImagePath;
    }
    
    public boolean isUseCustomImage() {
        return useCustomImage;
    }
    
    public void setUseCustomImage(boolean useCustomImage) {
        this.useCustomImage = useCustomImage;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    // WILSON SCORE getteri i setteri
    public double getWilsonScore() {
        return wilsonScore;
    }
    
    public void setWilsonScore(double wilsonScore) {
        this.wilsonScore = wilsonScore;
    }
    
    public String getThumbnailUrl() {
        if (useCustomImage && customImagePath != null && !customImagePath.trim().isEmpty()) {
            return customImagePath;
        } else {
            String cleanYoutubeId = youtubeId.contains("_") ? youtubeId.split("_")[0] : youtubeId;
            return "https://img.youtube.com/vi/" + cleanYoutubeId + "/mqdefault.jpg";
        }
    }
    
    public String getHdThumbnailUrl() {
        if (useCustomImage && customImagePath != null && !customImagePath.trim().isEmpty()) {
            return customImagePath;
        } else {
            String cleanYoutubeId = youtubeId.contains("_") ? youtubeId.split("_")[0] : youtubeId;
            return "https://img.youtube.com/vi/" + cleanYoutubeId + "/maxresdefault.jpg";
        }
    }
    
    public String getFormattedWilsonScore() {
        return String.format("%.4f", wilsonScore);
    }
    
    public String getWilsonScorePercentage() {
        return String.format("%.2f%%", wilsonScore * 100);
    }
    
    public double getConfidenceRating() {
        return wilsonScore * 5.0; // Za prikaz kao rating od 0-5
    }
    
    public int getTotalVotes() {
        return positiveVotes + negativeVotes;
    }
    
    public int getScore() {
        return positiveVotes - negativeVotes;
    }
    
    public double getPositivePercentage() {
        if (getTotalVotes() == 0) return 0.0;
        return (double) positiveVotes / getTotalVotes() * 100.0;
    }
    
    public String getFormattedCreatedAt() {
        return createdAt != null ? createdAt.toString() : "";
    }
    
    public int getVotes() {
        return positiveVotes;
    }
    
    public void setVotes(int votes) {
        this.positiveVotes = votes;
    }
    
    public String getCreatedAtString() {
        return getFormattedCreatedAt();
    }
    
    public void setCreatedAtString(String createdAtString) {
        try {
            this.createdAt = LocalDateTime.parse(createdAtString);
        } catch (Exception e) {
            this.createdAt = LocalDateTime.now();
        }
    }
    
    @Override
    public String toString() {
        return "Video{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", youtubeId='" + youtubeId + '\'' +
                ", positiveVotes=" + positiveVotes +
                ", negativeVotes=" + negativeVotes +
                ", wilsonScore=" + String.format("%.4f", wilsonScore) +
                ", useCustomImage=" + useCustomImage +
                ", customImagePath='" + customImagePath + '\'' +
                '}';
    }
}
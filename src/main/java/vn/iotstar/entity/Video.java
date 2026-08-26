package vn.iotstar.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;

@Entity
@Table(name = "Video")
@NamedQuery(name = "Video.findAll", query = "SELECT v FROM Video v")
public class Video {
    @Id
    @Column(name = "videoId", length = 50)
    private String videoId;

    @Column(name = "active")
    private Boolean active = true;

    @Column(name = "description", length = 1000)
    private String description;

    @Column(name = "poster")
    private String poster;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "views")
    private Integer views = 0;

    @ManyToOne
    @JoinColumn(name = "categoryId")
    private Category category;

    public Video() {
    }

    public String getVideoId() { return videoId; }
    public void setVideoId(String videoId) { this.videoId = videoId; }
    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getPoster() { return poster; }
    public void setPoster(String poster) { this.poster = poster; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public Integer getViews() { return views; }
    public void setViews(Integer views) { this.views = views; }
    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }
}

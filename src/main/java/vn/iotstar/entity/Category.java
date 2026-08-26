package vn.iotstar.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "Category")
@NamedQuery(name = "Category.findAll", query = "SELECT c FROM Category c ORDER BY c.categoryId")
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cate_id")
    private int categoryId;

    @Column(name = "cate_name", nullable = false)
    private String categoryname;

    @Column(name = "icons")
    private String images;

    @Column(name = "status")
    private Integer status = 1;

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Video> videos = new ArrayList<>();

    public Category() {
    }

    public Category(String categoryname, String images, Integer status) {
        this.categoryname = categoryname;
        this.images = images;
        this.status = status;
    }

    public int getCategoryId() { return categoryId; }
    public void setCategoryId(int categoryId) { this.categoryId = categoryId; }
    public String getCategoryname() { return categoryname; }
    public void setCategoryname(String categoryname) { this.categoryname = categoryname; }
    public String getImages() { return images; }
    public void setImages(String images) { this.images = images; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
    public List<Video> getVideos() { return videos; }
    public void setVideos(List<Video> videos) { this.videos = videos == null ? new ArrayList<>() : videos; }

    // Aliases keep the existing JSP/controller contract unchanged.
    public int getId() { return categoryId; }
    public void setId(int id) { this.categoryId = id; }
    public String getName() { return categoryname; }
    public void setName(String name) { this.categoryname = name; }
    public String getIcon() { return images; }
    public void setIcon(String icon) { this.images = icon; }

    public void addVideo(Video video) {
        videos.add(video);
        video.setCategory(this);
    }

    public void removeVideo(Video video) {
        videos.remove(video);
        video.setCategory(null);
    }
}

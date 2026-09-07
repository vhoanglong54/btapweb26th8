package vn.iotstar.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import jakarta.persistence.*;

@Entity
@Table(name = "Product")
public class Product {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) @Column(name = "product_id") private int productId;
    @Column(name = "product_name", nullable = false) private String name;
    @Column(length = 2000) private String description;
    @Column(nullable = false, precision = 18, scale = 2) private BigDecimal price;
    private String image;
    @Column(nullable = false) private Integer status = 1;
    @Column(name = "created_at", nullable = false) private LocalDateTime createdAt;
    @ManyToOne(fetch = FetchType.EAGER, optional = false) @JoinColumn(name = "category_id", nullable = false) private Category category;
    public int getProductId() { return productId; } public void setProductId(int productId) { this.productId = productId; }
    public String getName() { return name; } public void setName(String name) { this.name = name; }
    public String getDescription() { return description; } public void setDescription(String description) { this.description = description; }
    public BigDecimal getPrice() { return price; } public void setPrice(BigDecimal price) { this.price = price; }
    public String getImage() { return image; } public void setImage(String image) { this.image = image; }
    public Integer getStatus() { return status; } public void setStatus(Integer status) { this.status = status; }
    public LocalDateTime getCreatedAt() { return createdAt; } public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public Category getCategory() { return category; } public void setCategory(Category category) { this.category = category; }
}

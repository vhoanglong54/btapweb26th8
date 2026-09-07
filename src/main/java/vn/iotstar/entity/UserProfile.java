package vn.iotstar.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.Nationalized;

/** JPA projection of the editable fields in the existing SQL Server User table. */
@Entity
@Table(name = "\"User\"", schema = "dbo")
public class UserProfile {
    @Id
    @Column(name = "id")
    private int id;

    @Nationalized
    @Column(name = "fullname", nullable = false, length = 255)
    private String fullName;

    @Nationalized
    @Column(name = "phone", length = 30)
    private String phone;

    @Nationalized
    @Column(name = "avatar", length = 255)
    private String avatar;

    public int getId() { return id; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getAvatar() { return avatar; }
    public void setAvatar(String avatar) { this.avatar = avatar; }
}

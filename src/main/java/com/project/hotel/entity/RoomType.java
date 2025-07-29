package com.project.hotel.entity;
import jakarta.persistence.*;

@Entity
@Table(name = "RoomTypes")
public class RoomType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String typeName;

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }
    public RoomType(String typeName) {
        this.typeName = typeName;
    }
    public RoomType(){}
}

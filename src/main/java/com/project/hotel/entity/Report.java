package com.project.hotel.entity;
import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "reports")
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "staff_id", nullable = false)
    private Staff staff;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String content;

    @Column(name = "report_date", nullable = false)
    private LocalDate reportDate;


    // Getters and Setters
    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    public Staff getStaff() { return staff; }

    public void setStaff(Staff staff) { this.staff = staff; }

    public String getTitle() { return title; }

    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }

    public void setContent(String content) { this.content = content; }

    public LocalDate getReportDate() { return reportDate; }

    public void setReportDate(LocalDate reportDate) { this.reportDate = reportDate; }
}

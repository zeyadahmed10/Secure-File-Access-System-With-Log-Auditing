package com.zeyad.securefileacess.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "file", schema = "public")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "file_id", nullable = false)
    private Integer id;

    private String name;
    private String content;
    private Timestamp lastUpdated;
    private String checksum;
    @ManyToOne
    @JoinColumn(name = "created_by")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private UserEntity createdBy;

    @ManyToMany(cascade = { CascadeType.ALL }, fetch = FetchType.EAGER)
    @JoinTable(
            name = "file_user",
            joinColumns = @JoinColumn(name = "file_id") ,
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    Set<UserEntity> authorizedUsers = new HashSet<>();

}

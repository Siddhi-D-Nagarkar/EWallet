package org.sdn.userservice.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;

@Setter
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class Wallet {


    private Integer id;

    private String contact;

    private Double balance;

    protected Date createdOn;

    protected Date updatedOn;
}

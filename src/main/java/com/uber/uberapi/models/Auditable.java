package com.uber.uberapi.models;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@MappedSuperclass  //don't create table for auditable
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public abstract class Auditable implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(updatable = false)
    @Temporal(TemporalType.TIMESTAMP)    //provided by jpa
    @CreatedDate    //provided by hibernate
    private Date createdAt;

    @Temporal(TemporalType.TIMESTAMP)
    @LastModifiedDate
    private Date updatedAt;

    @Override
    public int hashCode() {
        return id == null ? 0 : id.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if(this == obj) return true;
        if(obj instanceof Auditable) {
            Auditable auditableObj = (Auditable) obj;
            if(id == null && obj == null) return true;
            if(id == null || obj == null) return false;
            return id == auditableObj.id;
        } else {
            return super.equals(obj);
        }
    }
}

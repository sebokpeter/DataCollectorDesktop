/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Entity;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Peter
 */
@Entity
@Table(name = "DESCRIPTOR")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Descriptor.findAll", query = "SELECT d FROM Descriptor d"),
    @NamedQuery(name = "Descriptor.findById", query = "SELECT d FROM Descriptor d WHERE d.id = :id"),
    @NamedQuery(name = "Descriptor.findByDId", query = "SELECT d FROM Descriptor d WHERE d.dId = :dId"),
    @NamedQuery(name = "Descriptor.findByDbField", query = "SELECT d FROM Descriptor d WHERE d.dbField = :dbField"),
    @NamedQuery(name = "Descriptor.findByType", query = "SELECT d FROM Descriptor d WHERE d.type = :type"),
    @NamedQuery(name = "Descriptor.findByItemorder", query = "SELECT d FROM Descriptor d WHERE d.itemorder = :itemorder"),
    @NamedQuery(name = "Descriptor.findByNamespace", query = "SELECT d FROM Descriptor d WHERE d.namespace = :namespace"),
    @NamedQuery(name = "Descriptor.findByNodeid", query = "SELECT d FROM Descriptor d WHERE d.nodeid = :nodeid"),
    @NamedQuery(name = "Descriptor.findByNodeidType", query = "SELECT d FROM Descriptor d WHERE d.nodeidType = :nodeidType")})
public class Descriptor implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "ID")
    private Long id;
    @Basic(optional = false)
    @Column(name = "D_ID")
    private int dId;
    @Basic(optional = false)
    @Column(name = "DB_FIELD")
    private String dbField;
    @Basic(optional = false)
    @Column(name = "TYPE")
    @Enumerated(EnumType.STRING)
    private DatabaseFieldType type;
    @Column(name = "ITEMORDER")
    private Integer itemorder;
    @Column(name = "NAMESPACE")
    private Integer namespace;
    @Column(name = "NODEID")
    private String nodeid;
    @Column(name = "NODEID_TYPE")
    private String nodeidType;

    public Descriptor() {
    }

    public Descriptor(Long id) {
        this.id = id;
    }

    public Descriptor(Long id, int dId, String dbField, String type) {
        this.id = id;
        this.dId = dId;
        this.dbField = dbField;
        type.toUpperCase();
        this.type = DatabaseFieldType.valueOf(type);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getDId() {
        return dId;
    }

    public void setDId(int dId) {
        this.dId = dId;
    }

    public String getDbField() {
        return dbField;
    }

    public void setDbField(String dbField) {
        this.dbField = dbField;
    }

    public DatabaseFieldType getType() {
        return type;
    }

    public void setType(String type) {
        type.toUpperCase();
        this.type = DatabaseFieldType.valueOf(type);
    }

    public Integer getItemorder() {
        return itemorder;
    }

    public void setItemorder(Integer itemorder) {
        this.itemorder = itemorder;
    }

    public Integer getNamespace() {
        return namespace;
    }

    public void setNamespace(Integer namespace) {
        this.namespace = namespace;
    }

    public String getNodeid() {
        return nodeid;
    }

    public void setNodeid(String nodeid) {
        this.nodeid = nodeid;
    }

    public String getNodeidType() {
        return nodeidType;
    }

    public void setNodeidType(String nodeidType) {
        this.nodeidType = nodeidType;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Descriptor)) {
            return false;
        }
        Descriptor other = (Descriptor) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Entity.Descriptor[ id=" + id + " ]";
    }
    
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Entity;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Peter
 */
@Entity
@Table(name = "SQLDATA")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "SQLData.findAll", query = "SELECT s FROM SQLData s"),
    @NamedQuery(name = "SQLData.findById", query = "SELECT s FROM SQLData s WHERE s.id = :id"),
    @NamedQuery(name = "SQLData.findByName", query = "SELECT s FROM SQLData s WHERE s.name = :name"),
    @NamedQuery(name = "SQLData.findByPassword", query = "SELECT s FROM SQLData s WHERE s.password = :password"),
    @NamedQuery(name = "SQLData.findByDbName", query = "SELECT s FROM SQLData s WHERE s.dbName = :dbName"),
    @NamedQuery(name = "SQLData.findByDbAddress", query = "SELECT s FROM SQLData s WHERE s.dbAddress = :dbAddress"),
    @NamedQuery(name = "SQLData.findByDbPort", query = "SELECT s FROM SQLData s WHERE s.dbPort = :dbPort"),
    @NamedQuery(name = "SQLData.findByDId", query = "SELECT s FROM SQLData s WHERE s.dId = :dId")})
public class SQLData implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "ID")
    private Integer id;
    @Column(name = "NAME")
    private String name;
    @Column(name = "PASSWORD")
    private String password;
    @Column(name = "DB_NAME")
    private String dbName;
    @Column(name = "DB_ADDRESS")
    private String dbAddress;
    @Column(name = "DB_PORT")
    private Integer dbPort;
    @Column(name = "D_ID")
    private Integer dId;
    
    @Transient
    private DescriptorConn dc;
    @Transient
    private List<Descriptor> descriptors;
    
    public SQLData() {
    }

    public SQLData(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    public String getDbAddress() {
        return dbAddress;
    }

    public void setDbAddress(String dbAddress) {
        this.dbAddress = dbAddress;
    }

    public Integer getDbPort() {
        return dbPort;
    }

    public void setDbPort(Integer dbPort) {
        this.dbPort = dbPort;
    }

    public Integer getDId() {
        return dId;
    }

    public void setDId(Integer dId) {
        this.dId = dId;
    }

    public DescriptorConn getDc() {
        return dc;
    }

    public void setDc(DescriptorConn dc) {
        this.dc = dc;
    }

    public List<Descriptor> getDescriptors() {
        return descriptors;
    }

    public void setDescriptors(List<Descriptor> descriptors) {
        this.descriptors = descriptors;
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
        if (!(object instanceof SQLData)) {
            return false;
        }
        SQLData other = (SQLData) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Entity.Sqldata[ id=" + id + " ]";
    }
    
}

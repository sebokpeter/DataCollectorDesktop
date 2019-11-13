package Entity;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

/**
 * Used to connect a group of descriptors
 * TODO: find a better name
 *
 * @author Peter
 */
@Entity
@Table(name = "DESCRIPTOR_CONN")
@NamedQueries({
    @NamedQuery(name = "DescriptorConn.findAll", query = "SELECT d FROM DescriptorConn d"),
    @NamedQuery(name = "DescriptorConn.findByDId", query = "SELECT d FROM DescriptorConn d WHERE d.dId = :dId"),
    @NamedQuery(name = "DescriptorConn.findByName", query = "SELECT d FROM DescriptorConn d WHERE d.name = :name"),
    @NamedQuery(name = "DescriptorConn.findByTableName", query = "SELECT d FROM DescriptorConn d WHERE d.tableName = :tableName")})
public class DescriptorConn implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "D_ID")
    private Integer dId; // ID
    @Column(name = "NAME")
    private String name; // Name 
    @Column(name = "TABLE_NAME")
    private String tableName; // Table name in the target database

    public DescriptorConn() {
    }

    public DescriptorConn(Integer dId) {
        this.dId = dId;
    }

    public Integer getDId() {
        return dId;
    }

    public void setDId(Integer dId) {
        this.dId = dId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (dId != null ? dId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof DescriptorConn)) {
            return false;
        }
        DescriptorConn other = (DescriptorConn) object;
        return !((this.dId == null && other.dId != null) || (this.dId != null && !this.dId.equals(other.dId)));
    }

    @Override
    public String toString() {
        return "Entity.DescriptorConn[ dId=" + dId + " ]";
    }

}

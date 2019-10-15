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
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Represent data that can be used to connect to an OPC-UA server.
 * @author Peter
 */
@Entity
@Table(name = "OPCDATA")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "OPCData.findAll", query = "SELECT o FROM OPCData o"),
    @NamedQuery(name = "OPCData.findById", query = "SELECT o FROM OPCData o WHERE o.id = :id"),
    @NamedQuery(name = "OPCData.findByUrl", query = "SELECT o FROM OPCData o WHERE o.url = :url"),
    @NamedQuery(name = "OPCData.findByAnon", query = "SELECT o FROM OPCData o WHERE o.anon = :anon"),
    @NamedQuery(name = "OPCData.findByUsername", query = "SELECT o FROM OPCData o WHERE o.username = :username"),
    @NamedQuery(name = "OPCData.findByPassword", query = "SELECT o FROM OPCData o WHERE o.password = :password"),
    @NamedQuery(name = "OPCData.findByName", query = "SELECT o FROM OPCData o WHERE o.name = :name")})
public class OPCData implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "ID")
    private Integer id;
    @Basic(optional = false)
    @Column(name = "URL") 
    private String url; // URL of the OPC-UA server
    @Basic(optional = false)
    @Column(name = "ANON")
    private Boolean anon; // Whether or not we can connect anonimously
    @Column(name = "USERNAME")
    private String username; // Username to connect to the OPC-UA server (if can't connect anonimusly)
    @Column(name = "PASSWORD")
    private String password; // Password to connet to the OPC-UA server (if can't connect anonimusly)
    @Basic(optional = false)
    @Column(name = "NAME")
    private String name; // Identifying name

    public OPCData() {
    }

    public OPCData(Integer id) {
        this.id = id;
    }

    public OPCData(Integer id, String url, Boolean anon, String name) {
        this.id = id;
        this.url = url;
        this.anon = anon;
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Boolean getAnon() {
        return anon;
    }

    public void setAnon(Boolean anon) {
        this.anon = anon;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
        if (!(object instanceof OPCData)) {
            return false;
        }
        OPCData other = (OPCData) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Entity.OPCData[ id=" + id + " ]";
    }
    
}

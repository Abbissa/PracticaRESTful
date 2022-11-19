package datos;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement(name = "usuario")
public class Usuario {
    private String username;
    private String nombre;
    private Integer edad;
    private String localidad;
    
    private ArrayList<Tesoro> tesoros;

    public Usuario(String username, String nombre, Integer edad, String localidad) {
		super();
		this.username = username;
		this.nombre = nombre;
		this.edad = edad;
		this.localidad = localidad;
		this.tesoros = new ArrayList<Tesoro>();
	}
    
    public Usuario() {
        this.tesoros = new ArrayList<Tesoro>();
    }

    @XmlAttribute(required=false)
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    
    public Integer getEdad() {
        return edad;
    }

    public void setEdad(Integer edad) {
        this.edad = edad;
    }

    public String getLocalidad() {
        return localidad;
    }

    public void setLocalidad(String localidad) {
        this.localidad = localidad;
    }

   public Usuario(String username, String nombre, int edad, String localidad) {
        super();
        this.username = username;
        this.nombre = nombre;
        this.edad = edad;
        this.localidad = localidad;
        this.tesoros = new ArrayList<>();
    }
}

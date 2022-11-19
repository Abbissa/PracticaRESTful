package datos;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "resumen")
public class Resumen {
	/*private String username;
	private Integer edad;
	private String localidad;
	private String nombre;*/
	private Integer numEncontrados;
	private ArrayList<Tesoro> encontrados;
	private Integer numAmigos;
	private Integer numCreados;
	private Usuario usuario;
	
	@XmlAttribute(required=false)
	
	/*public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
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
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}*/
	
	
	public Integer getNumEncontrados() {
		return numEncontrados;
	}
	public void setNumEncontrados(Integer numEncontrados) {
		this.numEncontrados = numEncontrados;
	}
	public ArrayList<Tesoro> getEncontrados() {
		return encontrados;
	}
	public void setEncontrados(ArrayList<Tesoro> encontrados) {
		this.encontrados = encontrados;
	}
	public Integer getNumAmigos() {
		return numAmigos;
	}
	public void setNumAmigos(Integer numAmigos) {
		this.numAmigos = numAmigos;
	}
	public Integer getNumCreados() {
		return numCreados;
	}
	public void setNumCreados(Integer numCreados) {
		this.numCreados = numCreados;
	}
	public Usuario getUsuario() {
		return usuario;
	}
	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}
	
	public Resumen() {
		
	}
	
	public Resumen (Integer numEncontrados, ArrayList<Tesoro> encontrados, Integer numAmigos, Integer numCreados, Usuario usuario) {
		/*this.username = username;
		this.edad = edad;
		this.localidad = localidad;
		this.nombre = nombre;*/
		this.numEncontrados = numEncontrados;
		this.encontrados = encontrados;
		this.numAmigos = numAmigos;
		this.numCreados = numCreados;
		this.usuario = usuario;
	}
}

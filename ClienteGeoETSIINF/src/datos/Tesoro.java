package datos;

import java.util.Date;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "tesoro")
public class Tesoro {
	private int idTesoro;
	// Usuario que ha creado el tesoro
	private String username;
	private String terreno;
	private String dificultad;
	private Double latitud;
	private Double longitud;
	private String pista;
	private String tamanyo;
	private String fecha_creado;
	//private Date fecha_encontrado;

	public Tesoro(String username, String terreno, String dificultad, Double latitud, Double longitud,
			String pista, String tamanyo, String fecha_creado) {
		this.username = username;
		this.terreno = terreno;
		this.dificultad = dificultad;
		this.latitud = latitud;
		this.longitud = longitud;
		this.pista = pista;
		this.tamanyo = tamanyo;
		this.fecha_creado = fecha_creado;
	}

	

	@XmlAttribute(required=false)
	public int getIdTesoro() {
		return idTesoro;
	}

	

	public void setIdTesoro(int idTesoro) {
		this.idTesoro = idTesoro;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getTerreno() {
		return terreno;
	}

	public void setTerreno(String terreno) {
		this.terreno = terreno;
	}

	public String getDificultad() {
		return dificultad;
	}

	public void setDificultad(String dificultad) {
		this.dificultad = dificultad;
	}

	public Double getLatitud() {
		return latitud;
	}

	public void setLatitud(Double latitud) {
		this.latitud = latitud;
	}

	public Double getLongitud() {
		return longitud;
	}

	public void setLongitud(Double longitud) {
		this.longitud = longitud;
	}

	public String getPista() {
		return pista;
	}

	public void setPista(String pista) {
		this.pista = pista;
	}

	public String getTamanyo() {
		return tamanyo;
	}

	public void setTamanyo(String tamanyo) {
		this.tamanyo = tamanyo;
	}

	public String getFecha_creado() {
		return fecha_creado;
	}

	public void setFecha_creado(String fecha_creado) {
		this.fecha_creado = fecha_creado;
	}

	/*public Date getFecha_encontrado() {
		return fecha_encontrado;
	}

	public void setFecha_encontrado(Date fecha_encontrado) {
		this.fecha_encontrado = fecha_encontrado;
	}*/
	
	public Tesoro() {

    }

    public Tesoro(int idTesoro, String username, String terreno, String dificultad, Double latitud, Double longitud, String pista, String tamanyo, String fecha_creado) {
        super();
        this.idTesoro = idTesoro;
        this.username = username;
        this.terreno = terreno;
        this.dificultad = dificultad;
        this.latitud = latitud;
        this.longitud = longitud;
        this.pista = pista;
        this.tamanyo = tamanyo;
        this.fecha_creado = fecha_creado;
        }
}

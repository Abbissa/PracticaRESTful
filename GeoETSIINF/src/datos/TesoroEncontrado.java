package datos;

import java.util.Date;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "tesoroEncontrado")
public class TesoroEncontrado {
	private int idTesoro;
	private String username;
	private String fecha_encontrado;

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

	public String getFecha_encontrado() {
		return fecha_encontrado;
	}

	public void setFecha_encontrado(String fecha_encontrado) {
		this.fecha_encontrado = fecha_encontrado;
	}
	
	public TesoroEncontrado() {

    }

    public TesoroEncontrado(int idTesoro, String username, String fecha_encontrado) {
        super();
        this.idTesoro = idTesoro;
        this.username = username;
        this.fecha_encontrado = fecha_encontrado;
        }
}

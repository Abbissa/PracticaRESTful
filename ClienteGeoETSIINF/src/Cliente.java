
import java.net.URI;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import org.glassfish.jersey.client.ClientConfig;

import datos.Tesoro;
import datos.TesoroEncontrado;
import datos.Usuario;

public class Cliente {

	public static void main(String[] args) throws Exception {

		ClientConfig config = new ClientConfig();
		Client client = ClientBuilder.newClient(config);
		WebTarget target = client.target(getBaseURI());

		// GET usuarios
		System.out.println("Consultar datos de todos los usuarios");
		Response r = target.path("api/usuarios").queryParam("pattern", "mar").queryParam("offset", "3")
				.queryParam("count", "5")
				.request().accept(MediaType.APPLICATION_XML)
				.get(Response.class);

		System.out.println("Estado: " + r.getStatus());
		String valor = r.readEntity(String.class).replaceAll("},", "},\n");
		System.out.println("Entidad: " + valor);
		System.out.println();

		// POST usuarios
		System.out.println("Crear un nuevo usuario");
		Usuario usuario = new Usuario("mariacc", "Maria", 34, "Almeria");

		r = target.path("api/usuarios")
				.request()
				.post(Entity.xml(usuario));

		System.out.println("Estado: " + r.getStatus());
		r.close();
		System.out.println();

		// GET usuarios/username
		System.out.println("Recogiendo los datos del nuevo usuario creado");
		System.out.println("Entidad: " + target.path("api/usuarios/mariacc")
				.request().accept(MediaType.APPLICATION_XML)
				.get(String.class));
		System.out.println();

		// PUT usuarios/username
		System.out.println("Modificando los datos del nuevo usuario creado");
		usuario = new Usuario("mariacc", "Maria", 20, "Barcelona");
		r = target.path("api/usuarios/mariacc")
				.request()
				.put(Entity.entity(usuario, MediaType.APPLICATION_XML));
		System.out.println("Estado: " + r.getStatus());
		System.out.println();

		// DELETE usuarios/username
		System.out.println("Borrar el usuario creado");
		r = target.path("api/usuarios/mariacc").request().delete();
		System.out.println("Estado: " + r.getStatus());
		System.out.println();

		// GET usuarios/username/tesoros_creados
		System.out.println(
				"Consultar datos de todos los tesoro creados por un usuario con terreno llano y dificultado normal");
		r = target.path("api/usuarios/tajo/tesoros_creados").queryParam("terreno", "llano")
				.queryParam("dificultad", "normal").queryParam("count", "5")
				.request().accept(MediaType.APPLICATION_XML)
				.get(Response.class);

		System.out.println("Estado: " + r.getStatus());
		valor = r.readEntity(String.class).replaceAll("},", "},\n");
		System.out.println("Entidad: " + valor);
		System.out.println();

		// POST usuarios/username/tesoros_creados
		System.out.println("Crear un nuevo tesoro");
		Tesoro tesoro = new Tesoro("paulina", "llano", "normal", 30.24214, -180.0, "abajo", "pequenyo", "2014-04-30");

		r = target.path("api/usuarios/paulina/tesoros_creados")
				.request()
				.post(Entity.xml(tesoro));

		System.out.println("Estado: " + r.getStatus());
		String path = r.getHeaderString("Location");
		int id = Integer.parseInt(path.split("tesoros_creados/")[1]);
		System.out.println();

		// GET usuarios/username/tesoros_creados/id_Tesoro
		tesoro = client.target(path)
				.request().accept(MediaType.APPLICATION_XML)
				.get(Tesoro.class);

		System.out.println("Consultar datos del tesoro creado");
		System.out.println(client.target(path)
				.request().accept(MediaType.APPLICATION_XML)
				.get(String.class));

		// PUT usuarios/username/tesoros_creados/id_tesoro
		System.out.println("Modificando los datos del nuevo tesoro creado");
		Tesoro tesoro2 = new Tesoro(tesoro.getIdTesoro(), "paulina", "llano", "normal", 30.24214, -180.0, "abajo",
				"pequenyo", "2014-04-30");
		r = target.path("api/usuarios/paulina/tesoros_creados/" + tesoro.getIdTesoro())
				.request()
				.put(Entity.entity(tesoro2, MediaType.APPLICATION_XML));
		System.out.println("Estado: " + r.getStatus());
		System.out.println();

		// DELETE usuarios/username/tesoros_creados/id_tesoro
		System.out.println("Borrar el tesoro creado");
		r = target.path("api/usuarios/paulina/tesoros_creados/" + tesoro.getIdTesoro()).request().delete();
		System.out.println("Estado: " + r.getStatus());
		System.out.println();

		// GET usuarios/username/tesoros_encontrados

		System.out.println("Consultar datos de todos los tesoro encontrados por un usuario");
		r = target.path("api/usuarios/tajo/tesoros_encontrados")
				.request().accept(MediaType.APPLICATION_XML)
				.get(Response.class);

		System.out.println("Estado: " + r.getStatus());
		valor = r.readEntity(String.class).replaceAll("},", "},\n");
		System.out.println("Entidad: " + valor);
		System.out.println();

		// POST usuarios/username/tesoros_encontrados
		System.out.println("Crear un nuevo tesoro encontrado");
		TesoroEncontrado tesEnc = new TesoroEncontrado(1, "paulina", "2014-08-23");

		r = target.path("api/usuarios/paulina/tesoros_encontrados")
				.request()
				.post(Entity.xml(tesEnc));

		System.out.println("Estado: " + r.getStatus());
		System.out.println();

		// GET usuarios/username/tesoros_encontrados/id_tesoro

		System.out.println("Consultar datos del nuevo tesoro encontrado creado");
		r = target.path("api/usuarios/paulina/tesoros_encontrados/1")
				.request().accept(MediaType.APPLICATION_XML)
				.get(Response.class);

		System.out.println("Estado: " + r.getStatus());
		valor = r.readEntity(String.class);
		System.out.println("Entidad: " + valor);
		System.out.println();

		// GET usuarios/username/amigos
		System.out.println("Consultar lista de amigos de un usuario");
		r = target.path("api/usuarios/paulina/amigos")
				.request().accept(MediaType.APPLICATION_XML)
				.get(Response.class);

		System.out.println("Estado: " + r.getStatus());
		valor = r.readEntity(String.class).replaceAll("},", "},\n");
		System.out.println("Entidad: " + valor);
		System.out.println();

		// POST usuarios/username/amigos
		System.out.println("Crear un nuevo amigo");
		String amigo = "master902";

		r = target.path("api/usuarios/paulina/amigos")
				.request()
				.post(Entity.xml(amigo));

		System.out.println("Estado: " + r.getStatus());
		System.out.println();

		// GET usuarios/username/amigos/username
		System.out.println("Consultar el amigo creado");
		r = target.path("api/usuarios/paulina/amigos/master902")
				.request().accept(MediaType.APPLICATION_XML)
				.get(Response.class);

		System.out.println("Estado: " + r.getStatus());
		valor = r.readEntity(String.class).replaceAll("},", "},\n");
		System.out.println("Entidad: " + valor);
		System.out.println();

		// DELETE usuarios/username/amigo/username
		System.out.println("Borrar el amigo creado");
		r = target.path("api/usuarios/paulina/amigos/master902").request().delete();
		System.out.println("Estado: " + r.getStatus());
		System.out.println();

		// GET usuarios/username/resumen
		System.out.println("Consultar el resumen de un usuario");
		r = target.path("api/usuarios/paulina/resumen")
				.request().accept(MediaType.APPLICATION_XML)
				.get(Response.class);

		System.out.println("Estado: " + r.getStatus());
		valor = r.readEntity(String.class).replaceAll("},", "},\n");
		System.out.println("Entidad: " + valor);
		System.out.println();

		// GET tesorosCercanos

		System.out.println("Consultar los tesoros cercanos a unas coordenadas");
		r = target.path("api/tesoros_cercanos").pathParam("latitud","89.38941").pathParam("latitud","-84.87899")
				.request().accept(MediaType.APPLICATION_XML)
				.get(Response.class);

		System.out.println("Estado: " + r.getStatus());
		valor = r.readEntity(String.class).replaceAll("},", "},\n");
		System.out.println("Entidad: " + valor);
		System.out.println();

		r.close();
	}

	private static URI getBaseURI() {
		return UriBuilder.fromUri("http://localhost:8080/GeoETSIINF/").build();
	}

}

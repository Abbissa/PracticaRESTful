package recursosbdd;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.apache.naming.NamingContext;

import datos.Usuario;
import datos.Usuarios;
import datos.Link;
import datos.Resumen;
import datos.Tesoro;
import datos.TesoroEncontrado;
import datos.Tesoros;

@Path("/usuarios")
public class UsuariosRecurso {

	@Context
	private UriInfo uriInfo;

	private DataSource ds;
	private Connection conn;

	public UsuariosRecurso() {
		InitialContext ctx;
		try {
			ctx = new InitialContext();
			NamingContext envCtx = (NamingContext) ctx.lookup("java:comp/env");
			ds = (DataSource) envCtx.lookup("jdbc/mydb");

			conn = ds.getConnection();

		} catch (NamingException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	//Consultar lista de usuarios
	@GET
	@Produces({MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON})
	public Response getUsuarios2(@QueryParam("offset") @DefaultValue("1") String offset,
			@QueryParam("count") @DefaultValue("10") String count,
			@QueryParam("pattern") @DefaultValue("") String pattern) {
		try {
			int off = Integer.parseInt(offset);
			int c = Integer.parseInt(count);
			String sql = "SELECT * FROM mydb.Usuario WHERE username LIKE "+"'%"+pattern+"%'"+" order by username LIMIT " + (off - 1) + "," + c + ";";
			PreparedStatement ps = conn.prepareStatement(sql);
			ResultSet rs = ps.executeQuery();

			Usuarios g = new Usuarios();
			ArrayList<Link> usuarios = g.getUsuarios();
			rs.beforeFirst();
			while (rs.next()) {
				usuarios.add(new Link(uriInfo.getAbsolutePath() + "/" + rs.getString("username"),"self"));
			}

			return Response.status(Response.Status.OK).entity(g).build(); // No se puede devolver el ArrayList (para generar XML)
		} catch (NumberFormatException e) {
			return Response.status(Response.Status.BAD_REQUEST).entity("No se pudieron convertir los índices a números")
					.build();
		} catch (SQLException e) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error de acceso a BBDD").build();
		}
	}
	

	// Consultar usuario concreto
	@GET
	@Path("{usuario_id}")
	@Produces({MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON})
	public Response getUsuario(@PathParam("usuario_id") String id) {
		try {
			String sql = "SELECT * FROM mydb.Usuario where username =\"" + id + "\";";
			PreparedStatement ps = conn.prepareStatement(sql);
			ResultSet rs = ps.executeQuery();


			if (rs.next()) {
				Usuario user = new Usuario();

				user.setUsername(rs.getString("username"));
				user.setNombre(rs.getString("nombre"));
				user.setEdad(rs.getInt("edad"));
				user.setLocalidad(rs.getString("localidad"));
				return Response.status(Response.Status.OK).entity(user).build();
			} else {
				return Response.status(Response.Status.NOT_FOUND).entity("Elemento no encontrado").build();
			}
		} catch (SQLException e) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error de acceso a BBDD").build();
		}
	}

	
	// Actualizar usuario
	@PUT
	@Path("{id_usuario}")
	@Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	public Response editUsuario(@PathParam("id_usuario") String id,Usuario user) {
		try {
			String sql = "SELECT * FROM mydb.Usuario where username =\"" + id + "\";";
			PreparedStatement ps = conn.prepareStatement(sql);
			ResultSet rs = ps.executeQuery();

			if (!rs.next()) 
				return Response.status(Response.Status.NOT_FOUND).entity("El usuario no existe").build();
			if(!rs.getString("username").equals(user.getUsername()))
				return Response.status(Response.Status.FORBIDDEN).entity("El nombre de un usuario no puede ser modificado").build();
			if(user.getEdad()==null||user.getLocalidad()==null||user.getNombre()==null)
				return Response.status(Response.Status.BAD_REQUEST).entity("Han de proporcionar todos los datos del usuario").build();


			user.setUsername(id);

			sql = "UPDATE `mydb`.`Usuario` SET "
					+ "nombre = \""+user.getNombre()+"\""
					+", edad = "+user.getEdad()
					+", localidad = \""+user.getLocalidad()+"\""
					+" WHERE username = \""+user.getUsername()+"\";" ;
			
			ps = conn.prepareStatement(sql);
			ps.execute();

			// Obtener el ID del elemento recién creado. 
			// Necesita haber indicado Statement.RETURN_GENERATED_KEYS al ejecutar un statement.executeUpdate() o al crear un PreparedStatement

			
			String location = uriInfo.getAbsolutePath()+"/"+ user.getUsername();;
			return Response.status(Response.Status.OK).header("Content-Location", location).build();
		} catch (SQLException e) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("No se pudo crear el usuario\n" + e.getMessage()).build();
		}
	}

	
	// Crear un nuevo usuario
	@POST
	@Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	public Response createUsuario(Usuario user) {
		try {
			if(user.getEdad()==null||user.getLocalidad()==null||user.getNombre()==null||user.getUsername()==null)
				return Response.status(Response.Status.BAD_REQUEST).entity("Todos los parametros del usuario han de tener un valor").build();
			String sql = "INSERT INTO `mydb`.`Usuario` (`username`, `nombre`, `edad`, `localidad`) " + "VALUES ('"
					+ user.getUsername() + "', '" + user.getNombre() + "', '" + user.getEdad() + "', '" + user.getLocalidad() + "');";
			PreparedStatement ps = conn.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);
			int affectedRows = ps.executeUpdate();

			boolean si = true;

			// Obtener el ID del elemento recién creado. 
			// Necesita haber indicado Statement.RETURN_GENERATED_KEYS al ejecutar un statement.executeUpdate() o al crear un PreparedStatement
			ResultSet generatedID = ps.getGeneratedKeys();
			if (si) {
				String location = uriInfo.getAbsolutePath() + "/" + user.getUsername();

			
				return Response.status(Response.Status.CREATED).header("Location", location).header("Content-Location", location).build();
			}
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("No se pudo crear el usuario").build();

		} catch (SQLException e) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("No se pudo crear el usuario\n" + e.getStackTrace()).build();
		}
	}
	
	
	// Borrar un usuario concreto
	@DELETE
	@Path("{idUsuario}")
	public Response deleteUsuario(@PathParam("idUsuario") String id) {
		try {
			//int int_id = Integer.parseInt(id);
			String sql = "DELETE FROM `mydb`.`Usuario` WHERE `username`='" + id + "';";
			PreparedStatement ps = conn.prepareStatement(sql);
			int affectedRows = ps.executeUpdate();
			if (affectedRows == 1)
				return Response.status(Response.Status.NO_CONTENT).build();
			else 
				return Response.status(Response.Status.NOT_FOUND).entity("Elemento no encontrado").build();		
		} catch (SQLException e) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("No se pudo eliminar el garaje\n" + e.getStackTrace()).build();
		}
	}


	// Consultar tesoros creados por un usuario concreto
	// Lista de tesoros JSON/XML generada con listas en JAXB
		@GET
		@Path("{usuario_id}/tesoros_creados")
		@Produces({MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON})
		public Response getTesorosCreados(@PathParam("usuario_id") String id, @QueryParam("offset") @DefaultValue("1") String offset,
				@QueryParam("count") @DefaultValue("10") String count,
				@QueryParam("terreno")  @DefaultValue("") String terreno,
				@QueryParam("dificultad") @DefaultValue("") String dificultad,
				@QueryParam("tamanyo") @DefaultValue("") String tamanyo) {
			try {
				System.out.println(id);
				int off = Integer.parseInt(offset);
				int c = Integer.parseInt(count);
				String constraints ="";
				if(!terreno.equals("")) 
					constraints+=" AND terreno=\""+terreno+"\"";
				if(!dificultad.equals("")) 
					constraints+=" AND dificultad =\""+dificultad+"\"";
				if(!tamanyo.equals("")) 
					constraints+=" AND tamanyo =\""+tamanyo+"\"";

				String sql = "SELECT * FROM mydb.Tesoro WHERE username=\""+ id + "\""+constraints+" order by idTesoro LIMIT " + (off - 1) + "," + c + ";";
				PreparedStatement ps = conn.prepareStatement(sql);
				ResultSet rs = ps.executeQuery();
				Tesoros g = new Tesoros();
				ArrayList<Link> tesoros = g.getTesoros();
				rs.beforeFirst();
				
				while (rs.next()) {
					tesoros.add(new Link(uriInfo.getAbsolutePath() + "/" + rs.getInt("idTesoro"),"self"));
				}
				return Response.status(Response.Status.OK).entity(g).build(); // No se puede devolver el ArrayList (para generar XML)
			} catch (NumberFormatException e) {
				return Response.status(Response.Status.BAD_REQUEST).entity("No se pudieron convertir los índices a números")
						.build();
			} catch (SQLException e) {
				return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error de acceso a BBDD").build();
			}
		}
		
		// Crear tesoro
		@POST
		@Path("{usuario_id}/tesoros_creados")
		@Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
		public Response createTesoro(@PathParam("usuario_id") String idUsuario, Tesoro tesoro) {
			try {
				
				if (!idUsuario.equals(tesoro.getUsername())||tesoro.getLatitud() == null || tesoro.getLongitud() == null || tesoro.getDificultad() == null || tesoro.getTamanyo() == null || tesoro.getTerreno() == null || tesoro.getPista() == null)
					return Response.status(Response.Status.BAD_REQUEST).entity("Campos obligatorios del tesoro: latitud, longitud, dificultad, tamanyo, terreno y pista.\n").build();
				String sql = "INSERT INTO mydb.Tesoro (`username`, `terreno`, `dificultad`, `latitud`, `longitud`, `pista`, `tamanyo`, `fecha_creado`) VALUES (\"" + idUsuario + "\",\"" + tesoro.getTerreno() + "\",\"" + tesoro.getDificultad() +
						"\"," + tesoro.getLatitud() + "," + tesoro.getLongitud() + ",\"" + tesoro.getPista() + "\",\"" + tesoro.getTamanyo() + "\", \"" + tesoro.getFecha_creado() +  "\");";
				PreparedStatement ps = conn.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);
				int affectedRows = ps.executeUpdate();
				
				// Obtener el ID del elemento recién creado. 
				// Necesita haber indicado Statement.RETURN_GENERATED_KEYS al ejecutar un statement.executeUpdate() o al crear un PreparedStatement
				ResultSet generatedID = ps.getGeneratedKeys();
				if (generatedID.next()) {
					tesoro.setIdTesoro(generatedID.getInt(1));
					String location = uriInfo.getAbsolutePath() + "/" + tesoro.getIdTesoro();
					return Response.status(Response.Status.CREATED).entity(tesoro).header("Location", location).header("Content-Location", location).build();
				}
				return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("No se pudo crear el tesoro").build();
				
			} catch (SQLException e) {
				return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("No se pudo crear el tesoro\n" + e.getStackTrace()).build();
			}
		}
		
		// Actualizar tesoro
		@PUT
		@Path("{id_usuario}/tesoros_creados/{id_tesoro}")
		@Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
		public Response editTesoro(@PathParam("id_usuario") String idUsuario, @PathParam("id_tesoro") Integer idTesoro, Tesoro tesoro) {
			try {
				String sql = "SELECT * FROM mydb.Tesoro where username =\"" + idUsuario + "\" and idTesoro='" + idTesoro + "';";
				PreparedStatement ps = conn.prepareStatement(sql);
				ResultSet rs = ps.executeQuery();

				if (!rs.next()) 
					return Response.status(Response.Status.NOT_FOUND).entity("El tesoro no existe").build();


				tesoro.setIdTesoro(idTesoro);
				tesoro.setUsername(idUsuario.toString());

				sql = "UPDATE `mydb`.`Tesoro` SET "
						+ "username = \""+idUsuario+"\""
						+", terreno = \""+tesoro.getTerreno() + "\""
						+", dificultad = \""+tesoro.getDificultad() + "\""
						+", latitud = \""+tesoro.getLatitud()+"\""
						+", longitud = \""+tesoro.getLongitud()+"\""
						+", pista = \""+tesoro.getPista()+"\""
						+", tamanyo = \""+tesoro.getTamanyo()+"\""
						+", fecha_creado = \""+tesoro.getFecha_creado()+"\""
						+" WHERE idTesoro = "+tesoro.getIdTesoro()+";" ;
				
				ps = conn.prepareStatement(sql);
				ps.execute();

				// Obtener el ID del elemento recién creado. 
				// Necesita haber indicado Statement.RETURN_GENERATED_KEYS al ejecutar un statement.executeUpdate() o al crear un PreparedStatement

				
				String location = uriInfo.getAbsolutePath()+"/"+ tesoro.getIdTesoro();
				return Response.status(Response.Status.OK).header("Content-Location", location).build();
			} catch (SQLException e) {
				return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("No se pudo crear el tesoro\n" + e.getMessage()).build();
			}
		}
		
		// Borrar un tesoro concreto
		@DELETE
		@Path("{id_usuario}/tesoros_creados/{id_tesoro}")
		public Response deleteTesoro(@PathParam("id_usuario") String idUsuario, @PathParam("id_tesoro") Integer idTesoro) {
			try {
				//int int_id = Integer.parseInt(id);
				String sql = "DELETE FROM `mydb`.`Tesoro` WHERE `username`='" + idUsuario + "' and idTesoro='" + idTesoro + "';";
				PreparedStatement ps = conn.prepareStatement(sql);
				int affectedRows = ps.executeUpdate();
				if (affectedRows == 1)
					return Response.status(Response.Status.NO_CONTENT).build();
				else 
					return Response.status(Response.Status.NOT_FOUND).entity("El tesoro no existe o no fue creado por este usuario").build();		
			} catch (SQLException e) {
				return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("No se pudo eliminar el tesoro\n" + e.getStackTrace()).build();
			}
		}
		
	// Consultar tesoros encontrados por un usuario
		// añadir filtros
	@GET
	@Path("{id_usuario}/tesoros_encontrados")
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public Response getTesorosEncontrados(@QueryParam("offset") @DefaultValue("1") String offset,
			@QueryParam("count") @DefaultValue("10") String count,
			@QueryParam("pattern") @DefaultValue("") String pattern, 
			@QueryParam("fecha") @DefaultValue("0000-00-00") String fecha,
			@QueryParam("terreno") @DefaultValue("") String terreno,
			@QueryParam("dificultad") @DefaultValue("") String dificultad,
			@QueryParam("tamanyo") @DefaultValue("") String tamanyo,
			@PathParam("id_usuario") String idUsuario) {
		try {
			int off = Integer.parseInt(offset);
			int c = Integer.parseInt(count);
			String constraints = "";
			if (!terreno.equals(""))
				constraints += " AND t2.terreno=\"" + terreno + "\"";
			if (!dificultad.equals(""))
				constraints += " AND t2.dificultad=\"" + dificultad + "\"";
			if (!tamanyo.equals(""))
				constraints += " AND t2.tamanyo=\"" + tamanyo + "\"";
			//String sql = "SELECT * FROM mydb.Tesoro_encontrado_por_usuario WHERE Usuario_idUsuario=\"" + idUsuario + "\";";
			//String sql = "SELECT * FROM mydb.Tesoro_encontrado_por_usuario WHERE Usuario_idUsuario=\""+ idUsuario + "\" AND Fecha_encontrado > \"" + fecha + "\"" + constraints + "order by Fecha_encontrado LIMIT " + (off - 1) + "," + c + ";";
			String sql = "SELECT * FROM mydb.Tesoro_encontrado_por_usuario=t, mydb.Tesoro=t2 WHERE t.Usuario_idUsuario=\""+ idUsuario + "\" AND t.Fecha_encontrado > \"" + fecha + "\" AND t2.idTesoro=t.Tesoro_idTesoro" + constraints + " order by t.Fecha_encontrado LIMIT " + (off - 1) + "," + c + ";";
			PreparedStatement ps = conn.prepareStatement(sql);
			ResultSet rs = ps.executeQuery();

			Tesoros g = new Tesoros();
			ArrayList<Link> tesoros = g.getTesoros();
			rs.beforeFirst();
			while (rs.next()) {
				tesoros.add(new Link(uriInfo.getAbsolutePath() + "/" + rs.getString("Tesoro_idTesoro"), "self"));
			}

			return Response.status(Response.Status.OK).entity(g).build(); // No se puede devolver el ArrayList (para
																			// generar XML)
		} catch (NumberFormatException e) {
			return Response.status(Response.Status.BAD_REQUEST).entity("No se pudieron convertir los índices a números")
					.build();
		} catch (SQLException e) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error de acceso a BBDD").build();
		}
	}

	// Añadir tesoro encontrado
	// NO funciona si se envia un objeto xml, pero con json si
	@POST
	@Path("{id_usuario}/tesoros_encontrados")
	@Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public Response createTesoroEncontrado(TesoroEncontrado tesoroEncontrado, @PathParam("id_usuario") String idUsuario) {
		try {
			// Primero comprobar si el tesoro existe en la base de datos
			System.out.println(tesoroEncontrado.getIdTesoro());
			System.out.println(tesoroEncontrado.getUsername());
			System.out.println(tesoroEncontrado.getFecha_encontrado());
			String sql = "SELECT * FROM mydb.Tesoro where idTesoro='" + tesoroEncontrado.getIdTesoro() + "';";
			PreparedStatement ps = conn.prepareStatement(sql);
			ResultSet rs = ps.executeQuery();

			if (!rs.next())
				return Response.status(Response.Status.NOT_FOUND)
						.entity("El tesoro no existe en la base de datos").build();

			// Si existe, podemos añadirlo a los tesoros encontrados
			tesoroEncontrado.setIdTesoro(tesoroEncontrado.getIdTesoro());
			//tesoroEncontrado.setUsername(idUsuario.toString());

			sql = "INSERT INTO mydb.Tesoro_encontrado_por_usuario (`Usuario_idUsuario`, `Tesoro_idTesoro`, `Fecha_encontrado`) VALUES (\"" + idUsuario + "\"," + tesoroEncontrado.getIdTesoro() + ",\"" + tesoroEncontrado.getFecha_encontrado() + "\");";

			ps = conn.prepareStatement(sql);
			ps.execute();

			// Obtener el ID del elemento recién creado.
			// Necesita haber indicado Statement.RETURN_GENERATED_KEYS al ejecutar un
			// statement.executeUpdate() o al crear un PreparedStatement

			String location = uriInfo.getAbsolutePath() + "/" + tesoroEncontrado.getIdTesoro();
			return Response.status(Response.Status.OK).header("Content-Location", location).build();
		} catch (SQLException e) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
					.entity("No se pudo crear el tesoro\n" + e.getMessage()).build();
		}
	}

	// Consultar tesoro encontrado
	@GET
	@Path("{id_usuario}/tesoros_encontrados/{id_tesoro}")
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public Response getTesoroEncontrado(@PathParam("id_tesoro") int id) {
		try {
			String sql = "SELECT * FROM mydb.Tesoro where idTesoro =" + id + ";";
			PreparedStatement ps = conn.prepareStatement(sql);
			ResultSet rs = ps.executeQuery();

			if (rs.next()) {
				Tesoro tesoro = new Tesoro();

				tesoro.setIdTesoro(id);
				tesoro.setUsername(rs.getString("username"));
				tesoro.setTerreno(rs.getString("terreno"));
				tesoro.setDificultad(rs.getString("dificultad"));
				tesoro.setLatitud(rs.getDouble("latitud"));
				tesoro.setLongitud(rs.getDouble("longitud"));
				tesoro.setPista(rs.getString("pista"));
				tesoro.setTamanyo(rs.getString("tamanyo"));
				tesoro.setFecha_creado(rs.getString("fecha_creado"));

				return Response.status(Response.Status.OK).entity(tesoro).build();
			} else {
				return Response.status(Response.Status.NOT_FOUND).entity("Elemento no encontrado").build();
			}
		} catch (SQLException e) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error de acceso a BBDD").build();
		}
	}
	
	// Consultar lista de amigos
	@GET
	@Path("{id_usuario}/amigos")
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public Response getAmigos(@QueryParam("offset") @DefaultValue("1") String offset,
			@QueryParam("count") @DefaultValue("10") String count,
			@QueryParam("pattern") @DefaultValue("") String pattern, @PathParam("id_usuario") String idUsuario) {
		try {
			int off = Integer.parseInt(offset);
			int c = Integer.parseInt(count);
			//String sql = "SELECT * FROM mydb.Usuario_amigo_de_Usuario WHERE Usuario_idUsuario=\"" + idUsuario + "\" and Usuario_idUsuario1 LIKE " + "'%" + pattern + "%'"
				//	+ " order by Usuario_idUsuario1 LIMIT " + (off - 1) + "," + c + ";";
			//String sql = "SELECT * FROM mydb.Usuario_amigo_de_Usuario WHERE Usuario_idUsuario=\"" + idUsuario + "\";";
			String sql = "SELECT * FROM mydb.Usuario_amigo_de_Usuario WHERE Usuario_idUsuario=\"" + idUsuario +  "\" and Usuario_idUsuario1 LIKE "+"'%"+pattern+"%'"+" order by Usuario_idUsuario1 LIMIT " + (off - 1) + "," + c + ";";
			PreparedStatement ps = conn.prepareStatement(sql);
			ResultSet rs = ps.executeQuery();

			Usuarios g = new Usuarios();
			ArrayList<Link> usuarios = g.getUsuarios();
			rs.beforeFirst();
			while (rs.next()) {
				usuarios.add(new Link(uriInfo.getAbsolutePath() + "/" + rs.getString("Usuario_idUsuario1"), "self"));
			}

			return Response.status(Response.Status.OK).entity(g).build(); // No se puede devolver el ArrayList (para
																			// generar XML)
		} catch (NumberFormatException e) {
			return Response.status(Response.Status.BAD_REQUEST).entity("No se pudieron convertir los índices a números")
					.build();
		} catch (SQLException e) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error de acceso a BBDD").build();
		}
	}
	
	// Añadir amigo
	@POST
	@Path("{id_usuario}/amigos")
	@Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public Response createAmigo(String amigo, @PathParam("id_usuario") String idUsuario) {
		try {
			System.out.println(amigo);
			String sql = "INSERT INTO mydb.Usuario_amigo_de_Usuario (Usuario_idUsuario, Usuario_idUsuario1) VALUES (\"" + idUsuario + "\", \"" + amigo + "\");";
			PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			int affectedRows = ps.executeUpdate();

			boolean si = true;

			// Obtener el ID del elemento recién creado.
			// Necesita haber indicado Statement.RETURN_GENERATED_KEYS al ejecutar un
			// statement.executeUpdate() o al crear un PreparedStatement
			ResultSet generatedID = ps.getGeneratedKeys();
			if (si) {
				String location = uriInfo.getAbsolutePath() + "/" + amigo;

				return Response.status(Response.Status.CREATED).header("Location", location)
						.header("Content-Location", location).build();
			}
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("No se pudo añadir el amigo").build();

		} catch (SQLException e) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
					.entity("No se pudo añadir el amigo\n" + e.getStackTrace()).build();
		}
	}
	
	// Eliminar un amigo
	@DELETE
	@Path("{id_usuario}/amigos/{id_amigo}")
	public Response deleteAmigo(@PathParam("id_usuario") String idUsuario, @PathParam("id_amigo") String idAmigo) {
		try {
			// int int_id = Integer.parseInt(id);
			String sql = "DELETE FROM `mydb`.`Usuario_amigo_de_Usuario` WHERE `Usuario_idUsuario`='" + idUsuario + "' and Usuario_idUsuario1='" + idAmigo
					+ "';";
			PreparedStatement ps = conn.prepareStatement(sql);
			int affectedRows = ps.executeUpdate();
			if (affectedRows == 1)
				return Response.status(Response.Status.NO_CONTENT).build();
			else
				return Response.status(Response.Status.NOT_FOUND)
						.entity("No se pudo eliminar el amigo").build();
		} catch (SQLException e) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
					.entity("No se pudo eliminar el amigo\n" + e.getStackTrace()).build();
		}
	}

	// Consultar un amigo
	@GET
	@Path("{id_usuario}/amigos/{id_amigo}")
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public Response getAmigo(@PathParam("id_amigo") String idAmigo) {
		try {
			String sql = "SELECT * FROM mydb.Usuario where username =\"" + idAmigo + "\";";
			PreparedStatement ps = conn.prepareStatement(sql);
			ResultSet rs = ps.executeQuery();

			if (rs.next()) {
				Usuario user = new Usuario();

				user.setUsername(rs.getString("username"));
				user.setNombre(rs.getString("nombre"));
				user.setEdad(rs.getInt("edad"));
				user.setLocalidad(rs.getString("localidad"));
				return Response.status(Response.Status.OK).entity(user).build();
			} else {
				return Response.status(Response.Status.NOT_FOUND).entity("Elemento no encontrado").build();
			}
		} catch (SQLException e) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error de acceso a BBDD").build();
		}
	}
	
	// Ver resumen usuario
	@GET
	@Path("{usuario_id}/resumen")
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public Response getResumen(@PathParam("usuario_id") String id) {
		Resumen resumen = new Resumen();
		try {
			
			// Coger datos de usuario
			
			String sql = "SELECT * FROM mydb.Usuario where username =\"" + id + "\";";
			PreparedStatement ps = conn.prepareStatement(sql);
			ResultSet rs = ps.executeQuery();

			if (!rs.next())
				return Response.status(Response.Status.NOT_FOUND).entity("El tesoro no existe en la base de datos").build();
			
			Usuario user = new Usuario();
			user.setUsername(rs.getString("username"));
			user.setNombre(rs.getString("nombre"));
			user.setEdad(rs.getInt("edad"));
			user.setLocalidad(rs.getString("localidad"));
			resumen.setUsuario(user);
			
			// Numero de tesoros encontrados y 5 ultimos
			//ArrayList<Link> tesorosEncontrados = new ArrayList<Link>();
			ArrayList<Integer> tesorosEncontrados = new ArrayList<Integer>();
			sql = "SELECT * FROM mydb.Tesoro_encontrado_por_usuario WHERE Usuario_idUsuario=\"" + id + "\" ORDER BY Fecha_encontrado;";
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			
			rs.beforeFirst();
			while (rs.next()) {
				tesorosEncontrados.add(rs.getInt("Tesoro_idTesoro"));
			}
			ArrayList<Tesoro> tesorosUltimos = new ArrayList<Tesoro>();

			for (int i = 0; i < 5 && i < tesorosEncontrados.size(); i++) {
				sql = "SELECT * FROM mydb.Tesoro WHERE idTesoro=" + tesorosEncontrados.get(i)
						+ ";";
				ps = conn.prepareStatement(sql);
				rs = ps.executeQuery();

				rs.beforeFirst();
				while (rs.next()) {
					tesorosUltimos.add(new Tesoro(rs.getInt("idTesoro"), rs.getString("username"), rs.getString("terreno"), rs.getString("dificultad"), 
							rs.getDouble("latitud"), rs.getDouble("longitud"), rs.getString("pista"), rs.getString("tamanyo"), rs.getString("fecha_creado")));
				}
			}
			resumen.setNumEncontrados(tesorosEncontrados.size());
			resumen.setEncontrados(tesorosUltimos);
			
			// Numero de amigos
			sql = "SELECT COUNT(*) FROM mydb.Usuario_amigo_de_Usuario WHERE Usuario_idUsuario=\"" + id + "\";";
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			int n = 0;
			if (rs.next())
				n = rs.getInt(1);
			resumen.setNumAmigos(n);

			// Numero de tesoros creados
			sql = "SELECT COUNT(*) FROM mydb.Tesoro WHERE username=\"" + id + "\";";
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			n = 0;
			if (rs.next())
				n = rs.getInt(1);
			resumen.setNumCreados(n);

			return Response.status(Response.Status.OK).entity(resumen).build();
		} catch (SQLException e) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error de acceso a BBDD").build();
		}
	}
	
	
/*	@GET
	@Path("{id_usuario}/resu")
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public Response getResume(@PathParam("id_usuario") String id) {
		try {
			String sql = "SELECT * FROM mydb.Tesoro_encontrado_por_usuario WHERE Usuario_idUsuario=\"" + id
					+ "\" ORDER BY Fecha_encontrado;";
			PreparedStatement ps = conn.prepareStatement(sql);
			ResultSet rs = ps.executeQuery();

			rs.beforeFirst();
			ArrayList<Integer> encontrados = new ArrayList<Integer>();
			while (rs.next()) {
				encontrados.add(rs.getInt("Tesoro_idTesoro"));
			}

			return Response.status(Response.Status.OK).entity(encontrados).build(); // No se puede devolver el ArrayList
																					// (para generar XML)
		} catch (NumberFormatException e) {
			return Response.status(Response.Status.BAD_REQUEST).entity("No se pudieron convertir los índices a números")
					.build();
		} catch (SQLException e) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error de acceso a BBDD").build();
		}
	}*/
	
		
		
/*	@GET
	@Path("{usuario_id}/tesoros_creados")
	@Produces({MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON})
	public Response getTesorosCreados(@PathParam("usuario_id") String id,
			@QueryParam("offset") @DefaultValue("1") String offset,
			@QueryParam("count") @DefaultValue("10") String count) {
		try {
			int off = Integer.parseInt(offset);
			int c = Integer.parseInt(count);
			String sql = "SELECT * FROM mydb.Tesoro WHERE idUsuario = "+id+" order by idUsuario LIMIT " + (off - 1) + "," + c + ";";
			PreparedStatement ps = conn.prepareStatement(sql);
			ResultSet rs = ps.executeQuery();
			rs.beforeFirst();

			Tesoros g = new Tesoros();
			ArrayList<Link> tesoros = g.getTesoros();
			rs.beforeFirst();
			while (rs.next()) {

				Tesoro tesoro = new Tesoro();
				tesoro.setIdTesoro(rs.getInt("idTesoro"));
				tesoro.setUsername(rs.getString("username"));
				tesoro.setTerreno(rs.getString("terreno"));
				tesoro.setDificultad(rs.getString("dificultad"));
				tesoro.setLatitud(rs.getDouble("latitud"));
				tesoro.setLongitud(rs.getDouble("longitud"));
				tesoro.setPista(rs.getString("pista"));
				tesoro.setTamanyo(rs.getString("tamanyo"));
				tesoro.setFecha_creado(rs.getDate("fecha_creado"));

				tesoros.add(tesoro);
			}

			return Response.status(Response.Status.OK).entity(g).build(); // No se puede devolver el ArrayList (para generar XML)
		} catch (NumberFormatException e) {
			return Response.status(Response.Status.BAD_REQUEST).entity("No se pudieron convertir los índices a números")
					.build();
		} catch (SQLException e) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error de acceso a BBDD").build();
		}
	}*/
	

	@GET
	@Path("{usuario_id}/tesoros_creados/{id_tesoro}")
	@Produces({MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON})
	public Response getTesoroCreado(@PathParam("usuario_id") String username,@PathParam("id_tesoro") String idT) {
		try {
			int idTesoro=Integer.parseInt(idT);
			
			String sql = "SELECT * FROM mydb.Tesoro WHERE username = \""+username+"\"&& idTesoro= "+idTesoro+";";
			PreparedStatement ps = conn.prepareStatement(sql);
			ResultSet rs = ps.executeQuery();
			rs.beforeFirst();


			rs.beforeFirst();
			if (rs.next()) {
				Tesoro tesoro = new Tesoro();
				tesoro.setUsername(username);
				tesoro.setDificultad(rs.getString("dificultad"));
				tesoro.setIdTesoro(rs.getInt("idTesoro"));
				tesoro.setLatitud(rs.getDouble("Latitud"));
				tesoro.setLongitud(rs.getDouble("Longitud"));
				tesoro.setPista(rs.getString("pista"));
				tesoro.setTerreno(rs.getString("terreno"));
				tesoro.setFecha_creado(rs.getDate("fecha_creado").toString());
				
				return Response.status(Response.Status.OK).entity(tesoro).build();
			} else {
				return Response.status(Response.Status.NOT_FOUND).entity("Elemento no encontrado").build();
			}
		} catch (NumberFormatException e) {
			return Response.status(Response.Status.BAD_REQUEST).entity("No se pudieron convertir los índices a números")
					.build();
		} catch (SQLException e) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error de acceso a BBDD").build();
		}
	}


	/*@DELETE
	@Path("{garaje_id}")
	public Response deleteGaraje(@PathParam("garaje_id") String id) {
		try {
			Garaje garaje;
			int int_id = Integer.parseInt(id);
			String sql = "DELETE FROM `GarajesyEmpleados`.`Garaje` WHERE `id`='" + int_id + "';";
			PreparedStatement ps = conn.prepareStatement(sql);
			int affectedRows = ps.executeUpdate();
			if (affectedRows == 1)
				return Response.status(Response.Status.NO_CONTENT).build();
			else 
				return Response.status(Response.Status.NOT_FOUND).entity("Elemento no encontrado").build();		
		} catch (SQLException e) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("No se pudo eliminar el garaje\n" + e.getStackTrace()).build();
		}
	}*/


	/*private Usuario usuarioFromRS(ResultSet rs) throws SQLException {
		System.out.println("aqui");
		Usuario usuario = new Usuario(rs.getString("nombre"), rs.getInt("edad"), rs.getString("localidad"));
		usuario.setUsername(rs.getString("username"));
		return usuario;
	}*/




	
}

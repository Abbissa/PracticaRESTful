package recursosbdd;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.apache.naming.NamingContext;

import datos.Link;
import datos.Tesoro;
import datos.Tesoros;
import datos.Usuario;
import datos.Usuarios;

@Path("/tesorosCercanos")
public class TesorosCercanosRecurso {
	
	@Context
	private UriInfo uriInfo;

	private DataSource ds;
	private Connection conn;

	public TesorosCercanosRecurso() {
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
	
	
	// Obtener lista de tesoros cercanos
	@GET
	@Produces({MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON})
	public Response getTesorosCreados(@PathParam("usuario_id") String id, @QueryParam("offset") @DefaultValue("1") String offset,
			@QueryParam("count") @DefaultValue("10") String count,
			@QueryParam("terreno")  @DefaultValue("") String terreno,
			@QueryParam("dificultad") @DefaultValue("") String dificultad,
			@QueryParam("tamanyo") @DefaultValue("") String tamanyo, 
			@QueryParam("latitud") String latitud,
			@QueryParam("longitud") String longitud,
			@QueryParam("fecha") @DefaultValue("0000-00-00") String fecha) {
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

			//String sql = "SELECT * FROM mydb.Tesoro ORDER BY ABS(SQRT(POWER(" + latitud + "- latitud, 2)+POWER(" + longitud + "- longitud, 2)))";
			String sql = "SELECT * FROM mydb.Tesoro WHERE Fecha_creado > \"" + fecha + "\"" + constraints + " ORDER BY ABS(SQRT(POWER(" + latitud + "- latitud, 2)+POWER(" + longitud + "- longitud, 2)))";
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

	
	// Consultar tesoro
	@GET
	@Path("{tesoro_id}")
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public Response getUsuario(@PathParam("tesoro_id") int id) {
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
				
				return Response.status(Response.Status.OK).entity(tesoro).build();
			} else {
				return Response.status(Response.Status.NOT_FOUND).entity("Elemento no encontrado").build();
			}
		} catch (SQLException e) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error de acceso a BBDD").build();
		}
	}
}

package mx.trillas.seguimientoembarques.persitence.servlets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import mx.trillas.seguimientoembarques.persitence.adapters.ft91Adapter;
import mx.trillas.seguimientoembarques.persitence.adapters.ft96Adapter;
import mx.trillas.seguimientoembarques.persitence.dao.Ft91DAO;
import mx.trillas.seguimientoembarques.persitence.dao.Ft96DAO;
import mx.trillas.seguimientoembarques.persitence.impl.Ft91DAODBImpl;
import mx.trillas.seguimientoembarques.persitence.impl.Ft96DAODBImpl;
import mx.trillas.seguimientoembarques.persitence.pojos.Almacen;
import mx.trillas.seguimientoembarques.persitence.pojos.Ft91;
import mx.trillas.seguimientoembarques.persitence.pojos.Ft96;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

@WebServlet("/traspasos")
public class TraspasosServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static Ft91DAO ft91dao = new Ft91DAODBImpl();
	private static Ft96DAO ft96dao = new Ft96DAODBImpl();

	public TraspasosServlet() {
		super();
		// TODO Auto-generated constructor stub
	}

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		GsonBuilder gsonBuilder = new GsonBuilder();
		String jsonA = "";
		try {
			Object folioObj = request.getParameter("folio");
			Integer folio = null;
			if (folioObj != null && folioObj instanceof String) {
				String folioStr = (String)folioObj;
				if(folioStr.equals("undefined")){
					jsonA = "NA";
					response.getWriter().write(jsonA);
					return;
				}
				folio = Integer.parseInt((String) folioObj);
			} else {
				jsonA = "NA";
				response.getWriter().write(jsonA);
				return;
			}

			Object serieObj = request.getParameter("serie");
			String serie = null;
			if (serieObj != null && serieObj instanceof String) {
				serie = (String)serieObj;
			} else {
				jsonA = "NA";
				response.getWriter().write(jsonA);
				return;
			}

			Ft96 ft96 = ft96dao.getFt96(serie, folio);

			if (ft96 != null) {
				response.setContentType("application/json");
				gsonBuilder.registerTypeAdapter(Ft96.class, new ft96Adapter());
				Gson gson = gsonBuilder.create();
				jsonA = gson.toJson(ft96);
			} else {
				jsonA = "NA";
			}
			response.getWriter().write(jsonA);

		} catch (Exception e) {
			String err = e.getCause().getLocalizedMessage();
			response.getWriter().print(err);
			e.printStackTrace();
		}

	}

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		GsonBuilder gsonBuilder = new GsonBuilder();
		// get id user to get almacen
		HttpSession session = request.getSession();
		List<Integer> idsAlmacenes = new ArrayList<>();
		Object almacenesListObj = session.getAttribute("almacenes");
		if (almacenesListObj != null && almacenesListObj instanceof List<?>) {
			List<?> almacenesObjList = (List<?>) almacenesListObj;
			for (Object almacenObj : almacenesObjList) {
				if (almacenObj != null && almacenObj instanceof Almacen) {
					Almacen almacen = (Almacen) almacenObj;
					idsAlmacenes.add(almacen.getClave());
				}
			}
		}
		// idsAlmacenes.add(94);
		try {
			List<Ft91> traspasos = ft91dao.getTraspasos(idsAlmacenes);
			response.setContentType("application/json");
			gsonBuilder.registerTypeAdapter(Ft91.class, new ft91Adapter());
			Gson gson = gsonBuilder.create();

			String jsonA = gson.toJson(traspasos);
			response.getWriter().write(jsonA);

		} catch (Exception e) {
			String err = e.getCause().getLocalizedMessage();
			response.getWriter().print(err);
			e.printStackTrace();
		}

	}

}

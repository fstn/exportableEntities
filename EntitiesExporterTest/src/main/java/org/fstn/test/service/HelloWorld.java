package org.fstn.test.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.apache.poi.ss.usermodel.Workbook;
import org.fstn.exportable.bean.ExportBean;
import org.fstn.exportable.model.Exportable;
import org.fstn.test.model.Customer;
import org.fstn.test.model.Vehicule;

@Path("/")
public class HelloWorld {

	@GET
	@Path("/excel")
	@Produces("application/vnd.ms-excel")
	public Response getFile() throws IOException {
		ExportBean exportBean = new ExportBean();
		List<Exportable> customers = new ArrayList<Exportable>();
		List<Vehicule> vehiculesToAdd = new ArrayList<Vehicule>();
		vehiculesToAdd.add(new Vehicule(){{
			trend = "Renault";
			color = "Red";
		}});
		vehiculesToAdd.add(new Vehicule(){{
			trend = "Honda";
			color = "Blue";
		}});
		customers.add(new Customer() {
			{
				firstName = "Paul";
				lastName = "DUPOND";
				vehicules = vehiculesToAdd;
			}
		});
		customers.add(new Customer() {
			{
				firstName = "Pierre";
				lastName = "ROUSSEGUES";
				vehicules = vehiculesToAdd;
			}
		});
		Workbook sheet = exportBean.generateExcel(customers, null, null);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		sheet.write(baos);
		ResponseBuilder response = Response.ok(baos.toByteArray());
		response.header("Content-disposition", "attachment; filename=export.xls");
		return response.build();
	}
}
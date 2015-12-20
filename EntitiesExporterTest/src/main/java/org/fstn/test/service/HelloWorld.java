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
import javax.ws.rs.core.Response.Status;

import org.apache.poi.ss.usermodel.Workbook;
import org.fstn.exportable.bean.ExportBean;
import org.fstn.exportable.model.Exportable;
import org.fstn.test.model.Customer;

@Path("/")
public class HelloWorld {

	@GET
	@Path("/excel")
	@Produces("application/vnd.ms-excel")
	public Response getFile() throws IOException {
		ExportBean exportBean = new ExportBean();
		List<Exportable> customers = new ArrayList<Exportable>();
		customers.add(new Customer() {
			{
				firstName = "toto";
				lastName = "titi";
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
package org.fstn.exportable.bean;

import java.io.Serializable;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.PrintSetup;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.fstn.exportable.facade.ExportableFacade;
import org.fstn.exportable.model.ExportResult;
import org.fstn.exportable.model.Exportable;

import com.google.common.base.Function;
import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;


public class ExportBean implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	private List<Exportable> exportableObjects;


	// permet de précharger les données à exporter pour afficher un poup de
	/**
	 * Gets the header to export.
	 *
	 * @param exportableObject
	 *            the candidat
	 * @return the header to export
	 * @throws Exception
	 *             the exception
	 */
	// choix des données
	public List<String> getHeaderToExport(Exportable exportableObject) throws Exception {

		List<String> headers = new ArrayList<String>();
		if (null == exportableObject) {
			throw new Exception(
					"Le premier candidat de la liste d'export est null");
		}
		@SuppressWarnings("unchecked")
		//TODO
		//export here
		
		ExportResult exportResultObject = new ExportResult(){{
			key="test";
			value="value";
			columnHeader="column";
		}};
		List<ExportResult> exportResult = new ArrayList<ExportResult>();
		exportResult.add(exportResultObject);
		for (ExportResult exportUnit : exportResult) {

			String name = (String) exportUnit.getName();
			String columnHeader = (String) exportUnit.getColumnHeader();
			// on retire le numéro de fin pour pouvoir selectionner tous les
			// référents, tous les ...
			columnHeader = columnHeader.substring(0, columnHeader.length() - 1);
			if (!headers.contains(columnHeader)) {
				headers.add(columnHeader);
			}
		}

		return headers;
	}

	/**
	 * Geenrate candidat data.
	 *
	 * @param exportableObjects
	 *            the candidats filtres
	 * @param selectedHeaders
	 *            the selected headers
	 * @param headers
	 *            the headers
	 * @return the list
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> generateData(
			List<Exportable> exportableObjects) {

		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		this.exportableObjects = exportableObjects;
		
		List<ExportResult> exportResult;

		for (Exportable exportableObject : exportableObjects) {
			//récupération de l'exportResult contenant toutes les données du dossier
			//TODO 

			ExportResult exportResultObject = new ExportResult(){{
				key="test";
				value="value";
				columnHeader="column";
			}};
			 exportResult = new ArrayList<ExportResult>();
			exportResult.add(exportResultObject);

			//transformation en liste et suppression des éléments nulls
			exportResult = ImmutableSet.copyOf(
					Iterables.filter(exportResult,
							Predicates.not(Predicates.isNull()))).asList();
			//création d'une map contenant les valeurs
			Map<String, ExportResult> resultMap = Maps.uniqueIndex(
					exportResult, new Function<ExportResult, String>() {
						public String apply(ExportResult exportResult) {
							return exportResult.getKey();
						}
					});
			// modification des données en type objet
			Map<String, Object> resultValueMap = Maps.transformValues(resultMap,
					new Function<ExportResult, Object>() {

						public Object apply(ExportResult input) {
							return input.getValue();
						}
					});
			
			result.add(resultValueMap);

		}
		return result;

	}

	/**
	 * Generate excel.
	 *
	 * @param exportableObjects
	 *            the candidats filtres
	 * @param selectedHeaders
	 *            the selected headers
	 * @param headers
	 *            the headers
	 * @return the workbook
	 */
	public Workbook generateExcel(List<Exportable> exportableObjects,
			List<String> selectedHeaders, List<String> headers) {
		this.exportableObjects = exportableObjects;

		String name;
		String headerName;
		Cell parentHeaderCell;
		Cell cell;
		Row row;
		List<ExportResult> exportResult = null;
		Class type;
		Object value;

		Workbook workbook = new XSSFWorkbook();

		Sheet sheet = workbook.createSheet("Extraction excel");
		Integer numRow = 0;
		Integer numCell = 0;
		PrintSetup setup = sheet.getPrintSetup();
		setup.setLandscape(true);
		String email = "";

		Font defaultFont = workbook.createFont();
		defaultFont.setFontHeightInPoints((short) 16);
		defaultFont.setFontName("Arial");
		defaultFont.setColor(IndexedColors.BLACK.getIndex());
		defaultFont.setBoldweight(Font.BOLDWEIGHT_BOLD);
		defaultFont.setItalic(false);
		CellStyle style = workbook.createCellStyle();
		style.setFont(defaultFont);
		style.setFillBackgroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
		style.setBorderTop(CellStyle.BORDER_THIN);
		style.setBorderBottom(CellStyle.BORDER_THIN);
		style.setBorderLeft(CellStyle.BORDER_THIN);
		style.setBorderRight(CellStyle.BORDER_THIN);
		style.setTopBorderColor(IndexedColors.BLACK.getIndex());

		Row parentHeader = sheet.createRow(numRow);
		parentHeader.setHeight((short) 500);
		numRow = 1;
		Row header = sheet.createRow(numRow);
		header.setHeight((short) 500);
		numRow = 2;
		sheet.setDisplayGridlines(true);

		DateFormat dateFormatFrance = DateFormat.getInstance();
		dateFormatFrance = DateFormat.getDateTimeInstance(DateFormat.SHORT,
				DateFormat.SHORT, Locale.FRENCH);
		for (Exportable exportableObject : exportableObjects) {
				row = sheet.createRow(numRow);
				ExportableFacade exportableFacade = new ExportableFacade();
				exportResult = exportableFacade.export(exportableObject);
				numCell = 0;
				for (ExportResult exportUnit : exportResult) {
					name = (String) exportUnit.getName();
					headerName = (String) exportUnit.getColumnHeader();

					// on retire le numéro de fin pour pouvoir selectionner tous
					// les référents, tous les ...
					if ( selectedHeaders==null || selectedHeaders.contains(headerName.substring(0,headerName.length() - 1))) {
						// remplissage des headers seulemùent pour le premier
						// candidat
						//TODO only on first object
							parentHeaderCell = parentHeader.createCell(numCell);
							parentHeaderCell.setCellValue(headerName);
							// sheet.setColumnWidth(numCell, 20000);
							parentHeaderCell.setCellStyle(style);
							header.setRowStyle(style);
							Cell headerCell = header.createCell(numCell);
							headerCell.setCellStyle(style);
							headerCell.setCellValue(name);
							sheet.autoSizeColumn(numCell, true);

							//END TODO only on first object
						cell = row.createCell(numCell);
						cell.getCellStyle().setBorderBottom(
								CellStyle.BORDER_NONE);
						type = exportUnit.getType();
						value = exportUnit.getValue();

						if (type.equals(Boolean.class)) {
							cell.setCellValue((Boolean) value);
						}
						if (type.equals(String.class)) {
							cell.setCellValue((String) value);
						}
						if (type.equals(Integer.class)) {
							cell.setCellValue((Integer) value);
						}
						if (type.equals(Double.class)) {
							cell.setCellValue((Double) value);
						}
						if (type.equals(Timestamp.class)) {
							cell.setCellValue(dateFormatFrance.format(value));
						}
						if (type.equals(Date.class)) {
							cell.setCellValue(dateFormatFrance.format(value));
						}
						numCell++;
					}
				}
				numRow++;
		}
		return workbook;

	}


}

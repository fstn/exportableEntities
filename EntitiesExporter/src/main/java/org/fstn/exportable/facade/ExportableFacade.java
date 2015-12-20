package org.fstn.exportable.facade;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import org.fstn.exportable.annotation.ExportField;
import org.fstn.exportable.model.ExportResult;
import org.fstn.exportable.model.Exportable;

// TODO: Auto-generated Javadoc
/**
 * The Class ExportableFacade.
 */
public class ExportableFacade implements Serializable {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/** The logger. */
	private Logger logger = Logger.getLogger(ExportableFacade.class.getName());

	/**
	 * Export.
	 *
	 * @param exportableObject the exportable object
	 * @return the list
	 */
	public List<ExportResult> export( Exportable exportableObject) {
		return export(exportableObject, "");
	}

	/**
	 * Export.
	 *
	 * @param exportableObject the exportable object
	 * @param key the key
	 * @return the list
	 */
	public List<ExportResult> export(Exportable exportableObject,String key) {
		List<ExportResult> exportResult = new ArrayList<ExportResult>();
		Method getter;
		Object value;
		ExportField exportField;
		int maxOccurence;

		
		String elementTypeForFieldList;
		Class<?> type;
		
		Class<? extends Exportable> classType = exportableObject.getClass();
		Class<?> superClassType = exportableObject.getClass().getSuperclass();
		String name;
		String columnHeader;
		List<Field> classField = new ArrayList<Field>();
		for (Field field : classType.getDeclaredFields()) {
			classField.add(field);
		}
		if (superClassType != null) {
			for (Field field : superClassType.getDeclaredFields()) {
				classField.add(field);
			}
		}
		for (Field field : classField) {
			value = null;
			exportField = null;
			type = field.getType();
			name = field.getName();
			String fieldKey = key+"_"+name;
			exportField = field.getAnnotation(ExportField.class);
			if (exportField != null) {
				try {
					maxOccurence = exportField.maxOccurence();
					elementTypeForFieldList = exportField.elementType();
					getter = classType.getMethod(
							"get" + name.substring(0, 1).toUpperCase()
									+ name.substring(1), null);
					value = value = getter.invoke(exportableObject, null);
					if ( isExportable(type)) {
						// on comble les trous pour ne pas avoir de decallage dans l'excel
						if(value == null ){
							Constructor<?> ctor = type
									.getConstructor();
							value = ctor
									.newInstance(new Object[] {});
						}
						// si le one to one est checkable
						List<ExportResult> childResult = export(
								((Exportable) value),fieldKey);
						exportResult.addAll(childResult);
					}
					// champs liste parcours de tous les éléments fils pour
					// détecter les champs obligatoire
					else if (value instanceof List<?>) {
						List<Object> list = (List<Object>) value;
						int listOccurence = list.size();
						if (listOccurence < maxOccurence) {
							if (elementTypeForFieldList.equals("")) {
								throw new Exception(
										"Il faut spécifier un elementTypeForFieldList dans l'annotation SecretaryExport,"
												+ ", sinon il est impossible de déterminer le type de la liste et donc de combler les trous avec des objets vides");
							}
							try {
								// création d''léments vies pour boucher les trous dans les listes
								Class<?> clazz = Class
										.forName(elementTypeForFieldList);
								Constructor<?> ctor = clazz
										.getConstructor();
								Object object = ctor
										.newInstance(new Object[] {});
								while (list.size() < maxOccurence) {
									list.add(object);
								}
							} catch (ClassNotFoundException clnf) {
								throw new Exception(
										"Il n'y a pas de classe de type "
												+ elementTypeForFieldList
												+ " vérifier la chaine contenue dans l'annotation SecretaryExport"
												+ " Il faut que cette chaine soit le nom exact et complet(avec le package) de la classe ");

							}
						}
						int inc = 0;
						for (Object listObject : list) {
							if (isExportable(listObject.getClass())) {

								List<ExportResult> childResult = export(
										((Exportable) listObject),fieldKey+"_"+inc);
								//changement des noms de colonnes pour avoir par exemple langue 1 langue 2 ....
								for (ExportResult exportUnit : childResult){
									exportUnit.setColumnHeader(exportUnit.getColumnHeader()+inc);
									
									exportUnit.setKey(exportUnit.getKey());
								}
								inc++;
								exportResult.addAll(childResult);
							}
						}

					}
					if (value == null || value.getClass().equals(String.class)
							|| value.getClass().equals(Timestamp.class)
							|| value.getClass().equals(Boolean.class)
							|| value.getClass().equals(Integer.class)
							|| value.getClass().equals(Double.class)) {
						ExportResult exportUnit = new ExportResult();
						exportUnit.setParentType(classType);
						exportUnit.setKey(fieldKey);
						/*if(secretaryExport.key().isEmpty()){
							exportUnit.setKey(classType.getSimpleName().split("_\\$\\$_")[0]+"_"+name);							
						}else{
							exportUnit.setKey(classType.getSimpleName().split("_\\$\\$_")[0]+"_"+secretaryExport.key()+"_"+name);							
						}*/
						exportUnit.setExportable(exportField);
						exportUnit.setName(name);
						exportUnit.setValue("");
						exportUnit.setType(String.class);
						// l'espace est neccessaire car dans le jury bean on supprime la derniere lettre pour enlever les increment 
						exportUnit.setColumnHeader(exportableObject
								.exportColumnHeader()+" ");
						exportResult.add(exportUnit);
						if (value != null) {
							exportUnit.setValue(value);
							exportUnit.setType(value.getClass());
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
					logger.info("can't read field value" + name + " " + type);
				}
			}
		}
		return exportResult;

	}

	/**
	 * Checks if is exportable.
	 *
	 * @param valueClass the value class
	 * @return true, if is exportable
	 */
	private boolean isExportable(Class<?> valueClass) {
		boolean retour = false;
		boolean classInterfaceContainsExportable = false;
		boolean superClassInterfaceContainsExportable = false;
		if (valueClass.getInterfaces() != null) {
			List<Class<?>> interfaces = Arrays.asList(valueClass.getInterfaces());
			classInterfaceContainsExportable = interfaces
					.contains(Exportable.class);
		}
		if (valueClass.getSuperclass() != null) {
			List<Class<?>> superClassInterface = Arrays.asList(valueClass
					.getSuperclass().getInterfaces());
			superClassInterfaceContainsExportable = superClassInterface
					.contains(Exportable.class);
		}
		retour = classInterfaceContainsExportable
				|| superClassInterfaceContainsExportable;
		return retour;
	}
}

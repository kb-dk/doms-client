package dk.statsbiblioteket.doms.client.sdo;

import commonj.sdo.Property;

public class DOMSSDOUtil {
	
	public static String getAppInfo(Property property, String source) {
		String appinfo = null;
		org.eclipse.emf.ecore.EModelElement eModelElement;
		org.eclipse.emf.ecore.EAnnotation annotation;
		eModelElement = (org.eclipse.emf.ecore.EModelElement)property;
		annotation = eModelElement.getEAnnotation(source);
		if (annotation!=null) {
			appinfo = (String)annotation.getDetails().get("appinfo");
		}
		return appinfo;
	}

}

package org.fstn.exportable.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface SecretaryExport  {
	int maxOccurence () default -1;
	String elementType () default "";
}

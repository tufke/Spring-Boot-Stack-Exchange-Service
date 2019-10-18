package nl.merapar.stack.xml.model;

import javax.xml.namespace.QName;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

import lombok.Data;

@Data
public abstract class AbstractModel {

	public abstract QName getQName();
	
	public String toString() {
		return new ReflectionToStringBuilder(this).toString();
	}
}

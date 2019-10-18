package nl.merapar.stack.service.rest.resource.api;

import java.io.Serializable;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

import lombok.Data;

@Data
public abstract class AbstractServiceObject implements Serializable {

	private static final long serialVersionUID = -4287482970319777647L;

	@Override
	public String toString() {
		return new ReflectionToStringBuilder(this).toString();
	}
	
}

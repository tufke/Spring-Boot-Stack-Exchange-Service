package nl.merapar.stack.xml.parser.adapter;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

import nl.merapar.stack.xml.model.AbstractModel;

public abstract class AbstractParserAdapter<S extends AbstractModel> {

	public abstract void process(final S model);
	
	@Override
	public String toString() {
		return new ReflectionToStringBuilder(this).toString();
	}
}

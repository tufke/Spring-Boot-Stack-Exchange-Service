package nl.merapar.stack.service.rest.resource.operation;

import nl.merapar.stack.service.rest.resource.api.AbstractServiceObject;

public abstract class AbstractOperation<IN extends AbstractServiceObject, OUT extends AbstractServiceObject> {

	
	/**
	 * This method processes the received input and returns the desired output.
	 * It usually calls one or more backend components that handle the input.
	 * 
	 * For Services these backend components are adapters that map the input, 
	 * call to a backoffice system and map the output.
	 * 
	 * @param input The input SO.
	 * @return The output SO.
	 */
	public abstract OUT operate(final IN input);
	


}

package nl.merapar.stack.xml.parser.adapter.test;

import java.time.format.DateTimeFormatter;


public abstract class AbstractAdapterTest {

	protected DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");
}

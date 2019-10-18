package nl.merapar.stack.xml.model;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.xml.namespace.QName;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@Builder
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
public class PostRow extends AbstractModel implements Serializable {

    private final static long serialVersionUID = 100L;

    @JsonProperty(value="AcceptedAnswerId")
    protected Integer acceptedAnswerId;

    @JsonProperty(value="CreationDate")
    protected LocalDateTime creationDate;
    
    @JsonProperty(value="Score")
    protected short score;
    
	@Override 
	public QName getQName() {
		return new QName("", "row", ""); 
	}
    
 }

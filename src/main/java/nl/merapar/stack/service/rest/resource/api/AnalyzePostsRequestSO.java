package nl.merapar.stack.service.rest.resource.api;

import javax.validation.constraints.NotBlank;

import org.hibernate.validator.constraints.URL;

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
public class AnalyzePostsRequestSO extends AbstractServiceObject {
	
	private static final long serialVersionUID = 6754685303703146907L;

	@URL
	@NotBlank
	private String url;

}

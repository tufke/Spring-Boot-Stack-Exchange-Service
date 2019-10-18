package nl.merapar.stack.service.rest.resource.api;

import java.time.LocalDateTime;

import lombok.*;

@Data
@Builder
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
public class AnalyzePostsResponseSO extends AbstractServiceObject {

	private static final long serialVersionUID = 1585488207918070352L;

	@EqualsAndHashCode.Exclude
	protected final LocalDateTime analyseDate = LocalDateTime.now();
	
	@Builder.Default
	protected AnalyzePostsDetailsSO details = new AnalyzePostsDetailsSO();
	



	
}

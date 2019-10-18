package nl.merapar.stack.service.rest.resource.api;

import java.math.BigInteger;
import java.time.LocalDateTime;

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
public class AnalyzePostsDetailsSO extends AbstractServiceObject {

	private static final long serialVersionUID = -2981834373268843315L;

	@Builder.Default
	protected LocalDateTime firstPost = null;
	
	@Builder.Default
	protected LocalDateTime lastPost = null;
	
	@Builder.Default
	protected BigInteger totalPosts = BigInteger.ZERO;
	
	@Builder.Default
	protected BigInteger totalAcceptedPosts = BigInteger.ZERO;
	
	@Builder.Default
	protected Integer avgScore = 0;
	

}

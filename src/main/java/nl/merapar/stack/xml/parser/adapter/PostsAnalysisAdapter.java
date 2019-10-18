package nl.merapar.stack.xml.parser.adapter;

import java.math.BigInteger;
import java.time.LocalDateTime;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import nl.merapar.stack.xml.model.PostRow;

@Data
@Builder
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
@Component
@Scope("prototype")
public class PostsAnalysisAdapter extends AbstractParserAdapter<PostRow> {

	@Builder.Default
	protected BigInteger totalScore = BigInteger.ZERO;
	
	@EqualsAndHashCode.Exclude
	protected final LocalDateTime analyseDate = LocalDateTime.now();
	protected LocalDateTime firstPost;
	protected LocalDateTime lastPost;
	
	@Builder.Default
	protected BigInteger totalPosts = BigInteger.ZERO;
	
	@Builder.Default
	protected BigInteger totalAcceptedPosts = BigInteger.ZERO;
	protected Integer avgScore;
	
	@Override
	public void process(final PostRow post) {
   		if(post.getCreationDate() != null) {
			totalScore = totalScore.add(BigInteger.valueOf(post.getScore()));
	    	totalPosts = totalPosts.add(BigInteger.ONE);
	    	avgScore = totalScore.divide(totalPosts).intValue();
	    	if (post.getAcceptedAnswerId() != null) {
	    		totalAcceptedPosts = totalAcceptedPosts.add(BigInteger.ONE);
	    	}
	    	if (post.getCreationDate() != null) {
	        	if (firstPost == null || post.getCreationDate().isBefore(firstPost)) {
	        		firstPost = post.getCreationDate();
	        	}
	        	if (lastPost == null || post.getCreationDate().isAfter(lastPost)) {
	        		lastPost = post.getCreationDate();
	        	}
	        }	     		
   		}

	}



	
}

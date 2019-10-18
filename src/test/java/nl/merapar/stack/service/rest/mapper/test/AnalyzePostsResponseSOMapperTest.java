package nl.merapar.stack.service.rest.mapper.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.math.BigInteger;
import java.time.LocalDateTime;

import org.junit.Test;
import org.mapstruct.factory.Mappers;

import nl.merapar.stack.service.rest.mapper.AnalyzePostsResponseSOMapper;
import nl.merapar.stack.service.rest.resource.api.AnalyzePostsResponseSO;
import nl.merapar.stack.xml.parser.adapter.PostsAnalysisAdapter;

public class AnalyzePostsResponseSOMapperTest {

	private AnalyzePostsResponseSOMapper mapper = Mappers.getMapper(AnalyzePostsResponseSOMapper.class);
	
	@Test
	public void testMapFrom_Complete() {
		PostsAnalysisAdapter input =
			PostsAnalysisAdapter.builder()
    		.firstPost(LocalDateTime.now())
    		.lastPost(LocalDateTime.now())
    		.totalAcceptedPosts(BigInteger.TEN)
    		.totalPosts(BigInteger.TEN)
    		.totalScore(new BigInteger("100"))
    		.avgScore(10)
			.build();
		
		AnalyzePostsResponseSO result = mapper.mapFrom(input);
		assertMapperResult(input, result);
	}
	
	@Test
	public void testMapFrom_Empty() {
		PostsAnalysisAdapter input =
			PostsAnalysisAdapter.builder()
			.build();
		
		AnalyzePostsResponseSO result = mapper.mapFrom(input);
		assertMapperResult(input, result);
	}	
	
	private void assertMapperResult(PostsAnalysisAdapter input, AnalyzePostsResponseSO result) {
		assertNotNull(result);
		assertNotNull(result.getDetails());
		assertEquals(input.getAnalyseDate(), result.getAnalyseDate());
		assertEquals(input.getAvgScore(), result.getDetails().getAvgScore());
		assertEquals(input.getFirstPost(), result.getDetails().getFirstPost());
		assertEquals(input.getLastPost(), result.getDetails().getLastPost());
		assertEquals(input.getTotalAcceptedPosts(), result.getDetails().getTotalAcceptedPosts());
		assertEquals(input.getTotalPosts(), result.getDetails().getTotalAcceptedPosts());
	}
}

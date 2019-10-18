package nl.merapar.stack.xml.parser.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.math.BigInteger;
import java.time.LocalDateTime;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.junit4.SpringRunner;

import lombok.Getter;
import nl.merapar.stack.service.rest.exception.ServiceException;
import nl.merapar.stack.xml.parser.PostAnalysisParser;
import nl.merapar.stack.xml.parser.adapter.PostsAnalysisAdapter;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment=WebEnvironment.MOCK)
public class PostAnalysisParserTest extends AbstractParserTest {

	
	@Autowired
	@Getter
	PostAnalysisParser parser;
	
	@Test
	public void testParserSuccess_http() throws Exception {
		//Expect result
		String firstPost = "2016-01-12T18:45:19.963";
		String lastPost = "2016-01-12T18:46:22.083";
		Integer totalAcceptedPosts = 3;
		Integer totalPosts = 3;
		Integer totalScore = 56;
		Integer avgScore = 18;
		
		PostsAnalysisAdapter expectedResult = createAdapter(firstPost, lastPost, totalAcceptedPosts, totalPosts, totalScore, avgScore);

		
		// Call parser
		PostsAnalysisAdapter adapter = (PostsAnalysisAdapter)parseHttp("xml/posts/posts.xml");
		assertNotNull(adapter);
		assertEquals(expectedResult, adapter);
	}	
	
	@Test
	public void testParserSuccess_file() throws Exception {
		//Expect result
		String firstPost = "2016-01-12T18:45:19.963";
		String lastPost = "2016-01-12T18:46:22.083";
		Integer totalAcceptedPosts = 3;
		Integer totalPosts = 3;
		Integer totalScore = 56;
		Integer avgScore = 18;
		
		PostsAnalysisAdapter expectedResult = createAdapter(firstPost, lastPost, totalAcceptedPosts, totalPosts, totalScore, avgScore);
		
		// Call parser
		PostsAnalysisAdapter adapter = (PostsAnalysisAdapter)parseFile("xml/posts/posts.xml");
		assertNotNull(adapter);
		assertEquals(expectedResult, adapter);
	}
	
	@Test
	public void testParser_no_posts_http() throws Exception {
		//Expect result
		PostsAnalysisAdapter expectedResult = new PostsAnalysisAdapter();
		
		// Call parser
		PostsAnalysisAdapter adapter = (PostsAnalysisAdapter)parseHttp("xml/posts/no-posts.xml");
		assertNotNull(adapter);
		assertEquals(expectedResult, adapter);
	}
	
	@Test
	public void testParser_posts_no_acceptedanswerid_http() throws Exception {
		//Expect result
		String firstPost = "2016-01-12T18:45:19.963";
		String lastPost = "2016-01-12T18:45:19.963";
		Integer totalAcceptedPosts = 0;
		Integer totalPosts = 1;
		Integer totalScore = 10;
		Integer avgScore = 10;
		
		PostsAnalysisAdapter expectedResult = createAdapter(firstPost, lastPost, totalAcceptedPosts, totalPosts, totalScore, avgScore);
		
		// Call parser
		PostsAnalysisAdapter adapter = (PostsAnalysisAdapter)parseHttp("xml/posts/posts-no-acceptedanswerid.xml");
		assertNotNull(adapter);
		assertEquals(expectedResult, adapter);
	}
	
	@Test
	public void testParser_posts_no_score_http() throws Exception {
		//Expect result
		String firstPost = "2016-01-12T18:45:19.963";
		String lastPost = "2016-01-12T18:45:19.963";
		Integer totalAcceptedPosts = 1;
		Integer totalPosts = 1;
		Integer totalScore = 0;
		Integer avgScore = 0;
		
		PostsAnalysisAdapter expectedResult = createAdapter(firstPost, lastPost, totalAcceptedPosts, totalPosts, totalScore, avgScore);
		
		// Call parser
		PostsAnalysisAdapter adapter = (PostsAnalysisAdapter)parseHttp("xml/posts/posts-no-score.xml");
		assertNotNull(adapter);
		assertEquals(expectedResult, adapter);
	}	
	
	@Test
	public void testParser_posts_no_creationdate_http() throws Exception {
		//Expect result
		PostsAnalysisAdapter expectedResult = new PostsAnalysisAdapter();
		
		// Call parser
		PostsAnalysisAdapter adapter = (PostsAnalysisAdapter)parseHttp("xml/posts/posts-no-creationdate.xml");
		assertNotNull(adapter);
		assertEquals(expectedResult, adapter);
	}		
	
	@Test
	public void testParser_posts_no_rows_http() throws Exception {
		//Expect result
		PostsAnalysisAdapter expectedResult = new PostsAnalysisAdapter();
		
		// Call parser
		PostsAnalysisAdapter adapter = (PostsAnalysisAdapter)parseHttp("xml/posts/posts-no-rows.xml");
		assertNotNull(adapter);
		assertEquals(expectedResult, adapter);
	}		
	
	@Test
	public void testParser_posts_with_comment_http() throws Exception {
		//Expect result
		String firstPost = "2016-01-12T18:45:19.963";
		String lastPost = "2016-01-12T18:46:22.083";
		Integer totalAcceptedPosts = 3;
		Integer totalPosts = 3;
		Integer totalScore = 56;
		Integer avgScore = 18;
		
		PostsAnalysisAdapter expectedResult = createAdapter(firstPost, lastPost, totalAcceptedPosts, totalPosts, totalScore, avgScore);

		
		// Call parser
		PostsAnalysisAdapter adapter = (PostsAnalysisAdapter)parseHttp("xml/posts/posts-with-comment.xml");
		assertNotNull(adapter);
		assertEquals(expectedResult, adapter);
	}	
	
	@Test(expected = ServiceException.class)
	public void testParser_no_xml_file_http() throws Exception {
		// Call parser
		parseHttp("xml/posts/no-xml-file.txt");
		fail("ServiceException expected");
	}	
	
	@Test(expected = ServiceException.class)
	public void testParser_posts_invallid_xml_http() throws Exception {
		// Call parser
		parseHttp("xml/posts/posts-invallid-xml.xml");
		fail("ServiceException expected");
	}	
	
	private PostsAnalysisAdapter createAdapter(String firstPost, String lastPost, Integer totalAcceptedPosts, Integer totalPosts, Integer totalScore, Integer avgScore) {
		return PostsAnalysisAdapter.builder()
				.firstPost(LocalDateTime.parse(firstPost, dateTimeFormatter))
				.lastPost(LocalDateTime.parse(lastPost, dateTimeFormatter))
				.totalAcceptedPosts(new BigInteger(totalAcceptedPosts.toString()))
				.totalPosts(new BigInteger(totalPosts.toString()))
				.totalScore(new BigInteger(totalScore.toString()))
				.avgScore(avgScore)
				.build();
	}	
	
    
}

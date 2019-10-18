package nl.merapar.stack.xml.parser.adapter.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.math.BigInteger;
import java.time.LocalDateTime;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import nl.merapar.stack.xml.model.PostRow;
import nl.merapar.stack.xml.parser.adapter.PostsAnalysisAdapter;

@RunWith(MockitoJUnitRunner.class)
public class PostAnalysisAdapterTest extends AbstractAdapterTest {

	
	private PostsAnalysisAdapter adapter;
	
	@Before
	public void init() {
		adapter = new PostsAnalysisAdapter();
	}
	
	@Test
	public void test_zero_accepted_posts() {
		
		/** creationdate, acceptedanswerId, score */
		adapter.process(createPostRow("2016-01-12T18:45:19.963", null, 25));
		adapter.process(createPostRow("2017-01-12T18:45:19.963", null, 25));
		adapter.process(createPostRow("2018-01-12T18:45:19.963", null, 25));
		
		String firstPost = "2016-01-12T18:45:19.963";
		String lastPost = "2018-01-12T18:45:19.963";
		Integer totalAcceptedPosts = 0;
		Integer totalPosts = 3;
		Integer totalScore = 75;
		Integer avgScore = 25;
		
		/** FirstPost, LastPost, totalAcceptedPosts, totalPosts, totalScore, avgScore*/
		expect(createAdapter(firstPost, lastPost, totalAcceptedPosts, totalPosts, totalScore, avgScore));
	}	
	
	@Test
	public void test_one_accepted_post() {
		
		/** creationdate, acceptedanswerId, score */
		adapter.process(createPostRow("2016-01-12T18:45:19.963", 1, 25));
		adapter.process(createPostRow("2017-01-12T18:45:19.963", null, 25));
		adapter.process(createPostRow("2018-01-12T18:45:19.963", null, 25));
		
		String firstPost = "2016-01-12T18:45:19.963";
		String lastPost = "2018-01-12T18:45:19.963";
		Integer totalAcceptedPosts = 1;
		Integer totalPosts = 3;
		Integer totalScore = 75;
		Integer avgScore = 25;
		
		/** FirstPost, LastPost, totalAcceptedPosts, totalPosts, totalScore, avgScore*/
		expect(createAdapter(firstPost, lastPost, totalAcceptedPosts, totalPosts, totalScore, avgScore));
	}
	
	@Test
	public void test_only_accepted_posts() {
		
		/** creationdate, acceptedanswerId, score */
		adapter.process(createPostRow("2016-01-12T18:45:19.963", 1, 25));
		adapter.process(createPostRow("2017-01-12T18:45:19.963", 1, 25));
		adapter.process(createPostRow("2018-01-12T18:45:19.963", 2, 25));
		
		String firstPost = "2016-01-12T18:45:19.963";
		String lastPost = "2018-01-12T18:45:19.963";
		Integer totalAcceptedPosts = 3;
		Integer totalPosts = 3;
		Integer totalScore = 75;
		Integer avgScore = 25;
		
		/** FirstPost, LastPost, totalAcceptedPosts, totalPosts, totalScore, avgScore*/
		expect(createAdapter(firstPost, lastPost, totalAcceptedPosts, totalPosts, totalScore, avgScore));
	}	
	
	@Test
	public void test_zero_score() {
		
		/** creationdate, acceptedanswerId, score */
		adapter.process(createPostRow("2016-01-12T18:45:19.963", null, null));
		adapter.process(createPostRow("2017-01-12T18:45:19.963", null, null));
		adapter.process(createPostRow("2018-01-12T18:45:19.963", null, null));
		
		String firstPost = "2016-01-12T18:45:19.963";
		String lastPost = "2018-01-12T18:45:19.963";
		Integer totalAcceptedPosts = 0;
		Integer totalPosts = 3;
		Integer totalScore = 0;
		Integer avgScore = 0;
		
		/** FirstPost, LastPost, totalAcceptedPosts, totalPosts, totalScore, avgScore*/
		expect(createAdapter(firstPost, lastPost, totalAcceptedPosts, totalPosts, totalScore, avgScore));
	}		
	
	@Test
	public void test_one_score() {
		
		/** creationdate, acceptedanswerId, score */
		adapter.process(createPostRow("2016-01-12T18:45:19.963", null, 18));
		adapter.process(createPostRow("2017-01-12T18:45:19.963", null, null));
		adapter.process(createPostRow("2018-01-12T18:45:19.963", null, null));
		
		String firstPost = "2016-01-12T18:45:19.963";
		String lastPost = "2018-01-12T18:45:19.963";
		Integer totalAcceptedPosts = 0;
		Integer totalPosts = 3;
		Integer totalScore = 18;
		Integer avgScore = 6;
		
		/** FirstPost, LastPost, totalAcceptedPosts, totalPosts, totalScore, avgScore*/
		expect(createAdapter(firstPost, lastPost, totalAcceptedPosts, totalPosts, totalScore, avgScore));
	}		
	
	@Test
	public void test_only_score() {
		
		/** creationdate, acceptedanswerId, score */
		adapter.process(createPostRow("2016-01-12T18:45:19.963", null, 6));
		adapter.process(createPostRow("2017-01-12T18:45:19.963", null, 6));
		adapter.process(createPostRow("2018-01-12T18:45:19.963", null, 6));
		
		String firstPost = "2016-01-12T18:45:19.963";
		String lastPost = "2018-01-12T18:45:19.963";
		Integer totalAcceptedPosts = 0;
		Integer totalPosts = 3;
		Integer totalScore = 18;
		Integer avgScore = 6;
		
		/** FirstPost, LastPost, totalAcceptedPosts, totalPosts, totalScore, avgScore*/
		expect(createAdapter(firstPost, lastPost, totalAcceptedPosts, totalPosts, totalScore, avgScore));
	}		
	
	@Test
	public void test_average_score_no_fraction() {
		
		/** creationdate, acceptedanswerId, score */
		adapter.process(createPostRow("2016-01-12T18:45:19.963", null, 1));
		adapter.process(createPostRow("2017-01-12T18:45:19.963", null, 2));
		adapter.process(createPostRow("2018-01-12T18:45:19.963", null, 3));
		
		String firstPost = "2016-01-12T18:45:19.963";
		String lastPost = "2018-01-12T18:45:19.963";
		Integer totalAcceptedPosts = 0;
		Integer totalPosts = 3;
		Integer totalScore = 6;
		Integer avgScore = 2;
		
		/** FirstPost, LastPost, totalAcceptedPosts, totalPosts, totalScore, avgScore*/
		expect(createAdapter(firstPost, lastPost, totalAcceptedPosts, totalPosts, totalScore, avgScore));
	}	
	
	@Test
	public void test_average_score_rounded_down() {
		
		/** creationdate, acceptedanswerId, score */
		adapter.process(createPostRow("2016-01-12T18:45:19.963", null, 1));
		adapter.process(createPostRow("2017-01-12T18:45:19.963", null, 2));
		adapter.process(createPostRow("2018-01-12T18:45:19.963", null, 5));
		
		String firstPost = "2016-01-12T18:45:19.963";
		String lastPost = "2018-01-12T18:45:19.963";
		Integer totalAcceptedPosts = 0;
		Integer totalPosts = 3;
		Integer totalScore = 8;
		Integer avgScore = 2;
		
		/** FirstPost, LastPost, totalAcceptedPosts, totalPosts, totalScore, avgScore*/
		expect(createAdapter(firstPost, lastPost, totalAcceptedPosts, totalPosts, totalScore, avgScore));
	}
	
	@Test
	public void test_first_and_last_post_1_row() {
		
		/** creationdate, acceptedanswerId, score */
		adapter.process(createPostRow("2016-01-12T18:45:19.963", null, null));
		
		String firstPost = "2016-01-12T18:45:19.963";
		String lastPost = "2016-01-12T18:45:19.963";
		Integer totalAcceptedPosts = 0;
		Integer totalPosts = 1;
		Integer totalScore = 0;
		Integer avgScore = 0;
		
		/** FirstPost, LastPost, totalAcceptedPosts, totalPosts, totalScore, avgScore*/
		expect(createAdapter(firstPost, lastPost, totalAcceptedPosts, totalPosts, totalScore, avgScore));
	}	

	@Test
	public void test_first_and_last_post_2_rows_last_first() {
		
		/** creationdate, acceptedanswerId, score */
		adapter.process(createPostRow("2017-01-12T18:45:19.963", null, null));
		adapter.process(createPostRow("2016-01-12T18:45:19.963", null, null));
		
		String firstPost = "2016-01-12T18:45:19.963";
		String lastPost = "2017-01-12T18:45:19.963";
		Integer totalAcceptedPosts = 0;
		Integer totalPosts = 2;
		Integer totalScore = 0;
		Integer avgScore = 0;
		
		/** FirstPost, LastPost, totalAcceptedPosts, totalPosts, totalScore, avgScore*/
		expect(createAdapter(firstPost, lastPost, totalAcceptedPosts, totalPosts, totalScore, avgScore));
	}	
	
	@Test
	public void test_first_and_last_post_3_rows_last_middle() {
		
		/** creationdate, acceptedanswerId, score */
		adapter.process(createPostRow("2017-01-12T18:45:19.963", null, null));
		adapter.process(createPostRow("2018-01-12T18:45:19.963", null, null));
		adapter.process(createPostRow("2016-01-12T18:45:19.963", null, null));
		
		String firstPost = "2016-01-12T18:45:19.963";
		String lastPost = "2018-01-12T18:45:19.963";
		Integer totalAcceptedPosts = 0;
		Integer totalPosts = 3;
		Integer totalScore = 0;
		Integer avgScore = 0;
		
		/** FirstPost, LastPost, totalAcceptedPosts, totalPosts, totalScore, avgScore*/
		expect(createAdapter(firstPost, lastPost, totalAcceptedPosts, totalPosts, totalScore, avgScore));
	}	
	
	@Test
	public void test_first_post_parse_midnight() {
		
		/** creationdate, acceptedanswerId, score */
		adapter.process(createPostRow("2017-01-12T00:00:00.000", null, null));
		
		String firstPost = "2017-01-12T00:00:00.000";
		String lastPost = "2017-01-12T00:00:00.000";
		Integer totalAcceptedPosts = 0;
		Integer totalPosts = 1;
		Integer totalScore = 0;
		Integer avgScore = 0;
		
		/** FirstPost, LastPost, totalAcceptedPosts, totalPosts, totalScore, avgScore*/
		expect(createAdapter(firstPost, lastPost, totalAcceptedPosts, totalPosts, totalScore, avgScore));
	}
	
	private PostRow createPostRow(String creationDate, Integer acceptedAnswerId, Integer score) {
		PostRow post =
			PostRow.builder()
				.acceptedAnswerId(acceptedAnswerId)
				.build();
		
		if(creationDate != null) {
			post.setCreationDate(LocalDateTime.parse(creationDate, dateTimeFormatter));
		}
		if(score != null) {
			post.setScore(score.shortValue());
		}
		return post;
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
	
	private void expect(PostsAnalysisAdapter expected) {
		assertNotNull(expected);
		assertNotNull(adapter);
		assertEquals(expected, adapter);
	}
}

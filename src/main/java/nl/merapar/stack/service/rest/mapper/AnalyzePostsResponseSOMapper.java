package nl.merapar.stack.service.rest.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import nl.merapar.stack.service.rest.resource.api.AnalyzePostsResponseSO;
import nl.merapar.stack.xml.parser.adapter.PostsAnalysisAdapter;

@Mapper
public interface AnalyzePostsResponseSOMapper {

	AnalyzePostsResponseSOMapper INSTANCE = Mappers.getMapper( AnalyzePostsResponseSOMapper.class );
	
	@Mapping(source = "firstPost", target = "details.firstPost")
	@Mapping(source = "lastPost", target = "details.lastPost")
	@Mapping(source = "totalPosts", target = "details.totalPosts")
	@Mapping(source = "totalAcceptedPosts", target = "details.totalAcceptedPosts")
	@Mapping(source = "avgScore", target = "details.avgScore")
	AnalyzePostsResponseSO mapFrom(PostsAnalysisAdapter adapter);
}

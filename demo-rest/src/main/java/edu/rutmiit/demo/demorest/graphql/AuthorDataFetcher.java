package edu.rutmiit.demo.demorest.graphql;

import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsMutation;
import com.netflix.graphql.dgs.DgsQuery;
import com.netflix.graphql.dgs.InputArgument;
import edu.rutmiit.demo.booksapicontract.dto.AuthorRequest;
import edu.rutmiit.demo.booksapicontract.dto.AuthorResponse;
import edu.rutmiit.demo.demorest.service.AuthorService;

import java.util.List;
import java.util.Map;

@DgsComponent
public class AuthorDataFetcher {

    private final AuthorService authorService;

    public AuthorDataFetcher(AuthorService authorService) {
        this.authorService = authorService;
    }

    @DgsQuery
    public List<AuthorResponse> authors() {
        return authorService.findAll();
    }

    @DgsQuery
    public AuthorResponse authorById(@InputArgument Long id) {
        return authorService.findById(id);
    }

    @DgsMutation
    public AuthorResponse createAuthor(@InputArgument("input") Map<String, String> input) {
        AuthorRequest request = new AuthorRequest(input.get("firstName"), input.get("lastName"));
        return authorService.create(request);
    }

    @DgsMutation
    public AuthorResponse updateAuthor(@InputArgument Long id, @InputArgument("input") Map<String, String> input) {
        AuthorRequest request = new AuthorRequest(input.get("firstName"), input.get("lastName"));
        return authorService.update(id, request);
    }

    @DgsMutation
    public Long deleteAuthor(@InputArgument Long id) {
        authorService.delete(id);
        return id;
    }
}


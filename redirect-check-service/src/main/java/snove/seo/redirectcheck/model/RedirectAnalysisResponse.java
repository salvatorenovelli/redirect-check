package snove.seo.redirectcheck.model;

import lombok.Data;

/**
 * Describes the summary of the analysis of a redirect chain compared to what is expected according
 * to the provided {@link RedirectAnalysisRequest}
 */
@Data
public class RedirectAnalysisResponse {

    private final RedirectAnalysisRequest request;
    private final RedirectChain redirectChain;

    public RedirectAnalysisResponse(RedirectAnalysisRequest request, RedirectChain redirectChain) {
        this.request = request;
        this.redirectChain = redirectChain;
    }

}

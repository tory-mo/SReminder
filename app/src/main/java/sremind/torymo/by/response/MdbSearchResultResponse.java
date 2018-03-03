package sremind.torymo.by.response;


import java.util.List;

import sremind.torymo.by.response.SearchResponseResult;

public class MdbSearchResultResponse {
    int page;
    int total_results;
    int total_pages;
    public List<SearchResponseResult> results;
}

package sremind.torymo.by.data;


import java.util.List;

import sremind.torymo.by.SearchResponseResult;

public class MdbSearchResultResponse {
    int page;
    int total_results;
    int total_pages;
    public List<SearchResponseResult> results;
}

package sremind.torymo.by;

public class SRSeries {
	private String imdbId;
	private String name;
	private boolean watchlist;
	
	SRSeries(String imdbId, String name, boolean watchlist){
		this.imdbId = imdbId;
		this.name = name;
		this.watchlist = watchlist;
	}
	
	public String ImdbId(){
		return imdbId;
	}
	
	public String Name(){
		return name;
	}
	
	public boolean WatchList(){
		return watchlist;
	}
	
	public void ImdbId(String imdbId){
		this.imdbId = imdbId;
	}
	
	public void Name(String name){
		this.name = name;
	}
	
	public void WatchList(boolean watchList){
		this.watchlist = watchList;
	}

}

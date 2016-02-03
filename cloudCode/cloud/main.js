
// Use Parse.Cloud.define to define as many cloud functions as you want.
// For example:
Parse.Cloud.define("hello", function(request, response) {
  response.success("Hello world!");
});

Parse.Cloud.job("userMigration", function(request, status) {
  // Set up to modify user data
  var date = new Date();
  Parse.Push.send({
	channels: [""],
	data:{
		alert: "New message for you at "+date.getHours()+":"+date.getMinutes(),
		title: "Message title"
	},
	expiration_interval:300
  }, {
		success:function(){
			var date = new Date();
			status.success("sent successfully. ");
		},
		error: function(error){
			status.error("Uh oh, something went wrong.");
		}
	 });
	 
});

Parse.Cloud.define("getEpisodes",function(request,response){
	var series = Parse.Object.extend("Series");
	var query = new Parse.Query(series);
	query.equalTo("imdbId", request.params.imdbId);
	query.find({
		success: function(results) {
			
			if(results.length == 1){
				var url = "http://www.imdb.com/title/"+request.params.imdbId+"/episodes?season="+results[0].get("seasonsCnt");
				Parse.Cloud.httpRequest({
					url: url,
					success:function(httpResponse){
						var res_str = "";
						if(httpResponse.text.length>0){
							var beginChar = httpResponse.text.indexOf("list_item");
							var endChar = httpResponse.text.indexOf("<hr>");
							var episodes = httpResponse.text.slice(beginChar,endChar);
							var series_number, series_date, series_name;
							while(beginChar!=-1){
								beginChar = episodes.indexOf("<div")+4;
								episodes = episodes.slice(beginChar);
								beginChar = episodes.indexOf("<div")+4;
								episodes = episodes.slice(beginChar);
								beginChar = episodes.indexOf("<div")+5;
								endChar = episodes.indexOf("</div");
								
								series_number = episodes.slice(beginChar, endChar).trim();
								beginChar = episodes.indexOf("airdate")+9;
								endChar = episodes.indexOf("</div", beginChar);
								series_date = episodes.slice(beginChar, endChar).trim();
								beginChar = episodes.indexOf("title=")+7;
								endChar = episodes.indexOf("itemprop", beginChar)-2;
								series_name = episodes.slice(beginChar, endChar).trim();
								beginChar = episodes.indexOf("list_item");
								if(beginChar !=-1) episodes = episodes.slice(beginChar);
								console.log(series_number+"; "+series_date+"; "+series_name);
								res_str += series_number+"+;+"+series_date+"+;+"+series_name+":::";
							}
							response.success(res_str);
						}
						else console.log("we've got empty response")
					},
					error: function(httpResponse){
						console.error("response code: "+httpResponse.status);
					}
				});
			}		
		},

		error: function(error) {
			// error is an instance of Parse.Error.
		}
	});
});



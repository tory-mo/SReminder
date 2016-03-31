
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
                var url = "https://api.themoviedb.org/3/find/"+request.params.imdbId+"?external_source=imdb_id&api_key=6ad01c833dba757c5132002b79e99751";
				console.log(url);
                Parse.Cloud.httpRequest({
                    url: url,
                    success:function(httpResponse){
                        if(httpResponse!=null && typeof(httpResponse)!='undefined'){
							var id = httpResponse.tv_results[0].id;
							url = "https://api.themoviedb.org/3/tv/"+id+"/season/1?api_key=6ad01c833dba757c5132002b79e99751";
							Parse.Cloud.httpRequest({
								url: url,
								success:function(httpResponse1){
									var res_str = "";
									if(httpResponse1!=null && typeof(httpResponse1)!='undefined'){
										var series_number, series_date, series_name;
										for(var i = 0; i<httpResponse1.episodes.length; i++){
											series_name = httpResponse1.episodes[i].name;
											series_date = httpResponse1.episodes[i].air_date;
											series_number = httpResponse1.episodes[i].episode_number;
											
											console.log(series_number+"; "+series_date+"; "+series_name);
											res_str += series_number+"+;+"+series_date+"+;+"+series_name+":::";
										}
										response.success(res_str);
										
									} else console.log("we've got empty response");
								},
								error: function(httpResponse1){
									console.error("response code: "+httpResponse1.status);
								}
							});
							
						} else console.log("we've got empty response");
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



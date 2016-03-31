package sremind.torymo.by;

import android.app.Application;
import android.util.Log;

import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParsePush;
import com.parse.ParseUser;
import com.parse.SaveCallback;

public class SeriesReminderApp extends Application {

	@Override
	public void onCreate() {
		Parse.initialize(this,"G0eKBkvyWzBweoms9HMNT8zHxx9OKdM5YZUKXv4H", "HfQKZRokbt8JvqnmBwGIqyXwZkLaNYkCEuJ7ADf9");
		ParseUser.enableAutomaticUser();
	    ParseACL defaultACL = new ParseACL();
	    // If you would like all objects to be private by default, remove this line.
	    defaultACL.setPublicReadAccess(true);
	    ParseACL.setDefaultACL(defaultACL, true);
	    
	    //subscribe to all
		ParsePush.subscribeInBackground("", new SaveCallback() {
			  @Override
			  public void done(ParseException e) {
			    if (e == null) {
			      Log.d("com.parse.push", "successfully subscribed to the broadcast channel.");
			    } else {
			      Log.e("com.parse.push", "failed to subscribe for push", e);
			    }
			  }
		});

	    super.onCreate();
	}

}

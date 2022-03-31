package uk.ac.man.cs.eventlite.dao;

import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

public class TwitterService {
	private Twitter twitter;
	
	public TwitterService() {
		ConfigurationBuilder cb = new ConfigurationBuilder();
		cb.setDebugEnabled(true)
		.setOAuthConsumerKey("your consumer key")
		.setOAuthConsumerSecret("your consumer secret")
		.setOAuthAccessToken("your access token")
		.setOAuthAccessTokenSecret("your access token secret");
		TwitterFactory tf = new TwitterFactory(cb.build());
		twitter = tf.getInstance();
	}
	
	public Twitter getTwitter() {
		return twitter;
	}
}

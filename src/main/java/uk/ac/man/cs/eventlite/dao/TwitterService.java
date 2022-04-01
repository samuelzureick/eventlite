package uk.ac.man.cs.eventlite.dao;

import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

public class TwitterService {

	private Twitter twitter;

	public TwitterService() {
		ConfigurationBuilder cb = new ConfigurationBuilder();
		cb.setDebugEnabled(true)
		.setOAuthConsumerKey("bde2hcqJ6w6YlTjKjqI7cZ3NB")
		.setOAuthConsumerSecret("MGtMQm6F0nqjSXXWrRv4PVcZouwnd8uRG44i3A3hADILsOguvg")
		.setOAuthAccessToken("1509554147321913347-jgdKYFkTjbY4laMEhiLPe8LcXCJ3qp")
		.setOAuthAccessTokenSecret("vhj6VjQVLY4IeW3KnaS7wzgADTx3ptuQialkAq0Jhq2cZ");
		TwitterFactory tf = new TwitterFactory(cb.build());
		twitter = tf.getInstance();
	}

	public Twitter getTwitter() {
		return twitter;
	}
}

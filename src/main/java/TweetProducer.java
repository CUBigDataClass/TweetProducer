import com.google.common.collect.Lists;
import com.twitter.hbc.ClientBuilder;
import com.twitter.hbc.core.Client;
import com.twitter.hbc.core.Constants;
import com.twitter.hbc.core.endpoint.StatusesFilterEndpoint;
import com.twitter.hbc.core.processor.StringDelimitedProcessor;
import com.twitter.hbc.httpclient.auth.Authentication;
import com.twitter.hbc.httpclient.auth.OAuth1;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.Properties;
import java.util.List;
import java.util.ArrayList;

import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;

public class TweetProducer {
    private static final int memQsize = 100;

    public static void main(String[] args) {
        String consumerKey    = args[0];
        String consumerSecret = args[1];
        String accessToken    = args[2];
        String tokenSecret    = args[3];

        try { oauth(consumerKey, consumerSecret, accessToken, tokenSecret); }
        catch (Exception e) { System.err.println("Caught exception"); }
    }

    public static void oauth(String consumerKey, String consumerSecret, String token, String secret) throws InterruptedException {

        // Construct Kafka properties
        Properties props = new Properties();
        props.put("metadata.broker.list", "localhost:9092");
        props.put("serializer.class", "kafka.serializer.StringEncoder");
        props.put("partitioner.class", "SimplePartitioner");
        props.put("request.required.acks", "1");

        ProducerConfig config = new ProducerConfig(props);

        Producer<String, String> producer = new Producer<String, String>(config);
        System.out.println("Created Producer");

        BlockingQueue<String> queue = new LinkedBlockingQueue<String>(memQsize);
        StatusesFilterEndpoint endpoint = new StatusesFilterEndpoint();

        // add some track terms
        List<String> terms = new ArrayList<String>();
        terms.add("beer");
        terms.add("stout");
        terms.add("tripel");
        terms.add("dubbel");
        terms.add("porter");
        terms.add("pale ale");
        terms.add("IPA");
        terms.add("lager");

        terms.add("wine");
        terms.add("pinot noir");
        terms.add("merlot");
        terms.add("cabernet sauvignon");
        terms.add("chianti");
        terms.add("malbec");
        terms.add("zinfandel");
        terms.add("chardonnay");
        terms.add("pinot grigio");
        terms.add("riesling");

        endpoint.trackTerms(terms);

        Authentication auth = new OAuth1(consumerKey, consumerSecret, token, secret);

        // Create a new BasicClient. By default gzip is enabled.
        Client client = new ClientBuilder()
                .hosts(Constants.STREAM_HOST)
                .endpoint(endpoint)
                .authentication(auth)
                .processor(new StringDelimitedProcessor(queue))
                .build();

        System.out.println("Connecting to twitter...");

        // Establish a connection
        client.connect();
        System.out.println("Connected!");

        // Send tweets to Kafka
        try {
            long msgRead = 0;
            while (true) {
                String msg = queue.take();
                KeyedMessage<String, String> data = new KeyedMessage<String, String>("tweets", "" + msgRead, msg);
                producer.send(data);
                System.out.println(msgRead + " completed");
                msgRead++;
            }
        } catch(InterruptedException e) {
            System.out.println("Closing twitter connection...");
            client.stop();
            System.out.println("Stopping producer...");
            producer.close();
            System.out.println("Done!");
        }
    }
}

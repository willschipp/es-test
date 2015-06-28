package etisalat;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.unit.Fuzziness;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.util.StopWatch;

import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootApplication
public class Application implements CommandLineRunner {

	//sample line
	//210.8.79.228    201504300100609 GET http://data.kasabi.com/dataset/bricklink/set/4193-1/inventory/60477-120  0   TCP_MISS/200    -   188.139.105.139 -   0
	
	public static void main(String[] args) {
		SpringApplication.run(Application.class,args);
	}

	@Autowired
	private Client client;
	
	
	private ObjectMapper mapper = new ObjectMapper();
	
	public void run(String... arg0) throws Exception {
		StopWatch watch = new StopWatch();
		//index creation
		watch.start();
		load();
		watch.stop();
		System.out.println("response time " + watch.getLastTaskTimeMillis());		
		
		//search for f1e64531 --> 100k
		//search for 52f2cf6d --> 1m
		//search for 436bb901 --> 100m
		//search for 9b34ee0a --> 1b
		
		Thread.sleep(30 * 1000);
		
		watch.start();
		System.out.println("about to query");
		
		SearchResponse response = client.prepareSearch("squid_proxy")
		        .setTypes("log")
		        .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
		        .setQuery(QueryBuilders.queryString("+9b34ee0a")
		        		.field("url"))
		        .setFrom(0).setSize(Integer.MAX_VALUE).setExplain(true)
		        .execute()
		        .actionGet();
		
		
		System.out.println("response time " + response.getTookInMillis());
		System.out.println("total hits " + response.getHits().totalHits());
		
		SearchHit[] hits = response.getHits().getHits();
		
		for (SearchHit hit : hits) {
			System.out.println(">>> hit " + hit.getSource());
		}
		watch.stop();
		System.out.println(watch.getLastTaskTimeMillis());
		
	}	
	
	private void load() throws Exception {
		client.admin().indices().prepareCreate("squid_proxy").execute().actionGet();
		
		//read in the file
		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream("/tmp/onebillion.log")));
		String line;
		while ((line = reader.readLine()) != null) {
			String[] parts = line.split("\\s+");
			Map<String,Object> map = new HashMap<String,Object>();
			map.put("sourceIp",parts[0]);
			map.put("timestamp",parts[1]);
			map.put("requestType",parts[2]);
			map.put("url",parts[3]);
			map.put("latency",parts[4]);
			map.put("protocol",parts[5]);
			map.put("destinationIp",parts[6]);
			map.put("size",parts[9]);
			//save
			client.prepareIndex("squid_proxy", "log").setSource(map).execute().actionGet();
		}//end for
		reader.close();
		System.out.println("stored");		
	}
	
}

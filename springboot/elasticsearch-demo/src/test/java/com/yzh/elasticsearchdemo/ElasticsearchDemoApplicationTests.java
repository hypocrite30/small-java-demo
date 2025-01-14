package com.yzh.elasticsearchdemo;

import com.alibaba.fastjson.JSON;
import lombok.Data;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.Avg;
import org.elasticsearch.search.aggregations.metrics.AvgAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

@SpringBootTest
class ElasticsearchDemoApplicationTests {

	@Autowired
	private RestHighLevelClient restHighLevelClient;

	/**
	 * 测试向索引中添加文档
	 * @throws IOException
	 */
	@Test 
	public void indexData() throws IOException {
		IndexRequest indexRequest = new IndexRequest("user"); 
		User user = new User();
		user.setUserName("张三");
		user.setAge(20);
		user.setGender("男");
		String jsonString = JSON.toJSONString(user);
		// 设置保存的内容
		indexRequest.id("1");
		IndexRequest source = indexRequest.source(jsonString, XContentType.JSON);
		IndexResponse indexResponse = restHighLevelClient.index(source, RequestOptions.DEFAULT);
		System.out.println(indexResponse);
	}
	
	@Test
	public void getById() throws IOException {
		GetRequest request = new GetRequest("user", "1");
		GetResponse documentFields = restHighLevelClient.get(request, RequestOptions.DEFAULT);
		System.out.println(documentFields);
		String sourceAsString = documentFields.getSourceAsString();
		User user = JSON.parseObject(sourceAsString, User.class);
		System.out.println(user);
	}
	
	@Data	
	private static class User {
	    private String userName;
	    private Integer age;
	    private String gender;
	}
	@Data
	private static class Account{
		private int account_number;
		private int balance;
		private String firstname;
		private String lastname;
		private int age;
		private String gender;
		private String address;
		private String employer;
		private String email;
		private String city;
		private String state;
	}

	/**
	 * 测试复杂检索:在 bank 中搜索 address 中包含 mill 的所有人的年龄分布以及平均年龄, 平均薪资.
	 * 原始查询语句
	 * GET /bank/_search
	 * {
	 *   "query": {
	 *     "match": {
	 *       "address": "mill"
	 *     }
	 *   },
	 *   "aggs": {
	 *     "ageAgg": {
	 *       "terms": {
	 *         "field": "age"
	 *       }
	 *     },
	 *      "ageAvg": {
	 *       "avg": {
	 *         "field": "age"
	 *       }
	 *     },
	 *      "balanceAvg": {
	 *       "avg": {
	 *         "field": "balance"
	 *       }
	 *     }
	 *   }
	 * }
	 * @throws IOException
	 */
	@Test
	public void searchData() throws  IOException{
		SearchSourceBuilder builder = new SearchSourceBuilder();
		builder.query(QueryBuilders.matchQuery("address", "mill"));

		TermsAggregationBuilder ageAgg = AggregationBuilders.terms("ageAgg").field("age");
		builder.aggregation(ageAgg);
		
		AvgAggregationBuilder ageAvg = AggregationBuilders.avg("ageAvg").field("age");
		builder.aggregation(ageAvg);

		AvgAggregationBuilder balanceAvg = AggregationBuilders.avg("balanceAvg").field("balance");
		builder.aggregation(balanceAvg);

		SearchRequest searchRequest = new SearchRequest();
		searchRequest.source(builder);

		// 执行检索
		SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
		SearchHits hits = searchResponse.getHits();
		SearchHit[] hits1 = hits.getHits();
		for (SearchHit documentFields : hits1) {
			String sourceAsString = documentFields.getSourceAsString();
			System.out.println(sourceAsString);
			Account account = JSON.parseObject(sourceAsString, Account.class);
			System.out.println(account);
		}
		
		Aggregations aggregations = searchResponse.getAggregations();
		Terms ageAgg1 = aggregations.get("ageAgg");
		for (Terms.Bucket bucket : ageAgg1.getBuckets()) {
			String keyAsString = bucket.getKeyAsString();
			System.out.println(keyAsString+"---"+bucket.getDocCount());
		}

		Avg ageAvg1 = aggregations.get("ageAvg");
		System.out.println("ageAvg:"+ageAvg1.getValue());

		Avg balanceAvg1  = aggregations.get("balanceAvg");
		System.out.println("balanceAvg:"+balanceAvg1.getValue());
		
	}
	
}

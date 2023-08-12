package com.info7255.service;

import org.apache.http.HttpHost;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsRequest;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.Strings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.ToXContent;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.FetchSourceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class ElasticsearchService {

    @Autowired
    private RestHighLevelClient elasticsearchClient;
    private static final String INDEX = "plan";
//    @Autowired
//    private RestHighLevelClient elasticsearchClient;
////    private ElasticsearchRestTemplate restTemplate;

    public void writeDataToElasticsearch(String key, String jsonData) throws IOException {
        if (!indexExists()) {
            createIndex("plan");
        }
        IndexRequest request = new IndexRequest("plan");
//        request.id(key);
        request.source(jsonData, XContentType.JSON);

        IndexResponse response = elasticsearchClient.index(request, RequestOptions.DEFAULT);
        // 处理响应结果
    }

    public void update(String key, String json) throws IOException {
        if (!indexExists()) {
            createIndex("plan");
        }
        SearchRequest searchRequest = new SearchRequest(INDEX);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.matchQuery("objectId", key));
        searchRequest.source(searchSourceBuilder);

        SearchResponse searchResponse = elasticsearchClient.search(searchRequest, RequestOptions.DEFAULT);
        SearchHit[] searchHits = searchResponse.getHits().getHits();
        if (searchHits == null) return;
        for (SearchHit searchHit : searchHits) {
            UpdateRequest request = new UpdateRequest("plan",  searchHit.getId());
            request.doc(json, XContentType.JSON);
            elasticsearchClient.update(request, RequestOptions.DEFAULT);
        }


    }

    public String getDataFromElasticsearch(String key) throws IOException {
        GetRequest request = new GetRequest("plan").id(key);
        FetchSourceContext fetchSourceContext = new FetchSourceContext(true);
        request.fetchSourceContext(fetchSourceContext);

        GetResponse response = elasticsearchClient.get(request, RequestOptions.DEFAULT);
        if (response.isExists()) {
            return Strings.toString((ToXContent) response.getSourceAsMap());
        } else {
            // 处理数据不存在的逻辑
            return null;
        }
    }

    private boolean indexExists() throws IOException {
        GetIndexRequest request = new GetIndexRequest("plan");
        boolean exists = elasticsearchClient.indices().exists(request, RequestOptions.DEFAULT);
        return exists;
    }


    private void createIndex(String indexName) throws IOException {
        CreateIndexRequest request = new CreateIndexRequest(indexName);
        request.settings(Settings.builder().put("index.number_of_shards", 3).put("index.number_of_replicas", 2));
        String mapping = getMapping();
        request.mapping("_doc",mapping, XContentType.JSON);

        elasticsearchClient.indices().create(request, RequestOptions.DEFAULT);
    }

    private static String getMapping() {
        String mapping= "{\r\n" +
                "	\"properties\": {\r\n" +
                "		\"_org\": {\r\n" +
                "			\"type\": \"text\"\r\n" +
                "		},\r\n" +
                "		\"objectId\": {\r\n" +
                "			\"type\": \"keyword\"\r\n" +
                "		},\r\n" +
                "		\"objectType\": {\r\n" +
                "			\"type\": \"text\"\r\n" +
                "		},\r\n" +
                "		\"planType\": {\r\n" +
                "			\"type\": \"text\"\r\n" +
                "		},\r\n" +
                "		\"creationDate\": {\r\n" +
                "			\"type\": \"date\",\r\n" +
                "			\"format\" : \"MM-dd-yyyy\"\r\n" +
                "		},\r\n" +
                "		\"planCostShares\": {\r\n" +
                "			\"type\": \"nested\",\r\n" +
                "			\"properties\": {\r\n" +
                "				\"copay\": {\r\n" +
                "					\"type\": \"long\"\r\n" +
                "				},\r\n" +
                "				\"deductible\": {\r\n" +
                "					\"type\": \"long\"\r\n" +
                "				},\r\n" +
                "				\"_org\": {\r\n" +
                "					\"type\": \"text\"\r\n" +
                "				},\r\n" +
                "				\"objectId\": {\r\n" +
                "					\"type\": \"keyword\"\r\n" +
                "				},\r\n" +
                "				\"objectType\": {\r\n" +
                "					\"type\": \"text\"\r\n" +
                "				}\r\n" +
                "			}\r\n" +
                "		},\r\n" +
                "		\"linkedPlanServices\": {\r\n" +
                "			\"type\": \"nested\",\r\n" +
                "			\"properties\": {\r\n" +
                "				\"_org\": {\r\n" +
                "					\"type\": \"text\"\r\n" +
                "				},\r\n" +
                "				\"objectId\": {\r\n" +
                "					\"type\": \"keyword\"\r\n" +
                "				},\r\n" +
                "				\"objectType\": {\r\n" +
                "					\"type\": \"text\"\r\n" +
                "				},\r\n" +
                "				\"linkedService\": {\r\n" +
                "                   \"type\": \"nested\",\r\n" +
                "					\"properties\": {\r\n" +
                "						\"name\": {\r\n" +
                "							\"type\": \"text\"\r\n" +
                "						},\r\n" +
                "						\"_org\": {\r\n" +
                "							\"type\": \"text\"\r\n" +
                "						},\r\n" +
                "						\"objectId\": {\r\n" +
                "							\"type\": \"keyword\"\r\n" +
                "						},\r\n" +
                "						\"objectType\": {\r\n" +
                "							\"type\": \"text\"\r\n" +
                "						}\r\n" +
                "					}\r\n" +
                "				},\r\n" +
                "				\"planserviceCostShares\": {\r\n" +
                "                  \"type\": \"nested\",\r\n" +
                "					\"properties\": {\r\n" +
                "						\"copay\": {\r\n" +
                "							\"type\": \"long\"\r\n" +
                "						},\r\n" +
                "						\"deductible\": {\r\n" +
                "							\"type\": \"long\"\r\n" +
                "						},\r\n" +
                "						\"_org\": {\r\n" +
                "							\"type\": \"text\"\r\n" +
                "						},\r\n" +
                "						\"objectId\": {\r\n" +
                "							\"type\": \"keyword\"\r\n" +
                "						},\r\n" +
                "						\"objectType\": {\r\n" +
                "							\"type\": \"text\"\r\n" +
                "						}\r\n" +
                "					}\r\n" +
                "				}\r\n" +
                "			}\r\n" +
                "		}\r\n" +
                "	}\r\n" +
                "}";

        return mapping;
    }
}
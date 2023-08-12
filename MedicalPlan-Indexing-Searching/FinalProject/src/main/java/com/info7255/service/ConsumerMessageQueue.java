package com.info7255.service;

import org.apache.http.HttpHost;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentType;
import org.json.JSONObject;
import redis.clients.jedis.Jedis;

import java.io.IOException;

public class ConsumerMessageQueue {
    private static Jedis jedis;
    private static RestHighLevelClient client = new RestHighLevelClient(
            RestClient.builder(new HttpHost("47.120.3.234", 9200, "http")));
    private static final String IndexName="planindex";

    public static void main() throws IOException {
        jedis = new Jedis("47.120.3.234");
        System.out.println("Consumer MQ started");

            String message = "{\n" +
                    "    \"planCostShares\": {\n" +
                    "        \"deductible\": 2000,\n" +
                    "        \"_org\": \"example.com\",\n" +
                    "        \"copay\": 23,\n" +
                    "        \"objectId\": \"1234vxc2324sdf-501\",\n" +
                    "        \"objectType\": \"membercostshare\"\n" +
                    "    },\n" +
                    "    \"linkedPlanServices\": [\n" +
                    "        {\n" +
                    "            \"linkedService\": {\n" +
                    "                \"_org\": \"example.com\",\n" +
                    "                \"objectId\": \"1234520xvc30asdf-502\",\n" +
                    "                \"objectType\": \"service\",\n" +
                    "                \"name\": \"Yearly physical\"\n" +
                    "            },\n" +
                    "            \"planserviceCostShares\": {\n" +
                    "                \"deductible\": 10,\n" +
                    "                \"_org\": \"example.com\",\n" +
                    "                \"copay\": 0,\n" +
                    "                \"objectId\": \"1234512xvc1314asdfs-503\",\n" +
                    "                \"objectType\": \"membercostshare\"\n" +
                    "            },\n" +
                    "            \"_org\": \"example.com\",\n" +
                    "            \"objectId\": \"27283xvx9asdff-504\",\n" +
                    "            \"objectType\": \"planservice\"\n" +
                    "        },\n" +
                    "        {\n" +
                    "            \"linkedService\": {\n" +
                    "                \"_org\": \"example.com\",\n" +
                    "                \"objectId\": \"1234520xvc30sfs-505\",\n" +
                    "                \"objectType\": \"service\",\n" +
                    "                \"name\": \"well baby\"\n" +
                    "            },\n" +
                    "            \"planserviceCostShares\": {\n" +
                    "                \"deductible\": 10,\n" +
                    "                \"_org\": \"example.com\",\n" +
                    "                \"copay\": 175,\n" +
                    "                \"objectId\": \"1234512xvc1314sdfsd-506\",\n" +
                    "                \"objectType\": \"membercostshare\"\n" +
                    "            },\n" +
                    "            \"_org\": \"example.com\",\n" +
                    "            \"objectId\": \"27283xvx9sdf-507\",\n" +
                    "            \"objectType\": \"planservice\"\n" +
                    "        }\n" +
                    "    ],\n" +
                    "    \"_org\": \"example.com\",\n" +
                    "    \"objectId\": \"12xvxc345ssdsds-508\",\n" +
                    "    \"objectType\": \"plan\",\n" +
                    "    \"planType\": \"inNetwork\",\n" +
                    "    \"creationDate\": \"12-12-2017\"\n" +
                    "}";

//            if (message == null) {
//                continue;
//            }
        indexExists();
        createElasticIndex();
            JSONObject result = new JSONObject(message);

            // Get action
            Object obj = result.get("isDelete");
            System.out.println("isDelete: " + obj.toString());

            boolean isDelete = Boolean.parseBoolean(obj.toString());
            if(!isDelete) {
                JSONObject plan= new JSONObject(result.get("message").toString());
                postDocument(plan);
            }else {
                deleteDocument(result.get("message").toString());
            }
        }


    private static boolean indexExists() throws IOException {
        GetIndexRequest request = new GetIndexRequest(IndexName);
        boolean exists = client.indices().exists(request, RequestOptions.DEFAULT);
        return exists;
    }

    private static String postDocument(JSONObject plan) throws IOException {
        if(!indexExists()) {
            createElasticIndex();
        }
        IndexRequest request = new IndexRequest(IndexName);
        request.id(plan.get("objectId").toString());
        request.source(plan.toString(), XContentType.JSON);
        IndexResponse indexResponse = client.index(request, RequestOptions.DEFAULT);
        System.out.println("response id: "+indexResponse.getId());
        return indexResponse.getResult().name();
    }

    private static void createElasticIndex() throws IOException {
        CreateIndexRequest request = new CreateIndexRequest(IndexName);
        request.settings(Settings.builder().put("index.number_of_shards", 3).put("index.number_of_replicas", 2));
        String mapping = getMapping();
        request.mapping(mapping, XContentType.JSON);

        client.indices().create(request, RequestOptions.DEFAULT);
    }

    private static void deleteDocument(String documentId) throws IOException {
        DeleteRequest request = new DeleteRequest();
        client.delete(
                request, RequestOptions.DEFAULT);
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

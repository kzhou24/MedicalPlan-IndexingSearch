# MedicalPlan-IndexingSearch
# Tech Stack
  Spring Boot (Java)
  Redis
  Elastic Search
  RabbitMQ
  Features
  Authentication using Bearer Token generated by JWT
  Validate request JSON object with JSON Schema
  Cache Server Response and validate cache using ETag
  Support POST, PUT, PATCH, GET and DELETE Http Methods for the REST API
  Store JSON Objects in Redis key-value store for data persistence
  Index the JSON Objects in Elastic Server for Search capabilities
  Queueing indexing requests to Elastic Server using RabbitMQ
# Data Flow
  Generate token using the /token endpoint
  Validate further API requests using the Bearer Token
  Create JSON Object using the POST HTTP method
  Validate incoming JSON Object using the respective JSON Schema
  De-Structure hierarchial JSON Object while storing in Redis key-value store
  Enqueue object in RabbitMQ queue to index the object
  Dequeue from RabbitMQ queue and index data in ElasticServer
  Implement Search queries using Kibana Console to retrieve indexed data
# Steps to run:
  Install the Prerequisites for the project:
  Redis Server (https://redis.io/download)
  ElasticSearch (https://www.elastic.co/downloads/elasticsearch)
  Kibana (https://www.elastic.co/downloads/kibana)
  RabbitMQ Server (https://www.rabbitmq.com/download.html)
  Start the Redis Server with the redis-server command
  Start the ElasticSearch server with the elasticsearch command
  Start Kibana with the kibana command
  Start RabbitMQ server with the rabbitmq-server command
  This would start the server. Create the data using the REST API endpoints and query the indexed data on Kibana Console.

# API Endpoints
  GET /token - This generates a RSA-signed JWT token used to authenticate future requests.
  POST /plan - Creates a new plan provided in the request body
  PUT /plan/{id} - Updates an existing plan provided by the id
  A valid Etag for the object should also be provided in the If-Match HTTP Request Header
  PATCH /plan/{id} - Patches an existing plan provided by the id
  A valid Etag for the object should also be provided in the If-Match HTTP Request Header
  GET /plan/{id} - Fetches an existing plan provided by the id
  An Etag for the object can be provided in the If-None-Match HTTP Request Header
  If the request is successful, a valid Etag for the object is returned in the ETag HTTP Response Header
  DELETE /plan/{id} - Deletes an existing plan provided by the id
  A valid Etag for the object should also be provided in the If-Match HTTP Request Header

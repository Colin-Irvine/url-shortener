# URL Shortener

## API Gateway
This will be an HTTP Gateway that accepts POST requests to have a URL Shortened and its short name returned.
As well as GET requests, where the short name is the Path and returns a URL redirect on success.
## DynamoDB
This will be used as the cache to store shortened URLs. This will be basic key value with some metadata about the shortening of the URL.

## Lambda
This will be used for handling requests from the Gateway and creating the correct response.


## CDK v2
This will be used to control the URL Shortener service's resouces.
This could be where most of the work to return 3xx series responses with location is done.
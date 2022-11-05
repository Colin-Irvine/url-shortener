import { Stack, StackProps, Duration } from 'aws-cdk-lib';
import { Construct } from 'constructs';
import { RestApi, ResponseType, LambdaIntegration } from 'aws-cdk-lib/aws-apigateway';
import { Table, AttributeType, BillingMode } from 'aws-cdk-lib/aws-dynamodb';
import { Function, Code, Runtime } from 'aws-cdk-lib/aws-lambda';


export class CdkStack extends Stack {
  constructor(scope: Construct, id: string, props?: StackProps) {
    super(scope, id, props);

    const urlCache = new Table(this, 'url-shortener-cache', {
        tableName: 'url-shortener-cache',
        partitionKey: {
            name: 'shortName',
            type: AttributeType.STRING
        },
        billingMode: BillingMode.PAY_PER_REQUEST,
    });

    const shortenerLambda = new Function(this, 'url-shortener-lambda', {
        runtime: Runtime.JAVA_8,
        code: Code.fromAsset('../app/build/distributions/app.zip'),
        handler: 'url.shortener.LambdaHandler',
        timeout: Duration.seconds(10),
    })

    urlCache.grantReadWriteData(shortenerLambda);

    const shortenerApiGateway = new RestApi(this, 'url-shortener-api', {
        restApiName: 'url-shortener-api'
    });

    const retrieveApiResource = shortenerApiGateway.root.addResource('{short_name}');
        retrieveApiResource.addMethod('GET',
        new LambdaIntegration(shortenerLambda),
    );

    const shortenApiResource = shortenerApiGateway.root.addResource('shorten');
        shortenApiResource.addMethod('POST',
        new LambdaIntegration(shortenerLambda),
    );

  }
}

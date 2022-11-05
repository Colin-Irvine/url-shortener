import * as cdk from 'aws-cdk-lib';
import { Template } from 'aws-cdk-lib/assertions';
import * as Cdk from '../lib/cdk-stack';

// example test. To run these tests, uncomment this file along with the
// example resource in lib/cdk-stack.ts
test('API Gateway Created', () => {
  const app = new cdk.App();
    // WHEN
  const stack = new Cdk.CdkStack(app, 'MyTestStack');
    // THEN
  const template = Template.fromStack(stack);

  template.hasResourceProperties('AWS::ApiGateway::RestApi', {
    Name: 'url-shortener-api',
  });

  template.hasResourceProperties('AWS::ApiGateway::Resource', {
    PathPart: '{short_name}',

  })

  template.hasResourceProperties('AWS::ApiGateway::Method', {
    HttpMethod: 'GET',
  })
});

test('DynamoDB Table Created', () => {
  const app = new cdk.App();
    // WHEN
  const stack = new Cdk.CdkStack(app, 'MyTestStack');
    // THEN
  const template = Template.fromStack(stack);

  template.hasResourceProperties('AWS::DynamoDB::Table', {
    TableName: 'url-shortener-cache',
    KeySchema: [
          {
              "AttributeName": "shortName",
              "KeyType": "HASH"
          }
    ],
    BillingMode: 'PAY_PER_REQUEST',
  });

});

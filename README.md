# spring-rds

Get whatsapp business account id: https://graph.facebook.com/v19.0/me?fields=id,name&access_token=<ACCESS_TOKEN>

LOL.

`aws elbv2 create-load-balancer --name spring-rds-nlb --type network --subnets subnet-0f4e6c36eed207f95 subnet-01686dfb4588a737d
`
eipalloc-060146a38d4a57e0f

aws elbv2 create-target-group --name spring-rds-tg --protocol TCP --port 80 --vpc-id vpc-0e317da3362620629 --target-type ip


aws elbv2 create-listener --load-balancer-arn arn:aws:elasticloadbalancing:eu-north-1:474668390372:loadbalancer/net/spring-rds-nlb/324fdd31a31df2d1 --protocol TCP --port 80 --default-actions Type=forward,TargetGroupArn=arn:aws:elasticloadbalancing:eu-north-1:474668390372:targetgroup/spring-rds-tg/0bc2c5881d483fef

aws ecs list-services --cluster yugatechsolutions-cluster

aws ecs update-service --cluster yugatechsolutions-cluster --service spring-rds-service --load-balancers targetGroupArn=arn:aws:elasticloadbalancing:eu-north-1:474668390372:targetgroup/spring-rds-tg/0bc2c5881d483fef,containerName=spring-container,containerPort=3333

aws elbv2 describe-target-health --target-group-arn arn:aws:elasticloadbalancing:eu-north-1:474668390372:targetgroup/spring-rds-tg/0bc2c5881d483fef

aws logs describe-log-streams --log-group-name "/ecs/public-spring-task"

aws ecs update-service --cluster yugatechsolutions-cluster --service spring-rds-service --force-new-deployment

// File: deployToAWS.groovy

def call(String imageName, String region, String ecrName, String credentialsId) {
    pipeline {
        agent any
        stages {
            stage('docker-build') {
                steps {
                    bat "docker build -t ${imageName} ."
                }
            }
            stage('Deploy to AWS') {
                environment { 
                    // Define environment variables outside of the script block
                    AWS_DEFAULT_REGION = region
                    ECR_REPO_URL = 'https://us-east-1.console.aws.amazon.com/ecr/private-registry/repositories?region=us-east-1'
                    DOCKER_IMAGE_NAME = imageName
                }
                steps {
                    // Remove the unnecessary script block here
                    // You can add deployment steps directly
                    withCredentials([[
                        $class: 'AmazonWebServicesCredentialsBinding',
                        credentialsId: credentialsId,
                        accessKeyVariable: 'AWS_ACCESS_KEY_ID',
                        secretKeyVariable: 'AWS_SECRET_ACCESS_KEY'
                    ]]) {
                        bat "aws ecr get-login-password --region ${region} | docker login --username AWS --password-stdin 533267263918.dkr.ecr.us-east-1.amazonaws.com"
                        bat "docker tag ${imageName} 533267263918.dkr.ecr.us-east-1.amazonaws.com/${ecrName}:latest"
                        bat "docker push 533267263918.dkr.ecr.us-east-1.amazonaws.com/${ecrName}:latest"
                    }
                }
            }
        }
    }
}

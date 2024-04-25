def call (string imagename, string region, string ecrname, string credentialsId) 
    pipeline {
    agent any
    stages {
        stage('docker-build') {
            steps {
                bat "docker build -t ${imagename} ."
            }
        }
        stage('Deploy to AWS') {
            environment { 
                // Define environment variables outside of the script block
                bat "docker build -t ${imagename} ."
                AWS_DEFAULT_REGION = '$string.region}'
                ECR_REPO_URL = 'https://us-east-1.console.aws.amazon.com/ecr/private-registry/repositories?region=us-east-1'
                DOCKER_IMAGE_NAME = '${imagename}'
            }
            steps {
                // Remove the unnecessary script block here
                // You can add deployment steps directly
                withCredentials([[
                    $class: 'AmazonWebServicesCredentialsBinding',
                    credentialsId: '$ {credentialsI})',
                    accessKeyVariable: 'AWS_ACCESS_KEY_ID',
                    secretKeyVariable: 'AWS_SECRET_ACCESS_KEY'
                ]]) {
                    bat "aws ecr get-login-password --region us-east-1 | docker login --username AWS --password-stdin 533267263918.dkr.ecr.us-east-1.amazonaws.com"
                    bat "docker tag soundarya 533267263918.dkr.ecr.us-east-1.amazonaws.com/${ecrname}:latest"
                    bat "docker push 533267263918.dkr.ecr.us-east-1.amazonaws.com/${ecrname}:latest"
                }
            }
        }
    }
}

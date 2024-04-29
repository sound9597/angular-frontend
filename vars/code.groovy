def call(String imagename, String region, String ecrname, String credentialsId) {
    pipeline {
        agent any
        stages {
            stage('docker-build') {
                steps {
                    script {
                        // Docker build stage
                        sh "docker build -t ${imagename} ."
                    }
                }
            }
            stage('Deploy to AWS') {
                environment { 
                    // Define environment variables
                    AWS_DEFAULT_REGION = "${region}"
                    ECR_REPO_URL = "533267263918.dkr.ecr.${region}.amazonaws.com/${ecrname}"
                    DOCKER_IMAGE_NAME = "${imagename}"
                }
                steps {
                    // AWS ECR authentication and Docker push stage
                    withCredentials([
                        [ $class: 'AmazonWebServicesCredentialsBinding',
                          credentialsId: credentialsId,
                          accessKeyVariable: 'AWS_ACCESS_KEY_ID',
                          secretKeyVariable: 'AWS_SECRET_ACCESS_KEY'
                        ]
                    ]) {
                        script {
                            sh "aws ecr get-login-password --region ${region} | docker login --username AWS --password-stdin ${ECR_REPO_URL}"
                            sh "docker tag ${imagename} ${ECR_REPO_URL}:latest"
                            sh "docker push ${ECR_REPO_URL}:latest"
                        }
                    }
                }
            }
        }
    }
}

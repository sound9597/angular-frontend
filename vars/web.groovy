def call(String imagename, String region, String ecrname, String credentialsId) {
    pipeline {
        agent any
        stages {
            stage('docker-build') {
                steps {
                    script {
                        // Wrap the bat command in a script block
                        bat "docker build -t ${imagename} ."
                    }
                }
            }
            stage('Deploy to AWS') {
                environment { 
                    // Define environment variables outside of the script block
                    AWS_DEFAULT_REGION = region
                    ECR_REPO_URL = "533267263918.dkr.ecr.${region}.amazonaws.com/${ecrname}"
                    DOCKER_IMAGE_NAME = imagename
                }
                steps {
                    // Wrap the steps in a script block
                    script {
                        withCredentials([[
                            $class: 'AmazonWebServicesCredentialsBinding',
                            credentialsId: credentialsId,
                            accessKeyVariable: 'AWS_ACCESS_KEY_ID',
                            secretKeyVariable: 'AWS_SECRET_ACCESS_KEY'
                        ]]) {
                            // Wrap bat commands in a script block
                            bat "aws ecr get-login-password --region ${region} | docker login --username AWS --password-stdin ${ECR_REPO_URL}"
                            bat "docker tag ${imagename} ${ECR_REPO_URL}:latest"
                            bat "docker push ${ECR_REPO_URL}:latest"
                        }
                    }
                }
            }
        }
    }
}

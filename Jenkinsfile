pipeline {
    agent any

    tools {
        jdk 'openjdk 17'
        maven 'maven 3.9.11'
    }

    triggers {
        pollSCM('H/2 * * * *')
    }

    environment {
        IMAGE_NAME = 'taskapp'
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build and Test') {
            steps {
                sh 'mvn -B clean package'
            }
            post {
                always {
                    junit 'target/surefire-reports/*.xml'
                }
            }
        }

        stage('Docker Build') {
            steps {
                withCredentials([usernamePassword(credentialsId: 'dockerhub',
                        usernameVariable: 'DH_USER', passwordVariable: 'DH_PASS')]) {
                    sh '''
                        docker build -t "$DH_USER/$IMAGE_NAME:$BUILD_NUMBER" -t "$DH_USER/$IMAGE_NAME:latest" .
                    '''
                }
            }
        }

        stage('Docker Push') {
            steps {
                withCredentials([usernamePassword(credentialsId: 'dockerhub',
                        usernameVariable: 'DH_USER', passwordVariable: 'DH_PASS')]) {
                    sh '''
                        echo "$DH_PASS" | docker login -u "$DH_USER" --password-stdin
                        docker push "$DH_USER/$IMAGE_NAME:$BUILD_NUMBER"
                        docker push "$DH_USER/$IMAGE_NAME:latest"
                        docker logout
                    '''
                }
            }
        }
    }

    post {
        success { echo 'Pipeline termine avec succes' }
        failure { echo 'Echec du pipeline' }
    }
}

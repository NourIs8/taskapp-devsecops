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

        stage('Dependency Check') {
            steps {
                withCredentials([string(credentialsId: 'nvd-api-key', variable: 'NVD_KEY')]) {
                    sh '''
                        mkdir -p reports
                        chmod 777 reports
                        docker run --rm \
                          -v "$WORKSPACE":/src \
                          -v dc-data:/usr/share/dependency-check/data \
                          -v "$WORKSPACE/reports":/report \
                          owasp/dependency-check:latest \
                          --scan /src/target/taskapp-0.0.1-SNAPSHOT.jar \
                          --format HTML --format JSON --out /report \
                          --nvdApiKey "$NVD_KEY" \
                          --failOnCVSS 9
                    '''
                }
            }
            post {
                always {
                    archiveArtifacts artifacts: 'reports/dependency-check-report.*', allowEmptyArchive: true
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

        stage('Trivy Scan') {
            steps {
                withCredentials([usernamePassword(credentialsId: 'dockerhub',
                        usernameVariable: 'DH_USER', passwordVariable: 'DH_PASS')]) {
                    sh '''
                        mkdir -p reports
                        docker run --rm -v /var/run/docker.sock:/var/run/docker.sock -v trivy-cache:/root/.cache/ \
                          aquasec/trivy:latest image --timeout 20m --scanners vuln --skip-java-db-update \
                          --severity HIGH,CRITICAL "$DH_USER/$IMAGE_NAME:$BUILD_NUMBER" > reports/trivy-report.txt
                        docker run --rm -v /var/run/docker.sock:/var/run/docker.sock -v trivy-cache:/root/.cache/ \
                          aquasec/trivy:latest image --timeout 20m --scanners vuln --skip-java-db-update \
                          --severity CRITICAL --ignore-unfixed --exit-code 1 "$DH_USER/$IMAGE_NAME:$BUILD_NUMBER"
                    '''
                }
            }
            post {
                always {
                    archiveArtifacts artifacts: 'reports/trivy-report.txt', allowEmptyArchive: true
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

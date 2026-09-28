pipeline {
    agent any

    parameters {
        booleanParam(name: 'PUSH_IMAGES', defaultValue: false,
                     description: 'Pousser les images sur Docker Hub (credential "dockerhub-creds" requis)')
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Compile') {
            steps {
                dir('backend') {
                    sh 'mvn -B clean compile'
                }
            }
        }

        stage('Test') {
            steps {
                dir('backend') {
                    sh 'mvn -B test'
                }
            }
            post {
                always {
                    junit allowEmptyResults: true, testResults: 'backend/target/surefire-reports/*.xml'
                }
            }
        }

        stage('Package') {
            steps {
                dir('backend') {
                    sh 'mvn -B package -DskipTests'
                }
                archiveArtifacts artifacts: 'backend/target/*.jar', fingerprint: true
            }
        }

        stage('Docker Build') {
            steps {
                sh 'docker compose build'
            }
        }

        stage('Docker Push') {
            when { expression { params.PUSH_IMAGES } }
            steps {
                withCredentials([usernamePassword(credentialsId: 'dockerhub-creds',
                                                  usernameVariable: 'DH_USER',
                                                  passwordVariable: 'DH_PASS')]) {
                    sh '''
                        echo "$DH_PASS" | docker login -u "$DH_USER" --password-stdin
                        for img in backend frontend; do
                            docker tag gestion-projets-$img:latest $DH_USER/gestion-projets-$img:$BUILD_NUMBER
                            docker tag gestion-projets-$img:latest $DH_USER/gestion-projets-$img:latest
                            docker push $DH_USER/gestion-projets-$img:$BUILD_NUMBER
                            docker push $DH_USER/gestion-projets-$img:latest
                        done
                        docker logout
                    '''
                }
            }
        }

        stage('Deploy (Docker Compose)') {
            steps {
                sh 'docker compose up -d'
                sh 'docker compose ps'
            }
        }
    }
}

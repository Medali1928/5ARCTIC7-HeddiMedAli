pipeline {
    agent any

    environment {
        // Format imposé : nomprenom-classe-nomprojet (minuscules obligatoires pour Docker)
        IMAGE_BACKEND  = 'heddimedali-5arctic7-gestionprojets'
        IMAGE_FRONTEND = 'heddimedali-5arctic7-gestionprojets-frontend'
    }

    parameters {
        booleanParam(name: 'RUN_SONAR', defaultValue: true,
                     description: 'Lancer l\'analyse SonarQube (credential "sonar-token" requis)')
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

        stage('SonarQube') {
            when { expression { params.RUN_SONAR } }
            steps {
                dir('backend') {
                    withCredentials([string(credentialsId: 'sonar-token', variable: 'SONAR_TOKEN')]) {
                        sh '''
                            mvn -B sonar:sonar \
                              -Dsonar.projectKey=gestion-projets \
                              -Dsonar.projectName=gestion-projets \
                              -Dsonar.host.url=http://localhost:9000 \
                              -Dsonar.token=$SONAR_TOKEN
                        '''
                    }
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
                        # Docker exige des noms en minuscules (Medali1928 -> medali1928)
                        NS=$(echo "$DH_USER" | tr '[:upper:]' '[:lower:]')
                        docker tag gestion-projets-backend:latest  $NS/$IMAGE_BACKEND:$BUILD_NUMBER
                        docker tag gestion-projets-backend:latest  $NS/$IMAGE_BACKEND:latest
                        docker tag gestion-projets-frontend:latest $NS/$IMAGE_FRONTEND:$BUILD_NUMBER
                        docker tag gestion-projets-frontend:latest $NS/$IMAGE_FRONTEND:latest
                        docker push $NS/$IMAGE_BACKEND:$BUILD_NUMBER
                        docker push $NS/$IMAGE_BACKEND:latest
                        docker push $NS/$IMAGE_FRONTEND:$BUILD_NUMBER
                        docker push $NS/$IMAGE_FRONTEND:latest
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

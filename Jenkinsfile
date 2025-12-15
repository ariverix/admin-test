pipeline {
    agent any
    
    tools {
        maven 'Maven-3.9'
        jdk 'JDK-21'
    }
    
    stages {
        stage('Checkout') {
            steps {
                echo 'Получение кода из Git репозитория...'
                checkout scm
            }
        }
        
        stage('Build events-contract') {
            steps {
                echo 'Сборка events-contract...'
                dir('events-contract') {
                    sh 'mvn clean install -DskipTests'
                }
            }
        }
        
        stage('Build books-api-contract') {
            steps {
                echo 'Сборка books-api-contract...'
                dir('books-api-contract') {
                    sh 'mvn clean install -DskipTests'
                }
            }
        }
        
        stage('Build analytics-service') {
            steps {
                echo 'Сборка analytics-service...'
                dir('analytics-service') {
                    sh 'mvn clean package -DskipTests'
                }
            }
        }
        
        stage('Build audit-service') {
            steps {
                echo 'Сборка audit-service...'
                dir('audit-service') {
                    sh 'mvn clean package -DskipTests'
                }
            }
        }
        
        stage('Build demo-rest') {
            steps {
                echo 'Сборка demo-rest...'
                dir('demo-rest') {
                    sh 'mvn clean package -DskipTests'
                }
            }
        }
        
        stage('Build ws') {
            steps {
                echo 'Сборка ws (WebSocket service)...'
                dir('ws') {
                    sh 'mvn clean package -DskipTests'
                }
            }
        }
        
        stage('Build Docker Images') {
            steps {
                echo 'Сборка Docker образов...'
                sh 'docker-compose build'
            }
        }
    }

    post {
        success {
            echo '✅ Сборка успешно завершена!'
            echo 'Docker образы созданы.'
            echo 'Для запуска сервисов выполните: docker-compose up -d'
        }
        failure {
            echo '❌ Сборка завершилась с ошибкой'
        }
        always {
            echo 'Очистка рабочего пространства...'
            cleanWs()
        }
    }
}
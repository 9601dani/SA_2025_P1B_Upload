pipeline {
    agent any
    tools {
        maven 'Maven'
    }

    environment {
        JAVA_HOME = '/usr/lib/jvm/java-17-openjdk-amd64'
    }

    stages {
        stage('Checkout'){
            steps {
                checkout scm
            }
        }

        stage('Build Backend'){
            steps {
                dir('app-backend') {
                    sh 'mvn clean install'
                }
            }
        }

        stage('Verify Jacoco Exec') {
            steps {
                dir('app-backend/report/target') {
                    sh 'ls -l'
                }
            }
        }
    }

    post{
        success {
            script {
                jacoco (
                    execPattern: '**/target/*.exec',
                    classPattern: '**/target/*.classes',
                    sourcePttern: '**/src/main/java',
                    changeBuildStatus: true,
                    minimumLineCoverage: '85'
                )            
            }

            echo 'Backend build completed successfully'
        }

        failure{
            echo 'Backend build failed'
        }
    }
}

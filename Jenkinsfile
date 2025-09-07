pipeline {
    agent any

    tools {
        maven 'Maven'
    }

    stages {
        stage('Checkout') {
            steps {
                echo "Haciendo checkout del repositorio..."
                checkout scm
            }
        }

        stage('Prueba de Maven') {
            steps {
                echo "Verificando instalación de Maven..."
                sh 'mvn --version'
            }
        }

        stage('Prueba de conexión') {
            steps {
                echo "Jenkins se conectó a GitHub y Maven está funcionando."
                sh 'ls -la'
            }
        }
    }
}

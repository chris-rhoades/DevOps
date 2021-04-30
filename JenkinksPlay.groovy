#!/usr/bin/groovy
pipeline {
    agent any

    tools {
        Apache maven '3.6.3'
        //jdk '1.8.0'
    }
    environment {
        def NEXT_BUILD_ID = ''
        def TAG = ''
        def TAG_NEXT = ''
    }
    options {
        disableConcurrentBuilds()
        timeout(time: 30, unit: 'MINUTES')
    }

    stages {

        stage('Initialize Branch') {
            when {
                not {
                    branch 'master'
                }
            }
            steps {
                sh '''
                    echo "PATH = ${PATH}"
                    echo "M2_HOME = ${M2_HOME}"
                '''
                script {
                    echo "** branch: ${BRANCH_NAME} **"

                    TAG = "${BRANCH_NAME}.${env.BUILD_ID}"
                    echo "** TAG: ${TAG} **"

                    NEXT_BUILD_ID = Integer.parseInt(env.BUILD_ID) + 1
                    TAG_NEXT = "${BRANCH_NAME}.${NEXT_BUILD_ID}-SNAPSHOT"
                    echo "** TAG_NEXT: ${TAG_NEXT} **"
                }
            }
        }

        stage('Initialize Master') {
            when {
                branch 'master'
            }
            steps {
                sh '''
                    echo "PATH = ${PATH}"
                    echo "M2_HOME = ${M2_HOME}"
                '''
                script {
                    echo "** branch: ${BRANCH_NAME} **"

                    TAG = "3.1.${env.BUILD_ID}"
                    echo "** TAG: ${TAG} **"

                    NEXT_BUILD_ID = Integer.parseInt(env.BUILD_ID) + 1
                    TAG_NEXT = "3.1.${NEXT_BUILD_ID}-SNAPSHOT"
                    echo "** TAG_NEXT: ${TAG_NEXT} **"
                }
            }
        }

        stage('Prepare Branch') {
            when {
                not {
                    branch 'master'
                }
            }
            // don't modify the versions on the branch itself, just the tag
            steps {
                sh 'git checkout ${BRANCH_NAME}'
                sh "mvn -B clean versions:set -DgenerateBackupPoms=false -DnewVersion=${TAG}"
                sh 'git stash --all'
                sh "git tag ${TAG} refs/stash"
                sh "git push origin ${TAG}"
            }
        }

        stage('Prepare Master') {
            when {
                branch 'master'
            }
            steps {
                sh 'git checkout ${BRANCH_NAME}'
                sh "mvn -B clean versions:set -DgenerateBackupPoms=false -DnewVersion=${TAG}"
                sh 'git add .'
                sh "git commit -m 'Pipeline: Release Prepare ${TAG}'"
                sh "git tag ${TAG}"
                sh "mvn -B clean versions:set -DgenerateBackupPoms=false -DnewVersion=${TAG_NEXT}"
                sh 'git add .'
                sh "git commit -m 'Pipeline: Release Set Next Version ${TAG_NEXT}'"
                sh "git push origin ${TAG}"
                sh "git push origin ${BRANCH_NAME}"
            }
        }

        stage('Test') {
            steps {
                sh "mvn clean verify -P analysis"
                junit '**/target/surefire-reports/**/*.xml'
                recordIssues enabledForFailure: true, tool: checkStyle(), qualityGates: [[threshold: 1, type: 'TOTAL', failed: true]]
                recordIssues enabledForFailure: true, tool: spotBugs(pattern: '**/target/findbugsXml.xml'), qualityGates: [[threshold: 1, type: 'TOTAL', failed: true]]
                recordIssues enabledForFailure: true, tool: cpd(pattern: '**/target/cpd.xml'), qualityGates: [[threshold: 10, type: 'TOTAL', failed: true]]
                recordIssues enabledForFailure: true, tool: pmdParser(pattern: '**/target/pmd.xml'), qualityGates: [[threshold: 6, type: 'TOTAL', failed: true],[threshold: 1, type: 'TOTAL_HIGH', failed: true]]
                jacoco(execPattern: '**/**.exec')
            }
        }

        stage('Checkout Tag') {
            steps {
                sh "git checkout tags/${TAG}"
            }
        }

        stage('Artifactory install') {
            steps {
                script {
                    // Obtain an Artifactory server instance, defined in Jenkins --> Manage:
                    def server = Artifactory.server 'dub-artifactory'

                    def rtMaven = Artifactory.newMavenBuild()
                    rtMaven.tool = '3.3.3'
                    rtMaven.deployer releaseRepo: 'libs-release-local', snapshotRepo: 'libs-snapshot-local', server: server
                    rtMaven.resolver releaseRepo: 'libs-release', snapshotRepo: 'libs-snapshot', server: server
                    rtMaven.deployer.deployArtifacts = false // Disable artifacts deployment during Maven run

                    def buildInfo = Artifactory.newBuildInfo()

                    rtMaven.run pom: 'pom.xml', goals: 'install -DattachSource=true', buildInfo: buildInfo

                    rtMaven.deployer.deployArtifacts buildInfo
                    server.publishBuildInfo buildInfo
                }
            }
        }
    }
    post {
        failure {
            mail to: "adam.mitchell@playtech.com", bcc: '', body: "<b>${env.JOB_NAME}</b><br>Branch: ${BRANCH_NAME} <br>Build Number: ${env.BUILD_NUMBER} <br> build URL: ${env.BUILD_URL}", cc: '', charset: 'UTF-8', from: 'Corosin Jenkins <no-reply@playtech.corp>', mimeType: 'text/html', replyTo: 'no-reply@playtech.corp', subject: "Build Failed: ${env.JOB_NAME} - ${BRANCH_NAME}"
        }
        unstable {
            mail to: "glad", bcc: '', body: "<b>${env.JOB_NAME}</b><br>Branch: ${BRANCH_NAME} <br>Build Number: ${env.BUILD_NUMBER} <br> build URL: ${env.BUILD_URL}", cc: '', charset: 'UTF-8', from: 'Corosin Jenkins <no-reply@playtech.corp>', mimeType: 'text/html', replyTo: 'no-reply@playtech.corp', subject: "Build Unstable: ${env.JOB_NAME} - ${BRANCH_NAME}"
        }
        always {
            deleteDir()
        }
    }
}

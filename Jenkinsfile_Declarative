#!usr/bin/groovy
//Declarative Pipelines

pipeline {  // it starts always with pipeline directive and contains an agent directive 
    agent any  // Agent instructs Jenkins to allocate  an executor and a workspace for the pipeline, to get started i can use any agent so that Jenkins will allocate the pipeline to any available executors on the jankins master
               // Agents are also useful if you want to run the pipeline or a slave or agent Jenkis node. 
stages { //Another required directive is a stages directive, this contains sequences if one or more stages
  stage ('Build'){  // This stage directive requires a steps directive in order to be a valid declarative pipelines.
    steps{ // a step directive instructs Jenkins which command to execute, here we have our echo command
      echo 'Building stage...'
      }
    }
  stage ('Testing'){
    steps{
      echo 'Testing stage...'
      }
    }
 stage ('Deploy'){
    steps{
      echo 'Deploy stage...'
      } //close steps
    } //close stage
    
 } // close stages   
} // close pipelines

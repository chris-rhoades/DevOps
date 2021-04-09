#!/usr/bin/groovy  
//scripted Pipeline


node{ // the node allows to alocate the executer and the workspace to the pipeline, in the node could be one or more stages
  stage ('Build'){  // a stage is usually a task to be performed it should have a name in this case "Build" it will be visualized in the stage view
         echo 'This is the building section...'  // inside the stage i can have lots of commands of the task i want to execute, here we simply run the echo command
         }
  stage ('Test'){
         echo 'This is the testing section...'
         }
  stage ('Deploy'){
         echo 'This is the Deploy section...'
         }
}

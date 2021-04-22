pipeline {
  agent any
      
  
  environment { //In environment block, We define the variables which can be used later within our pipeline script.In above script, I have added our QA and CT application URLs.
    QA_SERVER = 'http://localhost:8080/'
    CT_SERVER = 'C:/Program Files/Python39/Scripts/MyWokspace/Results'

  }
  stages {
	   stage('Intialize Path') { //In above code, we just printing the system path details. just to use in case of failure and as helper details to troubleshoot.
	      steps {
	      bat 'c:'
	       bat 'echo "PATH= C:/Program Files/Python39/Scripts/MyWokspace'
	      }
	    }
	    
    
	    stage('Run Robot Framework Tests') {
	      steps {				         
		                //bat 'robot -d Results TestSuite1.robot'
				//bat 'exit 0' //In this line, We ask Jenkins pass the build.
				
/* ############## In this line, we are running robot framework lint to check for Programmatic as well as Stylistic errors on our code base########################		*/				
               
		      //bat 'python -m rflint --ignore LineTooLong ${CT_SERVER} ' 
		
/*###################### in this line, we are basically running the robot automation testcases available on folder MyWokspace and save execution results to Results folder */
		
				bat 'robot -d Results TestSuite1.robot'

/*  ############### Here We are trying to rerun the failed test cases of previous execution #############################*/
				bat 'robot -d --rerunfailed  Results/output.xml --Results Scripts/MyWokspace '   
	
 /* ############## In this line, We are merging the results of both executions and create a single report ############### */
		        	bat 'python -m robot.rebot --merge --output reports/output.xml -l reports/log.html -r Results/report.html Results/output.xml Results/output.xml' 
		     
				 
	      		}
	      post { //In this post block, We are passing the above generated execution results files to the Robot Framework Plugin.  Plugin will transform and show the results on Jenkins.
        	always {
		        script {
		          step(
			            [
			              $class              : 'RobotPublisher',
			              outputPath          : 'Results',
			              outputFileName      : '**/output.xml',
			              reportFileName      : '**/report.html',
			              logFileName         : '**/log.html',
			              disableArchiveOutput: false,
			              passThreshold       : 50,
			              unstableThreshold   : 40,
			              otherFiles          : "**/*.png,**/*.jpg",
			            ]
		          	)
		        }
	  		}		
	    }
	}    
  }
  
}

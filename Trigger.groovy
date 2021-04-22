pipeline {
  agent any
      
  
  environment { //In environment block, We define the variables which can be used later within our pipeline script.In above script, I have added our QA and CT application URLs.
    QA_SERVER = 'https://qa.application.com/'
    CT_SERVER = 'http://ct.application.com/'

  }
  stages {
	   /* stage('Intialize Path') { //In above code, we just printing the system path details. just to use in case of failure and as helper details to troubleshoot.
	      steps {
	        sh 'echo "PATH= ${C:/Program Files/Python39/Scripts/MyWokspace}'
	      }
	    }
	    */
    
	    stage('Run Robot Framework Tests') {
	      steps {		sh 'c:'
		                sh 'C:/Program Files/Python39/Scripts/MyWokspace/'
		        	sh 'python3 -m rflint --ignore LineTooLong myapp' // In this line, we are running robot framework lint to check for Programmatic as well as Stylistic errors on our code base
		        	sh 'python3 -m robot.run --NoStatusRC --variable SERVER:${CT_SERVER} --outputdir reports1 C:/Program Files/Python39/Scripts/MyWokspace/' //In this line, we are basically running the robot automation testcases available on folder  C:\Program Files\Python39\Scripts\MyWokspace and save execution results to report1
		        	sh 'robot -d Results TestSuite1.robot'
		        	sh 'python3 -m robot.run --NoStatusRC --variable SERVER:${CT_SERVER} --rerunfailed reports1/output.xml --outputdir reports C:/Program Files/Python39/Scripts/MyWokspace/' //Here We are trying to rerun the failed test cases of previous execution,
		        	sh 'python3 -m robot.rebot --merge --output reports/output.xml -l reports/log.html -r reports/report.html reports1/output.xml reports/output.xml' //In this line, We are merging the results of both executions and create a single report
		        	sh 'exit 0' //In this line, We ask Jenkins pass the build.
	      		}
	      post { //In this post block, We are passing the above generated execution results files to the Robot Framework Plugin.  Plugin will transform and show the results on Jenkins.
        	always {
		        script {
		          step(
			            [
			              $class              : 'RobotPublisher',
			              outputPath          : 'reports',
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

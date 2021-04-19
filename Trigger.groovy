agent {
      label 'qatest'
  }
  environment {
    QA_SERVER = 'https://qa.application.com/'
    CT_SERVER = 'http://ct.application.com/'

  }
  stages {
	    stage('intialize') {
	      steps {
	        sh 'echo "PATH= ${PATH}'
	      }
	    }
    
	    stage('Run Robot Tests') {
	      steps {
              
             			sh 'C:/Program Files/Python39/Scripts/MyWokspace'
		      		sh ' robot -d SSH.robot'
		        	sh 'python3 -m rflint --ignore LineTooLong myapp'
		        	sh 'python3 -m robot.run --NoStatusRC --variable SERVER:${CT_SERVER} --outputdir reports1 C:/Program Files/Python39/Scripts/MyWokspace'
		        	sh 'python3 -m robot.run --NoStatusRC --variable SERVER:${CT_SERVER} --rerunfailed reports1/output.xml --outputdir C:/Program Files/Python39/Scripts/MyWokspace'
		        	sh 'python3 -m robot.rebot --merge --output reports/output.xml -l reports/log.html -r reports/report.html reports1/output.xml reports/output.xml'
		        	sh 'exit 0'
	      		}
        
	      post {
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
  


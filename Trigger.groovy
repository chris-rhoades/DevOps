agent {
      agent any 
  }
 
  stages {
	    stage('intialize') {
	      steps {
	        sh 'echo "PATH= ${PATH}'
	    }  
	    }
    
	    stage('Run Robot Tests') {
	      steps {
              			sh 'c:'
             			sh 'C:/Program Files/Python39/Scripts/MyWokspace'
		      		sh ' robot -d SSH.robot'
		        	//sh 'python3 -m rflint --ignore LineTooLong myapp'
		        	//sh 'python3 -m robot.run --NoStatusRC --variable SERVER:${CT_SERVER} --outputdir reports1 C:/Program Files/Python39/Scripts/MyWokspace'
		        	//sh 'python3 -m robot.run --NoStatusRC --variable SERVER:${CT_SERVER} --rerunfailed reports1/output.xml --outputdir C:/Program Files/Python39/Scripts/MyWokspace'
		        	//sh 'python3 -m robot.rebot --merge --output reports/output.xml -l reports/log.html -r reports/report.html reports1/output.xml reports/output.xml'
		        	sh 'exit 0'
	      		}
         
	}    
  }
  


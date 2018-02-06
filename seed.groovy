#!groovy

// job-dsl langauge
String team= 'boise_devops_2016'
String gitUrl = "https://github.com/dcdr/$team"
String cronSchedule = 'H/2 * * * *'
services = ['hello', 'goodbye']
branches = ['master', 'qa', 'staging', 'prod']

folder "$team"

services.each {
  service = it
  currentFolder = "$team/$service"
  folder currentFolder

  branches.each {
    branch = it

    pipelineJob ("$currentFolder/$branch") {
      description("do not hand edit, built by seed.groovy")
      // when...
      triggers {
          scm cronSchedule
      }
      // what...
      scm {
        git {
          remote {
            url "$gitUrl/$service"
            branch "$branch"
          }
        }
      }
      definition {
  	     cpsScm {
    	      scm {
    		        git {
      		          remote {
        		            url "$gitUrl/$service"
        		            branch "$branch"
      		          }
    	          }
            }
            // how
            scriptPath "Jenkinsfile_$branch"
        }
      }
    }
  }

  // a benchtest for each repo...
  multibranchPipelineJob ("$team/$service/benchtest") {
    description("Benchtest pipelines. Push commit/branch/tag to git push <branchname>:refs/heads/benchtest/<branchname>, then the Jenkinsfile pipeline will execute" )
    branchSources {
        git {
          remote "$gitUrl/$service"
          includes "benchtest/**"
        }
    }
    triggers {
      cron cronSchedule
    }
    orphanedItemStrategy {
        discardOldItems {
            numToKeep 20
        }
    }
  }
  // a release area for each repo...
  multibranchPipelineJob("$team/$service/releases") {
    description "Releease pipelines. Push commit/branch/tag to git push <branchname>:refs/heads/releases/<branchname>, then the Jenkinsfile pipeline will execute"
    branchSources {
        git {
          remote "$gitUrl/$service"
          includes "releases/**"
        }
    }
    triggers {
      cron cronSchedule
    }
  }
}

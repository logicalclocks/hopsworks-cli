# Hopsworks CLI

A command-line client, written in Java, that uses the Hopsworks REST API for Job execution and file uploading.

## Maven dependency

    <dependency>
      <groupId>io.hops</groupId>
      <artifactId>hopsworks-cli</artifactId>
      <version>2.2.1</version>
    </dependency>

## Maven deployment

The repository used is on Archiva `https://archiva.hops.works/` and published using `mvn deploy`. Credentials for the archiva repository need to be added in the `settings.xml` file for maven.

    <servers>
        <server>
          <id>repository</id>
          <username>user</username>
          <password>pass</password>
        </server>
      </servers>

## Configuration

You can either edit the conf/hops.properties file or set environment variables:
HOPSWORKS_USER
HOPSWORKS_PASSWORD
HOPSWORKS_ENDPOINT=http://<hostname>:<port>/hopsworks
HOPSWORKS_PROJECT=projectname

## Command-line Utility

For help on using the command-line utility:
./hops-cli --help


## Usage as a Library

The output library is a shaded jar file. You can include it




## Running a job with hopsworks-cli

You need to follow a number of steps to run a job. First, you need to get the projectId from the project name, then copy your .jar or .py file into the project, then create a Job configuration and create the Job (if it doesn't already exist) in Hopsworks, then run the job, and finally download the job logs.



1. ProjectService.   @Path("/getProjectInfo/{projectName}")
 name -> project_id 
Use ProjectService REST API

2. Copy filename to the  <endpoint>/hopsworks-api/api/<id>/dataset/upload/<filename>
Use DatasetService REST API

3. Run Job with JobServer REST API
Return application_attempt_id and jobId

4. Download Logs with application_attempt_id


# Acknowledgement

This work was developed as part of the Streamline EU Project and it builds on earlier work done in the Aegis project: https://github.com/aegisbigdata/hopsworks-data-connector
https://www.aegis-bigdata.eu/


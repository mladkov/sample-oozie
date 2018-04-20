# Sample Oozie Jobs

Sample oozie project directory showing various samples of oozie workflows
and project design.

This project shows a number of samples such as:

- Spark actions (Spark 1.6)
- DistCp action
- HiveServer2 action
- Shell action

Another important item we show here is dealing with YARN resource pools, and
setting in one place where every action/task/job is launched in the same
YARN resource pool.

We also have this project enabled for Kerberos environment, together with
TLS enabled for HiveServer2.

# Deployment

Create the location in HDFS where you want this project to reside.

```sh
hdfs dfs -mkdir -p /proj/sample-oozie
```

Go into the project directory on the local filesystem and upload all the
files.

```sh
cd ~/sample-oozie;
hdfs dfs -rm -r -f -skipTrash /proj/sample-oozie/*;hdfs dfs -put * /proj/sample-oozie
```

Create the database for the sample job

```sh
beeline -u 'jdbc:hive2://mladen-secure-kudu-10.gce.cloudera.com:10000/;principal=hive/mladen-secure-kudu-10.gce.cloudera.com@AD.SEC.CLOUDERA.COM' -d org.apache.hive.jdbc.HiveDriver

create database sample;
```

Schedule the oozie job.

```sh
export OOZIE_URL=http://mladen-secure-kudu-10.gce.cloudera.com:11000/oozie
oozie job -run -config ~/sample-oozie/conf/sample-oozie-coord.properties
```

# Useful oozie CLI commands

```sh

# Run your job
oozie job -run -config conf/sample-oozie-coord.properties

# Info about your job
oozie job -info 0049130-161020154537822-oozie-oozi-C

# See running coordinators
oozie jobs -jobtype=COORDINATOR -filter status=RUNNING

# See running workflows
oozie jobs -jobtype=WF -filter status=RUNNING

# Oozie log for given coordinator or workflow
oozie job -log 0049132-161020154618986-oozie-oozi-W

# YARN application log fetch
# If you workflow had Ext ID job_1477330333470_0427 then replace 'job' with
# 'application'
yarn logs -applicationId application_1477330333470_0427

# Re-run a workflow in a coordinator. Running the -info option on coord shows:
# 0049130-161020154537822-oozie-oozi-C@1
# 0049130-161020154537822-oozie-oozi-C@2
# ...
# The number after the '@' is the action number
oozie job -rerun 0049130-161020154537822-oozie-oozi-C -action 1

# See config content from your properties file for given job
oozie job -configcontent 0049130-161020154537822-oozie-oozi-C

# See the workflow xml file for the job
oozie job -definition 0049130-161020154537822-oozie-oozi-C
oozie job -definition 0049132-161020154618986-oozie-oozi-W

```

# Important notes

See http://blog.cloudera.com/blog/2014/05/how-to-use-the-sharelib-in-apache-oozie-cdh-5/

Including jars in your workflow is often a source of confusion, so
re-iterating the learnings here:

1. Set `oozie.libpath=/path/to/jars,another/path/to/jars` in `job.properties`.

    This is useful if you have many workflows that all need the same jar;
    you can put it in one place in HDFS and use it with many workflows. The
    jars will be available to all actions in that workflow.

    There is no need to ever point this at the ShareLib location. (I see
    that in a lot of workflows.) Oozie knows where the ShareLib is and will
    include it automatically if you set `oozie.use.system.libpath=true` in
    job.properties.

2. Create a directory named `lib` next to your `workflow.xml` in HDFS and put
   jars in there.

    This is useful if you have some jars that you only need for one
    workflow. Oozie will automatically make those jars available to all
    actions in that workflow.

3. Specify the `<archive>` tag in an action with the path to a single jar;
   you can have multiple `<archive>` tags.

    This is useful if you want some jars only for a specific action and not
    all actions in a workflow.

    The downside is that you have to specify them in your `workflow.xml`, so
    if you ever need to add/remove some jars, you have to change your
    `workflow.xml`.

4. Add jars to the ShareLib
   (e.g. `/user/oozie/share/lib/lib_<timestamp>/pig`)

    While this will work, it’s not recommended for two reasons:

    The additional jars will be included with every workflow using that
    ShareLib, which may be unexpected to those workflows and users.  When
    upgrading the ShareLib, you’ll have to recopy the additional jars to the
    new ShareLib.

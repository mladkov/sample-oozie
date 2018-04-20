drop table if exists sample.oozie_t1_del;
drop table if exists sample.oozie_t1;
drop table if exists sample.oozie_t1_spark;

create table sample.oozie_t1_del
    (
        c1 int,
        c2 int,
        c3 int
    )
    row format delimited
    fields terminated by '|'
    stored as textfile
    tblproperties('serialization.null.format' = '')
    ;

create table sample.oozie_t1
    (
        c1 int,
        c2 int,
        c3 int
    )
    stored as parquet
    ;

create external table sample.oozie_t1_spark
    (
        c1 int,
        c2 int,
        c3 int
    )
    stored as parquet
    ;

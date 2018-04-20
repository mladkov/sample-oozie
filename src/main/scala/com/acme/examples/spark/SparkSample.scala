package com.acme.examples.spark

import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.sql.types._
import org.apache.spark.sql.{DataFrame, Row, SQLContext}
import scala.collection.JavaConverters._
import org.apache.spark.sql.functions._

/**
  * Created by Mladen Kovacevic on 04/20/18.
  */
object SparkSample {

  def main(args: Array[String]): Unit = {
    // Setup Spark configuration and related contexts
    val sparkConf = new SparkConf().
      setAppName("Example Spark Job")

    // Create a Spark and SQL context
    val sc = new SparkContext(sparkConf)
    val sqlContext = new SQLContext(sc)

    // Input and output paths supplied by command line
    val Array(inPath, outPath) = args

    println("In : " + inPath)
    println("Out: " + outPath)

    // Read in the table
    val inDF = sqlContext.read.parquet(inPath)

    // Register it as a Spark SQL table
    inDF.registerTempTable("in_table")

    // Apply Spark SQL statement
    val modDF = sqlContext.sql("SELECT (c1+100) as c1, c2, c3 from in_table")

    // Write out our content
    modDF.write.parquet(outPath)
  }

}

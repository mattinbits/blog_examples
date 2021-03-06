package com.mjlivesey.examples

import org.apache.spark.sql.functions._
import org.apache.spark.sql.SparkSession

object StructuredStreaming extends App {

  /*
    This is Spark 2.0, so  construct a
    SparkSession rather than Context.
   */
  val spark = SparkSession
    .builder
    .master("local[*]")
    .appName("StructuredNetworkWordCount")
    .getOrCreate()

  import spark.implicits._
  val lines = spark.readStream
    .format("socket")
    .option("host", "localhost")
    .option("port", 9999)
    .load()

  // Split the lines into words
  val words = lines.as[String].flatMap(_.split(" "))
  words.explain(true)
  // Generate running word count
  val wordCounts = words.groupBy($"value").count()
  val query = wordCounts.writeStream
    .outputMode("complete")
    .format("console")
    .start()

  query.awaitTermination()
}

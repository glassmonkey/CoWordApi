/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

// scalastyle:off println
package jp.excite.news

//import java.util.HashMap
import java.util.regex.{Matcher, Pattern}
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerConfig, ProducerRecord}

import org.apache.spark.SparkConf
import org.apache.spark.SparkContext._
import org.apache.spark.SparkContext
import org.apache.spark.streaming._
import org.apache.spark.streaming.kafka._
import org.atilika.kuromoji.Tokenizer
import org.atilika.kuromoji.Token

/**
 * Consumes messages from one or more topics in Kafka and does wordcount.
 * Usage: KafkaWordCount <zkQuorum> <group> <topics> <numThreads>
 *   <zkQuorum> is a list of one or more zookeeper servers that make quorum
 *   <group> is the name of kafka consumer group
 *   <topics> is a list of one or more kafka topics to consume from
 *   <numThreads> is the number of threads the kafka consumer should use
 *
 * Example:
 *    `$ bin/run-example \
 *      org.apache.spark.examples.streaming.KafkaWordCount zoo01,zoo02,zoo03 \
 *      my-consumer-group topic1,topic2 1`
 */
object WordCountApp{
  def main(args: Array[String]) {
    //StreamingExamples.setStreamingLogLevels()

    //val Array(zkQuorum, group, topics, numThreads) = args
    var zkQuorum = "localhost:2181"
    var group = "sentence_group"
    var topics = "topic1";
    var numThreads = "1";
    val sparkConf = new SparkConf().setMaster("local[4]").setAppName("WordCount App")
    val tokenizer = Tokenizer.builder.mode(Tokenizer.Mode.NORMAL).build()
    //val sc = new SparkContext(sparkConf)
    val ssc = new StreamingContext(sparkConf, Seconds(2))
    ssc.checkpoint("checkpoint")

    val topicMap = topics.split(",").map((_, numThreads.toInt)).toMap
    val lines = KafkaUtils.createStream(ssc, zkQuorum, group, topicMap).map(_._2)
    //var lines = sc.textFile("README.md")

   val words = lines.flatMap(line => {
   val tokenizer : Tokenizer = Tokenizer.builder().build()  // kuromojiの分析器
   val features : scala.collection.mutable.ArrayBuffer[String] = new collection.mutable.ArrayBuffer[String]() //解析結果を保持するための入れ物
   var tweetText : String = line

   val japanese_pattern : Pattern = Pattern.compile("[¥¥u3040-¥¥u309F]+") //「ひらがなが含まれているか？」の正規表現
   if(japanese_pattern.matcher(tweetText).find()) {  // ひらがなが含まれているツイートのみ処理
     // 不要な文字列の削除
     tweetText = tweetText.replaceAll("http(s*)://(.*)/", "").replaceAll("¥¥uff57", "") // 全角の「ｗ」は邪魔www

     // ツイート本文の解析
     val tokens : java.util.List[Token] = tokenizer.tokenize(tweetText) // 形態素解析
     val pattern : Pattern = Pattern.compile("^[a-zA-Z]+$|^[0-9]+$") //「英数字か？」の正規表現
     for(index <- 0 to tokens.size()-1) { //各形態素に対して。。。
       val token = tokens.get(index)
       val matcher : Matcher = pattern.matcher(token.getSurfaceForm())
       // 文字数が3文字以上で、かつ、英数字のみではない単語を検索
       if (tokens.get(index).getAllFeaturesArray()(0) == "名詞" && (tokens.get(index).getAllFeaturesArray()(1) == "一般" || tokens.get(index).getAllFeaturesArray()(1) == "固有名詞")) {
                features += tokens.get(index).getSurfaceForm
        }
     }
   }
   (features)
})

    val wordCounts = words.map(x => (x, 1L)).reduceByKeyAndWindow(_ + _, _ - _, Minutes(10), Seconds(2), 2)
    //wordCounts.collect.foreach(println)
    wordsCounts.foreachRDD(rdd =>


    )
    wordCounts.print()

    ssc.start()
    ssc.awaitTermination()
  }
}

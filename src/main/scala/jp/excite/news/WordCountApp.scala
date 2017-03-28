package jp.excite.news

import java.util.regex.{Matcher, Pattern}
import scala.collection.convert.WrapAsScala._
import org.apache.spark.SparkConf
import org.apache.spark.SparkContext._
import org.apache.spark.SparkContext
import org.atilika.kuromoji.Tokenizer
import org.atilika.kuromoji.Token

object WordCountApp{
  def main(args: Array[String]) {
    val sparkConf = new SparkConf().setMaster("local[4]").setAppName("WordCount App")
    val tokenizer = Tokenizer.builder.mode(Tokenizer.Mode.NORMAL).build()
    val sc = new SparkContext(sparkConf)
    val input = sc.textFile("input.txt").flatMap(text => {
        val tokens : java.util.List[Token] = Tokenizer.builder().build().tokenize(text)
        val output : scala.collection.mutable.ArrayBuffer[String] = new collection.mutable.ArrayBuffer[String]()
        tokens.foreach(token => {
        if(token.getAllFeatures().indexOf("名詞") != -1) {
            output += token.getSurfaceForm()
        }})
        output// return
    })
    val wordCounts = input.map(x => (x, 1L)).reduceByKey((x, y)=> x + y)
    val output = wordCounts.map( x => (x._2, x._1)).sortByKey(false).saveAsTextFile("ouput")
    //.take(10).foreach(x=>println(x))
  }
}

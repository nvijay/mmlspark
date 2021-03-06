// Copyright (C) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License. See LICENSE in project root for information.

package com.microsoft.ml.spark

import org.apache.spark.sql.{DataFrame, Dataset, Row}
import org.apache.spark.ml.Transformer
import org.apache.spark.ml.param._
import org.apache.spark.ml.util.{DefaultParamsReadable, Identifiable}
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.types._

object Repartition extends DefaultParamsReadable[Repartition]

class Repartition(val uid: String) extends Transformer with MMLParams {
  def this() = this(Identifiable.randomUID("Repartition"))

  val n: IntParam = IntParam(this, "n", "Number of partitions",
    validation = ParamValidators.gt[Int](0))

  final def getN: Int = $(n)

  def setN(value: Int): this.type = set(n,value)

  override def transform(dataset: Dataset[_]): DataFrame = {

    if (getN < dataset.rdd.getNumPartitions){
      dataset.coalesce(getN).toDF()
    }else{
      dataset.sqlContext.createDataFrame(
        dataset.rdd.repartition(getN).asInstanceOf[RDD[Row]],
        dataset.schema)
    }
  }

  def transformSchema(schema: StructType): StructType = {
    schema
  }

  def copy(extra: ParamMap): this.type = defaultCopy(extra)

}

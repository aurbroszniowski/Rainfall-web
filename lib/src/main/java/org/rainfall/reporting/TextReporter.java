/*
 * Copyright 2014 Aurélien Broszniowski
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.rainfall.reporting;

import org.rainfall.Reporter;
import org.rainfall.statistics.Statistics;
import org.rainfall.statistics.StatisticsHolder;

/**
 * report the statistics to the text console
 *
 * @author Aurelien Broszniowski
 */

public class TextReporter<K extends Enum<K>> implements Reporter<K> {

  @Override
  public void report(final StatisticsHolder<K> holder) {
    StringBuilder sb = new StringBuilder();
    Long timestamp = holder.getTimestamp();
    sb.append(timestamp).append(" \t\t ");
    sb.append("KEY \t counter \t minLatency \t maxLatency \t averageLatencyInMs ");
    sb.append(System.getProperty("line.separator"));
    Statistics<K> statistics = holder.getStatistics();
    sb.append("Total operations: ").append(statistics.sumOfCounters()).append(" ops \t");
    sb.append("Average Latency: ").append(String.format("%.2f", statistics.averageLatencyInMs())).append("ms \t");
    sb.append("Average TPS: ").append(statistics.averageTps());
    sb.append(System.getProperty("line.separator"));
    K[] results = statistics.getKeys();
    for (K result : results) {
      sb.append(result).append(" \t\t ");
      sb.append("Number of operations: ").append(statistics.getCounter(result)).append(" ops \t");
      sb.append("Average Latency: ").append(String.format("%.2f", statistics.getLatency(result))).append("ms \t");
      sb.append("TPS: ").append(statistics.getTps(result));
      sb.append(System.getProperty("line.separator"));
    }
    sb.append("--------------------------------------------------------------------------------------------");
    sb.append(System.getProperty("line.separator"));
    System.out.println(sb.toString());
  }
}

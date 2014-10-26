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

package org.rainfall.statistics;

/**
 * @author Aurelien Broszniowski
 */

public class StatisticsHolder<K extends Enum<K>> {

  private Long timestamp;
  private Statistics<K> statistics;

  public StatisticsHolder(final Long timestamp, final Statistics<K> statistics) {
    this.timestamp = timestamp;
    this.statistics = statistics;
  }

  public Long getTimestamp() {
    return timestamp;
  }

  public Statistics<K> getStatistics() {
    return statistics;
  }
}

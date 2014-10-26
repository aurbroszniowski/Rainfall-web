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

package org.rainfall.configuration;

import org.rainfall.AssertionEvaluator;
import org.rainfall.Configuration;
import org.rainfall.Execution;
import org.rainfall.Scenario;
import org.rainfall.TestException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * @author Aurelien Broszniowski
 */

public class ConcurrencyConfig extends Configuration {

  private int nbThreads = 1;
  private final Map<Integer, AtomicInteger> nbIterationsPerThread = new HashMap<Integer, AtomicInteger>();
  private long timeoutInSeconds = 600L;

  public static ConcurrencyConfig concurrencyConfig() {
    return new ConcurrencyConfig();
  }

  public ConcurrencyConfig threads(final int nbThreads) {
    this.nbThreads = nbThreads;
    return this;
  }

  public ConcurrencyConfig timeout(final int nb, final TimeUnit unit) {
    this.timeoutInSeconds = unit.toSeconds(nb);
    return this;
  }

  public int getNbThreads() {
    return nbThreads;
  }

  public long getTimeoutInSeconds() {
    return timeoutInSeconds;
  }

  public int getNbIterationsForThread(int threadNb, long nbIterations) {
    synchronized (nbIterationsPerThread) {
      if (nbIterationsPerThread.size() == 0) {
        for (int i = 0; i < nbThreads; i++)
          nbIterationsPerThread.put(i, new AtomicInteger());
        int i = 0;
        while (nbIterations > 0) {
          nbIterationsPerThread.get(i % nbThreads).incrementAndGet();
          i++;
          nbIterations--;
        }
      }
    }
    return nbIterationsPerThread.get(threadNb).intValue();
  }

  public void submit(final List<Execution> executions, final Scenario scenario, final Map<Class<? extends Configuration>, Configuration> configurations, final List<AssertionEvaluator> assertions) throws TestException {
    for (final Execution execution : executions) {
      execution.execute(scenario, configurations, assertions);
    }
  }
}

/*
 * Copyright (c) 2014-2019 Aurélien Broszniowski
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

package io.rainfall.web;

import io.rainfall.Runner;
import io.rainfall.Scenario;
import io.rainfall.SyntaxException;
import io.rainfall.configuration.ConcurrencyConfig;
import io.rainfall.configuration.ReportingConfig;
import io.rainfall.statistics.StatisticsPeekHolder;
import io.rainfall.utils.SystemTest;
import io.rainfall.web.configuration.HttpConfig;
import io.rainfall.web.statistics.HttpResult;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static io.rainfall.configuration.ReportingConfig.hlog;
import static io.rainfall.configuration.ReportingConfig.text;
import static io.rainfall.execution.Executions.during;
import static io.rainfall.execution.Executions.inParallel;
import static io.rainfall.execution.Executions.nothingFor;
import static io.rainfall.execution.Executions.once;
import static io.rainfall.unit.Every.every;
import static io.rainfall.unit.Instance.instances;
import static io.rainfall.unit.Over.over;
import static io.rainfall.unit.TimeDivision.seconds;
import static java.util.concurrent.TimeUnit.MINUTES;

/**
 * @author Aurelien Broszniowski
 */

@Category(SystemTest.class)
public class BasicTest {

  @Test
  public void testBasic() throws SyntaxException {
    HttpConfig httpConf = HttpConfig.httpConfig()
        .baseURL("https://www.google.com");
    ConcurrencyConfig concurrency = ConcurrencyConfig.concurrencyConfig()
        .threads(8).timeout(5, MINUTES);
    ReportingConfig reporting = ReportingConfig.report(HttpResult.class)
        .log(text(), hlog("rainfall-web-example"));

    Scenario scenario = Scenario.scenario("Google search")
        .exec(WebOperations.http("Search Java").get("/?#q=Java").queryParam("q", "Java"));

    StatisticsPeekHolder finalStats = Runner.setUp(scenario)
        .executed(once(5, instances), nothingFor(5, seconds), once(5, instances),
            inParallel(5, instances, every(1, seconds), over(10, seconds)),
            inParallel(5, instances, every(2, seconds), over(10, seconds)),
            during(10, seconds))
        .config(httpConf, concurrency, reporting)
        .assertion(WebAssertions.responseTime(), WebAssertions.isLessThan(1, seconds))
        .start();
  }

}

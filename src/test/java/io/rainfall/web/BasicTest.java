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

package io.rainfall.web;

import io.rainfall.Runner;
import io.rainfall.Scenario;
import io.rainfall.SyntaxException;
import io.rainfall.Unit;
import io.rainfall.configuration.ConcurrencyConfig;
import io.rainfall.configuration.ReportingConfig;
import io.rainfall.statistics.StatisticsPeekHolder;
import io.rainfall.unit.During;
import io.rainfall.unit.Every;
import io.rainfall.utils.SystemTest;
import io.rainfall.web.configuration.HttpConfig;
import io.rainfall.web.statistics.HttpResult;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static io.rainfall.configuration.ReportingConfig.html;
import static io.rainfall.configuration.ReportingConfig.text;
import static io.rainfall.execution.Executions.atOnce;
import static io.rainfall.execution.Executions.constantUsersPerSec;
import static io.rainfall.execution.Executions.during;
import static io.rainfall.execution.Executions.inParallel;
import static io.rainfall.execution.Executions.nothingFor;
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
        .threads(4).timeout(5, MINUTES);
    ReportingConfig reporting = ReportingConfig.report(HttpResult.class)
        .log(text(), html())
        .summary(text(), html());

    Scenario scenario = Scenario.scenario("Google search")
        .exec(WebOperations.http("Search Crocro").get("/?").queryParam("q", "Crocro"))
        .exec(WebOperations.http("Search Java").get("/?#q=Java").queryParam("q", "Java"));

      StatisticsPeekHolder finalStats = Runner.setUp(scenario)
        .executed(atOnce(5, Unit.users), nothingFor(5, seconds), atOnce(5, Unit.users),
            constantUsersPerSec(5, During.during(10, seconds)),
            inParallel(5, Unit.users, Every.every(2, seconds), During.during(10, seconds)),
            during(10, seconds))
        .config(httpConf, concurrency, reporting)
        .assertion(WebAssertions.responseTime(), WebAssertions.isLessThan(1, seconds))
        .start();
  }

}

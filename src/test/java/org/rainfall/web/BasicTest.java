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

package org.rainfall.web;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.rainfall.Runner;
import org.rainfall.Scenario;
import org.rainfall.SyntaxException;
import org.rainfall.configuration.ConcurrencyConfig;
import org.rainfall.configuration.ReportingConfig;
import org.rainfall.utils.SystemTest;
import org.rainfall.web.configuration.HttpConfig;
import org.rainfall.web.statistics.HttpResult;

import static java.util.concurrent.TimeUnit.MINUTES;
import static org.rainfall.Unit.users;
import static org.rainfall.execution.Executions.atOnce;
import static org.rainfall.execution.Executions.constantUsersPerSec;
import static org.rainfall.execution.Executions.inParallel;
import static org.rainfall.execution.Executions.nothingFor;
import static org.rainfall.unit.TimeDivision.seconds;
import static org.rainfall.unit.During.during;
import static org.rainfall.unit.Every.every;
import static org.rainfall.web.WebAssertions.isLessThan;
import static org.rainfall.web.WebAssertions.responseTime;
import static org.rainfall.web.WebOperations.http;
import static org.rainfall.web.configuration.HttpConfig.httpConfig;

/**
 * @author Aurelien Broszniowski
 */

@Category(SystemTest.class)
public class BasicTest {

  @Test
  public void testBasic() throws SyntaxException {
    HttpConfig httpConf = httpConfig()
        .baseURL("https://www.google.fr");
    ConcurrencyConfig concurrency = ConcurrencyConfig.concurrencyConfig()
        .threads(4).timeout(5, MINUTES);
    ReportingConfig reporting = ReportingConfig.reportingConfig(ReportingConfig.text(), ReportingConfig.html());

    Scenario scenario = Scenario.scenario("Google search")
        .exec(http("Recherche Crocro").get("/?").queryParam("q", "Crocro"))
        .exec(http("Recherche Gatling").post("/?#q=Gatling").queryParam("q", "Gatling"))
        .exec(http("Recherche Java").get("/?#q=Java").queryParam("q", "Java"));

    Runner.setUp(scenario)
        .executed(atOnce(5, users), nothingFor(5, seconds), atOnce(5, users),
            constantUsersPerSec(5, during(10, seconds)),
            inParallel(5, users, every(2, seconds), during(10, seconds)))
        .config(httpConf, concurrency, reporting)
        .assertion(responseTime(), isLessThan(1, seconds))
        .start();

/*
    HttpConfig httpConf = HttpConfig.httpConfig
        .baseURL("http://search.twitter.com")
        .acceptHeader("* /*")  pas d'espace!!!
        .acceptCharsetHeader("ISO-8859-1,utf-8;q=0.7,*;q=0.3")
        .acceptEncodingHeader("gzip,deflate,sdch")
        .acceptLanguageHeader("en-US,en;q=0.8")
        .connection("keep-alive")
        .userAgentHeader("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.22 (KHTML, like Gecko) Ubuntu/12.10 Chromium/25.0.1364.172 Chrome/25.0.1364.172 Safari/537.22")

    Scenario scn = Scenario.scenario("Recherches sur Twitter")
        .exec(http("Recherche JCertif").get("/search.json?q=jcertif"))
        .exec(http("Recherche Gatling").get("/search.json?q=gatling"))
        .exec(http("Recherche Nantes").get("/search.json?q=nantes"))
        .exec(http("Recherche Scala").get("/search.json?q=scala"))

    scn.beExecuted(
        GatlingOperations.atOnce(10, Unit.users), GatlingOperations.nothingFor(10, Unit.seconds),
        GatlingOperations.atOnce(10, Unit.users))


        setUp(
        // operations
            scn.beExecuted(
            GatlingOperations.nothingFor(4, Unit.seconds), GatlingOperations.atOnce(10, Unit.users), ramp(10, users), over(5, seconds),
            constantRate(20, usersPerSec), during(15, seconds),
            rampRate(10, usersPerSec), to(20, usersPerSec), during(10.minutes),
            split(1000, users).into(ramp(10, users), over(10, seconds)).separatedBy(10, seconds),
            split(1000, users).into(ramp(10, users)over(10, seconds)).separatedBy(atOnce(30, users))))
        // configuration
        .protocols(httpConf)
        //assertions
        .assertions(global.responseTime.max.lessThan(1000));
*/
  }

}

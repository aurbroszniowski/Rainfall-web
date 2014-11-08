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
import io.rainfall.unit.During;
import io.rainfall.unit.Every;
import io.rainfall.utils.SystemTest;
import io.rainfall.web.configuration.HttpConfig;
import org.junit.Test;
import org.junit.experimental.categories.Category;

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
        .baseURL("https://www.google.fr");
    ConcurrencyConfig concurrency = ConcurrencyConfig.concurrencyConfig()
        .threads(4).timeout(5, MINUTES);
    ReportingConfig reporting = ReportingConfig.reportingConfig(ReportingConfig.text(), ReportingConfig.html());

    Scenario scenario = Scenario.scenario("Google search")
        .exec(WebOperations.http("Recherche Crocro").get("/?").queryParam("q", "Crocro"))
        .exec(WebOperations.http("Recherche Gatling").post("/?#q=Gatling").queryParam("q", "Gatling"))
        .exec(WebOperations.http("Recherche Java").get("/?#q=Java").queryParam("q", "Java"));

    Runner.setUp(scenario)
        .executed(atOnce(5, Unit.users), nothingFor(5, seconds), atOnce(5, Unit.users),
            constantUsersPerSec(5, During.during(10, seconds)),
            inParallel(5, Unit.users, Every.every(2, seconds), During.during(10, seconds))
            , during(10, seconds))
        .config(httpConf, concurrency, reporting)
        .assertion(WebAssertions.responseTime(), WebAssertions.isLessThan(1, seconds))
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

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

package io.rainfall.web.operation;

import io.rainfall.AssertionEvaluator;
import io.rainfall.Configuration;
import io.rainfall.Operation;
import io.rainfall.TestException;
import io.rainfall.statistics.StatisticsHolder;
import io.rainfall.web.configuration.HttpConfig;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static io.rainfall.web.statistics.HttpResult.EXCEPTION;
import static io.rainfall.web.statistics.HttpResult.KO;
import static io.rainfall.web.statistics.HttpResult.OK;

/**
 * @author Aurelien Broszniowski
 */

public class HttpOperation extends Operation {
  private String description;
  private String path = null;
  private HttpRequest operation;
  private List<NameValuePair> queryParams = new ArrayList<NameValuePair>();

  public HttpOperation(String description) {
    this.description = description;
  }

  public HttpOperation get(String path) {
    this.path = path;
    this.operation = HttpRequest.GET;
    return this;
  }

  public HttpOperation post(String path) {
    this.path = path;
    this.operation = HttpRequest.POST;
    return this;
  }

  public HttpOperation queryParam(String key, String value) {
    queryParams.add(new BasicNameValuePair(key, value));
    return this;
  }

  @Override
  public void exec(final StatisticsHolder statisticsHolder, final Map<Class<? extends Configuration>,
      Configuration> configurations, final List<AssertionEvaluator> assertions) throws TestException {
    String url = null;
    HttpConfig httpConfig = (HttpConfig)configurations.get(HttpConfig.class);
    if (httpConfig != null) {
      url = httpConfig.getUrl();
    }
    if (url == null) {
      throw new TestException("baseURL of io.rainfall.web.HttpConfig is missing");
    }
    final HttpClient client = HttpClientBuilder.create().build();

    if (path != null) {
      url += path;
    }

    long start = getTimeInNs();
    try {
      HttpResponse response = client.execute(httpRequest(url));
      long end = getTimeInNs();
      if (response.getStatusLine().getStatusCode() == 200)
        statisticsHolder.record("http", (end - start), OK);
      else
        statisticsHolder.record("http", (end - start), KO);
    } catch (IOException e) {
      long end = getTimeInNs();
      statisticsHolder.record("http", (end - start), EXCEPTION);
    }

    //TODO : evaluate assertions
  }

  @Override
  public List<String> getDescription() {
    return Arrays.asList("http operation (TODO: all details)");
  }

  private HttpRequestBase httpRequest(final String finalUrl) {
    try {
      if (HttpRequest.GET.equals(this.operation)) {
        return new HttpGet(new URIBuilder(finalUrl).setParameters(this.queryParams).build());
      } else if (HttpRequest.POST.equals(this.operation)) {
        return new HttpPost(new URIBuilder(finalUrl).setParameters(this.queryParams).build());
      }
    } catch (URISyntaxException e) {
      return null;
    }
    return null;
  }
}

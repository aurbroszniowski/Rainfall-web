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

package org.rainfall.utils;

import org.hamcrest.core.IsNull;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;

/**
 * @author Aurelien Broszniowski
 */

public class RangeMapTest {

  @Test
  public void testMultipleWeights() {
    RangeMap<String > map = new RangeMap<String >();
    map.put(0.20, "Value1");
    map.put(0.30, "Value2");
    map.put(0.20, "Value3");

    assertThat("0.10 belongs to range [0, 0.20[, it should return the first value.", map.get(0.10), is(equalTo("Value1")));
    assertThat("0.20 belongs to range [0.20, 0.50[, it should return the second value.", map.get(0.20), is(equalTo("Value2")));
    assertThat("0.50 belongs to range [0.50, 0.70[, it should return the third value.", map.get(0.50), is(equalTo("Value3")));
    assertThat("0.70 belongs to range [0.70, 1.00], it should return no value.", map.get(0.70), is(equalTo(null)));
    assertThat("1.00 belongs to range [0.70, 1.00], it should return no value.", map.get(1.00), is(equalTo(null)));
  }
}

/*
 * Copyright © 2016 Google Inc.
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
 *
 */

package com.g11x.checklistapp.data;

import java.util.List;

public class Checklist {

  private final List<ChecklistItem> items;

  private Checklist(List<ChecklistItem> items) {
    this.items = items;
  }

  public static Checklist of(List<ChecklistItem> items) {
    return new Checklist(items);
  }

  public List<ChecklistItem> getItems() {
    return items;
  }
}

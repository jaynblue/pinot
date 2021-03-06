/**
 * Copyright (C) 2014-2016 LinkedIn Corp. (pinot-core@linkedin.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.linkedin.pinot.common.config;

import com.linkedin.pinot.common.data.StarTreeIndexSpec;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@JsonIgnoreProperties(ignoreUnknown = true)
public class IndexingConfig {
  private static final Logger LOGGER = LoggerFactory.getLogger(IndexingConfig.class);

  private List<String> _invertedIndexColumns;
  private boolean _autoGeneratedInvertedIndex;
  private List<String> _sortedColumn = new ArrayList<>();
  private String _loadMode;
  private Map<String, String> _streamConfigs = new HashMap<>();
  private String _segmentFormatVersion;
  private String _columnMinMaxValueGeneratorMode;
  private List<String> _noDictionaryColumns;
  private List<String> _onHeapDictionaryColumns;
  private StarTreeIndexSpec _starTreeIndexSpec;
  private SegmentPartitionConfig _segmentPartitionConfig;

  public List<String> getInvertedIndexColumns() {
    return _invertedIndexColumns;
  }

  public void setInvertedIndexColumns(List<String> invertedIndexColumns) {
    _invertedIndexColumns = invertedIndexColumns;
  }

  public boolean isAutoGeneratedInvertedIndex() {
    return _autoGeneratedInvertedIndex;
  }

  public void setAutoGeneratedInvertedIndex(boolean autoGeneratedInvertedIndex) {
    _autoGeneratedInvertedIndex = autoGeneratedInvertedIndex;
  }

  public List<String> getSortedColumn() {
    return _sortedColumn;
  }

  public void setSortedColumn(List<String> sortedColumn) {
    _sortedColumn = sortedColumn;
  }

  public String getLoadMode() {
    return _loadMode;
  }

  public void setLoadMode(String loadMode) {
    _loadMode = loadMode;
  }

  public Map<String, String> getStreamConfigs() {
    return _streamConfigs;
  }

  public void setStreamConfigs(Map<String, String> streamConfigs) {
    _streamConfigs = streamConfigs;
  }

  public String getSegmentFormatVersion() {
    return _segmentFormatVersion;
  }

  public void setSegmentFormatVersion(String segmentFormatVersion) {
    _segmentFormatVersion = segmentFormatVersion;
  }

  public String getColumnMinMaxValueGeneratorMode() {
    return _columnMinMaxValueGeneratorMode;
  }

  public void setColumnMinMaxValueGeneratorMode(String columnMinMaxValueGeneratorMode) {
    _columnMinMaxValueGeneratorMode = columnMinMaxValueGeneratorMode;
  }

  public List<String> getNoDictionaryColumns() {
    return _noDictionaryColumns;
  }

  public List<String> getOnHeapDictionaryColumns() {
    return _onHeapDictionaryColumns;
  }

  public void setNoDictionaryColumns(List<String> noDictionaryColumns) {
    _noDictionaryColumns = noDictionaryColumns;
  }

  public void setOnHeapDictionaryColumns(List<String> onHeapDictionaryColumns) {
    _onHeapDictionaryColumns = onHeapDictionaryColumns;
  }

  public void setStarTreeIndexSpec(StarTreeIndexSpec starTreeIndexSpec) {
    _starTreeIndexSpec = starTreeIndexSpec;
  }

  public StarTreeIndexSpec getStarTreeIndexSpec() {
    return _starTreeIndexSpec;
  }

  public void setSegmentPartitionConfig(SegmentPartitionConfig config) {
    _segmentPartitionConfig = config;
  }

  public SegmentPartitionConfig getSegmentPartitionConfig() {
    return _segmentPartitionConfig;
  }

  @Override
  public String toString() {
    final StringBuilder result = new StringBuilder();
    final String newLine = System.getProperty("line.separator");

    result.append(this.getClass().getName());
    result.append(" Object {");
    result.append(newLine);

    //determine fields declared in this class only (no fields of superclass)
    final Field[] fields = this.getClass().getDeclaredFields();

    //print field names paired with their values
    for (final Field field : fields) {
      result.append("  ");
      try {
        result.append(field.getName());
        result.append(": ");
        //requires access to private field:
        result.append(field.get(this));
      } catch (final IllegalAccessException ex) {
        if (LOGGER.isWarnEnabled()) {
          LOGGER.warn("Caught exception while processing field " + field, ex);
        }
      }
      result.append(newLine);
    }
    result.append("}");

    return result.toString();
  }
}

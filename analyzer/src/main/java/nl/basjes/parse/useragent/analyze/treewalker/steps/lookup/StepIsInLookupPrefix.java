/*
 * Yet Another UserAgent Analyzer
 * Copyright (C) 2013-2020 Niels Basjes
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package nl.basjes.parse.useragent.analyze.treewalker.steps.lookup;

import nl.basjes.collections.PrefixMap;
import nl.basjes.collections.prefixmap.StringPrefixMap;
import nl.basjes.parse.useragent.analyze.treewalker.steps.Step;
import nl.basjes.parse.useragent.analyze.treewalker.steps.WalkList.WalkResult;
import org.antlr.v4.runtime.tree.ParseTree;

import java.util.Map;

public class StepIsInLookupPrefix extends Step {

    private final String            lookupName;
    private final PrefixMap<String> prefixMap;

    @SuppressWarnings("unused") // Private constructor for serialization systems ONLY (like Kryo)
    private StepIsInLookupPrefix() {
        lookupName = null;
        prefixMap = null;
    }

    public StepIsInLookupPrefix(String lookupName, Map<String, String> prefixList) {
        this.lookupName = lookupName;
        this.prefixMap = new StringPrefixMap<>(false);
        this.prefixMap.putAll(prefixList);
    }

    @Override
    public WalkResult walk(ParseTree tree, String value) {
        String input = getActualValue(tree, value);

        String result = prefixMap.getLongestMatch(input);

        if (result == null) {
            return null;
        }
        return walkNextStep(tree, input);
    }

    @Override
    public String toString() {
        return "IsInLookupPrefix(@" + lookupName + ")";
    }

}
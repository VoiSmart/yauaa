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

package nl.basjes.parse.useragent.analyze.treewalker.steps.compare;

import nl.basjes.parse.useragent.analyze.treewalker.steps.Step;
import nl.basjes.parse.useragent.analyze.treewalker.steps.WalkList.WalkResult;
import org.antlr.v4.runtime.tree.ParseTree;

import java.util.Collections;
import java.util.Set;

public class StepIsInSet extends Step {

    private final String listName;
    private final Set<String> list;

    @SuppressWarnings("unused") // Private constructor for serialization systems ONLY (like Kryo)
    private StepIsInSet() {
        listName = "<< Should not be seen anywhere >>";
        list = Collections.emptySet();
    }


    public StepIsInSet(String listName, Set<String> list) {
        this.listName = listName;
        this.list = list;
    }

    @Override
    public WalkResult walk(ParseTree tree, String value) {
        String actualValue = getActualValue(tree, value);

        if (list.contains(actualValue.toLowerCase())) {
            return walkNextStep(tree, actualValue);
        }
        return null;
    }

    @Override
    public String toString() {
        return "IsInSet(@" + listName + ")";
    }

}

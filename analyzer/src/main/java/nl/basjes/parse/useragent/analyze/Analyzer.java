/*
 * Yet Another UserAgent Analyzer
 * Copyright (C) 2013-2017 Niels Basjes
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package nl.basjes.parse.useragent.analyze;

import nl.basjes.parse.useragent.analyze.WordRangeVisitor.Range;
import org.antlr.v4.runtime.tree.ParseTree;

import java.util.Collections;
import java.util.Set;

public interface Analyzer {
    void inform(String path, String value, ParseTree ctx);

    void informMeAbout(MatcherAction matcherAction, String keyPattern);

    default void lookingForRange(String treeName, Range range) {
        // Do nothing
    }

    default Set<Range> getRequiredInformRanges(String treeName){
        return Collections.emptySet();
    }

    default void informMeAboutPrefix(MatcherAction matcherAction, String treeName, String prefix) {
        // Do nothing
    }

    default Set<Integer> getRequiredPrefixLengths(String treeName){
        return Collections.emptySet();
    }

}

/*
 * Copyright 2009 Alin Dreghiciu.
 * Copyright 2011 Toni Menzel.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.
 *
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.fossbrain.extensions.paxexam.karaf.container.internal;

import java.util.ArrayList;
import java.util.List;

import org.ops4j.pax.exam.ExamSystem;
import org.ops4j.pax.exam.TestContainer;
import org.ops4j.pax.exam.TestContainerFactory;
import org.ops4j.pax.exam.options.CustomFrameworkOption;

/**
 * Factory for {@link KarafTestContainer}.
 * 
 * @author Toni Menzel
 * @since 2.0, March 09, 2011
 */
public class KarafTestContainerFactory
        implements TestContainerFactory {

    private RMIRegistry m_rmiRegistry;
    private static final int DEFAULTPORT = 21412;

    public KarafTestContainerFactory()

    {
        m_rmiRegistry = new RMIRegistry(DEFAULTPORT, DEFAULTPORT + 1, DEFAULTPORT + 99).selectGracefully();
    }

    /**
     * {@inheritDoc}
     */
    public TestContainer[] create(ExamSystem system)
    {
        CustomFrameworkOption[] options = system.getOptions(CustomFrameworkOption.class);
        if (options == null || options.length == 0) {
            throw new IllegalStateException(
                "It is required to define which distribution you would like to use for your karaf distribution based test cases.");
        }
        List<TestContainer> containers = new ArrayList<TestContainer>();
        for (CustomFrameworkOption testContainer : options) {
            containers.add(new KarafTestContainer(system, m_rmiRegistry, testContainer));
        }
        return containers.toArray(new TestContainer[containers.size()]);
    }

}

/*
 *  Copyright 2016 Aino.io
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package io.aino.agents.core;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

import io.aino.agents.core.config.FileConfigBuilder;
import io.aino.agents.core.config.InvalidAgentConfigException;
import org.junit.*;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.io.File;
import java.io.FileNotFoundException;

public class AgentTest {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test(expected = InvalidAgentConfigException.class)
    public void testGetFactoryLoggerIsDisabledIfNotConfigured() throws Exception {
        Agent agent = Agent.getFactory().build();
    }

    @Test(expected = FileNotFoundException.class)
    public void testGetFactoryThrowsWithNonExistentConfigurationFile() throws Exception {
        Agent agent = Agent.getFactory()
                .setConfigurationBuilder(new FileConfigBuilder(new File("path/to/config/file")))
                .build();
    }

    @Test
    public void testShutdownAgent() throws Exception {
        Agent agent = Agent.getFactory().setConfigurationBuilder(new FileConfigBuilder(new File("src/test/resources/validConfig.xml"))).build();

        agent.increaseThreads();
        agent.increaseThreads();
        assertEquals("thread count", agent.getSenderThreadCount(), 3);

        agent.shutdown();
        assertEquals("thread count", agent.getSenderThreadCount(), 0);
    }

}
package org.jenkinsci.plugins.pipeline.utility.steps.toml;

import org.jenkinsci.plugins.workflow.cps.CpsFlowDefinition;
import org.jenkinsci.plugins.workflow.job.WorkflowJob;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.jvnet.hudson.test.JenkinsRule;

/**
 * Tests for {@link WriteTomlStep}.
 *
 * @author bright.ma
 */
public class WriteTomlStepTest {
    @Rule
    public JenkinsRule j = new JenkinsRule();
    @Rule
    public TemporaryFolder temp = new TemporaryFolder();

    @Test
    public void writeFile() throws Exception {
        WorkflowJob p = j.jenkins.createProject(WorkflowJob.class, "p");
        p.setDefinition(new CpsFlowDefinition(
                "node {\n" +
                        "       def amap = ['something': 'my datas',\n" +
                        "                    'size': 3,\n" +
                        "                    'isEmpty': false]\n" +
                        "\n" +
                        "        writeToml file: 'data.toml', toml: amap\n" +
                        "        def read = readToml file: 'data.toml'\n" +
                        "\n" +
                        "        assert read.something == 'my datas'\n" +
                        "        assert read.size == 3\n" +
                        "        assert read.isEmpty == false" +
                        "}", true));
        j.assertBuildStatusSuccess(p.scheduleBuild2(0));

    }

    @Test
    public void returnText() throws Exception {
        WorkflowJob p = j.jenkins.createProject(WorkflowJob.class, "p");
        p.setDefinition(new CpsFlowDefinition(
                "node {\n" +
                        "        def amap = ['something': 'my datas',\n" +
                        "                    'size': 3,\n" +
                        "                    'isEmpty': false]\n" +
                        "\n" +
                        "        String tomlText = writeToml returnText: true, toml: amap\n" +
                        "        def read = readToml text: tomlText\n" +
                        "\n" +
                        "        assert read.something == 'my datas'\n" +
                        "        assert read.size == 3\n" +
                        "        assert read.isEmpty == false" +
                        "}",
                true));
        j.assertBuildStatusSuccess(p.scheduleBuild2(0));
    }


}

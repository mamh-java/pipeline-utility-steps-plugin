package org.jenkinsci.plugins.pipeline.utility.steps.toml;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.Writer;

import hudson.model.Result;
import org.apache.commons.io.IOUtils;
import org.jenkinsci.plugins.pipeline.utility.steps.FilenameTestsUtils;
import org.jenkinsci.plugins.workflow.cps.CpsFlowDefinition;
import org.jenkinsci.plugins.workflow.job.WorkflowJob;
import org.jenkinsci.plugins.workflow.job.WorkflowRun;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.jvnet.hudson.test.JenkinsRule;

import static org.jenkinsci.plugins.pipeline.utility.steps.Messages.AbstractFileOrTextStepDescriptorImpl_missingRequiredArgument;


/**
 * Tests for {@link ReadTomlStep}.
 *
 * @author bright.ma
 */
public class ReadTomlStepTest {
    @Rule
    public JenkinsRule j = new JenkinsRule();

    @Rule
    public TemporaryFolder temp = new TemporaryFolder();

    private String tomlText = "# This is a TOML document\n" +
            "\n" +
            "title = \"TOML Example\"\n" +
            "\n" +
            "[owner]\n" +
            "name = \"Tom Preston-Werner\"\n" +
            "dob = 1979-05-27T07:32:00-08:00\n" +
            "\n" +
            "[database]\n" +
            "enabled = true\n" +
            "ports = [ 8000, 8001, 8002 ]\n" +
            "data = [ [\"delta\", \"phi\"], [3.14] ]\n" +
            "temp_targets = { cpu = 79.5, case = 72.0 }\n" +
            "\n" +
            "[servers]\n" +
            "\n" +
            "[servers.alpha]\n" +
            "ip = \"10.0.0.1\"\n" +
            "role = \"frontend\"\n" +
            "\n" +
            "[servers.beta]\n" +
            "ip = \"10.0.0.2\"\n" +
            "role = \"backend\"";

    @Test
    public void readFile() throws Exception {
        String file = writeToml(tomlText);

        WorkflowJob p = j.jenkins.createProject(WorkflowJob.class, "p");

        p.setDefinition(new CpsFlowDefinition(
                "node {\n" +
                        "  def datas = readToml file: '" + file + "'\n" +
                        "  assert datas.database.enabled == true\n" +
                        "  assert datas.database.ports[1] == 8001\n" +
                        "  assert datas.database.ports[2] == 8002\n" +
                        "  assert datas.database.temp_targets.cpu == 79.5\n" +
                        "  assert datas.database.temp_targets.case == 72.0\n" +
                        "  assert datas.servers.alpha.ip == \"10.0.0.1\"\n" +
                        "  assert datas.servers.alpha.role == \"frontend\"\n" +
                        "  assert datas.servers.beta.ip == \"10.0.0.2\"\n" +
                        "  assert datas.servers.beta.role == \"backend\"\n" +
                        "}", true));
        j.assertBuildStatusSuccess(p.scheduleBuild2(0));
    }

    @Test
    public void readText() throws Exception {
        WorkflowJob p = j.jenkins.createProject(WorkflowJob.class, "p");
        p.setDefinition(new CpsFlowDefinition(
                "node {\n" +
                        "  def datas = readToml text: '''" + tomlText + "'''\n" +
                        "  assert datas.database.enabled == true\n" +
                        "  assert datas.database.ports[1] == 8001\n" +
                        "  assert datas.database.ports[2] == 8002\n" +
                        "  assert datas.database.temp_targets.cpu == 79.5\n" +
                        "  assert datas.database.temp_targets.case == 72.0\n" +
                        "  assert datas.servers.alpha.ip == \"10.0.0.1\"\n" +
                        "  assert datas.servers.alpha.role == \"frontend\"\n" +
                        "  assert datas.servers.beta.ip == \"10.0.0.2\"\n" +
                        "  assert datas.servers.beta.role == \"backend\"\n" +
                        "}", true));
        j.assertBuildStatusSuccess(p.scheduleBuild2(0));
    }

    @Test
    public void readNone() throws Exception {
        WorkflowJob p = j.jenkins.createProject(WorkflowJob.class, "p");
        p.setDefinition(new CpsFlowDefinition(
                "node {\n" +
                        "  def datas = readToml()\n" +
                        "}", true));
        WorkflowRun run = j.assertBuildStatus(Result.FAILURE, p.scheduleBuild2(0).get());
        j.assertLogContains(AbstractFileOrTextStepDescriptorImpl_missingRequiredArgument("readToml"), run);
    }

    @Test
    public void readFileAndText() throws Exception {
        String file = writeToml(tomlText);

        WorkflowJob p = j.jenkins.createProject(WorkflowJob.class, "p");
        p.setDefinition(new CpsFlowDefinition(
                "node {\n" +
                        "  def datas = readToml( text: 'a = 1', file: '" + file + "' )\n" +
                        "}", true));
        WorkflowRun run = j.assertBuildStatus(Result.FAILURE, p.scheduleBuild2(0).get());
        j.assertLogContains(Messages.ReadTomlStepExecution_tooManyArguments("readToml"), run);
    }

    private String writeToml(String toml) throws IOException {
        File file = temp.newFile();
        try (Writer f = new FileWriter(file); Reader r = new StringReader(toml)) {
            IOUtils.copy(r, f);
        }
        return FilenameTestsUtils.toPath(file);
    }
}

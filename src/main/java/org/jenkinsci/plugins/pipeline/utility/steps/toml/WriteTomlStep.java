/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 Nikolas Falco
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.jenkinsci.plugins.pipeline.utility.steps.toml;

import com.google.common.collect.ImmutableSet;
import com.moandjiezana.toml.TomlWriter;
import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.Extension;
import hudson.model.TaskListener;
import org.jenkinsci.plugins.workflow.steps.*;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;

import java.io.StringWriter;
import java.io.Writer;
import java.util.Set;

import static org.apache.commons.lang.StringUtils.isBlank;

/**
 * Writes a Map to toml file in the current working directory.
 *
 * @author bright.ma
 */
public class WriteTomlStep extends Step {

    private String file;
    private final Object toml;
    private boolean returnText;

    @DataBoundConstructor
    public WriteTomlStep(Object toml) {
        this.toml = toml;
    }

    /**
     * Returns the name of the file to write.
     *
     * @return the file name
     */
    public String getFile() {
        return file;
    }

    @DataBoundSetter
    public void setFile(String file) {
        this.file = file;
    }

    /**
     * Return the object(map) to save.
     *
     * @return an object
     */
    public Object getToml() {
        return toml;
    }

    public boolean isReturnText() {
        return returnText;
    }

    @DataBoundSetter
    public void setReturnText(boolean returnText) {
        this.returnText = returnText;
    }

    @Override
    public StepExecution start(StepContext context) throws Exception {
        if (this.toml == null) {
            throw new IllegalArgumentException(Messages.WriteTomlStepExecution_missing(this.getDescriptor().getFunctionName()));
        }

        if (this.returnText) {
            if (this.file != null) {
                throw new IllegalArgumentException(Messages.WriteTomlStepExecution_bothReturnTextAndFile(this.getDescriptor().getFunctionName()));
            }
            return new ReturnTextExecution(this, context);
        }

        if (isBlank(this.file)) {
            throw new IllegalArgumentException(Messages.WriteTomlStepExecution_missingReturnTextAndFile(this.getDescriptor().getFunctionName()));
        }
        return new WriteTomlStepExecution(this, context);
    }

    @Extension
    public static class DescriptorImpl extends StepDescriptor {

        public DescriptorImpl() {

        }

        @Override
        public Set<? extends Class<?>> getRequiredContext() {
            return ImmutableSet.of(TaskListener.class);
        }

        @Override
        public String getFunctionName() {
            return "writeToml";
        }

        @Override
        @NonNull
        public String getDisplayName() {
            return Messages.WriteTomlStep_DescriptorImpl_displayName();
        }

    }

    void execute(Writer writer) throws java.io.IOException {
        new TomlWriter().write(this.toml, writer);
    }

    private static class ReturnTextExecution extends SynchronousNonBlockingStepExecution<String> {
        private static final long serialVersionUID = 1L;

        private transient WriteTomlStep step;

        protected ReturnTextExecution(WriteTomlStep step, @NonNull StepContext context) {
            super(context);
            this.step = step;
        }

        @Override
        protected String run() throws Exception {
            StringWriter w = new StringWriter();
            this.step.execute(w);
            return w.toString();
        }
    }

}

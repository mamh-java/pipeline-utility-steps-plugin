/*
 * The MIT License
 *
 * Copyright (C) 2021 bright.ma. All rights reserved.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.jenkinsci.plugins.pipeline.utility.steps.excel;

import com.google.common.collect.ImmutableSet;
import hudson.Extension;
import hudson.FilePath;
import hudson.model.TaskListener;
import org.codehaus.groovy.control.customizers.ImportCustomizer;
import org.jenkinsci.plugins.workflow.cps.CpsFlowExecution;
import org.jenkinsci.plugins.workflow.cps.GroovyShellDecorator;
import org.jenkinsci.plugins.workflow.steps.Step;
import org.jenkinsci.plugins.workflow.steps.StepContext;
import org.jenkinsci.plugins.workflow.steps.StepDescriptor;
import org.jenkinsci.plugins.workflow.steps.StepExecution;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;

import javax.annotation.Nonnull;
import java.util.Set;


public class ReadExcelStep extends Step {

    private static final String COM_ALIBABA_EXCEL = "com.alibaba.excel";
    private String file;

    @DataBoundConstructor
    public ReadExcelStep() {
    }

    @Override
    public StepExecution start(StepContext context) throws Exception {
        return new ReadExcelStepExecution(this, context);
    }

    public String getFile() {
        return this.file;
    }

    @DataBoundSetter
    public void setFile(String file) {
        this.file = file;
    }

    @Extension
    public static class DescriptorImpl extends StepDescriptor {

        public DescriptorImpl() {
        }

        @Override
        public Set<? extends Class<?>> getRequiredContext() {
            return ImmutableSet.of(TaskListener.class, FilePath.class);
        }

        @Override
        public String getFunctionName() {
            return "readExcel";
        }

        @Override
        @Nonnull
        public String getDisplayName() {
            return Messages.ReadExcelStep_DescriptorImpl_displayName();
        }
    }


    /**
     * Auto imports org.apache.commons.csv.* .
     */
    @Extension(optional = true)
    public static class PackageAutoImporter extends GroovyShellDecorator {
        @Override
        public void customizeImports(CpsFlowExecution context, ImportCustomizer ic) {
            ic.addStarImports(COM_ALIBABA_EXCEL);
        }
    }


}

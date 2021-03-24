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

import com.alibaba.excel.EasyExcel;
import hudson.FilePath;
import org.jenkinsci.plugins.workflow.steps.MissingContextVariableException;
import org.jenkinsci.plugins.workflow.steps.StepContext;
import org.jenkinsci.plugins.workflow.steps.SynchronousNonBlockingStepExecution;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

import static org.apache.commons.lang.StringUtils.isNotBlank;


public class ReadExcelStepExecution extends SynchronousNonBlockingStepExecution<List> {
    private static final long serialVersionUID = 1L;

    private final transient ReadExcelStep step;

    protected ReadExcelStepExecution(@Nonnull ReadExcelStep step, @Nonnull StepContext context) {
        super(context);
        this.step = step;
    }

    @Override
    protected List run() throws Exception {
        FilePath ws = getContext().get(FilePath.class);
        if (ws == null && isNotBlank(step.getFile())) {
            throw new MissingContextVariableException(FilePath.class);
        }
        List list = new ArrayList();
        if (isNotBlank(step.getFile())) {
            FilePath path = ws.child(step.getFile());
            if (path.exists() && !path.isDirectory()) {
                list = EasyExcel.read(path.read()).sheet().doReadSync();
            }
        }
        return list;

    }
}

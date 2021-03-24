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
import org.apache.commons.lang.StringUtils;
import org.jenkinsci.plugins.workflow.steps.StepContext;
import org.jenkinsci.plugins.workflow.steps.SynchronousNonBlockingStepExecution;

import javax.annotation.Nonnull;
import java.io.FileNotFoundException;
import java.util.List;

public class WriteExcelStepExecution extends SynchronousNonBlockingStepExecution<Void> {
    private static final long serialVersionUID = 1L;

    private final transient WriteExcelStep step;

    protected WriteExcelStepExecution(@Nonnull WriteExcelStep step, @Nonnull StepContext context) {
        super(context);
        this.step = step;
    }


    @Override
    protected Void run() throws Exception {
        FilePath ws = getContext().get(FilePath.class);
        assert ws != null;

        List<?> list = step.getList();
        if (list == null) {
            throw new IllegalArgumentException(Messages.WriteExcelStepExecution_missingRecords(step.getDescriptor().getFunctionName()));
        }

        String file = step.getFile();
        if (StringUtils.isBlank(file)) {
            throw new IllegalArgumentException(Messages.WriteExcelStepExecution_missingFile(step.getDescriptor().getFunctionName()));
        }

        FilePath path = ws.child(file);
        if (path.isDirectory()) {
            throw new FileNotFoundException(Messages.ExcelStepExecution_fileIsDirectory(path.getRemote()));
        }

        EasyExcel.write(path.write()).sheet().doWrite(list);

        return null;
    }
}

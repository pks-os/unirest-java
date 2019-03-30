/**
 * The MIT License
 *
 * Copyright for portions of unirest-java are held by Kong Inc (c) 2013.
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package kong.unirest.apache;

import kong.unirest.ProgressMonitor;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.content.FileBody;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Objects;

public class MonitoringFileBody extends FileBody {
    private final ProgressMonitor monitor;
    private long length;
    private String name;

    public MonitoringFileBody(File file, ContentType contentType, ProgressMonitor monitor) {
        super(file, contentType);
        this.monitor = monitor;
        this.length = file.length();
        this.name = file.getName();
    }

    @Override
    public void writeTo(OutputStream out) throws IOException {
        if(Objects.nonNull(monitor)){
            super.writeTo(new MonitoringStream(out, length));
        } else {
            super.writeTo(out);
        }
    }

    class MonitoringStream extends OutputStream {

        private final OutputStream out;
        private volatile long written = 0;
        private long length;

        public MonitoringStream(OutputStream out, long length) {
            this.out = out;
            this.length = length;
        }

        @Override
        public void write(int b) throws IOException {
            out.write(b);
            written++;
            monitor.accept(name, written, length);
        }

        @Override
        public void write(byte[] b) throws IOException {
            out.write(b);
            written += b.length;
            monitor.accept(name, written, length);
        }

        @Override
        public void write(byte[] b, int off, int len) throws IOException {
            out.write(b, off, len);
            written += len;
            monitor.accept(name, written, length);
        }

        @Override
        public void flush() throws IOException {
            out.flush();
        }

        @Override
        public void close() throws IOException {
            out.close();
        }
    }
}
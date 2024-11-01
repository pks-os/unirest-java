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

package BehaviorTests;

import io.javalin.Javalin;
import io.javalin.http.Context;
import io.javalin.http.UnauthorizedResponse;
import kong.unirest.core.Unirest;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.net.*;
/**
 *
 * @author jcgonzalez.com
 *
 */
public class JankyProxy {
    private static int remoteport;
    private static boolean wasused;

    public static void runServer(String host, int remoteport, int localport) {
        JankyProxy.remoteport = remoteport;
        var server = Javalin.create(c -> {

        }).start(host, localport);

        server.before("/*", JankyProxy::before);
        server.get("/*", JankyProxy::execute);

    }

    private static void before(@NotNull Context it) {
        if (it.basicAuthCredentials() == null) {
            it.header("WWW-Authenticate", "Basic realm=\"User Visible Realm\", charset=\"UTF-8\"");
            it.status(401);
            it.result("Login required");
            throw new UnauthorizedResponse();
        }
    }

    private static void execute(@NotNull Context context) {
          wasused = true;
          context.status(200);
          context.result("Im a proxy");
    }

    public static boolean wasUsed(){
        return wasused;
    }

    public static void shutdown(){
    }
}

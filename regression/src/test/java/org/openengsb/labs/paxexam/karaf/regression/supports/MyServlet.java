package org.openengsb.labs.paxexam.karaf.regression.supports;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class MyServlet extends HttpServlet {

    // now we just send a echo response back
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        copy(request.getInputStream(), response.getOutputStream(), 4096);
    }

    public static int copy(final InputStream input, final OutputStream output, int bufferSize) throws IOException {
        int avail = input.available();
        if (avail > 262144) {
            avail = 262144;
        }
        if (avail > bufferSize) {
            bufferSize = avail;
        }

        final byte[] buffer = new byte[bufferSize];
        int n = input.read(buffer);
        int total = 0;
        while (-1 != n) {
            output.write(buffer, 0, n);
            total += n;
            n = input.read(buffer);
        }
        output.flush();
        return total;
    }
}

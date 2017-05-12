package io.hgc.sonar.todo.support;

import com.google.common.io.Resources;
import org.apache.commons.io.IOUtils;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

public class MockHttpApi extends AbstractHandler implements TestRule {
    private String cannedResponseFilePath = null;
    private Map<String, String[]> lastRequestParameters;

    @Override
    public void handle(String s,
                       Request baseRequest,
                       HttpServletRequest request,
                       HttpServletResponse response) throws IOException, ServletException {
        lastRequestParameters = request.getParameterMap();

        if (cannedResponseFilePath != null) {
            response.setContentType("application/json;charset=UTF-8");
            response.setStatus(HttpServletResponse.SC_OK);

            IOUtils.copy(
                    Resources.getResource(cannedResponseFilePath).openStream(),
                    response.getWriter());

            baseRequest.setHandled(true);
        }
    }

    @Override
    public Statement apply(Statement base, Description description) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                resetState();
                base.evaluate();
            }
        };
    }

    private void resetState() {
        cannedResponseFilePath = null;
        lastRequestParameters = null;
    }

    public void setCannedResponseFilePath(String cannedResponseFilePath) {
        this.cannedResponseFilePath = cannedResponseFilePath;
    }

    public Map<String, String[]> getLastRequestParameters() {
        return lastRequestParameters;
    }
}

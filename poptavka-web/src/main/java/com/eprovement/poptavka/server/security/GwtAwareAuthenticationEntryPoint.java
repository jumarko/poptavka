/**
 *
 */
package com.eprovement.poptavka.server.security;

import com.eprovement.poptavka.shared.security.SecurityResponseMessage;
import com.eprovement.poptavka.shared.security.SecurityResponseMessageFactory;
import java.io.IOException;
import java.io.Writer;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;
import com.google.web.bindery.autobean.shared.AutoBeanUtils;
import com.google.web.bindery.autobean.vm.AutoBeanFactorySource;

/**
 * GWT dedicated entry point that can delegate to a alternative entry point if
 * the request is not GWT-like.
 *
 * @author dmartin
 *
 */
public class GwtAwareAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private static final Log GWT_ENTRY_POINT_LOGGER = LogFactory.getLog(GwtAwareAuthenticationEntryPoint.class);
    private AuthenticationEntryPoint alternativeEntryPoint;
    private String reasonPhrase;
    private String loginFormUrl;
    private boolean gwtLoginForm = false;

    public GwtAwareAuthenticationEntryPoint() {
        this(HttpStatus.UNAUTHORIZED, ". Authentication needed");
    }

    public GwtAwareAuthenticationEntryPoint(HttpStatus status, String complementPhrase) {
        GWT_ENTRY_POINT_LOGGER.debug("constructor: status=" + status.toString()
                + ", complementPhrase=" + complementPhrase);
        final StringBuilder sb = new StringBuilder(status.getReasonPhrase());
        if (StringUtils.isNotBlank(complementPhrase)) {
            sb.append(complementPhrase);
        }
        reasonPhrase = sb.toString();
    }

    public void setAlternativeEntryPoint(final AuthenticationEntryPoint alternativeEntryPoint) {
        GWT_ENTRY_POINT_LOGGER.debug("setAlternativeEntryPoin:" + alternativeEntryPoint.toString());
        this.alternativeEntryPoint = alternativeEntryPoint;
    }

    public void setLoginFormUrl(String loginFormUrl) {
        this.loginFormUrl = loginFormUrl;
    }

    public void setGwtLoginForm(boolean gwtLoginForm) {
        this.gwtLoginForm = gwtLoginForm;
    }

    /**
     * @see
     * org.springframework.security.web.AuthenticationEntryPoint#commence(javax.servlet.http.HttpServletRequest,
     * javax.servlet.http.HttpServletResponse,
     * org.springframework.security.core.AuthenticationException)
     */
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException authException) throws IOException, ServletException {
        GWT_ENTRY_POINT_LOGGER.debug("commence: response=" + response.toString() + ", request=" + request.toString()
                + ", authException=" + authException.toString());
        boolean isGwtRequest = HttpRequestUtils.containsHeaderStartingWith(request, "X-GWT");
        if (!isGwtRequest) {
            if (GWT_ENTRY_POINT_LOGGER.isDebugEnabled()) {
                GWT_ENTRY_POINT_LOGGER.debug(
                        "GwtAwareAuthenticationEntryPoint delegating to the alternative entry point");
            }
            alternativeEntryPoint.commence(request, response, authException);
            return;
        }

        final HttpServletResponseWrapper responseWrapper = new HttpServletResponseWrapper(response);

        final Writer out = responseWrapper.getWriter();

        response.setStatus(HttpStatus.UNAUTHORIZED.value());

        final SecurityResponseMessageFactory factory =
                AutoBeanFactorySource.create(SecurityResponseMessageFactory.class);
        final AutoBean<SecurityResponseMessage> autobeanMessage = factory.create(SecurityResponseMessage.class);
        final SecurityResponseMessage message = autobeanMessage.as();

        message.setStatus(HttpStatus.UNAUTHORIZED.value());
        message.setMessage(reasonPhrase);
        if (!gwtLoginForm) {
            message.setLoginFormUrl(loginFormUrl);
        }

        final AutoBean<SecurityResponseMessage> bean = AutoBeanUtils.getAutoBean(message);
        final String encodedString = AutoBeanCodex.encode(bean).getPayload();

        if (GWT_ENTRY_POINT_LOGGER.isDebugEnabled()) {
            GWT_ENTRY_POINT_LOGGER.debug("GwtAwareAuthenticationEntryPoint writing its response: " + encodedString);
        }

        out.write(encodedString);
        out.flush();
        out.close();
    }
}

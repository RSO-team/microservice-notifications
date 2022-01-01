package si.fri.rsoteam.api.v1.filters;

import com.kumuluz.ee.logs.LogManager;
import com.kumuluz.ee.logs.Logger;
import si.fri.rsoteam.services.config.RestConfig;

import javax.inject.Inject;
import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebFilter("/v1/*")
public class AuthFilter implements Filter {
    private final Logger LOG = LogManager.getLogger(AuthFilter.class.getName());

    @Inject
    private RestConfig restConfig;

    @Override
    public void init(FilterConfig filterConfig) {
    }

    @Override
    public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain)
            throws IOException, ServletException {
        String apiToken = restConfig.getApiToken();

        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;

        String token = httpServletRequest.getHeader("apiToken");
        if (apiToken.equals(token)) {
            chain.doFilter(request, response);
        } else {
            httpServletResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            chain.doFilter(request, response);
        }
    }

    @Override
    public void destroy() {
    }
}

package com.zhengqing.modules.common.filter;

import com.zhengqing.config.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 *  <p> 记录url接口收到与返回参数日志 </p>
 *
 * @description :
 * @author : zhengqing
 * @date : 2019/8/23 10:04
 */
@Component
public class LogRequestFilter extends OncePerRequestFilter implements Ordered {

    private static final Logger LOG = LoggerFactory.getLogger(LogRequestFilter.class);

    private int order = Ordered.LOWEST_PRECEDENCE - 8;
    /**
     * 请求路径
     */
    private static String url;

    @Override
    public int getOrder() {
        return order;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return !request.getRequestURI().contains("/api/");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper wrappedResponse = new ContentCachingResponseWrapper(response);
        filterChain.doFilter(wrappedRequest, wrappedResponse);
        logRequestBody(wrappedRequest);
        logResponseBody(wrappedRequest, wrappedResponse);
        wrappedResponse.copyBodyToResponse();
    }

    private void logRequestBody(ContentCachingRequestWrapper request) {
        ContentCachingRequestWrapper wrapper = request;
        if (wrapper != null) {
            byte[] buf = wrapper.getContentAsByteArray();
            if (buf.length > 0) {
                String payload;
                try {
                    payload = new String(buf, 0, buf.length, wrapper.getCharacterEncoding());
                } catch (UnsupportedEncodingException ex) {
                    payload = "[unknown]";
                }
                url = request.getRequestURI().replace("//", "/");
                LOG.debug(url);
                LOG.debug("{}接收到的参数:{}", Constants.URL_MAPPING_MAP.get(url), payload);
            }
        }
    }

    private void logResponseBody(ContentCachingRequestWrapper request, ContentCachingResponseWrapper response) {
        ContentCachingResponseWrapper wrapper = response;
        if (wrapper != null) {
            byte[] buf = wrapper.getContentAsByteArray();
            if (buf.length > 0) {
                String payload;
                try {
                    payload = new String(buf, 0, buf.length, wrapper.getCharacterEncoding());
//                    code = (Integer) JSON.parseObject(payload).get("code");
                } catch (UnsupportedEncodingException ex) {
                    payload = "[unknown]";
                }
                LOG.debug("{}返回的参数:{}", Constants.URL_MAPPING_MAP.get(request.getRequestURI()), payload);
            }
        }
    }

}

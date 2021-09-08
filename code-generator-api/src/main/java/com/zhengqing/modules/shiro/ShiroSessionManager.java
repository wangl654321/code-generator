package com.zhengqing.modules.shiro;

import com.zhengqing.config.Constants;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.apache.shiro.web.util.WebUtils;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.Serializable;

/**
 * <p> 自定义获取Token </p>
 *
 * @author : zhengqing
 * @description :
 * @date : 2019/8/23 15:55
 */
public class ShiroSessionManager extends DefaultWebSessionManager {

    /**
     * 重写构造器
     */
    public ShiroSessionManager() {
        super();
        this.setDeleteInvalidSessions(true);
    }

    /**
     * 重写方法实现从请求头获取Token便于接口统一
     * 每次请求进来,Shiro会去从请求头找REQUEST_HEADER这个key对应的Value(Token)
     */
    @Override
    public Serializable getSessionId(ServletRequest request, ServletResponse response) {
        String token = WebUtils.toHttp(request).getHeader(Constants.REQUEST_HEADER);

        // 如果请求头中存在token 则从请求头中获取token
        if (StringUtils.isNotBlank(token)) {
            return token;
        } else {
            // 否则按默认规则从cookie取token
            return super.getSessionId(request, response);
        }
    }
}

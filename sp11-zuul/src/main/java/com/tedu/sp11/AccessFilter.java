package com.tedu.sp11;

import javax.servlet.http.HttpServletRequest;

import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.stereotype.Component;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import com.tedu.web.util.JsonResult;

@Component
public class AccessFilter extends ZuulFilter{

	@Override
	public boolean shouldFilter() {
      //判断当前请求，是不是应用当前代码进行过滤器代码
		//如果请求后台item-service，执行当前代码
		RequestContext ctx = RequestContext.getCurrentContext();
		String serviceId = (String) ctx.get(FilterConstants.SERVICE_ID_KEY);
		if (serviceId.equals("item-service")) {
			return true;
		}
		return false;
	}

	@Override
	public Object run() throws ZuulException {
		// 过滤代码
		RequestContext ctx = RequestContext.getCurrentContext();
		HttpServletRequest request = ctx.getRequest();
		String token = request.getParameter("token");
		if (token==null||token.length()==0) {
			//阻止请求被路由到后台微服务
			ctx.setSendZuulResponse(false);
			ctx.setResponseStatusCode(200);
			ctx.setResponseBody(JsonResult.err().code(JsonResult.NOT_LOGIN).toString());

		}
		//zuul过滤器返回的数据设计为以后扩展使用，
		//目前该返回值没有被使用
		return null;
	}

	@Override
	public String filterType() {
		// 返回当前过滤器是什么类型的
		return FilterConstants.PRE_TYPE;
	}

	@Override
	public int filterOrder() {
		// 过滤顺序号
		return FilterConstants.PRE_DECORATION_FILTER_ORDER+1;
	}

}

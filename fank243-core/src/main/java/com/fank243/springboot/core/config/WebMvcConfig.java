package com.fank243.springboot.core.config;

import com.fank243.springboot.core.annotation.Interceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * 应用拦截器
 *
 * @author FanWeiJie
 * @date 2019-05-23 18:39:25
 */
@SuppressWarnings("AlibabaAvoidCommentBehindStatement")
@Slf4j
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    /** 可选注入，防止子项目启动失败 **/
    private final List<HandlerInterceptor> handlerInterceptors;

    public WebMvcConfig(List<HandlerInterceptor> handlerInterceptors) {
        this.handlerInterceptors = handlerInterceptors;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        if (handlerInterceptors == null || handlerInterceptors.isEmpty()) {
            return;
        }

        // 根据Order字段进行排序
        handlerInterceptors.sort(Comparator.comparingInt(this::getOrder));

        for (HandlerInterceptor handlerInterceptor : handlerInterceptors) {

            Interceptor annotation = AnnotationUtils.findAnnotation(handlerInterceptor.getClass(), Interceptor.class);
            InterceptorRegistration registration = registry.addInterceptor(handlerInterceptor);
            if (annotation == null) {
                continue;
            }

            // 需要拦截的资源
            List<String> includeList = new ArrayList<>(Arrays.asList(annotation.include()));
            if (includeList.size() > 0) {
                registration.addPathPatterns(includeList);
            }

            // 需要排除的资源以及静态资源
            List<String> excludeList = new ArrayList<>();
            excludeList.addAll(Arrays.asList(annotation.exclude()));
            excludeList
                .addAll(Arrays.asList("/style/**", "/js/**", "/layui/**", "*.js", "*.css", "*.icon", "*.png", "*.jpg"));
            if (excludeList.size() > 0) {
                registration.excludePathPatterns(excludeList);
            }
        }
    }

    private int getOrder(Object o) {
        Interceptor annotation = AnnotationUtils.findAnnotation(o.getClass(), Interceptor.class);
        if (annotation == null) {
            return 0;
        }
        return annotation.order();
    }

    /** 全局异常处理 **/
//    @Override
//    public void configureHandlerExceptionResolvers(List<HandlerExceptionResolver> resolvers) {
//        resolvers.add((request, response, handler, ex) -> {
//            ResultInfo result;
//            // shiro 权限
//            if (ex instanceof UnauthorizedException) {
//                result = ResultInfo.forbidden("您访问的资源未授权：" + ex.getMessage());
//            } else {
//                log.warn("监听到异常URI：{}", request.getRequestURI());
//                log.error(ex.getMessage(), ex);
//
//                if (ex instanceof NoHandlerFoundException) {
//                    result = ResultInfo.notFund("您访问的资源不存在：" + request.getRequestURI());
//                } else {
//                    result = ResultInfo.exception("您访问的资源出了点问题：" + ex.getMessage());
//                }
//            }
//
//            if (!WebUtils.isBrowser(request)) {
//                WebUtils.printJson2(result);
//                return new ModelAndView();
//            }
//
//            ModelAndView view;
//            if (ResultCode.R403.getCode() == result.getCode()) {
//                view = new ModelAndView("/error/403");
//            } else if (ResultCode.R404.getCode() == result.getCode()) {
//                view = new ModelAndView("/error/404");
//            } else {
//                view = new ModelAndView("/error/500");
//            }
//
//            view.addObject("message", result.getMsg());
//            return view;
//        });
//    }

    /** 添加静态资源映射 **/
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**", "/static/**", "/public/**").addResourceLocations("classpath:/static/",
            "classpath:/public/");
    }

}

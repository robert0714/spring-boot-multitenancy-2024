package com.thomasvitale.instrumentservice.aop;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;

import com.thomasvitale.instrumentservice.instrument.*;
import com.thomasvitale.instrumentservice.multitenancy.security.JWTInfoHelper;
import com.thomasvitale.instrumentservice.aop.utils.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.core.NamedThreadLocal;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;

import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.Map;

@AllArgsConstructor
@Slf4j
@Aspect
@Component
public class LogAspect {
    private static final ThreadLocal<Long> TIME_THREADLOCAL = new NamedThreadLocal<Long>("Cost Time");

     
    private final AsyncRequestRecordsService asyncRequestRecordsService;
    private final JWTInfoHelper helper;
    @Before(value = "@annotation(loggable)")
    public void doBefore(JoinPoint joinPoint, Loggable loggable) {
        TIME_THREADLOCAL.set(System.currentTimeMillis());
    }

    @AfterReturning(pointcut = "@annotation(loggable)", returning = "responseBody")
    public void doAfterReturning(JoinPoint joinPoint, Loggable loggable, Object responseBody) {
        handleLog(joinPoint, loggable, null, responseBody);
    }

    @AfterThrowing(value = "@annotation(loggable)", throwing = "e")
    public void doAfterThrowing(JoinPoint joinPoint, Loggable loggable, Exception e) {
        handleLog(joinPoint, loggable, e, null);
    }

    private void handleLog(final JoinPoint joinPoint, Loggable loggable, final Exception e, Object responseBody) {
        try {
            RequestRecords requestRecords = new RequestRecords();

            requestRecords.setStatus(1); // 1: success, 0: fail
            requestRecords.setUserName(helper.showUserName());
            requestRecords.setRealmname(helper.showRealmName());
            requestRecords.setUserIp(IpUtils.getIpAddr());
            requestRecords.setRequestMethod(ServletUtils.getRequest().getMethod());
            requestRecords.setRequestUrl(StringUtils.substring(ServletUtils.getRequest().getRequestURI(), 0, 255));
            // default api name : xxxController.method()
            requestRecords.setApiName(StrFormatter.format("{}.{}()", joinPoint.getTarget().getClass().getSimpleName(), joinPoint.getSignature().getName()));

            if (e != null) {
                requestRecords.setStatus(0);
                requestRecords.setErrorMsg(StringUtils.substring(e.getMessage(), 0, 2000));
            }

            handleData(joinPoint, loggable, requestRecords, responseBody);

            requestRecords.setRequestTime(OffsetDateTime.now());
            requestRecords.setCostTime(System.currentTimeMillis() - TIME_THREADLOCAL.get());
            asyncRequestRecordsService.save(requestRecords);

        } catch (Exception exp) {
            log.error("Handle API Log error : {}", exp.getMessage());
            exp.printStackTrace();
        } finally {
            TIME_THREADLOCAL.remove();
        }
    }

    private void handleData(JoinPoint joinPoint, Loggable loggable, RequestRecords requestRecords, Object responseBody) throws JsonProcessingException {
        if (StringUtils.hasText(loggable.apiName())) {
            requestRecords.setApiName(loggable.apiName());
        }

        if (loggable.isSaveRequestData()) {
            handleRequestParam(joinPoint, requestRecords, loggable.excludeParamNames());
        }

        if (loggable.isSaveResponseData() && StringUtils.isNotNull(responseBody)) {
            requestRecords.setResponseBody(StringUtils.substring(JsonUtils.bean2Json(responseBody), 0, 2000));
        }
    }

    private void handleRequestParam(JoinPoint joinPoint, RequestRecords requestRecords, String[] excludeParamNames) throws JsonProcessingException {
        Map<?, ?> paramsMap = ServletUtils.getParamMap(ServletUtils.getRequest());
        String jsonRequestParam = null;

        if (StringUtils.isEmpty(paramsMap)) {
            jsonRequestParam = argsArrayToString(joinPoint.getArgs(), excludeParamNames);
        } else {
            jsonRequestParam = JsonUtils.bean2Json(paramsMap, getFilterProvider(excludeParamNames));
        }

        requestRecords.setRequestParam(StringUtils.substring(jsonRequestParam, 0, 2000));
    }

    private String argsArrayToString(Object[] paramsArray, String[] excludeParamNames) throws JsonProcessingException {
        String params = "";
        if (StringUtils.isNotEmpty(paramsArray)) {
            for (Object o : paramsArray) {
                if (StringUtils.isNotNull(o) && !isFilterObject(o)) {
                    String jsonObj = JsonUtils.bean2Json(o, getFilterProvider(excludeParamNames));
                    params += jsonObj + " ";
                }
            }
        }
        return params.trim();
    }

    public FilterProvider getFilterProvider(String[] excludeParamNames) {
        return new SimpleFilterProvider().addFilter("ParamFilter", SimpleBeanPropertyFilter.serializeAllExcept(excludeParamNames));
    }

    public boolean isFilterObject(final Object o) {  // 濾掉MultipartFile, HttpServletRequest, HttpServletResponse, BindingResult
        Class<?> clazz = o.getClass();
        if (clazz.isArray()) {
            return clazz.getComponentType().isAssignableFrom(MultipartFile.class);
        } else if (Collection.class.isAssignableFrom(clazz)) {
            Collection<?> collection = (Collection) o;
            for (Object value : collection) {
                return value instanceof MultipartFile;
            }
        } else if (Map.class.isAssignableFrom(clazz)) {
            Map<?,?> map = (Map) o;
            for (Object value : map.entrySet()) {
                Map.Entry entry = (Map.Entry) value;
                return entry.getValue() instanceof MultipartFile;
            }
        }
        return o instanceof MultipartFile || o instanceof HttpServletRequest || o instanceof HttpServletResponse || o instanceof BindingResult;
    }

}
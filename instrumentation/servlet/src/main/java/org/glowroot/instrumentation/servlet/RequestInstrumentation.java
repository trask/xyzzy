/*
 * Copyright 2011-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.glowroot.instrumentation.servlet;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.glowroot.instrumentation.api.Logger;
import org.glowroot.instrumentation.api.ThreadContext;
import org.glowroot.instrumentation.api.weaving.Advice;
import org.glowroot.instrumentation.api.weaving.Bind;
import org.glowroot.instrumentation.servlet.boot.RequestClassMeta;
import org.glowroot.instrumentation.servlet.boot.ServletMessageSupplier;

public class RequestInstrumentation {

    private static final Logger logger = Logger.getLogger(RequestInstrumentation.class);

    @Advice.Pointcut(className = "javax.servlet.ServletRequest",
                     methodName = "getParameter*",
                     methodParameterTypes = {".."},
                     nestingGroup = "servlet-inner-call")
    public static class GetParameterAdvice {

        @Advice.OnMethodReturn
        public static void onReturn(@Bind.This Object req,
                @Bind.ClassMeta RequestClassMeta requestClassMeta,
                ThreadContext context) {

            if (!(req instanceof HttpServletRequest)) {
                return;
            }
            HttpServletRequest request = (HttpServletRequest) req;
            // only now is it safe to get parameters (if parameters are retrieved before this, it
            // could prevent a servlet from choosing to read the underlying stream instead of using
            // the getParameter* methods) see SRV.3.1.1 "When Parameters Are Available"
            ServletMessageSupplier messageSupplier =
                    (ServletMessageSupplier) context.getServletRequestInfo();
            if (messageSupplier == null || messageSupplier.isRequestParametersCaptured()) {
                return;
            }
            // the request is being traced and the parameter map hasn't been captured yet
            captureRequestParameters(requestClassMeta, request, messageSupplier);
        }
    }

    private static void captureRequestParameters(RequestClassMeta requestClassMeta,
            HttpServletRequest request, ServletMessageSupplier messageSupplier) {

        if (requestClassMeta.isBadParameterMapImplementation()) {
            messageSupplier.setCaptureRequestParameters(
                    DetailCapture.captureRequestParameters(request));
            return;
        }
        Map</*@Nullable*/ String, ?> parameterMap;
        try {
            parameterMap = request.getParameterMap();
        } catch (Exception e) {
            // this is to handle at least one web container (ATG 9.4) which has a bad
            // implementation of getParameterMap(), specifically in ATG's case
            // atg.taglib.dspjsp.RequestWrapper delegates the other getParameter*() methods but
            // it fails to delegate getParameterMap(), which then falls through to super class
            // atg.servlet.MutableHttpServletRequest and generates a NullPointerException since
            // the super class was not set up expecting the call

            // log exception at debug level
            logger.debug(e.getMessage(), e);
            // set flag so don't keep generating/catching exception over and over
            requestClassMeta.setBadParameterMapImplementation();
            messageSupplier.setCaptureRequestParameters(
                    DetailCapture.captureRequestParameters(request));
            return;
        }
        if (parameterMap == null) {
            return;
        }
        if (parameterMap.isEmpty()) {
            // do not call setCaptureRequestParameters(), which will cause
            // isRequestParametersCaptured() to return true and prevent further capture of
            // request parameters, e.g. after a multipart/form-data request is wrapped by
            // org.springframework.web.multipart.support.DefaultMultipartHttpServletRequest
            return;
        }
        messageSupplier.setCaptureRequestParameters(
                DetailCapture.captureRequestParameters(parameterMap));
    }
}

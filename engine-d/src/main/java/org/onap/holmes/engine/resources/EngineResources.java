/**
 * Copyright 2017 ZTE Corporation.
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
package org.onap.holmes.engine.resources;


import com.codahale.metrics.annotation.Timed;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import java.util.Locale;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import lombok.extern.slf4j.Slf4j;
import org.jvnet.hk2.annotations.Service;
import org.onap.holmes.common.dmaap.DmaapService;
import org.onap.holmes.common.exception.CorrelationException;
import org.onap.holmes.common.utils.ExceptionUtil;
import org.onap.holmes.common.utils.LanguageUtil;
import org.onap.holmes.engine.manager.DroolsEngine;
import org.onap.holmes.engine.request.CompileRuleRequest;
import org.onap.holmes.engine.request.DeployRuleRequest;
import org.onap.holmes.engine.response.CorrelationRuleResponse;

@Service
@Path("/rule")
@Api(tags = {"Holmes Engine Management"})
@Produces(MediaType.APPLICATION_JSON)
@Slf4j
public class EngineResources {

    @Inject
    DroolsEngine droolsEngine;

    @PUT
    @ApiOperation(value = "Deploy a rule into the Drools engine.", response = CorrelationRuleResponse.class)
    @Produces(MediaType.APPLICATION_JSON)
    @Timed
    public CorrelationRuleResponse deployRule(
            @ApiParam(value = "The request entity of the HTTP call, which comprises three "
                    + "fields: \"content\" , \"loopcontrolname\" and \"engineid\". "
                    + "The \"content\" should be a valid Drools rule string and the \"engineid\" "
                    + "has to be \"engine-d\" in the Amsterdam release.", required = true) DeployRuleRequest deployRuleRequest,
            @Context HttpServletRequest httpRequest) {

        CorrelationRuleResponse crResponse = new CorrelationRuleResponse();
        Locale locale = LanguageUtil.getLocale(httpRequest);
        try {
            String packageName = droolsEngine.deployRule(deployRuleRequest, locale);
            DmaapService.loopControlNames
                    .put(packageName, deployRuleRequest.getLoopControlName());
            log.info("Rule deployed. Package name: " + packageName);
            crResponse.setPackageName(packageName);

        } catch (CorrelationException correlationException) {
            log.error(correlationException.getMessage(), correlationException);
            throw ExceptionUtil.buildExceptionResponse(correlationException.getMessage());
        }

        return crResponse;
    }

    @DELETE
    @ApiOperation(value = "Undeploy a rule from the Drools engine.")
    @Produces(MediaType.APPLICATION_JSON)
    @Timed
    @Path("/{packageName}")
    public boolean undeployRule(@PathParam("packageName") String packageName,
            @Context HttpServletRequest httpRequest) {

        Locale locale = LanguageUtil.getLocale(httpRequest);

        try {

            droolsEngine.undeployRule(packageName, locale);

        } catch (CorrelationException correlationException) {
            log.error(correlationException.getMessage(), correlationException);
            throw ExceptionUtil.buildExceptionResponse(correlationException.getMessage());
        }
        return true;
    }


    @POST
    @ApiOperation(value = "Check the validity of a rule.")
    @Produces(MediaType.APPLICATION_JSON)
    @Timed
    public boolean compileRule(CompileRuleRequest compileRuleRequest,
            @Context HttpServletRequest httpRequest) {

        Locale locale = LanguageUtil.getLocale(httpRequest);

        try {
            droolsEngine.compileRule(compileRuleRequest.getContent(), locale);
        } catch (CorrelationException correlationException) {
            log.error(correlationException.getMessage(), correlationException);
            throw ExceptionUtil.buildExceptionResponse(correlationException.getMessage());
        }
        return true;
    }
}

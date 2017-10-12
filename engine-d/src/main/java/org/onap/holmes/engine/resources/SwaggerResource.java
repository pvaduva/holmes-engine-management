/*
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

import io.swagger.annotations.Api;
import io.swagger.annotations.SwaggerDefinition;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import lombok.extern.slf4j.Slf4j;
import org.jvnet.hk2.annotations.Service;

@Service
@Path("/swagger.json")
@Produces(MediaType.APPLICATION_JSON)
@Slf4j
public class SwaggerResource {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getSwaggerJson() {
        URL url = SwaggerResource.class.getResource("/swagger.json");
        String ret = "{}";
        try {
            System.out.println(URLDecoder.decode(url.getPath(), "UTF-8"));
            File file = new File(URLDecoder.decode(url.getPath(), "UTF-8"));

            BufferedReader br = new BufferedReader(new FileReader(file));
            StringBuffer buffer = new StringBuffer();
            String line = " ";
            while ((line = br.readLine()) != null) {
                buffer.append(line);
            }
            ret = buffer.toString();
        } catch (FileNotFoundException e) {
            log.warn("Failed to read the API description file.");
        } catch (IOException e) {
            log.warn("An error occurred while reading swagger.json.");
        }
        return ret;
    }
}
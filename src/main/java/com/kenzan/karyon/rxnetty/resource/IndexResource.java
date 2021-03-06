/**
 * Copyright 2015 Kenzan, LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.kenzan.karyon.rxnetty.resource;

import io.netty.buffer.ByteBuf;
import io.reactivex.netty.protocol.http.server.HttpServerRequest;
import io.reactivex.netty.protocol.http.server.HttpServerResponse;
import io.reactivex.netty.protocol.http.server.RequestHandler;
import netflix.karyon.transport.http.SimpleUriRouter;
import rx.Observable;
import rx.functions.Func1;

import com.kenzan.karyon.rxnetty.endpoint.HelloEndpoint;
import java.io.*;

public class IndexResource implements RequestHandler<ByteBuf, ByteBuf>{

    private final SimpleUriRouter<ByteBuf, ByteBuf> delegate;
    private final HelloEndpoint endpoint;

    public static String execCmd(String cmd) throws java.io.IOException {
        java.util.Scanner s = new java.util.Scanner(Runtime.getRuntime().exec(cmd).getInputStream()).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }
    public IndexResource() {
        endpoint = new HelloEndpoint();
        delegate = new SimpleUriRouter<>();

        delegate
        .addUri("/", new RequestHandler<ByteBuf, ByteBuf>() {
            @Override
            public Observable<Void> handle(HttpServerRequest<ByteBuf> request,
                    final HttpServerResponse<ByteBuf> response) {

                return endpoint.getHello()
                .flatMap(new Func1<String, Observable<Void>>() {
                    @Override
                    public Observable<Void> call(String body) {
                        String instanceId = "none";
                      

                        try{
    
                            
                            instanceId = "instance id: " + execCmd("wget -q -O - http://169.254.169.254/latest/meta-data/instance-id");
                            
                            if (instanceId.equals("instance id: ")){
                                instanceId = "container id: " + execCmd("cat /proc/self/cgroup | grep docker | grep -o -E '[0-9a-f]{64}' | head -n 1");
                            }
                            
                            if (instanceId.equals("container id: ")){
                                instanceId = "container id: " + execCmd("hostname");
                            }
                            
                           
                
                            
                            
                        } catch (Exception e){
                            e.printStackTrace();
                        }
                        
                        
                        String caption = "chemical brothers!";
                        
                        
                        
                        response.writeString("<html><head><style>body{text-align:center;font-family:'Lucida Grande'}</style></head><body><img height='66' width='176' src='http://kenzan.com/wp-content/themes/kenzan/images/logo-reg.png' /><h2>" + caption + "</h2><h3>" + instanceId + "</h3></body></html>");
                        return response.close();
                    }
                });
            }
        });
    }
    @Override
    public Observable<Void> handle(HttpServerRequest<ByteBuf> request,
            HttpServerResponse<ByteBuf> response) {
        return delegate.handle(request, response);
    }

}

package com.mobei.zuul;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import springfox.documentation.swagger.web.SwaggerResource;
import springfox.documentation.swagger.web.SwaggerResourcesProvider;

import java.util.ArrayList;
import java.util.List;

/**
 * 添加文档来源
 *
 * 必须加@Primary注解,否则会报以下错误:
 *      Parameter 0 of constructor in springfox.documentation.swagger.web.ApiResourceController required a single bean, but 2 were found:
 */
@Component
@Primary
public class DocumentationConfig implements SwaggerResourcesProvider {

//    private final RouteLocator routeLocator;
//
//    public DocumentationConfig(RouteLocator routeLocator) {
//        this.routeLocator = routeLocator;
//    }

    @Override
    public List<SwaggerResource> get() {
        List<SwaggerResource> resources = new ArrayList();

//        List<Route> routes = routeLocator.getRoutes();
//        routes.forEach(route -> {
//            if (route.getId().startsWith("api-")) {
//                resources.add(swaggerResource(route.getId(),
//                        route.getFullPath().replace("**", "v2/api-docs"), "2.0"));
//            }
//        });


        //网关通过服务别名去获取远程服务的SwaggerApi:网关中配置了path为"/api-xxx/**"并且设置了ignored-services: "*",所以这里也要设置成api开头的,不能设置成服务名
        resources.add(swaggerResource("app-member", "/api-member/v2/api-docs", "2.0"));
        resources.add(swaggerResource("app-wechat", "/api-wechat/v2/api-docs", "2.0"));
        return resources;
    }

    private SwaggerResource swaggerResource(String name, String location, String version) {
        SwaggerResource swaggerResource = new SwaggerResource();
        swaggerResource.setName(name);//swagger获取对应服务API的别名
        swaggerResource.setLocation(location);//会将服务名转换成ip地址获取接口信息:比如127.0.0.1:8300/v2/api-docs
        swaggerResource.setSwaggerVersion(version);
        return swaggerResource;
    }
}

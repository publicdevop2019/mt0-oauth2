//package com.mt.aggregate.endpoint;
//
//import com.hw.aggregate.endpoint.model.BizEndpoint;
//import com.hw.aggregate.endpoint.representation.AppBizEndpointCardRep;
//import com.mt.common.rest.RoleBasedRestfulService;
//import com.mt.common.rest.VoidTypedClass;
//import com.mt.common.sql.RestfulQueryRegistry;
//import com.rabbitmq.client.Channel;
//import com.rabbitmq.client.Connection;
//import com.rabbitmq.client.ConnectionFactory;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Service;
//
//import java.io.IOException;
//import java.util.concurrent.TimeoutException;
//
//import static com.hw.config.filter.EndpointFilter.EXCHANGE_RELOAD_EP_CACHE;
//
//@Slf4j
//@Service
//public class AppBizEndpointApplicationService extends RoleBasedRestfulService<BizEndpoint, AppBizEndpointCardRep, Void, VoidTypedClass> {
//    {
//        entityClass = BizEndpoint.class;
//        role = RestfulQueryRegistry.RoleEnum.APP;
//    }
//
//    @Override
//    public AppBizEndpointCardRep getEntitySumRepresentation(BizEndpoint bizEndpoint) {
//        return new AppBizEndpointCardRep(bizEndpoint);
//    }
//
//    @Override
//    public void afterWriteComplete() {
//        ConnectionFactory factory = new ConnectionFactory();
//        factory.setHost("localhost");
//        try (Connection connection = factory.newConnection();
//             Channel channel = connection.createChannel()) {
//            channel.exchangeDeclare(EXCHANGE_RELOAD_EP_CACHE, "fanout");
//            channel.basicPublish(EXCHANGE_RELOAD_EP_CACHE, "", null, null);
//            log.info("sent clean filter cache message");
//        } catch (IOException | TimeoutException e) {
//            log.error("error in mq", e);
//        }
//    }
//}

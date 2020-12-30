//package com.mt.aggregate.endpoint;
//
//import com.hw.aggregate.endpoint.command.RootCreateBizEndpointCommand;
//import com.hw.aggregate.endpoint.command.RootUpdateBizEndpointCommand;
//import com.hw.aggregate.endpoint.model.BizEndpoint;
//import com.hw.aggregate.endpoint.model.RootBizEndpointPatchMiddleLayer;
//import com.hw.aggregate.endpoint.representation.RootBizEndpointCardRep;
//import com.hw.aggregate.endpoint.representation.RootBizEndpointRep;
//import com.mt.common.rest.RoleBasedRestfulService;
//import com.mt.common.sql.RestfulQueryRegistry;
//import com.rabbitmq.client.Channel;
//import com.rabbitmq.client.Connection;
//import com.rabbitmq.client.ConnectionFactory;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.io.IOException;
//import java.util.concurrent.TimeoutException;
//
//import static com.hw.config.filter.EndpointFilter.EXCHANGE_RELOAD_EP_CACHE;
//
//@Slf4j
//@Service
//public class RootBizEndpointApplicationService extends RoleBasedRestfulService<BizEndpoint, RootBizEndpointCardRep, RootBizEndpointRep, RootBizEndpointPatchMiddleLayer> {
//    {
//        entityClass = BizEndpoint.class;
//        role = RestfulQueryRegistry.RoleEnum.ROOT;
//        entityPatchSupplier = RootBizEndpointPatchMiddleLayer::new;
//    }
//    @Autowired
//    AppBizEndpointApplicationService appBizEndpointApplicationService;
//
//    @Override
//    public BizEndpoint replaceEntity(BizEndpoint bizEndpoint, Object command) {
//        return bizEndpoint.replace((RootUpdateBizEndpointCommand) command);
//    }
//
//    @Override
//    public RootBizEndpointCardRep getEntitySumRepresentation(BizEndpoint bizEndpoint) {
//        return new RootBizEndpointCardRep(bizEndpoint);
//    }
//
//    @Override
//    public RootBizEndpointRep getEntityRepresentation(BizEndpoint bizEndpoint) {
//        return new RootBizEndpointRep(bizEndpoint);
//    }
//
//    @Override
//    protected BizEndpoint createEntity(long id, Object command) {
//        return BizEndpoint.create(id, (RootCreateBizEndpointCommand) command, appBizEndpointApplicationService);
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

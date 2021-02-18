package com.mt.identityaccess.domain.service;

import com.mt.common.domain_event.DomainEventPublisher;
import com.mt.common.query.PageConfig;
import com.mt.common.sql.SumPagedRep;
import com.mt.identityaccess.application.endpoint.EndpointQuery;
import com.mt.identityaccess.domain.DomainRegistry;
import com.mt.identityaccess.domain.model.endpoint.Endpoint;
import com.mt.identityaccess.domain.model.endpoint.event.EndpointReloadRequested;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Service
public class EndpointService {
    public Set<Endpoint> getEndpointsOfQuery(EndpointQuery queryParam) {
        PageConfig queryPagingParam = new PageConfig();
        SumPagedRep<Endpoint> tSumPagedRep = DomainRegistry.endpointRepository().endpointsOfQuery(queryParam, queryPagingParam);
        if (tSumPagedRep.getData().size() == 0)
            return new HashSet<>();
        double l = (double) tSumPagedRep.getTotalItemCount() / tSumPagedRep.getData().size();//for accuracy
        double ceil = Math.ceil(l);
        int i = BigDecimal.valueOf(ceil).intValue();
        Set<Endpoint> data = new HashSet<>(tSumPagedRep.getData());
        for (int a = 1; a < i; a++) {
            data.addAll(DomainRegistry.endpointRepository().endpointsOfQuery(queryParam, queryPagingParam.pageOf(a)).getData());
        }
        return data;
    }

    public void reloadEndpointCache() {
        DomainEventPublisher.instance().publish(new EndpointReloadRequested());
    }
}

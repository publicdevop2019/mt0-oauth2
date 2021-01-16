package com.mt.identityaccess.domain.service;

import com.mt.common.domain_event.DomainEventPublisher;
import com.mt.common.query.DefaultPaging;
import com.mt.common.sql.SumPagedRep;
import com.mt.identityaccess.application.endpoint.EndpointQuery;
import com.mt.identityaccess.domain.DomainRegistry;
import com.mt.identityaccess.domain.model.endpoint.Endpoint;
import com.mt.identityaccess.domain.model.endpoint.event.EndpointCollectionModified;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class EndpointService {
    public List<Endpoint> getEndpointsOfQuery(EndpointQuery queryParam) {
        DefaultPaging queryPagingParam = new DefaultPaging();
        SumPagedRep<Endpoint> tSumPagedRep = DomainRegistry.endpointRepository().endpointsOfQuery(queryParam, queryPagingParam);
        if (tSumPagedRep.getData().size() == 0)
            return new ArrayList<>();
        double l = (double) tSumPagedRep.getTotalItemCount() / tSumPagedRep.getData().size();//for accuracy
        double ceil = Math.ceil(l);
        int i = BigDecimal.valueOf(ceil).intValue();
        List<Endpoint> data = new ArrayList<>(tSumPagedRep.getData());
        for (int a = 1; a < i; a++) {
            data.addAll(DomainRegistry.endpointRepository().endpointsOfQuery(queryParam, queryPagingParam.nextPage()).getData());
        }
        return data;
    }

    public void reloadEndpointCache() {
        DomainEventPublisher.instance().publish(new EndpointCollectionModified());
    }
}

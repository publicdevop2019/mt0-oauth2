package com.mt.identityaccess.domain.service;

import com.mt.common.sql.SumPagedRep;
import com.mt.identityaccess.application.client.EndpointPaging;
import com.mt.identityaccess.application.client.EndpointQuery;
import com.mt.identityaccess.domain.DomainRegistry;
import com.mt.identityaccess.domain.model.endpoint.Endpoint;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class EndpointService {
    public List<Endpoint> getEndpointsOfQuery(EndpointQuery queryParam) {
        EndpointPaging queryPagingParam = new EndpointPaging();
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

}

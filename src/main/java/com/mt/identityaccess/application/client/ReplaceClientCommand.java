package com.mt.identityaccess.application.client;

import com.mt.common.rest.AggregateUpdateCommand;
import lombok.Data;

import java.io.Serializable;

@Data
public class ReplaceClientCommand extends ProvisionClientCommand implements Serializable, AggregateUpdateCommand {
    private static final long serialVersionUID = 1;
    private Integer version;
}

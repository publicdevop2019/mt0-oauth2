package com.mt.identityaccess.application.client.command;

import com.mt.common.rest.AggregateUpdateCommand;
import lombok.Data;

import java.io.Serializable;

@Data
public class ClientUpdateCommand extends ClientCreateCommand implements Serializable, AggregateUpdateCommand {
    private static final long serialVersionUID = 1;
    private Integer version;
}

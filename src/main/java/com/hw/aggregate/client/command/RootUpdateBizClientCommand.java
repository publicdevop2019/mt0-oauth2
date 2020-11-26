package com.hw.aggregate.client.command;

import com.hw.shared.rest.AggregateUpdateCommand;
import lombok.Data;

import java.io.Serializable;

@Data
public class RootUpdateBizClientCommand extends RootCreateBizClientCommand implements Serializable, AggregateUpdateCommand {
    private static final long serialVersionUID = 1;
    private Integer version;
}

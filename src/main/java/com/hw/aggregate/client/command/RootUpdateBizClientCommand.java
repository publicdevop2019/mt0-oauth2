package com.hw.aggregate.client.command;

import lombok.Data;

import java.io.Serializable;

@Data
public class RootUpdateBizClientCommand extends RootCreateBizClientCommand implements Serializable {
    private static final long serialVersionUID = 1;
}

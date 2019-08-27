package com.bricerising.stages

import com.bricerising.tools.Tool

public interface Stage extends Serializable {
    private Queue<Tool> tools
    public void execute(steps, String opts)
}

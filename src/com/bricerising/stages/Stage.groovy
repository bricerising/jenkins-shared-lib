package com.bricerising.stages

import com.bricerising.tools.Tool

public class Stage extends Serializable {
    private LinkedList tools

    Stage() {
        this.tools = [] as LinkedList
    }

    public void add(Tool tool) {
        this.tools.add(tool)
    }

    public void execute(steps) {
        steps.unstash 'jobdir'
        for(Tool tool: this.tools) {
            tool.execute(steps)
        }
        steps.stash includes: '**', name: 'jobdir'
    }
}

package com.bricerising.stages

import com.bricerising.tools.Tool

public class Stage implements Serializable {
    private LinkedList tools

    Stage() {
        this.tools = [] as LinkedList
    }

    public void add(Tool tool) {
        this.tools.add(tool)
    }

    public void execute(steps) {
        steps.unstash 'jobdir'
        steps.sh 'chown -R 10000 .'
        for(int i=0; i < this.tools.size(); i++) {
            this.tools.get(i).execute(steps)
        }
        steps.stash includes: '**', name: 'jobdir'
    }
}

package com.bricerising.tools.build

import com.tools.Tool

public class DockerBuildTool implements Tool {
    
    private String tag

    DockerBuildTool(String tag, String contextPath = '.') {
        this.tag = tag
    }

    public void execute(steps, String opts) {
        steps.sh("docker build -t ${this.tag} ${opts} .")
    }

}

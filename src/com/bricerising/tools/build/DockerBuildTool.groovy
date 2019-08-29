package com.bricerising.tools.build

import com.bricerising.tools.Tool

public class DockerBuildTool implements Tool {
    
    private String image
    private String version
    private String opts

    DockerBuildTool(String image, String version, String opts = '.') {
        this.image = image
        this.version = version
        this.opts = opts
    }

    public void execute(steps) {
        steps.sh("docker build -t ${this.image}:${this.version} ${this.opts}")
    }

}

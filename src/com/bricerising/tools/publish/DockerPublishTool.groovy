package com.bricerising.tools.deploy

import com.tools.Tool

public class DockerPublishTool implements Tool {
    
    private String version
    private String image

    DockerPublishTool(String image, String version) {
        this.version = version
        this.image = image
    }

    public void execute(steps, String opts) {
        steps.sh "docker push ${opts} ${this.image}:${this.version}"
    }

}

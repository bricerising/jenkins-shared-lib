package com.bricerising.tools.deploy

import com.bricerising.tools.Tool

public class DockerPublishTool implements Tool {
    
    private String version
    private String image

    DockerPublishTool(String image, String version) {
        this.version = version
        this.image = image
    }

    public void execute(steps) {
        steps.sh "docker push ${this.image}:${this.version}"
    }

}

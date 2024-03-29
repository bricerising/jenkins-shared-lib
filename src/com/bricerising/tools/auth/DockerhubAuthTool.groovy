
package com.bricerising.tools.auth

import com.bricerising.tools.Tool

public class DockerhubAuthTool implements Tool {
    
    private String registryUrl

    DockerhubAuthTool(String registryUrl) {
        this.registryUrl = registryUrl
    }

    public void execute(steps) {
        steps.sh("docker login -u \${DOCKERHUB_USER} -p \${DOCKERHUB_PASSWORD} ${this.registryUrl}")
    }

}

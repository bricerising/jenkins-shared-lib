package com.bricerising.tools.build

import com.bricerising.tools.Tool
import groovy.json.JsonSlurper

public class NpmBuildTool implements Tool {

    String packageVersion = ''
    
    NpmBuildTool() {}

    public void execute(steps) {
        steps.sh("npm install")
        steps.sh("npm test")
        this.packageVersion = steps.sh(
            script: 'node -p "require(\'./package.json\').version"',
            returnStdout:true
        ).trim()
    }

}

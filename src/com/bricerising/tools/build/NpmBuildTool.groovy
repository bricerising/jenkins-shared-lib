package com.bricerising.tools.build

import com.bricerising.tools.Tool
import groovy.json.JsonSlurper

public class NpmBuildTool implements Tool {

    String packageVersion = ''
    
    NpmBuildTool() {}

    public void execute(steps) {
        steps.sh("npm install")
        steps.sh("npm test")
        def packageJson = new JsonSlurper().parse(new File('./package.json'))
        this.packageVersion = packageJson.version
    }

}

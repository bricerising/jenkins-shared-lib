package com.bricerising.tools.build

import com.tools.Tool

public class NpmBuildTool implements Tool {
    
    NpmBuildTool() {}

    public void execute(steps) {
        steps.sh("npm install")
        steps.sh("npm test")
    }

}

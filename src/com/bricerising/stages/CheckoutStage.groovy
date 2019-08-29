package com.bricerising.stages

import java.util.TreeMap

public class CheckoutStage implements Serializable {

    def scm
    TreeMap scmVars

    CheckoutStage(scm) {
        this.scm = scm
    }

    public void execute(steps) {
        this.scmVars = steps.checkout(this.scm)
        steps.sh 'chown -R 10000 .'
        steps.stash includes: '**', name: 'jobdir'
    }

}
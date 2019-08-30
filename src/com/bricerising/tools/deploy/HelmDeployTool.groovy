package com.bricerising.tools.deploy

import com.bricerising.tools.Tool

public class HelmDeployTool implements Tool {
    
    private String namespace
    private String name
    private String opts

    HelmDeployTool(String name, String namespace, String opts = '') {
        this.namespace = namespace
        this.name = name
        this.opts = opts
    }

    public void execute(steps) {
        String tillerServiceSetup = """
            set +e

            kubectl create namespace ${namespace}
            cat <<EoF | kubectl apply -f -
---
apiVersion: v1
kind: ServiceAccount
metadata:
  name: tiller
  namespace: ${namespace}
---
apiVersion: rbac.authorization.k8s.io/v1beta1
kind: RoleBinding
metadata:
  name: tiller
  namespace: ${namespace}
roleRef:
  apiGroup: rbac.authorization.k8s.io
  kind: ClusterRole
  name: cluster-admin
subjects:
  - kind: ServiceAccount
    name: tiller
    namespace: ${namespace}
EoF
        """
        String helmInitCommand = """
            helm init --upgrade \
                --service-account tiller \
                --tiller-namespace $(namespace}
        """
        String helmInstallCommand = """
          helm upgrade --install \
                --tiller-namespace ${namespace} \
                --namespace ${namespace} \
                ${this.opts} \
                ${this.name}
        """
        steps.sh tillerServiceSetup
        steps.sh helmInitCommand
        steps.sh helmInstallCommand
    }

}

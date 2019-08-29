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
        steps.sh """
            set +e

            kubectl create namespace ${this.namespace}
            cat <<EoF | kubectl apply -f -
---
apiVersion: v1
kind: ServiceAccount
metadata:
  name: tiller
  namespace: ${this.namespace}
---
apiVersion: rbac.authorization.k8s.io/v1beta1
kind: RoleBinding
metadata:
  name: tiller
  namespace: ${this.namespace}
roleRef:
  apiGroup: rbac.authorization.k8s.io
  kind: ClusterRole
  name: cluster-admin
subjects:
  - kind: ServiceAccount
    name: tiller
    namespace: ${this.namespace}
EoF
        """
        steps.sh """
            helm init --upgrade \
                --service-account tiller \
                --tiller-namespace $(this.namespace}
        """
        steps.sh """
            helm upgrade --install \
                --tiller-namespace ${this.namespace} \
                --namespace ${namespace} \
                ${this.opts} \
                ${this.name}
        """
    }

}

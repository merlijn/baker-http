# Baker on kubernetes

### Initial setup

```bash
# create a development namespace with required config
kubectl create -f kubernetes/namespace.yaml

# switch to that namespace
kubectl config use-context dev
```

### Start up

```bash
# build a docker image
sbt docker:publishLocal

# start a cluster of 3
kubectl create -f kubernetes/baker.yaml
```

### Re-start

```bash
# scale down to 0 nodes
kubectl scale deployment baker --replicas=0

# build a fresh image with your changes
sbt docker:publishLocal

# scale up again, a minimum of 2 nodes is required to boot the akka cluster
kubectl scale deployment baker --replicas=3
```

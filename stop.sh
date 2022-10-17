docker-compose -f ./doc/remind-thing.yml down
docker rmi `docker images | grep baidubce | awk '{print $3}'`